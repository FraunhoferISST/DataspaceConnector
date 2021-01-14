package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.iais.eis.Resource;
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
import java.io.IOException;
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
 * It provides database resource handling for all requested resources.
 */
@Service
public class RequestedResourceServiceImpl implements ResourceService {

    public static final Logger LOGGER = LoggerFactory.getLogger(RequestedResourceServiceImpl.class);

    private final RequestedResourceRepository requestedResourceRepository;
    private final IdsUtils idsUtils;
    private final PolicyHandler policyHandler;

    /**
     * Constructor for RequestedResourceServiceImpl.
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
     * Saves the resources with its metadata as external resource or internal resource.
     */
    @Override
    public UUID addResource(ResourceMetadata resourceMetadata) throws InvalidResourceException {
        final var resource = new RequestedResource(new Date(), new Date(), resourceMetadata, "", 0);

        storeResource(resource);

        LOGGER.debug("Added a new resource. [resource=({})]", resource);
        return resource.getUuid();
    }

    /**
     * Publishes the resource data.
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
     * Deletes a resource by id.
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
     * Gets a resource by id.
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
     * Gets resource metadata by id.
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
     * Gets resource data by id.
     */
    @Override
    public String getData(UUID resourceId) throws InvalidResourceException,
        ResourceNotFoundException, ResourceException {
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
     * {@inheritDoc}
     */
    @Override
    public List<Resource> getResources() {
        return getAllResources().parallelStream().map(idsUtils::getAsResource)
            .collect(Collectors.toList());
    }

    /**
     * Gets data from local or external data source.
     */
    @Override
    public String getDataByRepresentation(UUID resourceId, UUID representationId) throws
        OperationNotSupportedException {
        throw new OperationNotSupportedException("Operation not supported.");
    }

    @Override
    public ResourceRepresentation getRepresentation(UUID resourceId, UUID representationId)
        throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Operation not supported.");
    }

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
     * @param resource
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
