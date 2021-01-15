package de.fraunhofer.isst.dataspaceconnector.services.messages.notification;

import de.fraunhofer.iais.eis.LogMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.RequestService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class LogMessageService extends RequestService {

    private final ConfigurationContainer configurationContainer;
    private final TokenProvider tokenProvider;
    private final URI recipient;

    @Autowired
    public LogMessageService(TokenProvider tokenProvider, IDSHttpService idsHttpService,
        ConfigurationContainer configurationContainer, OfferedResourceServiceImpl resourceService)
        throws IllegalArgumentException {
        super(idsHttpService, resourceService);

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        this.configurationContainer = configurationContainer;
        this.tokenProvider = tokenProvider;

        recipient = URI.create("https://ch-ids.aisec.fraunhofer.de/logs/messages/");
    }

    @Override
    public Message buildHeader() throws MessageException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new LogMessageBuilder()
            ._issued_(Util.getGregorianNow())
            ._modelVersion_(connector.getOutboundModelVersion())
            ._issuerConnector_(connector.getId())
            ._senderAgent_(connector.getId())
            ._securityToken_(tokenProvider.getTokenJWS())
            ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(recipient))
            .build();
    }

    @Override
    public URI getRecipient() {
        return recipient;
    }
}
