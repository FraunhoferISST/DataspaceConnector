package de.fraunhofer.isst.dataspaceconnector.services.messages.response;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.model.BackendSource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.messages.ResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util.getGregorianNow;

@Service
public class DescriptionResponseService extends ResponseService {

    private final TokenProvider tokenProvider;
    private final SerializerProvider serializerProvider;
    private final ResourceService resourceService;
    private final ConfigurationContainer configurationContainer;
    private URI recipient, correlationMessageId;

    @Autowired
    public DescriptionResponseService(TokenProvider tokenProvider,
        IDSHttpService idsHttpService, SerializerProvider serializerProvider,
        RequestedResourceServiceImpl requestedResourceService, IdsUtils idsUtils,
        OfferedResourceServiceImpl resourceService,
        ConfigurationContainer configurationContainer) throws IllegalArgumentException {
        super(idsHttpService, idsUtils, serializerProvider, resourceService);

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        if (requestedResourceService == null)
            throw new IllegalArgumentException("The ResourceService cannot be null.");

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        this.configurationContainer = configurationContainer;
        this.tokenProvider = tokenProvider;
        this.serializerProvider = serializerProvider;
        this.resourceService = requestedResourceService;
    }

    @Override
    public ResponseMessage buildHeader() throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new DescriptionResponseMessageBuilder()
            ._securityToken_(tokenProvider.getTokenJWS())
            ._correlationMessage_(correlationMessageId)
            ._issued_(getGregorianNow())
            ._issuerConnector_(connector.getId())
            ._modelVersion_(connector.getOutboundModelVersion())
            ._senderAgent_(connector.getId())
            ._recipientConnector_(Util.asList(recipient))
            .build();
    }

    @Override
    public URI getRecipient() {
        return null;
    }

    public void setParameter(URI recipient, URI correlationMessageId) {
        this.recipient = recipient;
        this.correlationMessageId = correlationMessageId;
    }

    /**
     * Saves the metadata to the internal database.
     *
     * @param response The data resource as string.
     * @return The UUID of the created resource.
     * @throws Exception if any.
     */
    public UUID saveMetadata(String response, URI resourceId) throws Exception {
        Resource resource;
        try {
            resource = serializerProvider.getSerializer().deserialize(response, ResourceImpl.class);
        } catch (Exception e) {
            resource = findResource(response, resourceId);
        }

        ResourceMetadata metadata;
        try {
            metadata = deserializeMetadata(resource);
        } catch (Exception e) {
            throw new Exception("Metadata could not be deserialized: " + e.getMessage());
        }

        try {
            return resourceService.addResource(metadata);
        } catch (Exception e) {
            throw new Exception("Metadata could not be saved: " + e.getMessage());
        }
    }

    /**
     * Find a resource from a connector's resource catalog.
     *
     * @return The resource object.
     * @throws Exception - if the payload could not be parsed to a base connector.
     */
    private Resource findResource(String payload, URI resourceId) throws Exception {
        Resource resource = null;
        try {
            Connector connector = serializerProvider.getSerializer().deserialize(payload, BaseConnector.class);
            if (connector.getResourceCatalog() != null && !connector.getResourceCatalog().isEmpty()) {
                for (Resource r : connector.getResourceCatalog().get(0).getOfferedResource()) {
                    if (r.getId().equals(resourceId)) {
                        resource = r;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Response could not be deserialized: " + payload);
        }
        return resource;
    }

    /**
     * Maps a received Infomodel resource to the internal metadata model.
     *
     * @return the metadata object.
     */
    private ResourceMetadata deserializeMetadata(Resource resource) {
        var metadata = new ResourceMetadata();

        if (resource.getKeyword() != null) {
            List<String> keywords = new ArrayList<>();
            for (var t : resource.getKeyword()) {
                keywords.add(t.getValue());
            }
            metadata.setKeywords(keywords);
        }

        if (resource.getRepresentation() != null) {
            var representations = new HashMap<UUID, ResourceRepresentation>();
            for (Representation r : resource.getRepresentation()) {
                int byteSize = 0;
                String name = null;
                String type = null;
                if (r.getInstance() != null && !r.getInstance().isEmpty()) {
                    Artifact artifact = (Artifact) r.getInstance().get(0);
                    if (artifact.getByteSize() != null)
                        byteSize = artifact.getByteSize().intValue();
                    if (artifact.getFileName() != null)
                        name = artifact.getFileName();
                    if (r.getMediaType() != null)
                        type = r.getMediaType().getFilenameExtension();
                }

                ResourceRepresentation representation = new ResourceRepresentation(
                    UUIDUtils.uuidFromUri(r.getId()), type, byteSize, name,
                    new BackendSource(BackendSource.Type.LOCAL, null, null, null)
                );

                representations.put(representation.getUuid(), representation);
            }
            metadata.setRepresentations(representations);
        }

        if (resource.getTitle() != null && !resource.getTitle().isEmpty())
            metadata.setTitle(resource.getTitle().get(0).getValue());

        if (resource.getDescription() != null && !resource.getDescription().isEmpty())
            metadata.setDescription(resource.getDescription().get(0).getValue());

        if (resource.getContractOffer() != null && !resource.getContractOffer().isEmpty())
            metadata.setPolicy(resource.getContractOffer().get(0).toRdf());

        if (resource.getPublisher() != null)
            metadata.setOwner(resource.getPublisher());

        if (resource.getStandardLicense() != null)
            metadata.setLicense(resource.getStandardLicense());

        if (resource.getVersion() != null)
            metadata.setVersion(resource.getVersion());

        return metadata;
    }
}
