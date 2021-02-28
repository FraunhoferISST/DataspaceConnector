package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractOfferBuilder;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UUIDFormatException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceAlreadyExistsException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.BackendSource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.repositories.OfferedResourceRepository;
import de.fraunhofer.isst.dataspaceconnector.services.utils.HttpUtils;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class implements all methods of {@link ResourceService}.
 * It provides methods for performing the CRUD operations for offered resources.
 */
@Service
public class OfferedResourceServiceImpl implements ResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferedResourceServiceImpl.class);

    private final OfferedResourceRepository offeredResourceRepository;
    private final HttpUtils httpUtils;
    private final IdsUtils idsUtils;
    private final ContractOffer contractOffer;

    /**
     * Constructor for OfferedResourceServiceImpl.
     *
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Autowired
    public OfferedResourceServiceImpl(OfferedResourceRepository offeredResourceRepository,
        HttpUtils httpUtils, IdsUtils idsUtils) throws IllegalArgumentException {
        if (offeredResourceRepository == null)
            throw new IllegalArgumentException("The OfferedResourceRepository cannot be null.");

        if (httpUtils == null)
            throw new IllegalArgumentException("The HttpUtils cannot be null.");

        if (idsUtils == null)
            throw new IllegalArgumentException("The IdsUtils cannot be null.");

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
    }

    /**
     * Returns a list containing all offered resources as IDS information model resources.
     *
     * @return the list
     */
    @Override
    public List<Resource> getResources() {
        return getAllResources().parallelStream().map(idsUtils::getAsResource)
            .collect(Collectors.toList());
    }

    /**
     * Returns all offered resources as a map, where resources are mapped to their IDs.
     *
     * @return the map
     */
    public Map<UUID, Resource> getOfferedResources() {
        return getAllResources().parallelStream().collect(Collectors
            .toMap(OfferedResource::getUuid, idsUtils::getAsResource));
    }

    /**
     * Saves the resource with its metadata.
     *
     * @param resourceMetadata the resource's metadata.
     * @return the UUID of the newly created resource.
     * @throws InvalidResourceException if the resource is not valid.
     * @throws ResourceAlreadyExistsException    - if the resource does already exists.
     * @throws ResourceException        - if the resource could not be created.
     */
    @Override
    public UUID addResource(ResourceMetadata resourceMetadata) throws ResourceException {
        try {
            final var uuid = UUIDUtils.createUUID((UUID x) -> {
                try {
                    return getResource(x) != null;
                } catch (InvalidResourceException exception) {
                    return false;
                }
            });

            addResourceWithId(resourceMetadata, uuid);
            return uuid;
        } catch (UUIDFormatException exception) {
            throw new ResourceException("Failed to create resource.", exception);
        }
    }

    /**
     * Saves the resource with its metadata and a given ID.
     *
     * @param resourceMetadata the resource's metadata.
     * @param uuid the ID
     * @throws InvalidResourceException if the resource is not valid.
     * @throws ResourceAlreadyExistsException    - if the resource does already exists.
     */
    public void addResourceWithId(ResourceMetadata resourceMetadata, UUID uuid) throws
        InvalidResourceException, ResourceAlreadyExistsException {
        if (getResource(uuid) != null) {
            throw new ResourceAlreadyExistsException("The resource does already exist.");
        }

        if(resourceMetadata.getRepresentations() != null) {
            computeMissingRepresentationIds(resourceMetadata);
        }

        resourceMetadata.setPolicy(contractOffer.toRdf());
        final var resource = new OfferedResource(uuid, new Date(), new Date(), resourceMetadata,
            "");

        storeResource(resource);
        LOGGER.debug("Added a new resource. [uuid=({}), metadata=({})]", uuid, resourceMetadata);
    }

    private void computeMissingRepresentationIds(final ResourceMetadata metaData) {
        final var updated = new HashMap<UUID, ResourceRepresentation>();

        for (final var representation : metaData.getRepresentations().values()) {
            if (representation.getUuid() == null) {
                representation.setUuid(generateRepresentationId());
                updated.put(representation.getUuid(), representation);
            }else{
                updated.put(representation.getUuid(), representation);
            }
        }

        metaData.getRepresentations().clear();
        metaData.getRepresentations().putAll(updated);
    }

    private UUID generateRepresentationId() {
        return UUIDUtils.createUUID((UUID x) -> {
            try {
                for(final var resource : getAllResources()) {
                    if(getAllRepresentations(resource.getUuid()).keySet().stream().anyMatch(y -> y.equals(x)))
                        return true;
                }

                return false;
            } catch (InvalidResourceException e) {
                return false;
            }
        });
    }

    /**
     * Publishes the resource data by ID.
     *
     * @param resourceId ID of the resource
     * @param data data as string
     * @throws ResourceNotFoundException if the resource could not be found
     * @throws InvalidResourceException if the resource is invalid
     */
    @Override
    public void addData(UUID resourceId, String data) throws InvalidResourceException,
        ResourceNotFoundException {
        final var resource = getResource(resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("The resource does not exist.");
        }

        resource.setData(data);
        storeResource(resource);
        LOGGER.debug("Added data to resource. [resourceId=({}), data=({})]", resourceId, data);
    }

    /**
     * Updates resource metadata by ID.
     *
     * @param resourceId ID of the resource
     * @param resourceMetadata the updated metadata
     * @throws InvalidResourceException if the resource is invalid.
     * @throws ResourceNotFoundException if the resource could not be found
     */
    public void updateResource(UUID resourceId, ResourceMetadata resourceMetadata) throws
        InvalidResourceException, ResourceNotFoundException {
        final var resource = getResource(resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("The resource does not exist.");
        }

        if(resourceMetadata.getRepresentations() != null) {
            computeMissingRepresentationIds(resourceMetadata);
        }

        resource.setResourceMetadata(resourceMetadata);
        storeResource(resource);
        LOGGER.debug("Updated resource. [resourceId=({}), metadata=({})]", resourceId,
            resourceMetadata);
    }

    /**
     * Updates resource policy by ID.
     *
     * @param resourceId ID of the resource
     * @param policy the updated policy
     * @throws InvalidResourceException if the resource is invalid.
     * @throws ResourceNotFoundException if the resource could not be found
     */
    public void updateContract(UUID resourceId, String policy) throws ResourceNotFoundException,
        InvalidResourceException {
        final var resourceMetadata = getMetadata(resourceId);

        // NOTE SAFETY CHECK
        resourceMetadata.setPolicy(policy);
        updateResource(resourceId, resourceMetadata);
        LOGGER.debug("Updated contract of resource. [resourceId=({}), policy=({})]", resourceId,
            policy);
    }

    /**
     * Deletes a resource by ID.
     *
     * @param resourceId ID of the resource
     * @return true, if the the resource was deleted; false otherwise
     */
    @Override
    public boolean deleteResource(UUID resourceId) {
        try {
            if (getResource(resourceId) != null) {
                offeredResourceRepository.deleteById(resourceId);
                LOGGER.debug("Deleted resource. [resourceId=({})]", resourceId);
                return true;
            }
        }catch(InvalidResourceException exception){
            // The resource exists, delete it
            offeredResourceRepository.deleteById(resourceId);
            LOGGER.debug("Deleted resource. [resourceId=({})]", resourceId);
            return true;
        }

        return false;
    }

    /**
     * Finds a resource by ID.
     *
     * @param resourceId ID of the resource
     * @return the resource
     */
    @Override
    public OfferedResource getResource(UUID resourceId) throws InvalidResourceException {
        final var resource = offeredResourceRepository.findById(resourceId);

        if (resource.isEmpty()) {
            return null;
        } else {
            invalidResourceGuard(resource.get());
            return resource.get();
        }
    }

    /**
     * Returns all offered resources as a list.
     * @return the list
     */
    public List<OfferedResource> getAllResources() {
        return offeredResourceRepository.findAll();
    }

    /**
     * Gets resource metadata by ID.
     *
     * @return the metadata
     * @throws InvalidResourceException if the resource is invalid.
     * @throws ResourceNotFoundException if the resource could not be found
     */
    @Override
    public ResourceMetadata getMetadata(UUID resourceId) throws ResourceNotFoundException,
        InvalidResourceException {
        final var resource = getResource(resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("The resource does not exist.");
        }

        return resource.getResourceMetadata();
    }

    /**
     * Returns all representations of a given resource as a map, where representations are mapped to their IDs.
     *
     * @param resourceId ID of the resource
     * @return the map
     * @throws InvalidResourceException if the resource is invalid.
     * @throws ResourceNotFoundException if the resource could not be found
     */
    public Map<UUID, ResourceRepresentation> getAllRepresentations(UUID resourceId) throws
        ResourceNotFoundException, InvalidResourceException {
        return getMetadata(resourceId).getRepresentations();
    }

    /**
     * Finds a representation by ID.
     *
     * @param resourceId ID of the resource.
     * @param representationId ID of the representation
     * @return the representation
     * @throws InvalidResourceException if the resource is invalid.
     * @throws ResourceNotFoundException if the resource could not be found
     */
    @Override
    public ResourceRepresentation getRepresentation(UUID resourceId, UUID representationId) throws
        ResourceNotFoundException, InvalidResourceException {
        return getAllRepresentations(resourceId).get(representationId);
    }

    /**
     * Retrieves resource data from the local database by ID.
     *
     * @param resourceId ID of the resource
     * @return resource data as string
     * @throws InvalidResourceException if the resource is invalid.
     * @throws ResourceNotFoundException if the resource could not be found
     */
    @Override
    public String getData(UUID resourceId) throws ResourceNotFoundException,
            ResourceException {
        final var representations = getAllRepresentations(resourceId);
        for (var representationId : representations.keySet()) {
            try {
                return getDataByRepresentation(resourceId, representationId, null);
            } catch (ResourceException exception) {
                // The resource is incomplete or wrong.
                LOGGER.debug("Resource exception. [resourceId=({}), representationId=({}), " +
                        "exception=({})]", resourceId, representationId, exception);
                throw exception;
            } catch (RuntimeException exception) {
                // The resource could not be received.
                LOGGER.debug("Failed to get resource data. [resourceId=({}), representationId=({}), " +
                        "exception=({})]", resourceId, representationId, exception);
                throw exception;
            }
        }

        // This code should never be reached since the representation should have at least one
        // representation.
        invalidResourceGuard(getResource(resourceId));
        // Add a runtime exception in case the resource valid logic changed.
        throw new RuntimeException("This code should not have been reached.");
    }

    /**
     * Retrieves resource data from the local database by ID or from an external data source using
     * the given QueryInput.
     *
     * @param resourceId ID of the resource
     * @param queryInput Headers, path variables and params for data request from backend.
     * @return resource data as string
     * @throws InvalidResourceException if the resource is invalid.
     * @throws ResourceNotFoundException if the resource could not be found
     */
    public String getData(UUID resourceId, QueryInput queryInput) {
        final var representations = getAllRepresentations(resourceId);
        for (var representationId : representations.keySet()) {
            try {
                return getDataByRepresentation(resourceId, representationId, queryInput);
            } catch (ResourceException exception) {
                // The resource is incomplete or wrong.
                LOGGER.debug("Resource exception. [resourceId=({}), representationId=({}), " +
                        "exception=({})]", resourceId, representationId, exception);
                throw exception;
            } catch (IllegalArgumentException exception) {
                // Query input was invalid.
                LOGGER.debug("Invalid query input. [resourceId=({}), representationId=({}), " +
                        "exception=({})]", resourceId, representationId, exception);
                throw exception;
            } catch (RuntimeException exception) {
                // The resource could not be received.
                LOGGER.debug("Failed to get resource data. [resourceId=({}), representationId=({}), " +
                        "exception=({})]", resourceId, representationId, exception);
                throw exception;
            }
        }

        // This code should never be reached since the representation should have at least one
        // representation.
        invalidResourceGuard(getResource(resourceId));
        // Add a runtime exception in case the resource valid logic changed.
        throw new RuntimeException("This code should not have been reached.");
    }

    /**
     * Retrieves resource data from the local database or an external data source by ID.
     *
     * @param resourceId ID of the resource
     * @param representationId ID of the representation
     * @param queryInput Headers, path variables and params for data request from backend.
     * @return resource data as string
     * @throws ResourceNotFoundException if the resource could not be found
     * @throws ResourceException if the resource data could not be retrieved
     */
    @Override
    public String getDataByRepresentation(UUID resourceId, UUID representationId, QueryInput queryInput)
            throws ResourceNotFoundException, ResourceException {
        final var resource = getResource(resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("The resource does not exist.");
        }

        final var representation = getRepresentation(resourceId, representationId);
        if (representation == null) {
            throw new ResourceNotFoundException("The resource representation does not exist.");
        }

        return getDataString(resource, representation, queryInput);
    }

    /**
     * Adds a representation to a resource.
     *
     * @param resourceId ID of the resource
     * @param representation the representation
     * @return ID of the newly created representation
     * @throws InvalidResourceException if the resource is invalid.
     * @throws ResourceNotFoundException if the resource could not be found
     * @throws ResourceAlreadyExistsException if the representation already exists
     */
    public UUID addRepresentation(UUID resourceId, ResourceRepresentation representation) throws
        ResourceNotFoundException, InvalidResourceException, ResourceAlreadyExistsException {
        final var uuid = UUIDUtils.createUUID(
            (UUID x) -> {
                try {
                    return getRepresentation(resourceId, x) != null;
                } catch (InvalidResourceException e) {
                    return false;
                }
            });

        return addRepresentationWithId(resourceId, representation, uuid);
    }

    /**
     * Adds a representation with a given ID to a resource.
     *
     * @param resourceId ID of the resource
     * @param representation the representation
     * @param representationId ID of the representation
     * @return ID of the newly created representation
     * @throws InvalidResourceException if the resource is invalid.
     * @throws ResourceNotFoundException if the resource could not be found
     * @throws ResourceAlreadyExistsException if the representation already exists
     */
    public UUID addRepresentationWithId(UUID resourceId, ResourceRepresentation representation,
        UUID representationId) throws
        ResourceNotFoundException, InvalidResourceException, ResourceAlreadyExistsException {
        final var metaData = getMetadata(resourceId);
        if (getRepresentation(resourceId, representationId) != null) {
            throw new ResourceAlreadyExistsException("The representation does already exist.");
        }

        representation.setUuid(representationId);
        metaData.getRepresentations().put(representation.getUuid(), representation);

        updateResource(resourceId, metaData);
        LOGGER.debug("Added representation to resource. [resourceId=({}), representationId=({}), " +
                        "representation=({})]", resourceId, representationId, representation);
        return representationId;
    }

    /**
     * Updates a representation by ID.
     *
     * @param resourceId ID of the resource
     * @param representationId ID of the representation
     * @param representation the updated representation
     * @throws InvalidResourceException if the resource is invalid.
     * @throws ResourceNotFoundException if the resource could not be found
     */
    public void updateRepresentation(UUID resourceId, UUID representationId,
        ResourceRepresentation representation) throws
        ResourceNotFoundException, InvalidResourceException {
        if (getRepresentation(resourceId, representationId) != null) {
            representation.setUuid(representationId);
            var representations = getAllRepresentations(resourceId);
            representations.put(representationId, representation);

            var metadata = getMetadata(resourceId);
            metadata.setRepresentations(representations);

            updateResource(resourceId, metadata);
            LOGGER.debug(
                "Updated representation of resource. [resourceId=({}), representationId=({}), representation=({})]",
                resourceId, representationId, representation);
        } else {
            LOGGER.debug("Failed to update resource representation. It does not exist. " +
                    "[resourceId=({}), representationId=({}), representation=({})]",
                    resourceId, representationId, representation);
            throw new ResourceNotFoundException("The resource representation does not exist.");
        }
    }


    /**
     * Deletes a representation by ID.
     *
     * @param resourceId ID of the resource
     * @param representationId ID of the representation
     * @return true, if the the representation was deleted; false otherwise
     * @throws ResourceNotFoundException if the resource could not be found.
     * @throws InvalidResourceException if the resource is not valid.
     */
    public boolean deleteRepresentation(UUID resourceId, UUID representationId) throws
        ResourceNotFoundException, InvalidResourceException {
        var representations = getAllRepresentations(resourceId);
        if (representations.remove(representationId) != null) {
            var metadata = getMetadata(resourceId);
            metadata.setRepresentations(representations);

            updateResource(resourceId, metadata);
            LOGGER.debug("Deleted resource representation. [resourceId=({}), representationId=({})]",
                resourceId, representationId);
            return true;
        } else {
            LOGGER.debug(
                "Failed to delete resource representation. It does not exist. [resourceId=({}), representationId=({})]",
                resourceId, representationId);
            return false;
        }
    }

    /**
     * Checks if a given offered resource is valid.
     * @param resource the offered resource
     * @return an optional string: empty, if the resource is valid; contains error description otherwise
     */
    public Optional<String> isValidOfferedResource(OfferedResource resource) {
        if (resource == null) {
            return Optional.of("The resource cannot be null.");
        }

        if (resource.getResourceMetadata() == null) {
            return Optional.of("The resource metadata cannot be null.");
        }

        if (resource.getResourceMetadata().getRepresentations() == null) {
            return Optional.of("The resource representation cannot be null.");
        }

        for (ResourceRepresentation representation :
                resource.getResourceMetadata().getRepresentations().values()) {
            BackendSource source = representation.getSource();
            if (source.getType().equals(BackendSource.Type.HTTP_GET)
                    || source.getType().equals(BackendSource.Type.HTTPS_GET)
                    || source.getType().equals(BackendSource.Type.HTTPS_GET_BASICAUTH)) {
                long openingBracesCount = source.getUrl().toString().chars()
                        .filter(ch -> ch == '{').count();
                long closingBracesCount = source.getUrl().toString().chars()
                        .filter(ch -> ch == '}').count();

                if (openingBracesCount != closingBracesCount) {
                    return Optional.of("URL of backend source must contain same number of"
                            + " '{' and '}'");
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Validates an offered resource.
     *
     * @param resource the resource to be validated
     * @throws InvalidResourceException if the resource is not valid.
     */
    private void invalidResourceGuard(OfferedResource resource) throws InvalidResourceException {
        final var error = isValidOfferedResource(resource);
        if (error.isPresent()) {
            LOGGER.debug("Failed resource validation. [error=({}), resource=({})]", error.get(), resource);
            throw new InvalidResourceException("Not a valid resource. " + error.get());
        }
    }

    /**
     * Saves a resource after validating it.
     *
     * @throws InvalidResourceException if the resource is not valid.
     */
    private void storeResource(OfferedResource resource) throws InvalidResourceException {
        invalidResourceGuard(resource);
        offeredResourceRepository.save(resource);
        LOGGER.debug("Made resource persistent. [resource=({})]", resource);
    }

    /**
     * Gets resource data as string.
     *
     * @param resource       the connector resource object.
     * @param representation the representation.
     * @param queryInput Header and params for data request from backend.
     * @return resource data as string
     * @throws ResourceException if the resource source is not defined or source url is
     *                           ill-formatted.
     */
    private String getDataString(OfferedResource resource,
                                 ResourceRepresentation representation,
                                 QueryInput queryInput) throws ResourceException {
        if (representation.getSource() != null) {
            try {
                final var address = representation.getSource().getUrl();
                final var username = representation.getSource().getUsername();
                final var password = representation.getSource().getPassword();

                switch (representation.getSource().getType()) {
                    case LOCAL:
                        return resource.getData();
                    case HTTP_GET:
                        return httpUtils.sendHttpGetRequest(address.toString(), queryInput);
                    case HTTPS_GET:
                        return httpUtils.sendHttpsGetRequest(address.toString(), queryInput);
                    case HTTPS_GET_BASICAUTH:
                        return httpUtils
                            .sendHttpsGetRequestWithBasicAuth(address.toString(), username,
                                password, queryInput);
                    default:
                        // This exception is only thrown when BackendSource.Type is expanded but this
                        // switch is not
                        throw new NotImplementedException("This type is not supported");
                }
            } catch (URISyntaxException exception) {
                // One of the http requests received a non url as address
                LOGGER.debug("Failed to resolve the target address. The resource representation " +
                        "is not a URI. [resource=({}), representation=({}), exception=({}))]",
                        resource, representation, exception);
                throw new ResourceException("The deposited address is not a valid URI.",
                    exception);
            } catch (IllegalArgumentException exception) {
                // Query input was invalid.
                LOGGER.debug("Invalid query input. [resource=({}), representation=({}), " +
                        "exception=({})]", resource, representation, exception);
                throw exception;
            } catch (RuntimeException exception) {
                // One of the http calls encountered problems.
                LOGGER.debug("Failed to establish source connection. [resource=({}), " +
                        "representation=({}), exception=({}))]", resource, representation, exception);
                throw new ResourceException("Failed to retrieve the data.", exception);
            }
        } else {
            LOGGER.debug("Failed to receive the resource. The resource has no defined backend. " +
                    "[resource=({}), representation=({}))]", resource, representation);
            throw new ResourceException("The resource has no defined backend.");
        }
    }
}
