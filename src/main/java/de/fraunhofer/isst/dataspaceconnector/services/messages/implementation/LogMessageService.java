package de.fraunhofer.isst.dataspaceconnector.services.messages.implementation;

import de.fraunhofer.iais.eis.LogMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * The service for log messages
 */
@Service
public class LogMessageService extends MessageService {

    private final ConfigurationContainer configurationContainer;
    private final DapsTokenProvider tokenProvider;
    private final URI recipient;

    /**
     * Constructor
     *
     * @param tokenProvider The service for providing tokens
     * @param idsHttpService The service for ids messaging
     * @param configurationContainer The container with the configuration
     * @param resourceService The service for resources
     * @param serializerProvider The service for serializing
     * @throws IllegalArgumentException if any of the parameters is null
     */
    @Autowired
    public LogMessageService(DapsTokenProvider tokenProvider, IDSHttpService idsHttpService,
        ConfigurationContainer configurationContainer, OfferedResourceServiceImpl resourceService,
        SerializerProvider serializerProvider) throws IllegalArgumentException {
        super(idsHttpService, serializerProvider, resourceService, configurationContainer);

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        this.configurationContainer = configurationContainer;
        this.tokenProvider = tokenProvider;

        recipient = URI.create("https://ch-ids.aisec.fraunhofer.de/logs/messages/");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message buildRequestHeader() throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

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
     * {@inheritDoc}
     */
    @Override
    public Message buildResponseHeader() throws MessageBuilderException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getRecipient() {
        return recipient;
    }
}
