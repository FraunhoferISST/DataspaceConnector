package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import java.net.URI;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.config.ConnectorConfiguration;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageRequestException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RdfBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.LogMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.MessageProcessedNotificationMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.EntityResolver;
import de.fraunhofer.isst.dataspaceconnector.services.EntityUpdateService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.LogMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.MessageProcessedNotificationService;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    /**
     * Class level logger.
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ContractAgreementHandler.class);

    /**
     * Service for message processing.
     */
    private final @NonNull MessageService messageService;

    /**
     * Service for the message exception handling.
     */
    private final @NonNull MessageResponseService exceptionService;

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for updating database entities from ids object.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * Service for handling log messages.
     */
    private final @NonNull LogMessageService logService;

    /**
     * Service for connector configurations.
     */
    private final @NonNull ConnectorConfiguration connectorConfig;

    /**
     * Service for handling notification messages.
     */
    private final @NonNull MessageProcessedNotificationService notificationService;

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @param message The received contract agreement message.
     * @param payload The message's content.
     * @return The response message.
     * @throws RuntimeException if the response body failed to be build.
     */
    @Override
    public MessageResponse handleMessage(final ContractAgreementMessageImpl message,
                                         final MessagePayload payload) throws RuntimeException {
        // Validate incoming message.
        try {
            messageService.validateIncomingRequestMessage(message);
        } catch (MessageEmptyException exception) {
            return exceptionService.handleMessageEmptyException(exception);
        } catch (VersionNotSupportedException exception) {
            return exceptionService.handleInfoModelNotSupportedException(exception,
                    message.getModelVersion());
        }

        // Read relevant parameters for message processing.
        final var issuerConnector = MessageUtils.extractIssuerConnector(message);
        final var messageId = MessageUtils.extractMessageId(message);

        // Read message payload as string.
        String payloadAsString;
        try {
            payloadAsString = MessageUtils.getPayloadAsString(payload);
        } catch (MessageRequestException exception) {
            return exceptionService.handleMessagePayloadException(exception, messageId,
                    issuerConnector);
        }

        try {
            // Deserialize string to contract object.
            final var agreement = deserializationService.getContractAgreement(payloadAsString);
            final var agreementId = agreement.getId();

            // Get stored ids contract agreement.
            final var storedAgreement = entityResolver.getAgreementByUri(agreementId);
            final var storedIdsAgreement = deserializationService
                    .getContractAgreement(storedAgreement.getValue());

            // Compare both contract agreements.
            PolicyUtils.compareContractAgreements(agreement, storedIdsAgreement);

            // Update contract agreement to confirmed.
            updateService.updateAgreementToConfirmed(storedAgreement);

            // Send contract to clearing house.
            sendAgreementToClearingHouse(agreement);

            return respondToMessage(issuerConnector, messageId);
        } catch (IllegalArgumentException exception) {
            return exceptionService.handleIllegalArgumentException(exception, payloadAsString,
                    issuerConnector, messageId);
        } catch (ResourceNotFoundException exception) {
            return exceptionService.handleMessageProcessingFailed(exception, payloadAsString,
                    issuerConnector, messageId);
        } catch (ContractException exception) {
            return exceptionService.handleContractException(exception, payloadAsString,
                    issuerConnector, messageId);
        }
    }

    /**
     * Send contract agreement to clearing house.
     *
     * @param agreement The ids contract agreement.
     */
    private void sendAgreementToClearingHouse(final ContractAgreement agreement) {
        try {
            final var recipient = connectorConfig.getClearingHouse();
            final var rdf = IdsUtils.toRdf(agreement);

            // Build ids response message.
            final var desc = new LogMessageDesc();
            desc.setRecipient(recipient);
            logService.sendMessage(desc, rdf);
        } catch (MessageBuilderException | RdfBuilderException exception) {
            LOGGER.warn("Failed to send contract agreement to clearing house. [exception=({})]",
                    exception.getMessage());
        }
    }

    /**
     * Build and send response message.
     *
     * @param issuerConnector The issuer connector.
     * @param messageId       The message id.
     * @return A message response.
     */
    private MessageResponse respondToMessage(final URI issuerConnector,
                                             final URI messageId) {
        try {
            // Build ids response message.
            final var desc = new MessageProcessedNotificationMessageDesc(issuerConnector, messageId);
            final var header = notificationService.buildMessage(desc);

            // Send ids response message.
            return BodyResponse.create(header, "Received contract agreement message.");
        } catch (MessageBuilderException | ConstraintViolationException exception) {
            return exceptionService.handleResponseMessageBuilderException(exception,
                    issuerConnector, messageId);
        }
    }
}
