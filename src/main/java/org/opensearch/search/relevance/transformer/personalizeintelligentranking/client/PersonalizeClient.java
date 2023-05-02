/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */
package org.opensearch.search.relevance.transformer.personalizeintelligentranking.client;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.personalizeruntime.AmazonPersonalizeRuntime;
import com.amazonaws.services.personalizeruntime.AmazonPersonalizeRuntimeClientBuilder;
import com.amazonaws.services.personalizeruntime.model.GetPersonalizedRankingRequest;
import com.amazonaws.services.personalizeruntime.model.GetPersonalizedRankingResult;

import java.io.Closeable;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class PersonalizeClient implements Closeable {
    private final AmazonPersonalizeRuntime personalizeRuntime;
    private final AWSCredentialsProvider credentialsProvider;
    public PersonalizeClient() {
        // TODO: Use credentials from keystore or assume IAM role
        credentialsProvider = AccessController.doPrivileged(
                (PrivilegedAction<AWSCredentialsProvider>) () -> DefaultAWSCredentialsProviderChain.getInstance());
        personalizeRuntime = AccessController.doPrivileged(
                (PrivilegedAction<AmazonPersonalizeRuntime>) () -> AmazonPersonalizeRuntimeClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(Regions.US_WEST_2)
                .build());
    }

    public GetPersonalizedRankingResult getPersonalizedRanking(GetPersonalizedRankingRequest request) {
        GetPersonalizedRankingResult result;
        try {
            result = personalizeRuntime.getPersonalizedRanking(request);
        } catch (AmazonServiceException ex) {
            throw ex;
        }
        return result;
    }

    @Override
    public void close() throws IOException {
        if (personalizeRuntime != null) {
            personalizeRuntime.shutdown();
        }
    }
}
