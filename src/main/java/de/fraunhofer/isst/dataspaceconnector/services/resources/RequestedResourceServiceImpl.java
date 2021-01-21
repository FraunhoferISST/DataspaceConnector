package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.OperationNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.repositories.RequestedResourceRepository;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class implements all methods of {@link ResourceService}.
 * It provides methods for performing the CRUD operations for requested resources.
 */
@Service
public class RequestedResourceServiceImpl implements ResourceService {

    public static final Logger LOGGER = LoggerFactory.getLogger(RequestedResourceServiceImpl.class);

    private final RequestedResourceRepository requestedResourceRepository;
    private final IdsUtils idsUtils;
    private final PolicyHandler policyHandler;

    /**
     * Constructor for RequestedResourceServiceImpl.
     *
     * @throws IllegalArgumentException - if any of the parameters is null.
     */
    @Autowired
    public RequestedResourceServiceImpl(RequestedResourceRepository requestedResourceRepository,
        IdsUtils idsUtils, PolicyHandler policyHandler) throws IllegalArgumentException {
        if (requestedResourceRepository == null)
            throw new IllegalArgumentException("The RequestedResourceRepository cannot be null.");

        if (idsUtils == null)
            throw new IllegalArgumentException("The IdsUtils cannot be null.");

        if (policyHandler == null)
            throw new IllegalArgumentException("The PolicyHandler cannot be null.");

        this.requestedResourceRepository = requestedResourceRepository;
        this.idsUtils = idsUtils;
        this.policyHandler = policyHandler;
    }

    /**
     * Saves the resource with its metadata.
     *
     * @param resourceMetadata the resource's metadata.
     * @return the UUID of the newly created resource.
     * @throws InvalidResourceException - if the resource is not valid.
     */
    @Override
    public UUID addResource(ResourceMetadata resourceMetadata) throws InvalidResourceException {
        final var resource = new RequestedResource(new Date(), new Date(), resourceMetadata, "", 0);

        storeResource(resource);

        LOGGER.debug("Added a new resource. [resource=({})]", resource);
        return resource.getUuid();
    }

    /**
     * Publishes resource data by ID.
     *
     * @param resourceId ID of the resource
     * @param data data as string
     * @throws ResourceNotFoundException - if the resource could not be found
     * @throws InvalidResourceException - if the resource is invalid
     */
    @Override
    public void addData(UUID resourceId, String data) throws ResourceNotFoundException,
        InvalidResourceException {
        final var resource = getResource(resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("The resource does not exist.");
        }

        resource.setData(data);

        storeResource(resource);
        LOGGER.debug("Added data to resource. [resourceId=({}), data=({})]", resourceId, data);
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
                requestedResourceRepository.deleteById(resourceId);
                LOGGER.debug("Deleted resource. [resourceId=({})]", resourceId);
                return true;
            }
        } catch(InvalidResourceException exception){
            // The resource exists, delete it
            requestedResourceRepository.deleteById(resourceId);
            LOGGER.debug("Deleted resource. [resourceId=({})]", resourceId);
            return true;
        }

        return false;
    }

    /**
     * Gets a resource by ID.
     *
     * @param resourceId ID of the resource
     * @return the resource
     */
    @Override
    public RequestedResource getResource(UUID resourceId) throws InvalidResourceException {
        final var resource = requestedResourceRepository.findById(resourceId);

        if (resource.isEmpty()) {
            return null;
        } else {
            invalidResourceGuard(resource.get());
            return resource.get();
        }
    }

    public List<RequestedResource> getAllResources() {
        return requestedResourceRepository.findAll();
    }

    /**
     * Gets resource metadata by ID.
     *
     * @param resourceId ID of the resource
     * @return the metadata
     * @throws ResourceNotFoundException - if the resource could not be found
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
     * Gets resource data by ID.
     *
     * @param resourceId ID of the resource
     * @return the data
     * @throws ResourceNotFoundException - if the resource could not be found.
     * @throws ResourceException - if the data could not be retrieved.
     * @throws ContractException - if the policy could not be parsed or the policy pattern in not supported.
     */
    @Override
    public String getData(UUID resourceId) throws ResourceNotFoundException,
        ResourceException, ContractException {
        final var resource = getResource(resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("The resource does not exist.");
        }

        if (policyHandler.onDataAccess(resource)) {
            final var data = resource.getData();
            storeResource(resource);
            return data;
        } else {
            LOGGER.debug("Failed to access the resource. The resource is policy restricted. [resourceId=({})]", resourceId);
            return "Policy Restriction!";
        }
    }

    /**
     * Returns all requested resources as a list.
     */
    @Override
    public List<Resource> getResources() {
        return getAllResources().parallelStream().map(idsUtils::getAsResource)
            .collect(Collectors.toList());
    }

    /**
     * Gets data from the local database or an external data source.
     *
     * @param resourceId ID of the resource
     * @param representationId ID of the representation
     * @return resource data as string
     * @throws OperationNotSupportedException - always
     */
    @Override
    public String getDataByRepresentation(UUID resourceId, UUID representationId) throws
        OperationNotSupportedException {
        throw new OperationNotSupportedException("Operation not supported.");
    }

    /**
     * Gets a resource representation by ID.
     *
     * @param resourceId ID of the resource
     * @param representationId ID of the representation
     * @return the representation
     * @throws OperationNotSupportedException - always
     */
    @Override
    public ResourceRepresentation getRepresentation(UUID resourceId, UUID representationId)
        throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Operation not supported.");
    }

    /**
     * Checks if a given requested resource is valid.
     * @param resource the requested resource
     * @return an optional string: empty, if the resource is valid; contains error description otherwise
     */
    public Optional<String> isValidRequestedResource(RequestedResource resource) {
        if (resource == null) {
            return Optional.of("The resource cannot be null.");
        }

        if (resource.getResourceMetadata() == null) {
            return Optional.of("The resource metadata cannot be null.");
        }

        if (resource.getResourceMetadata().getRepresentations() == null) {
            return Optional.of("The resource representation cannot be null.");
        }

        return Optional.empty();
    }

    /**
     * Validates a requested resource.
     *
     * @param resource the resource to be validated
     * @throws InvalidResourceException - if the resource is not valid.
     */
    private void invalidResourceGuard(RequestedResource resource) throws InvalidResourceException {
        final var error = isValidRequestedResource(resource);
        if (error.isPresent()) {
            LOGGER.debug("Failed resource validation. [error=({}), resource=({})]", error.get(), resource);
            throw new InvalidResourceException(error.get());
        }
    }

    private void storeResource(RequestedResource resource) throws InvalidResourceException {
        invalidResourceGuard(resource);
        requestedResourceRepository.save(resource);
        LOGGER.debug("Made resource persistent. [resource=({})]", resource);
    }
}
