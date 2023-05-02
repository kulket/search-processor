/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration;

import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.settings.Settings;
import org.opensearch.core.xcontent.XContentParser;
import org.opensearch.search.relevance.configuration.ResultTransformerConfiguration;
import org.opensearch.search.relevance.configuration.ResultTransformerConfigurationFactory;

import java.io.IOException;

import static org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration.Constants.PERSONALIZE_INTELLIGENT_RANKING_TRANSFORMER_NAME;

public class PersonalizeIntelligentRankingConfigurationFactory implements ResultTransformerConfigurationFactory {

    private PersonalizeIntelligentRankingConfigurationFactory() {
    }

    public static final PersonalizeIntelligentRankingConfigurationFactory INSTANCE
            = new PersonalizeIntelligentRankingConfigurationFactory();

    @Override
    public String getName() {
        return PERSONALIZE_INTELLIGENT_RANKING_TRANSFORMER_NAME;
    }

    @Override
    public ResultTransformerConfiguration configure(Settings indexSettings) {

        return new PersonalizeIntelligentRankingConfiguration(indexSettings);
    }

    @Override
    public ResultTransformerConfiguration configure(XContentParser parser) throws IOException {
        return PersonalizeIntelligentRankingConfiguration.parse(parser);
    }

    @Override
    public ResultTransformerConfiguration configure(StreamInput streamInput) throws IOException {
        return new PersonalizeIntelligentRankingConfiguration(streamInput);
    }
}
