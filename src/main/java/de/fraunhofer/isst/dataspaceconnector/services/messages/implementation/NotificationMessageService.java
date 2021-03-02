package de.fraunhofer.isst.dataspaceconnector.services.messages.implementation;

import de.fraunhofer.iais.eis.LogMessageBuilder;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageBuilder;
import de.fraunhofer.iais.eis.NotificationMessage;
import de.fraunhofer.iais.eis.NotificationMessageBuilder;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Service class for notification messages.
 */
@Service
@RequiredArgsConstructor
public class NotificationMessageService extends MessageService {

    /**
     * The configuration container.
     */
    private final @NonNull ConfigurationContainer configurationContainer;

    /**
     * The token provider.
     */
    private final @NonNull DapsTokenProvider tokenProvider;

    /**
     * Build an ids notification message.
     *
     * @param recipient The message's recipient.
     * @return The notification message.
     * @throws MessageBuilderException If the message could not be built.
     */
    public NotificationMessage buildNotificationMessage(final URI recipient) throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new NotificationMessageBuilder()
            ._issued_(getGregorianNow())
            ._modelVersion_(connector.getOutboundModelVersion())
            ._issuerConnector_(connector.getId())
            ._senderAgent_(connector.getId())
            ._securityToken_(tokenProvider.getDAT())
            ._recipientConnector_(Util.asList(recipient))
            .build();
    }

    public NotificationMessage buildLogMessage(final URI recipient) throws MessageException {
        // Get a local copy of the current connector.
        final var connector = configurationContainer.getConnector();

        return new LogMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._securityToken_(tokenProvider.getDAT())
                ._recipientConnector_(Util.asList(recipient))
                .build();
    }

    /**
     * Build an ids message processed notification message.
     *
     * @param recipient The recipient of the response.
     * @param correlationId The id of the correlation message.
     * @return The notification message.
     * @throws MessageBuilderException If the message could not be built.
     */
    public NotificationMessage buildMessageProcessedNotification(final URI recipient, final URI correlationId) throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new MessageProcessedNotificationMessageBuilder()
                ._securityToken_(tokenProvider.getDAT())
                ._correlationMessage_(correlationId)
                ._issued_(getGregorianNow())
                ._issuerConnector_(connector.getId())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._senderAgent_(connector.getId())
                ._recipientConnector_(Util.asList(recipient))
                .build();
    }

    public Map<String, String> sendNotificationMessage(final URI recipient, final String payload) throws MessageException {
        final var header = buildNotificationMessage(recipient);
        return sendMessage(header, payload, recipient);
    }

    public Map<String, String> sendLogMessage(final URI recipient, final String payload) throws MessageException {
        final var header = buildLogMessage(recipient);
        return sendMessage(header, payload, recipient);
    }

//    catch (MessageBuilderException exception) {
//        LOGGER.warn("Message could not be built. [exception=({})]", exception.getMessage());
//        throw new MessageBuilderException("Message could not be built.", exception);
//    }
}
