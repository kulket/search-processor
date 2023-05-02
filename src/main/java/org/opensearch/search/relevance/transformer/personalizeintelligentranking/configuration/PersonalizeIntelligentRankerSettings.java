/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration;

import org.opensearch.common.settings.Setting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class PersonalizeIntelligentRankerSettings {
    public static final Setting<Integer> PERSONALIZE_ORDER_SETTING = Setting.intSetting(Constants.ORDER_SETTING_NAME, 1, 1,
            Setting.Property.Dynamic, Setting.Property.IndexScope);

    public static final Setting<List<String>> ITEM_ID_FIELD_SETTING = Setting.listSetting(Constants.ITEM_ID_FIELD_SETTING_NAME,
            Collections.emptyList(), Function.identity(),
            Setting.Property.Dynamic, Setting.Property.IndexScope);

    public static final Setting<List<String>> CAMPAIGN_FIELD_SETTING = Setting.listSetting(Constants.CAMPAIGN_FIELD_SETTING_NAME,
            Collections.emptyList(), Function.identity(),
            Setting.Property.Dynamic, Setting.Property.IndexScope);

    public static List<Setting<?>> getAllSettings() {
        return Arrays.asList(
                PERSONALIZE_ORDER_SETTING,
                ITEM_ID_FIELD_SETTING,
                CAMPAIGN_FIELD_SETTING
        );
    }
}
