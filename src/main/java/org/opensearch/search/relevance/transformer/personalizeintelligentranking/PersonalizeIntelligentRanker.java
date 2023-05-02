/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.search.relevance.transformer.personalizeintelligentranking;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.common.settings.Setting;
import org.opensearch.search.SearchHits;
import org.opensearch.search.relevance.configuration.ResultTransformerConfiguration;
import org.opensearch.search.relevance.configuration.ResultTransformerConfigurationFactory;
import org.opensearch.search.relevance.transformer.ResultTransformer;
import org.opensearch.search.relevance.transformer.personalizeintelligentranking.client.PersonalizeClient;
import org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration.PersonalizeIntelligentRankerSettings;
import org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration.PersonalizeIntelligentRankingConfigurationFactory;

import java.util.List;

public class PersonalizeIntelligentRanker implements ResultTransformer {
    private static final Logger logger = LogManager.getLogger(PersonalizeIntelligentRanker.class);

    public static final String NAME = "personalize_intelligent_ranking";

    private final PersonalizeClient personalizeClient;

    public PersonalizeIntelligentRanker(PersonalizeClient client) {
        this.personalizeClient = client;
    }

    @Override
    public List<Setting<?>> getTransformerSettings() {
        return PersonalizeIntelligentRankerSettings.getAllSettings();
    }

    @Override
    public ResultTransformerConfigurationFactory getConfigurationFactory() {
        return PersonalizeIntelligentRankingConfigurationFactory.INSTANCE;
    }

    /**
     * Check if search request is eligible for personalize re-ranking
     *
     * @param request Search Request
     * @return boolean decision on whether to re-rank
     */
    @Override
    public boolean shouldTransform(SearchRequest request, ResultTransformerConfiguration configuration) {
        // TODO: Update logic to ensure to block Personalize request when not necessary
        return true;
    }

    @Override
    public SearchRequest preprocessRequest(SearchRequest request, ResultTransformerConfiguration configuration) {
        // TODO: Update any request preprocess logic here
        return request;
    }

    /**
     * @param hits    Search hits to rerank with respect to query
     * @param request Search request
     * @return SearchHits re-ranked search hits
     */
    @Override
    public SearchHits transform(SearchHits hits, SearchRequest request, ResultTransformerConfiguration configuration) {
        logger.info("Re-ranking search results");
        return hits;
    }
}
