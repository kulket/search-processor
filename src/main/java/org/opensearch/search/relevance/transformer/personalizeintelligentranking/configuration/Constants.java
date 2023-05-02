/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration;

import static org.opensearch.search.relevance.configuration.Constants.*;

public class Constants {
    // Personalize transformer name
    public static final String PERSONALIZE_INTELLIGENT_RANKING_TRANSFORMER_NAME = "personalize_intelligent_ranking";

    // Personalize transformer properties
    public static final String ITEM_ID_FIELD = "item_id_field";
    public static final String CAMPAIGN_FIELD = "campaign_arn";

    public static final String PERSONALIZE_SETTINGS_PREFIX =
            String.join(".", RESULT_TRANSFORMER_SETTING_PREFIX, PERSONALIZE_INTELLIGENT_RANKING_TRANSFORMER_NAME);

    public static final String ORDER_SETTING_NAME =
            String.join(".", PERSONALIZE_SETTINGS_PREFIX, ORDER);

    public static final String ITEM_ID_FIELD_SETTING_NAME =
            String.join(".", PERSONALIZE_SETTINGS_PREFIX, PROPERTIES, ITEM_ID_FIELD);

    public static final String CAMPAIGN_FIELD_SETTING_NAME =
            String.join(".", PERSONALIZE_SETTINGS_PREFIX, PROPERTIES, CAMPAIGN_FIELD);
}
