package de.fraunhofer.isst.dataspaceconnector.services.resource;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceTypeException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UUIDFormatException;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.HttpUtils;
import de.fraunhofer.isst.dataspaceconnector.services.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.services.UUIDUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.*;

/**
 * This class implements all methods of {@link de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService}. It provides database resource handling for all offered resources.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class OfferedResourceServiceImpl implements OfferedResourceService {
    /**
     * Constant <code>LOGGER</code>
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(OfferedResourceServiceImpl.class);

    private final OfferedResourceRepository offeredResourceRepository;
    private final HttpUtils httpUtils;
    private final IdsUtils idsUtils;

    private final Map<UUID, Resource> offeredResources;
    private final ContractOffer contractOffer;

    /**
     * <p>Constructor for OfferedResourceServiceImpl.</p>
     *
     * @param offeredResourceRepository a {@link OfferedResourceRepository} object.
     * @param httpUtils                 a {@link HttpUtils} object.
     * @param idsUtils                  a {@link IdsUtils} object.
     */
    @Autowired
    public OfferedResourceServiceImpl(@NotNull OfferedResourceRepository offeredResourceRepository,
                                      @NotNull HttpUtils httpUtils, @NotNull IdsUtils idsUtils) {
        this.offeredResourceRepository = offeredResourceRepository;
        this.httpUtils = httpUtils;
        this.idsUtils = idsUtils;

        contractOffer = new ContractOfferBuilder()
                ._permission_(Util.asList(new PermissionBuilder()
                        ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                        ._description_(Util.asList(new TypedLiteral("provide-access")))
                        ._action_(Util.asList(Action.USE))
                        .build()))
                .build();

        // NOTE: This does not scale
        offeredResources = new HashMap<>();
        for (OfferedResource resource : offeredResourceRepository.findAll()) {
            offeredResources.put(resource.getUuid(), idsUtils.getAsResource(resource));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the resource list.
     */
    @Override
    public ArrayList<Resource> getResourceList() {
        return new ArrayList<>(offeredResources.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<UUID, Resource> getOfferedResources() {
        return offeredResources;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Saves the resources with its metadata as external resource or internal resource.
     */
    @Override
    public UUID addResource(ResourceMetadata resourceMetadata) throws ResourceException {
        try {
            final var uuid = UUIDUtils.createUUID((UUID x) -> getResource(x) != null);
            addResourceWithId(resourceMetadata, uuid);
            return uuid;
        } catch (UUIDFormatException exception) {
            throw new ResourceException("Failed to create resource.", exception);
        }
    }

    @Override
    public void addResourceWithId(ResourceMetadata resourceMetadata, UUID uuid) throws ResourceException {
        if (getResource(uuid) != null)
            throw new ResourceException("This resource does already exist.");

        resourceMetadata.setPolicy(contractOffer.toRdf());
        final var resource = new OfferedResource(uuid, new Date(), new Date(), resourceMetadata,
                "");

        storeResource(resource);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Publishes the resource data.
     */
    @Override
    public void addData(UUID resourceId, String data) throws ResourceException {
        final var resource = getResource(resourceId);
        if (resource == null)
            throw new ResourceException("This resource does not exist.");

        resource.setData(data);
        storeResource(resource);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates resource metadata by id.
     */
    @Override
    public void updateResource(UUID resourceId, ResourceMetadata resourceMetadata) throws ResourceException {
        final var resource = getResource(resourceId);
        if (resource == null)
            throw new ResourceException("This resource does not exist.");

        resource.setResourceMetadata(resourceMetadata);
        storeResource(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateContract(UUID resourceId, String policy) throws ResourceException {
        final var resourceMetadata = getMetadata(resourceId);

        resourceMetadata.setPolicy(policy);
        updateResource(resourceId, resourceMetadata);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Deletes a resource by id.
     */
    @Override
    public void deleteResource(UUID resourceId) {
        final var key = offeredResources.remove(resourceId);
        if (key != null) {
            offeredResourceRepository.deleteById(resourceId);
        } else {
            LOGGER.warn("Tried to delete resource that does not exist.");
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gets a resource by id.
     */
    @Override
    public OfferedResource getResource(UUID resourceId) throws ResourceTypeException {
        final var resource = offeredResourceRepository.findById(resourceId);

        if (resource.isEmpty()) {
            return null;
        } else {
            final var error = isValidResource(resource.get());
            if (error.isPresent())
                throw new ResourceTypeException("The resource is not valid. " + error.get());

            return resource.get();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gets resource metadata by id.
     */
    @Override
    public ResourceMetadata getMetadata(UUID resourceId) throws ResourceNotFoundException,
            ResourceTypeException {
        final var resource = getResource(resourceId);
        if (resource == null)
            throw new ResourceNotFoundException("This resource does not exist.");

        return resource.getResourceMetadata();
    }

    public Map<UUID, ResourceRepresentation> getAllRepresentations(UUID resourceId) throws ResourceNotFoundException, ResourceTypeException {
        return getMetadata(resourceId).getRepresentations();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceRepresentation getRepresentation(UUID resourceId, UUID representationId) throws ResourceNotFoundException, ResourceTypeException {
        return getAllRepresentations(resourceId).get(representationId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gets data from local database.
     */
    @Override
    public String getData(UUID resourceId) throws RuntimeException {
        final var representations = getAllRepresentations(resourceId);
        for (var representationId : representations.keySet()) {
            try {
                return getDataByRepresentation(resourceId, representationId);
            } catch (ResourceException exception) {
                // The resource is incomplete or wrong.
                LOGGER.warn("Resource exception.");
            } catch (RuntimeException exception) {
                // The resource could not be received.
                LOGGER.warn("Failed to get resource data.");
            }
        }

        throw new RuntimeException("Resource get the data.");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gets data from local or external data source.
     */
    @Override
    public String getDataByRepresentation(UUID resourceId, UUID representationId) throws ResourceTypeException, ResourceNotFoundException, ResourceException {
        final var resource = getResource(resourceId);
        if (resource == null) {
            throw new ResourceException("The resource does not exist.");
        }

        final var representation = getRepresentation(resourceId, representationId);
        if (representation == null)
            throw new ResourceException("Resource not found exception.");

        return getDataString(resource, representation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID addRepresentation(UUID resourceId, ResourceRepresentation representation) throws ResourceException, ResourceTypeException{
        final var uuid = UUIDUtils.createUUID(
                (UUID x) -> getRepresentation(resourceId, x) != null);
        return addRepresentationWithId(resourceId, representation, uuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID addRepresentationWithId(UUID resourceId, ResourceRepresentation representation,
                                        UUID representationId) throws ResourceException, ResourceTypeException{
        final var metaData = getMetadata(resourceId);
        if (getRepresentation(resourceId, representationId) != null)
            throw new ResourceException("This representation does already exist.");

        representation.setUuid(representationId);
        metaData.getRepresentations().put(representation.getUuid(), representation);

        updateResource(resourceId, metaData);
        return representationId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateRepresentation(UUID resourceId, UUID representationId,
                                     ResourceRepresentation representation) throws ResourceException, ResourceNotFoundException, ResourceTypeException{
        if (getRepresentation(resourceId, representationId) != null) {
            representation.setUuid(representationId);
            var representations = getAllRepresentations(resourceId);
            representations.put(representationId, representation);

            var metadata = getMetadata(resourceId);
            metadata.setRepresentations(representations);

            updateResource(resourceId, metadata);
        } else {
            throw new ResourceException("Tried to update a representation that does not exist.");
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteRepresentation(UUID resourceId, UUID representationId) throws ResourceException, ResourceNotFoundException, ResourceTypeException{
        var representations = getAllRepresentations(resourceId);
        if (representations == null || representations.size() == 0)
            throw new ResourceException("This resource metadata has no representations.");

        if (representations.remove(representationId) != null) {
            var metadata = getMetadata(resourceId);
            metadata.setRepresentations(representations);

            updateResource(resourceId, metadata);
        } else {
            throw new ResourceException("Tried to update a representation that does not exist.");
        }
    }

    public Optional<String> isValidResource(OfferedResource resource) {
        if (resource == null)
            return Optional.of("The resource cannot be null.");

        if (resource.getResourceMetadata() == null)
            return Optional.of("The resource metadata cannot be null.");

        if (resource.getResourceMetadata().getRepresentations() == null)
            return Optional.of("The resource representation cannot be null.");

        if (resource.getResourceMetadata().getRepresentations().size() < 1)
            return Optional.of("The resource representation must have at least one element.");

        return Optional.empty();
    }

    private void storeResource(OfferedResource resource) throws ResourceException{
        final var error = isValidResource(resource);
        if(error.isPresent())
            throw new ResourceException("Not a valid resource. " + error.get());

        offeredResourceRepository.save(resource);
        offeredResources.put(resource.getUuid(), idsUtils.getAsResource(resource));
    }

    /**
     * Gets data as string.
     *
     * @param resource       The connector resource object.
     * @param representation The representation.
     * @return The string or an exception.
     * @throws ResourceException - if the resource source is not defined or source url is
     *                           ill-formatted.
     * @throws RuntimeException  - if the data could not be received
     */
    private String getDataString(OfferedResource resource, ResourceRepresentation representation) throws RuntimeException, ResourceException {
        if (representation.getSource() != null) {
            try {
                final var address = representation.getSource().getUrl();
                final var username = representation.getSource().getUsername();
                final var password = representation.getSource().getPassword();

                switch (representation.getSource().getType()) {
                    case LOCAL:
                        return resource.getData();
                    case HTTP_GET:
                        return httpUtils.sendHttpGetRequest(address.toString());
                    case HTTPS_GET:
                        return httpUtils.sendHttpsGetRequest(address.toString());
                    case HTTPS_GET_BASICAUTH:
                        return httpUtils.sendHttpsGetRequestWithBasicAuth(address.toString(), username,
                                password);
                    default:
                        // This exception is only thrown when BackendSource.Type is expanded but this
                        // switch is not
                        throw new NotImplementedException("This type is not supported");
                }
            } catch (MalformedURLException exception) {
                // One of the http requests received a non url as address
                LOGGER.error("The resource representation is not an url.", exception);
                throw new ResourceException("The resource source representation is not an url.",
                        exception);
            } catch (RuntimeException exception) {
                // One of the http calls encountered problems.
                throw exception;
            }
        } else {
            throw new ResourceException("The resource has no defined backend.");
        }
    }
}
