/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.common.routing;

/**
 * Contains the names of parameters used in routes and processors.
 */
public final class ParameterUtils {

    /**
     * Private constructor for utility class.
     */
    private ParameterUtils() { }

    /**
     * Name of the header containing the IDSCPv2 message.
     */
    public static final String IDSCP_HEADER = "idscp2-header";

    /**
     * Name of the header part of a multipart message.
     */
    public static final String HEADER_PART_NAME = "header";

    /**
     * Name of the payload part of a multipart message.
     */
    public static final String PAYLOAD_PART_NAME = "payload";

    /**
     * Name of the parameter containing the resource ID.
     */
    public static final String RESOURCE_ID_PARAM = "resourceId";

    /**
     * Name of the parameter containing the contract agreement.
     */
    public static final String CONTRACT_AGREEMENT_PARAM = "contractAgreement";

    /**
     * Name of the parameter containing the recipient URI.
     */
    public static final String RECIPIENT_PARAM = "recipient";

    /**
     * Name of the parameter containing the element ID.
     */
    public static final String ELEMENT_ID_PARAM = "elementId";

    /**
     * Name of the parameter containing the resource URI list.
     */
    public static final String RESOURCES_PARAM = "resources";

    /**
     * Name of the parameter containing the transfer contract URI.
     */
    public static final String TRANSFER_CONTRACT_PARAM = "transferContract";

    /**
     * Name of the parameter containing the artifact ID.
     */
    public static final String ARTIFACT_ID_PARAM = "artifactId";

    /**
     * Name of the parameter containing the artifact URI list.
     */
    public static final String ARTIFACTS_PARAM = "artifacts";

    /**
     * Name of the parameter containing the list of rules.
     */
    public static final String RULE_LIST_PARAM = "ruleList";

    /**
     * Name of the parameter containing the contract request.
     */
    public static final String CONTRACT_REQUEST_PARAM = "contractRequest";

    /**
     * Name of the parameter containing the query string.
     */
    public static final String QUERY_PARAM = "query";

    /**
     * Name of the parameter containing the query search term.
     */
    public static final String QUERY_TERM_PARAM = "term";

    /**
     * Name of the parameter containing the query limit.
     */
    public static final String QUERY_LIMIT_PARAM = "limit";

    /**
     * Name of the parameter containing the query offset.
     */
    public static final String QUERY_OFFSET_PARAM = "offset";

    /**
     * Name of the parameter containing the agreement UUID.
     */
    public static final String AGREEMENT_ID_PARAM = "agreementId";

    /**
     * Name of the parameter containing the current artifact ID (in loops).
     */
    public static final String CURRENT_ARTIFACT_PARAM = "currentArtifact";

    /**
     * Name of the parameter containing the boolean for download.
     */
    public static final String DOWNLOAD_PARAM = "download";

    /**
     * Name of the parameter containing the query input.
     */
    public static final String QUERY_INPUT_PARAM = "queryInput";

    /**
     * Name of the parameter for the subscription description.
     */
    public static final String SUBSCRIPTION_DESC_PARAM = "subscriptionDesc";

    /**
     * Inserts the specified recipient into the URI used to make IDSCP2 client calls.
     *
     * @param recipient the recipient
     * @return the IDSCP2 client URI with the recipient.
     */
    public static String getIdscp2ClientUri(final String recipient) {
        return "idscp2client://" + recipient + "?awaitResponse=true"
                + "&sslContextParameters=#serverSslContext"
                + "&useIdsMessages=true";
    }

}
