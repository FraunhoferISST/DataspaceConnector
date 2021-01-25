package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RequestFormatException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UUIDFormatException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.v1.ResourceContract;
import de.fraunhofer.isst.dataspaceconnector.services.messages.NegotiationService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.ContractMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.LogMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ContractAgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * This @{@link ContractMessageHandler} handles all incoming messages that have a
 * {@link de.fraunhofer.iais.eis.ContractRequestMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link de.fraunhofer.iais.eis.ContractRequestMessageImpl} JsonTypeName annotation.
 */
@Component
@SupportedMessageType(ContractRequestMessageImpl.class)
public class ContractMessageHandler implements MessageHandler<ContractRequestMessageImpl> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ContractMessageHandler.class);

    private final ConfigurationContainer configurationContainer;
    private final NegotiationService negotiationService;
    private final PolicyHandler policyHandler;
    private final ContractMessageService messageService;
    private final DapsTokenProvider tokenProvider;
    private final ContractAgreementService contractAgreementService;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final LogMessageService logMessageService;
    private RequestMessage requestMessage;

    /**
     * Constructor for NotificationMessageHandler.
     *
     * @param configurationContainer The container with the configuration
     * @param negotiationService The service with the negotation
     * @param policyHandler The service for policy negotation
     * @param contractAgreementService The service for the contract agreements
     * @param messageService The service for sending messages
     * @param logMessageService The service for logging
     * @param tokenProvider The provider for token
     * @throws IllegalArgumentException if one of the parameters is null.
     */
    @Autowired
    public ContractMessageHandler(ConfigurationContainer configurationContainer,
        NegotiationService negotiationService, PolicyHandler policyHandler,
        ContractAgreementService contractAgreementService,
        ContractMessageService messageService,
        LogMessageService logMessageService, DapsTokenProvider tokenProvider)
        throws IllegalArgumentException {
        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (negotiationService == null)
            throw new IllegalArgumentException("The NegotiationService cannot be null.");

        if (policyHandler == null)
            throw new IllegalArgumentException("The PolicyHandler cannot be null.");

        if (contractAgreementService == null)
            throw new IllegalArgumentException("The ContractAgreementService cannot be null.");

        if (messageService == null)
            throw new IllegalArgumentException("The ContractRequestService cannot be null.");

        if (logMessageService == null)
            throw new IllegalArgumentException("The LogMessageService cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        this.configurationContainer = configurationContainer;
        this.negotiationService = negotiationService;
        this.policyHandler = policyHandler;
        this.contractAgreementService = contractAgreementService;
        this.messageService = messageService;
        this.logMessageService = logMessageService;
        this.tokenProvider = tokenProvider;
    }

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param requestMessage The received contract request message.
     * @param messagePayload The message's content.
     * @return The response message.
     * @throws RuntimeException                - if the response body failed to be build.
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
        if (!messageService.versionSupported(requestMessage.getModelVersion())) {
            LOGGER.warn("Information Model version of requesting connector is not supported.");
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
                LOGGER.error("Contract is missing.");
                return ErrorResponse
                    .withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                        "Missing contract request.",
                        connector.getId(), connector.getOutboundModelVersion());
            }
        } catch (IOException e) {
            return ErrorResponse
                .withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                    "Malformed payload.",
                    connector.getId(), connector.getOutboundModelVersion());
        }

        try {
            // Check the contract content.
            return checkContractRequest(payload);
        } catch (RuntimeException exception) {
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
            URI artifactId = messageService.getArtifactIdFromContract(contractRequest);
            // Load contract offer from metadata.
            ContractOffer contractOffer = getContractOfferByArtifact(artifactId);

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
            return ErrorResponse.withDefaultHeader(
                RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
                connector.getId(), connector.getOutboundModelVersion());
        } catch (RuntimeException exception) {
            return ErrorResponse.withDefaultHeader(
                RejectionReason.BAD_PARAMETERS,
                "Malformed contract request.",
                connector.getId(), connector.getOutboundModelVersion());
        }
    }

    /**
     * Gets the contract offer by artifact id.
     *
     * @param artifactId The artifact's id
     * @return The resource's contract offer.
     */
    private ContractOffer getContractOfferByArtifact(URI artifactId) throws ResourceNotFoundException {
        UUID uuid = UUIDUtils.uuidFromUri(artifactId);
        final var resource = messageService.findResourceFromArtifactId(uuid);
        if (resource == null)
            throw new ResourceNotFoundException("Artifact not known.");
        return resource.getContractOffer().get(0);
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

        messageService.setResponseParameters(
            requestMessage.getIssuerConnector(), requestMessage.getId(), null);
        // Turn the accepted contract request into a contract agreement.
        ContractAgreement contractAgreement = messageService.buildContractAgreement(contractRequest);
        // Build message header.
        Message message = messageService.buildResponseHeader();

        // Save contract agreement to database.
        UUID uuid = UUIDUtils.uuidFromUri(contractAgreement.getId());
        contractAgreementService.addContract(new ResourceContract(uuid, contractAgreement.toRdf()));
        // Send ContractAgreement to the ClearingHouse. TODO: Activate Clearing House communication as soon as it accepts IM 4.
//        try {
//            Response response = logMessageService.sendMessage(contractAgreement.toRdf());
//            if (response == null)
//                throw new MessageException("Response body is empty.");
//        } catch (MessageException exception) {
//            // Log if the message could not be sent to the clearing house.
//            LOGGER.warn("Could not connect to clearing house. " + exception.getMessage());
//        }

        // Send response to the data consumer.
        return BodyResponse.create(message, contractAgreement.toRdf());
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
