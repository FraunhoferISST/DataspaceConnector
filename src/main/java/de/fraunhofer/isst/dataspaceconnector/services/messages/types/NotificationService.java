package de.fraunhofer.isst.dataspaceconnector.services.messages.types;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.NotificationMessageBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.PolicyExecutionException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.NotificationMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URI;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Message service for ids notification messages.
 */
@Log4j2
@Service
public final class NotificationService extends AbstractMessageService<NotificationMessageDesc> {

    /**
     * @throws IllegalArgumentException If desc is null.
     */
    @Override
    public Message buildMessage(final NotificationMessageDesc desc)
            throws ConstraintViolationException {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        final var recipient = desc.getRecipient();

        return new NotificationMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                .build();
    }

    @Override
    protected Class<?> getResponseMessageType() {
        return null;
    }

    /**
     * Send a notification message. Allow the access only if that operation was successful.
     *
     * @param recipient The message's recipient.
     * @param logItem   The item that should be logged.
     * @throws PolicyExecutionException if sending the notification message was unsuccessful.
     */
    public void sendMessage(final URI recipient, final Object logItem) throws PolicyExecutionException {
        try {
            final var response = send(new NotificationMessageDesc(recipient), logItem);
            if (response == null) {
                if (log.isDebugEnabled()) {
                    log.debug("No response received.");
                }
                throw new PolicyExecutionException("Notification has no valid response.");
            }
        } catch (MessageException e) {
            if (log.isDebugEnabled()) {
                log.debug("Notification not sent. [exception=({})]", e.getMessage());
            }
            throw new PolicyExecutionException("Notification was not successful.");
        }
    }
}
