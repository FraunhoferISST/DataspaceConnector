package de.fraunhofer.isst.dataspaceconnector.services.communication.request;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RequestMessage;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceImpl;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.model.BackendSource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.UUIDUtils;
import de.fraunhofer.isst.dataspaceconnector.services.communication.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resource.ResourceService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import de.fraunhofer.isst.ids.framework.util.MultipartStringParser;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DescriptionRequestMessageService extends MessageService {

    public static final Logger LOGGER = LoggerFactory.getLogger(DescriptionRequestMessageService.class);

    private final Connector connector;
    private final TokenProvider tokenProvider;
    private final SerializerProvider serializerProvider;
    private final ResourceService resourceService;
    private URI recipient, resourceId;

    @Autowired
    public DescriptionRequestMessageService(ConfigurationContainer configurationContainer,
        TokenProvider tokenProvider, IDSHttpService idsHttpService, SerializerProvider serializerProvider,
        RequestedResourceServiceImpl requestedResourceService) {
        super(idsHttpService);

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        if (requestedResourceService == null)
            throw new IllegalArgumentException("The ResourceService cannot be null.");

        this.connector = configurationContainer.getConnector();
        this.tokenProvider = tokenProvider;
        this.serializerProvider = serializerProvider;
        this.resourceService = requestedResourceService;
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
