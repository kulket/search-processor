/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.search.relevance.transformer.personalizeintelligentranking;

import com.amazonaws.auth.AWSCredentialsProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.search.SearchResponseSections;
import org.opensearch.ingest.ConfigurationUtils;
import org.opensearch.search.SearchHits;
import org.opensearch.search.aggregations.InternalAggregations;
import org.opensearch.search.internal.InternalSearchResponse;
import org.opensearch.search.pipeline.Processor;
import org.opensearch.search.pipeline.SearchResponseProcessor;
import org.opensearch.search.profile.SearchProfileShardResults;
import org.opensearch.search.relevance.transformer.personalizeintelligentranking.client.PersonalizeClient;
import org.opensearch.search.relevance.transformer.personalizeintelligentranking.client.PersonalizeClientSettings;
import org.opensearch.search.relevance.transformer.personalizeintelligentranking.client.PersonalizeCredentialsProviderFactory;
import org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration.PersonalizeIntelligentRankerConfiguration;
import org.opensearch.search.relevance.transformer.personalizeintelligentranking.requestparameter.PersonalizeRequestParameterUtil;
import org.opensearch.search.relevance.transformer.personalizeintelligentranking.requestparameter.PersonalizeRequestParameters;
import org.opensearch.search.relevance.transformer.personalizeintelligentranking.reranker.PersonalizedRanker;
import org.opensearch.search.relevance.transformer.personalizeintelligentranking.reranker.PersonalizedRankerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration.Constants.CAMPAIGN_ARN_CONFIG_NAME;
import static org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration.Constants.IAM_ROLE_ARN_CONFIG_NAME;
import static org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration.Constants.RECIPE_CONFIG_NAME;
import static org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration.Constants.REGION_CONFIG_NAME;
import static org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration.Constants.ITEM_ID_FIELD_CONFIG_NAME;
import static org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration.Constants.WEIGHT_CONFIG_NAME;

/**
 * This is a {@link SearchResponseProcessor} that applies Personalized intelligent ranking
 */
public class PersonalizeRankingResponseProcessor implements SearchResponseProcessor {

    private static final Logger logger = LogManager.getLogger(PersonalizeRankingResponseProcessor.class);

    public static final String TYPE = "personalize_ranking";
    private final String tag;
    private final String description;
    private final PersonalizeClient personalizeClient;
    private final PersonalizeIntelligentRankerConfiguration rankerConfig;

    /**
     * Constructor for Personalize ranking response processor
     *
     * @param tag           processor tag
     * @param description   processor description
     * @param rankerConfig  personalize ranker config
     * @param client        personalize client
     */
    public PersonalizeRankingResponseProcessor(String tag,
                                               String description,
                                               PersonalizeIntelligentRankerConfiguration rankerConfig,
                                               PersonalizeClient client) {
        super();
        this.tag = tag;
        this.description = description;
        this.rankerConfig = rankerConfig;
        this.personalizeClient = client;
    }

    /**
     * Transform the response hits by re ranking results using Personalize
     *
     * @param request Search request
     * @param response Search response that needs to be transformed
     * @return Transformed search response using personalized re ranking
     * @throws Exception Throws exception for any error while processing response
     */
    @Override
    public SearchResponse processResponse(SearchRequest request, SearchResponse response) throws Exception {
        SearchHits hits = response.getHits();

        if (hits.getHits().length == 0) {
            logger.info("TotalHits = 0. Returning search response without applying Personalize transform");
            return response;
        }
        logger.info("Personalizing search results.");
        PersonalizeRequestParameters personalizeRequestParameters =
                PersonalizeRequestParameterUtil.getPersonalizeRequestParameters(request);
        PersonalizedRankerFactory rankerFactory = new PersonalizedRankerFactory();
        PersonalizedRanker ranker = rankerFactory.getPersonalizedRanker(rankerConfig, personalizeClient);
        long startTime = System.nanoTime();
        SearchHits personalizedHits = ranker.rerank(hits, personalizeRequestParameters);
        long personalizeTimeTookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

        final SearchResponseSections transformedSearchResponseSections = new InternalSearchResponse(personalizedHits,
                (InternalAggregations) response.getAggregations(), response.getSuggest(),
                new SearchProfileShardResults(response.getProfileResults()), response.isTimedOut(),
                response.isTerminatedEarly(), response.getNumReducePhases());

        final SearchResponse transformedResponse = new SearchResponse(transformedSearchResponseSections, response.getScrollId(),
                response.getTotalShards(), response.getSuccessfulShards(),
                response.getSkippedShards(), response.getTook().getMillis() + personalizeTimeTookMs, response.getShardFailures(),
                response.getClusters());

        logger.info("Personalize ranking processor took " + personalizeTimeTookMs + " ms");

        return transformedResponse;
    }

    /**
     * Get the type of the processor.
     */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * Get the tag of a processor.
     */
    @Override
    public String getTag() {
        return tag;
    }

    /**
     * Gets the description of a processor.
     */
    @Override
    public String getDescription() {
        return description;
    }

    public static final class Factory implements Processor.Factory {

        PersonalizeClientSettings personalizeClientSettings;

        public Factory(PersonalizeClientSettings settings) {
            this.personalizeClientSettings = settings;
        }

        @Override
        public PersonalizeRankingResponseProcessor create(Map<String, Processor.Factory> processorFactories, String tag, String description, Map<String, Object> config) throws Exception {
            // TODO: Handle validation as well as required vs non required config differentiation with related user error / exception
            String personalizeCampaign = ConfigurationUtils.readStringProperty(TYPE, tag, config, CAMPAIGN_ARN_CONFIG_NAME);
            String iamRoleArn = ConfigurationUtils.readOptionalStringProperty(TYPE, tag, config, IAM_ROLE_ARN_CONFIG_NAME);
            String recipe = ConfigurationUtils.readStringProperty(TYPE, tag, config, RECIPE_CONFIG_NAME);
            String itemIdField = ConfigurationUtils.readOptionalStringProperty(TYPE, tag, config, ITEM_ID_FIELD_CONFIG_NAME);
            String awsRegion = ConfigurationUtils.readStringProperty(TYPE, tag, config, REGION_CONFIG_NAME);
            double weight = ConfigurationUtils.readDoubleProperty(TYPE, tag, config, WEIGHT_CONFIG_NAME);

            PersonalizeIntelligentRankerConfiguration rankerConfig =
                    new PersonalizeIntelligentRankerConfiguration(personalizeCampaign, iamRoleArn, recipe, itemIdField, awsRegion, weight);
            PersonalizeCredentialsProviderFactory credentialsProviderFactory = new PersonalizeCredentialsProviderFactory();
            AWSCredentialsProvider credentialsProvider = credentialsProviderFactory.getCredentialsProvider(personalizeClientSettings, iamRoleArn, awsRegion);
            PersonalizeClient personalizeClient = new PersonalizeClient(credentialsProvider, awsRegion);
            return new PersonalizeRankingResponseProcessor(tag, description, rankerConfig, personalizeClient);
        }
    }
}
