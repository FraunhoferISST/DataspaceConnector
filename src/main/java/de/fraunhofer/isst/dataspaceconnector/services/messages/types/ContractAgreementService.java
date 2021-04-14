package de.fraunhofer.isst.dataspaceconnector.services.messages.types;

import de.fraunhofer.iais.eis.ContractAgreementMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractAgreementMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.messages.AbstractMessageService;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.MessageUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Service;

import java.util.Map;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Message service for ids contract agreement messages.
 */
@Service
public final class ContractAgreementService extends AbstractMessageService<ContractAgreementMessageDesc> {

    /**
     * @throws IllegalArgumentException If desc is null.
     */
    @Override
    public Message buildMessage(final ContractAgreementMessageDesc desc)
            throws ConstraintViolationException {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        final var recipient = desc.getRecipient();
        final var correlationMessage = desc.getCorrelationMessage();

        return new ContractAgreementMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._correlationMessage_(correlationMessage)
                .build();
    }

    @Override
    protected Class<?> getResponseMessageType() {
        return MessageProcessedNotificationMessageImpl.class;
    }

    /**
     * Checks if the response message is of the right type. Overrides the message in
     * {@link AbstractMessageService} as it checks for subtypes of ResponseMessage. The
     * ContractAgreementMessage is the only message where a subtype of NotificationMessage is
     * expected as the response.
     *
     * @param message The received message response.
     * @return True if the response type is as expected.
     * @throws MessageResponseException If the response could not be read.
     */
    @Override
    public boolean isValidResponseType(final Map<String, String> message) throws MessageResponseException {
        try {
            // MessageResponseException is handled at a higher level.
            final var header = MessageUtils.extractHeaderFromMultipartMessage(message);
            final var idsMessage = getDeserializer().getNotificationMessage(header);

            final var messageType = idsMessage.getClass();
            final var allowedType = getResponseMessageType();
            return messageType.equals(allowedType);
        } catch (MessageResponseException | IllegalArgumentException e) {
            getLogger().debug("Failed to read response header. [exception=({})]", e.getMessage());
            throw new MessageResponseException(ErrorMessages.MALFORMED_HEADER.toString(), e);
        } catch (Exception e) {
            // NOTE: Should not be reached.
            getLogger().warn("Something else went wrong. [exception=({})]", e.getMessage());
            throw new MessageResponseException(ErrorMessages.INVALID_RESPONSE.toString(), e);
        }
    }
}
