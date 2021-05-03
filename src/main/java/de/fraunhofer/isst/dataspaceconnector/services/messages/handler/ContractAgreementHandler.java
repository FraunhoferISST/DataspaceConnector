package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.ContractAgreementMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageRequestException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.MessageProcessedNotificationMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.EntityResolver;
import de.fraunhofer.isst.dataspaceconnector.services.EntityUpdateService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.MessageProcessedNotificationService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyExecutionService;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * This @{@link ContractAgreementHandler} handles all incoming messages that have a
 * {@link ContractAgreementMessageImpl} as part one in the multipart message.
 * This header must have the correct '@type' reference as defined in the
 * {@link ContractAgreementMessageImpl} JsonTypeName annotation.
 */
@Component
@Log4j2
@RequiredArgsConstructor
@SupportedMessageType(ContractAgreementMessageImpl.class)
public class ContractAgreementHandler implements MessageHandler<ContractAgreementMessageImpl> {

    /**
     * Service for building and sending message responses.
     */
    private final @NonNull MessageResponseService responseService;

    /**
     * Service for handling notification messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

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
     * Policy execution point.
     */
    private final @NonNull PolicyExecutionService executionService;

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
            messageService.validateIncomingMessage(message);
        } catch (MessageEmptyException exception) {
            return responseService.handleMessageEmptyException(exception);
        } catch (VersionNotSupportedException exception) {
            return responseService.handleInfoModelNotSupportedException(exception,
                    message.getModelVersion());
        }

        // Read relevant parameters for message processing.
        final var issuer = MessageUtils.extractIssuerConnector(message);
        final var messageId = MessageUtils.extractMessageId(message);

        // Read message payload as string.
        String payloadAsString;
        try {
            payloadAsString = MessageUtils.getPayloadAsString(payload);
        } catch (MessageRequestException exception) {
            return responseService.handleMessagePayloadException(exception, messageId, issuer);
        }

        try {
            // Deserialize string to contract object.
            final var agreement = deserializationService.getContractAgreement(payloadAsString);
            final var agreementId = agreement.getId();

            // Get stored ids contract agreement.
            final var storedAgreement = entityResolver.getAgreementByUri(agreementId);
            final var storedIdsAgreement
                    = deserializationService.getContractAgreement(storedAgreement.getValue());

            // Compare both contract agreements.
            if (!PolicyUtils.compareContractAgreements(agreement, storedIdsAgreement)) {
                return responseService.handleContractException(
                        new ContractException("Not the same contract."), payloadAsString,
                        issuer, messageId);
            }

            // Update contract agreement to confirmed.
            if (!updateService.confirmAgreement(storedAgreement)) {
                return responseService.handleUnconfirmedAgreement(storedAgreement, issuer,
                        messageId);
            }

            // Send contract to clearing house.
            executionService.sendAgreement(agreement);

            return respondToMessage(issuer, messageId);
        } catch (IllegalArgumentException exception) {
            return responseService.handleIllegalArgumentException(exception, payloadAsString,
                    issuer, messageId);
        } catch (ResourceNotFoundException exception) {
            return responseService.handleMessageProcessingFailed(exception, payloadAsString,
                    issuer, messageId);
        } catch (ContractException exception) {
            return responseService.handleContractException(exception, payloadAsString,
                    issuer, messageId);
        }
    }

    /**
     * Build and send response message.
     *
     * @param issuer    The issuer connector.
     * @param messageId The message id.
     * @return A message response.
     */
    private MessageResponse respondToMessage(final URI issuer, final URI messageId) {
        try {
            // Build ids response message.
            final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
            final var header = messageService.buildMessage(desc);

            // Send ids response message.
            return BodyResponse.create(header, "Received contract agreement message.");
        } catch (MessageBuilderException | ConstraintViolationException e) {
            return responseService.handleResponseMessageBuilderException(e, issuer, messageId);
        }
    }
}
