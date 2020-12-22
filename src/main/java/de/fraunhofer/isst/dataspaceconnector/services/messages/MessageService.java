package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageNotSentException;
import de.fraunhofer.isst.ids.framework.messages.InfomodelMessageBuilder;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import java.net.URI;
import okhttp3.MultipartBody;
import okhttp3.Response;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Abstract class for building and sending ids messages.
 */
@Service
public abstract class MessageService {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    private final IDSHttpService idsHttpService;

    @Autowired
    public MessageService(IDSHttpService idsHttpService) {
        if (idsHttpService == null)
            throw new IllegalArgumentException("The IDSHttpService cannot be null.");

        this.idsHttpService = idsHttpService;
    }

    public abstract Message buildHeader() throws MessageException;

    public abstract URI getRecipient();

    /**
     * Send ids message with header and payload using the IDS Framework.
     *
     * @param service The corresponding service to the message type.
     * @param payload The message payload.
     * @return The http response.
     * @throws MessageException - if a header could not be built or the message could not be sent.
     */
    public Response sendMessage(MessageService service, String payload) throws MessageException {
        Message message;
        try {
            message = service.buildHeader();
        } catch (MessageBuilderException e) {
            LOGGER.warn("Message could not be built. " + e.getMessage());
            throw new MessageBuilderException("Message could not be built.", e);
        }

        try {
            MultipartBody body = InfomodelMessageBuilder.messageWithString(message, payload);
            return idsHttpService.send(body, service.getRecipient());
        } catch (MessageNotSentException | IOException e) {
            LOGGER.warn("Message could not be sent. " + e.getMessage());
            throw new MessageBuilderException("Message could not be sent.", e);
        }
    }
}
