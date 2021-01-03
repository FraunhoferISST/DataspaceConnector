package de.fraunhofer.isst.dataspaceconnector.services.messages.notification;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.LogMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.RequestService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogMessageService extends RequestService {

    public static final Logger LOGGER = LoggerFactory.getLogger(LogMessageService.class);

    private final Connector connector;
    private final TokenProvider tokenProvider;
    private URI recipient;

    @Autowired
    public LogMessageService(TokenProvider tokenProvider, IDSHttpService idsHttpService,
        IdsUtils idsUtils, OfferedResourceServiceImpl resourceService)
        throws IllegalArgumentException {
        super(idsHttpService, resourceService);

        if (idsUtils == null)
            throw new IllegalArgumentException("The IdsUtils cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        this.connector = idsUtils.getConnector();
        this.tokenProvider = tokenProvider;

        recipient = URI.create("https://ch-ids.aisec.fraunhofer.de/logs/messages/");
    }

    @Override
    public Message buildHeader() throws MessageException {
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
