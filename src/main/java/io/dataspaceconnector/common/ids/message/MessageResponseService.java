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
package io.dataspaceconnector.common.ids.message;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.ids.messaging.response.ErrorResponse;
import de.fraunhofer.ids.messaging.response.MessageResponse;
import io.dataspaceconnector.common.exception.ContractException;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.InvalidInputException;
import io.dataspaceconnector.common.exception.MessageEmptyException;
import io.dataspaceconnector.common.exception.PolicyRestrictionException;
import io.dataspaceconnector.common.exception.VersionNotSupportedException;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.agreement.Agreement;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * This class handles message responses.
 * NOTE: Used in the camel routes. Do not delete "unused" methods!
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class MessageResponseService {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorSvc;

    /**
     * Handles thrown
     * {@link io.dataspaceconnector.service.message.handler.exception.ConnectorOfflineException}.
     *
     * @return A message response.
     */
    public MessageResponse handleConnectorOfflineException() {
        if (log.isDebugEnabled()) {
            log.debug("Connector is offline. Will not process ids requests.");
        }
        return ErrorResponse.withDefaultHeader(
                RejectionReason.TEMPORARILY_NOT_AVAILABLE, "This connector is offline "
                        + "and will not process any ids requests.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handles thrown {@link MessageEmptyException}.
     *
     * @param exception Exception that was thrown when checking if the message is null.
     * @return A message response.
     * @throws IllegalArgumentException if exception is null.
     */
    public MessageResponse handleMessageEmptyException(final MessageEmptyException exception) {
        Utils.requireNonNull(exception, ErrorMessage.EXCEPTION_NULL);

        if (log.isDebugEnabled()) {
            log.debug("Cannot respond when there is no request. [exception=({})]",
                    exception.getMessage(), exception);
        }

        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                exception.getMessage(),
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handles thrown {@link VersionNotSupportedException}.
     *
     * @param exception Exception that was thrown when checking the Information Model version.
     * @param version   Information Model version of incoming message.
     * @return A message response.
     * @throws IllegalArgumentException if exception is null.
     */
    public MessageResponse handleInfoModelNotSupportedException(
            final VersionNotSupportedException exception, final String version) {
        Utils.requireNonNull(exception, ErrorMessage.EXCEPTION_NULL);

        if (log.isDebugEnabled()) {
            log.debug("Information Model version of requesting connector is not supported. "
                    + "[version=({}), exception=({})]", version, exception.getMessage(), exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.VERSION_NOT_SUPPORTED,
                exception.getMessage(),
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handles thrown exception when building the response message.
     *
     * @param exception       Exception that was thrown when building the response message.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The id of the incoming message.
     * @return A message response.
     * @throws IllegalArgumentException if exception is null.
     */
    public MessageResponse handleResponseMessageBuilderException(final Exception exception,
                                                                 final URI issuerConnector,
                                                                 final URI messageId) {
        Utils.requireNonNull(exception, ErrorMessage.EXCEPTION_NULL);

        if (log.isWarnEnabled()) {
            log.warn("Failed to convert ids object to string. [exception=({}), "
                            + "issuer=({}), messageId=({})]", exception.getMessage(),
                    issuerConnector, messageId, exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handles thrown {@link PolicyRestrictionException}.
     *
     * @param exception         Exception that was thrown when checking for data access.
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The transfer contract id.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handlePolicyRestrictionException(
            final PolicyRestrictionException exception, final URI requestedArtifact,
            final URI transferContract, final URI issuerConnector, final URI messageId) {
        if (log.isDebugEnabled()) {
            log.debug("Policy restriction detected. [exception=({}), artifact=({}), "
                            + "contract=({}), issuer=({}), messageId=({})]", exception.getMessage(),
                    requestedArtifact, transferContract, issuerConnector, messageId, exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_AUTHORIZED,
                "Policy restriction detected." + exception.getMessage(),
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handles thrown {@link IllegalArgumentException}.
     *
     * @param exception       Exception that was thrown when deserializing a message's payload.
     * @param payload         The message's payload.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleIllegalArgumentException(final IllegalArgumentException exception,
                                                          final String payload,
                                                          final URI issuerConnector,
                                                          final URI messageId) {
        if (log.isDebugEnabled()) {
            log.debug("Could not parse message payload. [exception=({}), payload=({}), "
                            + "issuer=({}), messageId=({})]", exception.getMessage(), payload,
                    issuerConnector, messageId, exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Could not parse message payload.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handles thrown exception while finding the requested element.
     *
     * @param exception        Exception that was thrown when trying to sendMessage the message.
     * @param requestedElement The requested element.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleResourceNotFoundException(final Exception exception,
                                                           final URI requestedElement,
                                                           final URI issuerConnector,
                                                           final URI messageId) {
        if (log.isDebugEnabled()) {
            log.debug("Element not found. [exception=({}), resourceId=({}), issuer=({}), "
                            + "messageId=({})]", exception.getMessage(), requestedElement,
                    issuerConnector, messageId, exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND, String.format(
                "The requested element %s could not be found.", requestedElement),
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handles thrown exception in processing a message's payload.
     *
     * @param exception       Exception that was thrown while reading a message's payload.
     * @param messageId       The id of the incoming message.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMessagePayloadException(final Exception exception,
                                                         final URI messageId,
                                                         final URI issuerConnector) {
        if (log.isDebugEnabled()) {
            log.debug("Failed to read payload. [exception=({}), messageId=({}), "
                            + "issuer=({})]", exception.getMessage(), messageId, issuerConnector,
                    exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                exception.getMessage(),
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle missing rules in contract request message.
     *
     * @param request         The contract request.
     * @param messageId       The id of the incoming message.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingRules(final ContractRequest request,
                                              final URI messageId,
                                              final URI issuerConnector) {
        if (log.isDebugEnabled()) {
            log.debug("No rules found. [request=({}), messageId=({}), issuer=({})]",
                    request, messageId, issuerConnector);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "Missing rules in contract request.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle missing target in rules of a contract request.
     *
     * @param request         The contract request.
     * @param messageId       The id of the incoming message.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingTargetInRules(final ContractRequest request,
                                                      final URI messageId,
                                                      final URI issuerConnector) {
        if (log.isDebugEnabled()) {
            log.debug("No targets found. [request=({}), messageId=({}), issuer=({})]",
                    request, messageId, issuerConnector);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "Missing targets in rules of contract request.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle missing contract offers matching the contract request targets.
     *
     * @param request         The contract request.
     * @param messageId       The id of the incoming message.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingContractOffers(final ContractRequest request,
                                                       final URI messageId,
                                                       final URI issuerConnector) {
        if (log.isDebugEnabled()) {
            log.debug("No contract offers found. [request=({}), messageId=({}), "
                    + "issuer=({})]", request, messageId, issuerConnector);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND,
                "Could not find any matching contract offers for your request.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle global message processing failed.
     *
     * @param exception       Exception that was thrown while processing a request message.
     * @param payload         The message's payload.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMessageProcessingFailed(final Exception exception,
                                                         final String payload,
                                                         final URI issuerConnector,
                                                         final URI messageId) {
        if (log.isWarnEnabled()) {
            log.warn("Could not process request message. [exception=({}), payload=({}), "
                            + "issuer=({}), messageId=({})]", exception.getMessage(), payload,
                    issuerConnector, messageId, exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Could not process request message. " + exception.getMessage(),
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle global message processing failed.
     *
     * @param exception         Exception that was thrown while processing a request message.
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The transfer contract id.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMessageProcessingFailedForArtifact(final Exception exception,
                                                                    final URI requestedArtifact,
                                                                    final URI transferContract,
                                                                    final URI issuerConnector,
                                                                    final URI messageId) {
        if (log.isWarnEnabled()) {
            log.warn("Could not process request message. [exception=({}), artifact=({}), "
                            + "contract=({}), issuer=({}), messageId=({})]", exception.getMessage(),
                    requestedArtifact, transferContract, issuerConnector, messageId, exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Could not process request message. " + exception.getMessage(),
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle contract exception.
     *
     * @param exception       The exception that was thrown when validating the contracts.
     * @param payload         The message's payload.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleContractException(final ContractException exception,
                                                   final String payload,
                                                   final URI issuerConnector,
                                                   final URI messageId) {
        if (log.isDebugEnabled()) {
            log.debug("Invalid contract agreement request. [exception=({}), payload=({}), "
                            + "issuer=({}), messageId=({})]", exception, payload, issuerConnector,
                    messageId, exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "This agreement does not match the one handled out before.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle exception when saving a contract agreement.
     *
     * @param exception       Exception that was thrown while storing a contract agreement.
     * @param agreement       The contract agreement.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleAgreementPersistenceException(final Exception exception,
                                                               final ContractAgreement agreement,
                                                               final URI issuerConnector,
                                                               final URI messageId) {
        if (log.isWarnEnabled()) {
            log.warn("Could not store contract agreement. [exception=({}), "
                            + "agreement=({}), issuer=({}), messageId=({})]",
                    exception.getMessage(), agreement, issuerConnector, messageId, exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Could not store contract agreement.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle missing transfer contract in request message.
     *
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The transfer contract id.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingTransferContract(final URI requestedArtifact,
                                                         final URI transferContract,
                                                         final URI issuerConnector,
                                                         final URI messageId) {
        if (log.isDebugEnabled()) {
            log.debug("Missing transfer contract. [artifact=({}), contract=({}), "
                            + "issuer=({}), messageId=({})]", requestedArtifact, transferContract,
                    issuerConnector, messageId);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "Missing transfer contract.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle {@link ContractException} because of invalid transfer contract for requested artifact.
     *
     * @param exception         Exception that was thrown while checking the transfer contract.
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The transfer contract id.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleInvalidTransferContract(final ContractException exception,
                                                         final URI requestedArtifact,
                                                         final URI transferContract,
                                                         final URI issuerConnector,
                                                         final URI messageId) {
        if (log.isDebugEnabled()) {
            log.debug("Invalid transfer contract. [exception=({}), artifact=({}), "
                            + "contract=({}), issuer=({}), messageId=({})]", exception.getMessage(),
                    requestedArtifact, transferContract, issuerConnector, messageId, exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "Invalid transfer contract for requested artifact.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle missing requested artifact in request message.
     *
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The transfer contract id.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingRequestedArtifact(final URI requestedArtifact,
                                                          final URI transferContract,
                                                          final URI issuerConnector,
                                                          final URI messageId) {
        if (log.isDebugEnabled()) {
            log.debug("Missing requested artifact. [artifact=({}), contract=({}), "
                            + "issuer=({}), messageId=({})]", requestedArtifact, transferContract,
                    issuerConnector, messageId);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "Missing requested artifact.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle {@link InvalidInputException} because of an invalid input in message payload.
     *
     * @param exception         Exception that was thrown while reading the input.
     * @param requestedArtifact The requested artifact.
     * @param transferContract  The transfer contract id.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleInvalidInput(final InvalidInputException exception,
                                              final URI requestedArtifact,
                                              final URI transferContract,
                                              final URI issuerConnector,
                                              final URI messageId) {
        if (log.isDebugEnabled()) {
            log.debug("Invalid input. [exception=({}), artifact=({}), contract=({}), "
                            + "issuer=({}), messageId=({})]", exception.getMessage(),
                    requestedArtifact, transferContract, issuerConnector, messageId, exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "Invalid input in payload.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle exception when retrieving the data.
     *
     * @param exception         Exception that was thrown while getting the data.
     * @param requestedArtifact The requested artifact.
     * @param issuerConnector   The issuer connector extracted from the incoming message.
     * @param messageId         The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleFailedToRetrieveData(final Exception exception,
                                                      final URI requestedArtifact,
                                                      final URI issuerConnector,
                                                      final URI messageId) {
        if (log.isWarnEnabled()) {
            log.warn("Failed to load data. [exception=({}), artifact=({}), issuer=({}), "
                            + "messageId=({})]", exception.getMessage(), requestedArtifact,
                    issuerConnector, messageId, exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Could not retrieve data.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle missing affected resource in request message.
     *
     * @param affectedResource The affected resource.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingAffectedResource(final URI affectedResource,
                                                         final URI issuerConnector,
                                                         final URI messageId) {
        if (log.isDebugEnabled()) {
            log.debug("Missing affected resource. [resource=({}), issuer=({}), "
                    + "messageId=({})]", affectedResource, issuerConnector, messageId);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "Missing affected resource.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle missing payload content in request message.
     *
     * @param affectedResource The affected resource.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMissingPayload(final URI affectedResource,
                                                final URI issuerConnector,
                                                final URI messageId) {
        if (log.isDebugEnabled()) {
            log.debug("Missing resource in payload. [resource=({}), issuer=({}), "
                    + "messageId=({})]", affectedResource, issuerConnector, messageId);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "Missing resource in payload.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle mismatch in affected resource and resource id of the incoming payload.
     *
     * @param resourceId       The id of the resource in the payload.
     * @param affectedResource The affected resource.
     * @param issuerConnector  The issuer connector extracted from the incoming message.
     * @param messageId        The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleInvalidAffectedResource(final URI resourceId,
                                                         final URI affectedResource,
                                                         final URI issuerConnector,
                                                         final URI messageId) {
        if (log.isDebugEnabled()) {
            log.debug("Affected resource does not match the resource id. [resource=({}), "
                            + "affectedResource=({}), issuer=({}), messageId=({})]", resourceId,
                    affectedResource, issuerConnector, messageId);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "Affected resource does not match the resource id.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Handle malformed rules in contract request.
     *
     * @param exception       Exception that was thrown while checking the contract rules.
     * @param payload         The message's payload.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleMalformedRules(final IllegalArgumentException exception,
                                                final String payload,
                                                final URI issuerConnector,
                                                final URI messageId) {
        if (log.isDebugEnabled()) {
            log.debug("Could not parse message payload. [exception=({}), payload=({}), "
                            + "issuer=({}), messageId=({})]", exception.getMessage(), payload,
                    issuerConnector, messageId, exception);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.MALFORMED_MESSAGE,
                "Invalid rules in message payload.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Respond with error if the transfer contract has not yet been confirmed.
     *
     * @param storedAgreement The contract agreement.
     * @param issuerConnector The issuer connector extracted from the incoming message.
     * @param messageId       The id of the incoming message.
     * @return A message response.
     */
    public MessageResponse handleUnconfirmedAgreement(final Agreement storedAgreement,
                                                      final URI issuerConnector,
                                                      final URI messageId) {
        if (log.isDebugEnabled()) {
            log.debug("This the transfer contract has not yet been confirmed. "
                            + "[agreement=({}), issuer=({}), messageId=({})]", storedAgreement,
                    issuerConnector, messageId);
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "This the transfer contract has not yet been confirmed.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }

    /**
     * Respond with error if no valid subscriptions could be created from supplied input.
     *
     * @param exception The exception.
     * @param input     String representation of the input for creating the subscription.
     * @return A message response.
     */
    public MessageResponse handleInvalidSubscription(final Exception exception,
                                                     final String input) {
        if (log.isDebugEnabled()) {
            log.debug("Unable to create valid subscription from input. "
                    + "[input=({}), exception=({})]", input, exception.getMessage());
        }
        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "Unable to create valid subscription from input.",
                connectorSvc.getConnectorId(), connectorSvc.getOutboundModelVersion());
    }
}
