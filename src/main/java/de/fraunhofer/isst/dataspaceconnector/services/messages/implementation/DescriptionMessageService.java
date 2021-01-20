package de.fraunhofer.isst.dataspaceconnector.services.messages.implementation;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DescriptionResponseMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RequestMessage;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceImpl;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.model.BackendSource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DescriptionMessageService extends MessageService {

    private final ConfigurationContainer configurationContainer;
    private final DapsTokenProvider tokenProvider;
    private final ResourceService resourceService;
    private URI recipient, resourceId, correlationMessageId;

    @Autowired
    public DescriptionMessageService(DapsTokenProvider tokenProvider, IDSHttpService idsHttpService,
        ConfigurationContainer configurationContainer, OfferedResourceServiceImpl resourceService,
        IdsUtils idsUtils, SerializerProvider serializerProvider,
        RequestedResourceServiceImpl requestedResourceService) throws IllegalArgumentException {
        super(idsHttpService, idsUtils, serializerProvider, resourceService);

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (requestedResourceService == null)
            throw new IllegalArgumentException("The ResourceService cannot be null.");

        this.configurationContainer = configurationContainer;
        this.tokenProvider = tokenProvider;
        this.resourceService = requestedResourceService;
    }

    @Override
    public RequestMessage buildRequestHeader() throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        if (resourceId == null) {
            return new DescriptionRequestMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._securityToken_(tokenProvider.getDAT())
                ._recipientConnector_(Util.asList(recipient))
                .build();
        } else {
            return new DescriptionRequestMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._requestedElement_(resourceId)
                ._securityToken_(tokenProvider.getDAT())
                ._recipientConnector_(Util.asList(recipient))
                .build();
        }
    }

    @Override
    public Message buildResponseHeader() throws MessageException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new DescriptionResponseMessageBuilder()
            ._securityToken_(tokenProvider.getDAT())
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
        return recipient;
    }

    public void setRequestParameters(URI recipient, URI resourceId) {
        this.recipient = recipient;
        this.resourceId = resourceId;
    }

    public void setResponseParameters(URI recipient, URI correlationMessageId) {
        this.recipient = recipient;
        this.correlationMessageId = correlationMessageId;
    }

    /**
     * Saves the metadata to the internal database.
     *
     * @param response The data resource as string.
     * @return The UUID of the created resource.
     * @throws ResourceException if any.
     * @throws InvalidResourceException If the ids object could not be deserialized.
     */
    public UUID saveMetadata(String response, URI resourceId) throws ResourceException,
        InvalidResourceException {
        Resource resource;
        try {
            resource = getSerializerProvider().getSerializer().deserialize(response, ResourceImpl.class);
        } catch (Exception e) {
            resource = findResource(response, resourceId);
        }

        ResourceMetadata metadata;
        try {
            metadata = deserializeMetadata(resource);
        } catch (Exception exception) {
            LOGGER.info("Failed to deserialize metadata. [exception=({})]", exception.getMessage());
            throw new InvalidResourceException("Metadata could not be deserialized.");
        }

        try {
            return resourceService.addResource(metadata);
        } catch (Exception exception) {
            LOGGER.info("Failed to save metadata. [exception=({})]", exception.getMessage());
            throw new ResourceException("Metadata could not be saved to database.");
        }
    }

    /**
     * Find a resource from a connector's resource catalog.
     *
     * @return The resource object.
     * @throws InvalidResourceException If the payload could not be deserialized to a base connector.
     */
    private Resource findResource(String payload, URI resourceId) throws InvalidResourceException {
        Resource resource = null;
        try {
            Connector connector = getSerializerProvider().getSerializer().deserialize(payload, BaseConnector.class);
            if (connector.getResourceCatalog() != null && !connector.getResourceCatalog().isEmpty()) {
                for (Resource r : connector.getResourceCatalog().get(0).getOfferedResource()) {
                    if (r.getId().equals(resourceId)) {
                        resource = r;
                        break;
                    }
                }
            }
        } catch (Exception exception) {
            LOGGER.info("Failed to save metadata. [exception=({})]", exception.getMessage());
            throw new InvalidResourceException("Response could not be deserialized: " + payload);
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
