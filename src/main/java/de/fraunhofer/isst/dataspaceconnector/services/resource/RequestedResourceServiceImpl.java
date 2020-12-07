package de.fraunhofer.isst.dataspaceconnector.services.resource;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.services.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * This class implements all methods of {@link de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceService}.
 * It provides database resource handling for all requested resources.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class RequestedResourceServiceImpl implements RequestedResourceService {

    /**
     * Constant <code>LOGGER</code>
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(RequestedResourceServiceImpl.class);

    private final RequestedResourceRepository requestedResourceRepository;
    private final IdsUtils idsUtils;

    private final PolicyHandler policyHandler;

    /**
     * <p>Constructor for RequestedResourceServiceImpl.</p>
     *
     * @param requestedResourceRepository a {@link RequestedResourceRepository} object.
     * @param idsUtils                    a {@link de.fraunhofer.isst.dataspaceconnector.services.IdsUtils}
     *                                    object.
     * @param policyHandler               a {@link PolicyHandler} object.
     */
    @Autowired
    public RequestedResourceServiceImpl(
        @NotNull RequestedResourceRepository requestedResourceRepository,
        @NotNull IdsUtils idsUtils,
        @NotNull PolicyHandler policyHandler) throws IllegalArgumentException {
        if (requestedResourceRepository == null) {
            throw new IllegalArgumentException("The RequestedResourceRepository cannot be null.");
        }

        if (idsUtils == null) {
            throw new IllegalArgumentException("The IdsUtils cannot be null.");
        }

        if (policyHandler == null) {
            throw new IllegalArgumentException("The PolicyHandler cannot be null.");
        }

        this.requestedResourceRepository = requestedResourceRepository;
        this.idsUtils = idsUtils;
        this.policyHandler = policyHandler;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Saves the resources with its metadata as external resource or internal resource.
     */
    @Override
    public UUID addResource(ResourceMetadata resourceMetadata) throws InvalidResourceException {
        final var resource = new RequestedResource(new Date(), new Date(), resourceMetadata, "", 0);

        storeResource(resource);

        return resource.getUuid();
    }

    /**
     * {@inheritDoc}
     * <p>
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
    }

    /**
     * {@inheritDoc}
     * <p>
     * Deletes a resource by id.
     */
    @Override
    public boolean deleteResource(UUID resourceId) {
        requestedResourceRepository.deleteById(resourceId);
        return true;
    }

    /**
     * {@inheritDoc}
     * <p>
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
     * {@inheritDoc}
     * <p>
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
     * {@inheritDoc}
     * <p>
     * Gets resource data by id.
     */
    @Override
    public String getData(UUID resourceId) throws InvalidResourceException,
        ResourceNotFoundException, ResourceException {
        final var resource = getResource(resourceId);
        if (resource == null) {
            throw new ResourceNotFoundException("The resource does not exist.");
        }

        try {
            if (policyHandler.onDataAccess(resource)) {
                final var data = resource.getData();
                storeResource(resource);
                return data;
            } else {
                return "Policy Restriction!";
            }
        } catch (IOException exception) {
            throw new ResourceException("Failed to process the policy data accesss.", exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Resource> getRequestedResources() {
        return getAllResources().parallelStream().map(idsUtils::getAsResource)
            .collect(Collectors.toList());
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

        if (resource.getResourceMetadata().getRepresentations().size() < 1) {
            return Optional.of("The resource representation must have at least one element.");
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
            throw new InvalidResourceException(error.get());
        }
    }

    private void storeResource(RequestedResource resource) throws InvalidResourceException {
        final var error = isValidRequestedResource(resource);
        if (error.isPresent()) {
            throw new InvalidResourceException("Not a valid resource. " + error.get());
        }

        requestedResourceRepository.save(resource);
    }
}
