package de.fraunhofer.isst.dataspaceconnector.services.communication;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RequestMessage;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceImpl;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageNotSentException;
import de.fraunhofer.isst.dataspaceconnector.model.BackendSource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.UUIDUtils;
import de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resource.ResourceService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.exceptions.HttpClientException;
import de.fraunhofer.isst.ids.framework.messages.InfomodelMessageBuilder;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import de.fraunhofer.isst.ids.framework.util.ClientProvider;
import de.fraunhofer.isst.ids.framework.util.MultipartStringParser;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import okhttp3.MultipartBody;
import okhttp3.Response;
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

    /**
     * Saves the metadata to the internal database.
     *
     * @param response The data resource as string.
     * @return The UUID of the created resource.
     * @throws java.lang.Exception if any.
     */
    public UUID saveMetadata(String response) throws Exception {
        Map<String, String> map = MultipartStringParser.stringToMultipart(response);
        final var header = map.get("header");
        final var payload = map.get("payload");

        try {
            serializerProvider.getSerializer().deserialize(header, DescriptionResponseMessage.class);
        } catch (Exception e) {
            throw new Exception("Wrong message type: " + header);
        }

        Resource resource;
        try {
            resource = serializerProvider.getSerializer().deserialize(payload, ResourceImpl.class);
        } catch (Exception e) {
            throw new Exception("Metadata could not be deserialized: " + payload);
        }

        try {
            return resourceService.addResource(deserializeMetadata(resource));
        } catch (Exception e) {
            throw new Exception("Metadata could not be saved: " + e.getMessage());
        }
    }

    private ResourceMetadata deserializeMetadata(Resource resource) {
        List<String> keywords = new ArrayList<>();
        for (TypedLiteral t : resource.getKeyword()) {
            keywords.add(t.getValue());
        }

        var representations = new HashMap<UUID, ResourceRepresentation>();
        for (Representation r : resource.getRepresentation()) {
            Artifact artifact = (Artifact) r.getInstance().get(0);
            ResourceRepresentation representation = new ResourceRepresentation(
                UUIDUtils.createUUID((UUID x) -> representations.get(x) != null),
                r.getMediaType().getFilenameExtension(),
                artifact.getByteSize().intValue(),
                artifact.getFileName(),
                new BackendSource(BackendSource.Type.LOCAL, null, null, null)
            );

            representations.put(representation.getUuid(), representation);
        }

        return new ResourceMetadata(
            resource.getTitle().get(0).getValue(),
            resource.getDescription().get(0).getValue(),
            keywords,
            resource.getContractOffer().get(0).toRdf(),
            resource.getPublisher(),
            resource.getStandardLicense(),
            resource.getVersion(),
            representations
        );
    }
}
