package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessageImpl;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceContract;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.LogMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.NotificationMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ContractAgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
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
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * This @{@link ContractAgreementHandler} handles all incoming messages that have a
 * {@link ContractAgreementMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link ContractAgreementMessageImpl} JsonTypeName annotation.
 */
@Component
@SupportedMessageType(ContractAgreementMessageImpl.class)
public class ContractAgreementHandler implements MessageHandler<ContractAgreementMessageImpl> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ContractAgreementHandler.class);

    private final ConfigurationContainer configurationContainer;
    private final PolicyHandler policyHandler;
    private final NotificationMessageService messageService;
    private final ContractAgreementService contractAgreementService;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final LogMessageService logMessageService;

    /**
     * Constructor for NotificationMessageHandler.
     *
     * @param configurationContainer The container with the configuration
     * @param policyHandler The service for policy negotiation
     * @param contractAgreementService The service for the contract agreements
     * @param messageService The service for sending messages
     * @param logMessageService The service for logging
     * @throws IllegalArgumentException if one of the parameters is null.
     */
    @Autowired
    public ContractAgreementHandler(ConfigurationContainer configurationContainer,
                                    PolicyHandler policyHandler,
                                    ContractAgreementService contractAgreementService,
                                    NotificationMessageService messageService,
                                    LogMessageService logMessageService)
            throws IllegalArgumentException {
        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (policyHandler == null)
            throw new IllegalArgumentException("The PolicyHandler cannot be null.");

        if (contractAgreementService == null)
            throw new IllegalArgumentException("The ContractAgreementService cannot be null.");

        if (messageService == null)
            throw new IllegalArgumentException("The NotificationMessageService cannot be null.");

        if (logMessageService == null)
            throw new IllegalArgumentException("The LogMessageService cannot be null.");

        this.configurationContainer = configurationContainer;
        this.policyHandler = policyHandler;
        this.contractAgreementService = contractAgreementService;
        this.messageService = messageService;
        this.logMessageService = logMessageService;
    }

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The received contract agreement message.
     * @param messagePayload The message's content.
     * @return The response message.
     * @throws RuntimeException if the response body failed to be build.
     */
    @Override
    public MessageResponse handleMessage(ContractAgreementMessageImpl message,
                                         MessagePayload messagePayload) throws RuntimeException {
        if (message == null) {
            LOGGER.warn("Cannot respond when there is no request.");
            throw new IllegalArgumentException("The requestMessage cannot be null.");
        }

        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        // Check if version is supported.
        if (!messageService.versionSupported(message.getModelVersion())) {
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
                LOGGER.debug("Contract agreement is missing [id=({}), payload=({})]",
                        message.getId(), payload);
                return ErrorResponse
                        .withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                                "Missing contract agreement.",
                                connector.getId(), connector.getOutboundModelVersion());
            }
        } catch (IOException e) {
            LOGGER.debug("Cannot read payload. [id=({}), payload=({})]",
                    message.getId(), messagePayload);
            return ErrorResponse
                    .withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                            "Malformed payload.",
                            connector.getId(), connector.getOutboundModelVersion());
        }

        try {
            saveContract(payload);

            // Build response header.
            messageService.setResponseParameters(message.getIssuerConnector(), message.getId());
            return BodyResponse.create(messageService.buildResponseHeader(),
                    "Message processed. The contract is legal.");
        } catch (ContractException exception) {
            LOGGER.warn("Failed to store the contract agreement. [exception=({})]",
                    exception.getMessage());
            return ErrorResponse.withDefaultHeader(
                    RejectionReason.INTERNAL_RECIPIENT_ERROR,
                    "Failed to store the contract agreement. Thus, it is not legal. " +
                            "Please try again.",
                    connector.getId(), connector.getOutboundModelVersion());
        }
    }


    /**
     * Saves the contract agreement to the internal database and send it to the ids clearing house.
     *
     * @param agreement The contract agreement from the data consumer.
     */
    private void saveContract(String agreement) throws ContractException, MessageException {

        ContractAgreement contractAgreement =
                (ContractAgreement) policyHandler.validateContract(agreement);

        try {
            // Save contract agreement to database.
            UUID uuid = UUIDUtils.uuidFromUri(contractAgreement.getId());
            contractAgreementService.addContract(new ResourceContract(uuid, contractAgreement.toRdf()));
        } catch (Exception exception) {
            LOGGER.warn("Failed to store the contract agreement. [exception=({})]",
                    exception.getMessage());
            throw new ContractException("Could not save contract agreement.");
        }

        // Send ContractAgreement to the ClearingHouse.
        // TODO: Activate Clearing House communication as soon as it accepts IM 4.
        try {
            logMessageService.sendRequestMessage(contractAgreement.toRdf());
        } catch (MessageBuilderException exception) {
            // Failed to build the log message.
            LOGGER.warn("Failed to build log message. [exception=({})]", exception.getMessage());
        } catch (MessageResponseException exception) {
            // Failed to read the response message.
            LOGGER.debug("Received invalid ids response. [exception=({})]", exception.getMessage());
        } catch (MessageNotSentException exception) {
            // Failed to send the log message.
            LOGGER.warn("Failed to send a log message. [exception=({})]", exception.getMessage());
        }
    }
}
