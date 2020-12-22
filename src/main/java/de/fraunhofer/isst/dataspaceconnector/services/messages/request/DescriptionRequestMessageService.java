package de.fraunhofer.isst.dataspaceconnector.services.messages.request;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.RequestMessage;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DescriptionRequestMessageService extends MessageService {

    public static final Logger LOGGER = LoggerFactory.getLogger(DescriptionRequestMessageService.class);

    private final Connector connector;
    private final TokenProvider tokenProvider;
    private URI recipient, resourceId;

    @Autowired
    public DescriptionRequestMessageService(IdsUtils idsUtils, TokenProvider tokenProvider,
        IDSHttpService idsHttpService, ConfigurationContainer configurationContainer) throws IllegalArgumentException {
        super(idsHttpService);

        if (idsUtils == null)
            throw new IllegalArgumentException("The IdsUtils cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        this.connector = idsUtils.getConnector();
        this.tokenProvider = tokenProvider;
    }

    @Override
    public RequestMessage buildHeader() throws MessageBuilderException {
        if (resourceId == null) {
            return new DescriptionRequestMessageBuilder()
                ._issued_(Util.getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._securityToken_(tokenProvider.getTokenJWS())
                ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(recipient))
                .build();
        } else {
            return new DescriptionRequestMessageBuilder()
                ._issued_(Util.getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._requestedElement_(resourceId)
                ._securityToken_(tokenProvider.getTokenJWS())
                ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(recipient))
                .build();
        }
    }

    @Override
    public URI getRecipient() {
        return recipient;
    }

    public void setParameter(URI recipient, URI resourceId) {
        this.recipient = recipient;
        this.resourceId = resourceId;
    }
}
