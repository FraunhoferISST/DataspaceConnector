package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessageImpl;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResponseMessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceContract;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.NotificationMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v1.ContractAgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyManagementService;
import de.fraunhofer.isst.dataspaceconnector.utils.UUIDUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
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
@RequiredArgsConstructor
public class ContractAgreementHandler implements MessageHandler<ContractAgreementMessageImpl> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ContractAgreementHandler.class);

    /**
     * The clearing house access url.
     */
    @Value("${clearing.house.url}")
    private String clearingHouse;

    private final @NonNull ConfigurationContainer configurationContainer;
    private final @NonNull PolicyManagementService pmp;
    private final @NonNull NotificationMessageService messageService;
    private final @NonNull ContractAgreementService contractAgreementService;

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
        messageService.checkForEmptyMessage(message);
        messageService.checkForVersionSupport(message.getModelVersion());

        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

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
            final var header = messageService.buildMessageProcessedNotification(message.getIssuerConnector(), message.getId());
            return BodyResponse.create(header, "Message processed. The contract is legal.");
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

        ContractAgreement contractAgreement = pmp.deserializeContractAgreement(agreement);

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
            messageService.sendLogMessage(URI.create(clearingHouse), contractAgreement.toRdf());
        } catch (ResponseMessageBuilderException exception) {
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
