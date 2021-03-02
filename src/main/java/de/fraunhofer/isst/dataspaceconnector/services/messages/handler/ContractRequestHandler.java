package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessage;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractRejectionMessageBuilder;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.RequestMessage;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RequestFormatException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UUIDFormatException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.services.EntityDependencyResolver;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.NotificationMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.ResponseMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.NegotiationService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.dataspaceconnector.utils.ContractUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.UUIDUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * This @{@link ContractRequestHandler} handles all incoming messages that have a
 * {@link de.fraunhofer.iais.eis.ContractRequestMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link de.fraunhofer.iais.eis.ContractRequestMessageImpl} JsonTypeName annotation.
 */
@Component
@SupportedMessageType(ContractRequestMessageImpl.class)
@RequiredArgsConstructor
public class ContractRequestHandler implements MessageHandler<ContractRequestMessageImpl> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ContractRequestHandler.class);

    private final @NonNull ConfigurationContainer configurationContainer;
    private final @NonNull NegotiationService negotiationService;
    private final @NonNull PolicyHandler policyHandler;
    private final @NonNull ResponseMessageService messageService;
    private final @NonNull DapsTokenProvider tokenProvider;
    private final @NonNull NotificationMessageService logMessageService;

    private final @NonNull EntityDependencyResolver entityDependencyResolver;

    private RequestMessage requestMessage;

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param requestMessage The received contract request message.
     * @param messagePayload The message's content.
     * @return The response message.
     * @throws RuntimeException if the response body failed to be build.
     */
    @Override
    public MessageResponse handleMessage(ContractRequestMessageImpl requestMessage,
                                         MessagePayload messagePayload) throws RuntimeException {
        if (requestMessage == null) {
            LOGGER.warn("Cannot respond when there is no request.");
            throw new IllegalArgumentException("The requestMessage cannot be null.");
        } else {
            this.requestMessage = requestMessage;
        }

        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        // Check if version is supported.
        if (!messageService.checkForVersionSupport(requestMessage.getModelVersion())) {
            LOGGER.debug("Information Model version of requesting connector is not supported.");
            return ErrorResponse.withDefaultHeader(
                    RejectionReason.VERSION_NOT_SUPPORTED,
                    "Information model version not supported.",
                    connector.getId(), connector.getOutboundModelVersion());
        }

        // Read message payload as string.
        String payload;
        try {
            payload = IOUtils
                    .toString(messagePayload.getUnderlyingInputStream(), StandardCharsets.UTF_8);
            // If request is empty, return rejection message.
            if (payload.equals("")) {
                LOGGER.debug("Contract is missing [id=({}), payload=({})]",
                        requestMessage.getId(), payload);
                return ErrorResponse
                        .withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                                "Missing contract request.",
                                connector.getId(), connector.getOutboundModelVersion());
            }
        } catch (IOException e) {
            LOGGER.debug("Cannot read payload. [id=({}), payload=({})]",
                    requestMessage.getId(), messagePayload);
            return ErrorResponse
                    .withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                            "Malformed payload.",
                            connector.getId(), connector.getOutboundModelVersion());
        }

        try {
            // Check the contract content.
            return checkContractRequest(payload);
        } catch (RuntimeException exception) {
            LOGGER.warn("Failed to check the contract request. [exception=({})]",
                    exception.getMessage());
            // Something went wrong (e.g invalid config), try to fix it at a higher level.
            throw new RuntimeException("Failed to construct a resource description.", exception);
        }
    }

    /**
     * Checks if the contract request content by the consumer complies with the contract offer by
     * the provider.
     *
     * @param payload The message payload containing a contract request.
     * @return A message response to the requesting connector.
     */
    public MessageResponse checkContractRequest(String payload) throws RuntimeException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        try {
            // Deserialize string to contract object.
            final var contractRequest = (ContractRequest) policyHandler.validateContract(payload);

            // Get artifact id from contract request.
            URI artifactId = entityDependencyResolver.getArtifactIdFromContract(contractRequest);
            // Load contract offer from metadata.
            ContractOffer contractOffer = entityDependencyResolver.getContractOfferByArtifact(UUIDUtils.uuidFromUri(artifactId));

            // Check if the contract request has the same content as the stored contract offer.
            if (negotiationService.compareContracts(contractRequest, contractOffer)) {
                return acceptContract(contractRequest);
            } else {
                // If differences have been detected.
                return rejectContract();
            }
        } catch (UUIDFormatException | RequestFormatException exception) {
            LOGGER.debug(
                    "Artifact has no valid uuid. [id=({}), artifactUri=({}), exception=({})]",
                    requestMessage.getId(), requestMessage.getTransferContract(),
                    exception.getMessage());
            return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                    "No valid resource id found.",
                    connector.getId(),
                    connector.getOutboundModelVersion());
        } catch (ResourceNotFoundException exception) {
            // The resource could be not be found.
            LOGGER.debug("The artifact could not be found. [id=({}), exception=({})]",
                    requestMessage.getId(), exception.getMessage());
            return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND,
                    "Artifact not found.", connector.getId(),
                    connector.getOutboundModelVersion());
        } catch (MessageBuilderException exception) {
            LOGGER.warn("Response could not be constructed. [id=({}), exception=({})]",
                    requestMessage.getId(), exception.getMessage());
            return ErrorResponse.withDefaultHeader(
                    RejectionReason.INTERNAL_RECIPIENT_ERROR,
                    "Response could not be constructed.",
                    connector.getId(), connector.getOutboundModelVersion());
        } catch (RuntimeException exception) {
            LOGGER.warn("Could not process contract request. [id=({}), exception=({})]",
                    requestMessage.getId(), exception.getMessage());
            return ErrorResponse.withDefaultHeader(
                    RejectionReason.BAD_PARAMETERS,
                    "Malformed contract request.",
                    connector.getId(), connector.getOutboundModelVersion());
        }
    }

    /**
     * Accept contract by building a {@link ContractAgreement} and sending it as payload with a
     * {@link ContractAgreementMessage}.
     *
     * @param contractRequest The contract request object from the data consumer.
     * @return The message response to the requesting connector.
     */
    private MessageResponse acceptContract(ContractRequest contractRequest)
            throws UUIDFormatException, MessageException {

        final var header = messageService.buildContractAgreementMessage(requestMessage.getIssuerConnector(), requestMessage.getId());
        // Turn the accepted contract request into a contract agreement.
        final var contractAgreement = ContractUtils.buildContractAgreement(contractRequest);

        // Send response to the data consumer.
        return BodyResponse.create(header, contractAgreement.toRdf());
    }

    /**
     * Builds a contract rejection message with a rejection reason.
     *
     * @return A contract rejection message.
     */
    private MessageResponse rejectContract() {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return ErrorResponse.create(new ContractRejectionMessageBuilder()
                ._securityToken_(tokenProvider.getDAT())
                ._correlationMessage_(requestMessage.getId())
                ._issued_(getGregorianNow())
                ._issuerConnector_(connector.getId())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._senderAgent_(connector.getId())
                ._recipientConnector_(Util.asList(requestMessage.getIssuerConnector()))
                ._rejectionReason_(RejectionReason.BAD_PARAMETERS)
                ._contractRejectionReason_(new TypedLiteral("Contract not accepted.", "en"))
                .build(), "Contract rejected.");
    }
}
