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
package io.dataspaceconnector.common.exception;

/**
 * Contains a list of common error messages used by exception.
 */
public enum ErrorMessage {

    /**
     * The passed authentication may not be null.
     */
    AUTH_NULL("The passed authentication may not be null."),

    /**
     * The passed desc parameter may not be null.
     */
    DESC_NULL("The description parameter may not be null."),

    /**
     * The passed dat may not be null.
     */
    DAT_NULL("The dat may not be null."),

    /**
     * The passed message may not be null.
     */
    MESSAGE_NULL("The message may not be null."),

    /**
     * The passed contract may not be null.
     */
    CONTRACT_NULL("The contract may not be null."),
    /**
     * The passed entity id may not be null.
     */
    ENTITYID_NULL("The entity id may not be null."),

    /**
     * The passed entity may not be null.
     */
    ENTITY_NULL("The entity may not be null."),

    /**
     * The passed pageable parameter may not be null.
     */
    PAGEABLE_NULL("The pageable parameter may not be null."),

    /**
     * The passed set of entities may not be null.
     */
    ENTITYSET_NULL("The set of entities may not be null."),

    /**
     * The exception parameter may not be null.
     */
    EXCEPTION_NULL("The exception parameter may not be null."),

    /**
     * Missing IDS version.
     */
    VERSION_NULL("The version must be set."),

    /**
     * The passed list may not be null.
     */
    LIST_NULL("The passed list may not be null."),

    /**
     * The passed uri may not be null.
     */
    URI_NULL("The passed uri may not be null."),

    /**
     * The passed url may not be null.
     */
    URL_NULL("The passed url may not be null."),

    /**
     * The passed http arguments may not be null.
     */
    HTTP_ARGS_NULL("The passed http arguments may not be null."),

    /**
     * One of the contracts is empty.
     */
    EMPTY_CONTRACT("Empty contracts cannot be compared."),

    /**
     * If the content of two contracts is not equal.
     */
    CONTRACT_MISMATCH("The contract's content do not match."),

    /**
     * No behavior has been defined for the object type.
     */
    UNKNOWN_TYPE("No behavior has been defined for this type."),

    /**
     * Failed to read response message.
     */
    INVALID_MESSAGE("Received invalid ids message."),

    /**
     * Multipart message building failed.
     */
    MESSAGE_BUILDING_FAILED("Failed to build ids message."),

    /**
     * DAT in response ids header is invalid.
     */
    INVALID_DAT("Invalid DAT in incoming response message."),

    /**
     * IDS message could not be sent or processed.
     */
    MESSAGE_HANDLING_FAILED("Message handling or processing failed."),

    /**
     * IDS message could not be sent.
     */
    MESSAGE_SENDING_FAILED("Message sending failed."),

    /**
     * Multipart message could not be sent to recipient.
     */
    GATEWAY_TIMEOUT("Gateway timeout when connecting to recipient."),

    /**
     * Payload of multipart message is malformed.
     */
    MALFORMED_PAYLOAD("Malformed message payload."),

    /**
     * Payload of multipart message is missing.
     */
    MISSING_PAYLOAD("Missing message payload."),

    /**
     * Entity is null.
     */
    EMTPY_ENTITY("Element could not be found."),

    /**
     * Failed to read rdf string from ids object.
     */
    RDF_FAILED("Could not retrieve rdf string."),

    /**
     * Number of data accesses has been reached.
     */
    DATA_ACCESS_NUMBER_REACHED("Valid access number reached."),

    /**
     * Data has been accessed in an invalid time interval.
     */
    DATA_ACCESS_INVALID_INTERVAL("Data access in invalid time interval."),

    /**
     * Data has been accessed by an invalid consumer.
     */
    DATA_ACCESS_INVALID_CONSUMER("Data access by invalid consumer connector."),

    /**
     * Data has been accessed with invalid security profile.
     */
    DATA_ACCESS_INVALID_SECURITY_PROFILE("Data access with invalid security profile."),

    /**
     * Application's base URL was retrieved without request context present.
     */
    NO_REQUEST_CONTEXT("No request context present for extracting base URL."),

    /**
     * Due to a prohibit access pattern, accessing the data is not allowed.
     */
    NOT_ALLOWED("Access is not allowed."),

    /**
     * A policy restriction has been detected.
     */
    POLICY_RESTRICTION("Policy restriction detected."),

    /**
     * The input could not be processed.
     */
    INVALID_INPUT("Invalid input, processing failed."),

    /**
     * The security profile claim is not present.
     */
    MISSING_SECURITY_PROFILE_CLAIM("The DAT of the issuer connector is missing attributes. "
            + "Cannot enforce security restricted policy. Access denied."),

    /**
     * If a resource update message could not be sent.
     */
    UPDATE_MESSAGE_FAILED("Failed to send update message."),

    /**
     * If an http response is null.
     */
    RESPONSE_NULL("Received an empty response for http request."),

    /**
     * If an PKIX error occurs.
     */
    CERTIFICATE_NOT_TRUSTED("The recipient's certificate authority is not trusted.");

    /**
     * Holds the enums string.
     */
    private final String value;

    /**
     * Constructor.
     *
     * @param message The msg of the error message.
     */
    ErrorMessage(final String message) {
        this.value = message;
    }

    @Override
    public String toString() {
        return value;
    }
}
