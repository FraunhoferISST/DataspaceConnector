package de.fraunhofer.isst.dataspaceconnector.services.messages.implementation;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

import de.fraunhofer.iais.eis.LogMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogMessageService extends MessageService {

    private final ConfigurationContainer configurationContainer;
    private final DapsTokenProvider tokenProvider;
    private final URI recipient;

    @Autowired
    public LogMessageService(DapsTokenProvider tokenProvider, IDSHttpService idsHttpService,
        ConfigurationContainer configurationContainer, OfferedResourceServiceImpl resourceService,
        IdsUtils idsUtils, SerializerProvider serializerProvider) throws IllegalArgumentException {
        super(idsHttpService, idsUtils, serializerProvider, resourceService);

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        this.configurationContainer = configurationContainer;
        this.tokenProvider = tokenProvider;

        recipient = URI.create("https://ch-ids.aisec.fraunhofer.de/logs/messages/");
    }

    @Override
    public Message buildRequestHeader() throws MessageException {
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

    @Override
    public Message buildResponseHeader() throws MessageException {
        return null;
    }

    @Override
    public URI getRecipient() {
        return recipient;
    }
}
