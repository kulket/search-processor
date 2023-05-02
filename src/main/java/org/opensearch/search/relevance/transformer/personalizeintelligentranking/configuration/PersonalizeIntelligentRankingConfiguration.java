/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration;

import org.opensearch.common.ParsingException;
import org.opensearch.common.io.stream.StreamInput;
import org.opensearch.common.io.stream.StreamOutput;
import org.opensearch.common.io.stream.Writeable;
import org.opensearch.common.settings.Settings;
import org.opensearch.core.ParseField;
import org.opensearch.core.xcontent.ObjectParser;
import org.opensearch.core.xcontent.ToXContentObject;
import org.opensearch.core.xcontent.XContentBuilder;
import org.opensearch.core.xcontent.XContentParser;
import org.opensearch.search.relevance.configuration.ResultTransformerConfiguration;
import org.opensearch.search.relevance.configuration.TransformerConfiguration;

import java.io.IOException;
import java.util.Objects;

import static org.opensearch.search.relevance.configuration.Constants.ORDER;
import static org.opensearch.search.relevance.transformer.personalizeintelligentranking.configuration.Constants.PERSONALIZE_INTELLIGENT_RANKING_TRANSFORMER_NAME;

public class PersonalizeIntelligentRankingConfiguration extends ResultTransformerConfiguration {
    private static final ObjectParser<PersonalizeIntelligentRankingConfiguration, Void> PARSER;

    static {
        PARSER = new ObjectParser<>("personalize_intelligent_ranking_configuration", PersonalizeIntelligentRankingConfiguration::new);
        PARSER.declareInt(TransformerConfiguration::setOrder, TRANSFORMER_ORDER);
        PARSER.declareObject(PersonalizeIntelligentRankingConfiguration::setProperties,
                PersonalizeIntelligentRankingProperties::parse,
                TRANSFORMER_PROPERTIES);
    }

    private PersonalizeIntelligentRankingProperties properties;

    public PersonalizeIntelligentRankingConfiguration(){}

    public PersonalizeIntelligentRankingConfiguration(final int order, final PersonalizeIntelligentRankingProperties properties) {
        this.order = order;
        this.properties = properties;
    }

    public PersonalizeIntelligentRankingConfiguration(StreamInput input) throws IOException {
        this.order = input.readInt();
        this.properties = new PersonalizeIntelligentRankingProperties(input);
    }

    public PersonalizeIntelligentRankingConfiguration(Settings settings) {
        this.order = settings.getAsInt(ORDER, 0);
        this.properties = new PersonalizeIntelligentRankingProperties(
                settings.get("properties.item_id_field"),
                settings.get("properties.campaign_arn"));
    }

    @Override
    public String getTransformerName() {
        return PERSONALIZE_INTELLIGENT_RANKING_TRANSFORMER_NAME;
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeInt(this.order);
        this.properties.writeTo(out);
    }

    public static PersonalizeIntelligentRankingConfiguration parse(XContentParser parser) throws IOException{
        try {
            PersonalizeIntelligentRankingConfiguration configuration = PARSER.parse(parser, null);
            if (configuration != null && configuration.getOrder() <= 0) {
                throw new ParsingException(parser.getTokenLocation(),
                        "Failed to parse value [" + configuration.getOrder() + "] for Transformer order, must be >= 1");
            }
            return configuration;
        } catch (IllegalArgumentException iae) {
            throw new ParsingException(parser.getTokenLocation(), iae.getMessage(), iae);
        }
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        builder.field(TRANSFORMER_ORDER.getPreferredName(), this.order);
        builder.field(TRANSFORMER_PROPERTIES.getPreferredName(), this.properties);
        return builder.endObject();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalizeIntelligentRankingConfiguration config = (PersonalizeIntelligentRankingConfiguration) o;
        if (order != config.order) return false;
        return properties.equals(config.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, properties);
    }

    public PersonalizeIntelligentRankingProperties getProperties() {
        return this.properties;
    }

    public void setProperties(PersonalizeIntelligentRankingProperties properties) {
        this.properties = properties;
    }

    public static class PersonalizeIntelligentRankingProperties implements Writeable, ToXContentObject {
        protected static final ParseField ITEM_ID_FIELD = new ParseField(Constants.ITEM_ID_FIELD);
        protected static final ParseField CAMPAIGN_FIELD = new ParseField(Constants.CAMPAIGN_FIELD);

        private static final ObjectParser<PersonalizeIntelligentRankingConfiguration.PersonalizeIntelligentRankingProperties, Void> PARSER;

        static {
            PARSER = new ObjectParser<>("personalize_intelligent_ranking_configuration",
                    PersonalizeIntelligentRankingConfiguration.PersonalizeIntelligentRankingProperties::new);
            PARSER.declareString(PersonalizeIntelligentRankingProperties::setItemIdField, ITEM_ID_FIELD);
            PARSER.declareString(PersonalizeIntelligentRankingProperties::setCampaign, CAMPAIGN_FIELD);
        }

        private String itemIdField;
        private String campaign;

        public PersonalizeIntelligentRankingProperties () {
            // TODO: Update for all settings
            this.itemIdField = "";
            this.campaign = "";
        }

        public PersonalizeIntelligentRankingProperties (String itemIdField, String campaign) {
            // TODO: Update for all settings
            this.itemIdField = itemIdField;
            this.campaign = campaign;
        }

        public PersonalizeIntelligentRankingProperties (StreamInput input) throws IOException {
            // TODO: Update for all settings
            this.itemIdField = input.readString();
            this.campaign = input.readString();
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            // TODO: Update for all settings
            out.writeString(this.itemIdField);
            out.writeString(this.campaign);
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            // TODO: Update for all settings
            builder.field(ITEM_ID_FIELD.getPreferredName(), this.itemIdField);
            builder.field(CAMPAIGN_FIELD.getPreferredName(), this.campaign);
            return builder.endObject();
        }

        public static PersonalizeIntelligentRankingProperties parse(XContentParser parser, Void context) throws IOException {
            try {
                PersonalizeIntelligentRankingProperties properties = PARSER.parse(parser, null);
                if (properties != null) {
                    // TODO: Add properties validation
                }
                return properties;
            } catch (IllegalArgumentException iae) {
                throw new ParsingException(parser.getTokenLocation(), iae.getMessage(), iae);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PersonalizeIntelligentRankingProperties properties = (PersonalizeIntelligentRankingProperties) o;

            // TODO: Update for all settings
            return itemIdField.equals(properties.itemIdField) && campaign.equals(properties.campaign);
        }

        @Override
        public int hashCode() {
            // TODO: Update for all settings
            return Objects.hash(itemIdField, campaign);
        }

        public String getItemIdField() {
            return itemIdField;
        }

        public void setItemIdField(String itemIdField) {
            this.itemIdField = itemIdField;
        }

        public String getCampaign() {
            return campaign;
        }

        public void setCampaign(String campaign) {
            this.campaign = campaign;
        }
    }
}
