package de.fraunhofer.isst.dataspaceconnector.services.resource;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceTypeException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.services.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * This class implements all methods of {@link de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceService}. It provides database resource handling for all requested resources.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class RequestedResourceServiceImpl implements RequestedResourceService {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(RequestedResourceServiceImpl.class);

    private final RequestedResourceRepository requestedResourceRepository;
    private final IdsUtils idsUtils;

    private final Map<UUID, Resource> requestedResources;
    private final PolicyHandler policyHandler;

    /**
     * <p>Constructor for RequestedResourceServiceImpl.</p>
     *
     * @param requestedResourceRepository a {@link RequestedResourceRepository} object.
     * @param idsUtils a {@link de.fraunhofer.isst.dataspaceconnector.services.IdsUtils} object.
     * @param policyHandler a {@link PolicyHandler} object.
     */
    @Autowired
    public RequestedResourceServiceImpl(@NotNull RequestedResourceRepository requestedResourceRepository,
                                        @NotNull IdsUtils idsUtils,
                                        @NotNull PolicyHandler policyHandler) throws IllegalArgumentException{
        if(requestedResourceRepository == null)
            throw new IllegalArgumentException("The RequestedResourceRepository cannot be null.");

        if(idsUtils == null)
            throw new IllegalArgumentException("The IdsUtils cannot be null.");

        if(policyHandler == null)
            throw new IllegalArgumentException("The PolicyHandler cannot be null.");

        this.requestedResourceRepository = requestedResourceRepository;
        this.idsUtils = idsUtils;
        this.policyHandler = policyHandler;

        // NOTE: This wont scale
        requestedResources = new HashMap<>();
        for (RequestedResource resource : requestedResourceRepository.findAll()) {
            requestedResources.put(resource.getUuid(), idsUtils.getAsResource(resource));
        }
    }

    /**
     * {@inheritDoc}
     *
     * Saves the resources with its metadata as external resource or internal resource.
     */
    @Override
    public UUID addResource(ResourceMetadata resourceMetadata) throws ResourceException {
        final var resource = new RequestedResource(new Date(), new Date(), resourceMetadata, "", 0);

        storeResource(resource);

        return resource.getUuid();
    }

    /**
     * {@inheritDoc}
     *
     * Publishes the resource data.
     */
    @Override
    public void addData(UUID resourceId, String data) throws ResourceNotFoundException,
            ResourceTypeException,
            ResourceException{
        final var resource = getResource(resourceId);
        if(resource == null)
            throw new ResourceNotFoundException("The resource does not exist.");

        resource.setData(data);

        storeResource(resource);
    }

    /**
     * {@inheritDoc}
     *
     * Deletes a resource by id.
     */
    @Override
    public void deleteResource(UUID resourceId) {
        final var key = requestedResources.remove(resourceId);
        if (key != null) {
            requestedResourceRepository.deleteById(resourceId);
        } else {
            LOGGER.warn("Tried to delete resource that does not exist.");
        }
    }

    /**
     * {@inheritDoc}
     *
     * Gets a resource by id.
     */
    @Override
    public RequestedResource getResource(UUID resourceId) throws ResourceTypeException{
        final var resource = requestedResourceRepository.findById(resourceId);

        if (resource.isEmpty()) {
            return null;
        } else {
            final var result = isValidResource(resource.get());
            if (result.isPresent())
                throw new ResourceTypeException("The resource is not valid. " + result.get());

            return resource.get();
        }
    }

    /**
     * {@inheritDoc}
     *
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

    /**
     * {@inheritDoc}
     *
     * Gets resource data by id.
     */
    @Override
    public String getData(UUID resourceId) throws ResourceNotFoundException,
            ResourceTypeException, ResourceException, IOException {
        final var resource = getResource(resourceId);
        if (resource == null)
            throw new ResourceNotFoundException("This resource does not exist.");

        if (policyHandler.onDataAccess(resource)) {
            final var data = resource.getData();
            try {
                storeResource(resource);
            }catch(ResourceException exception){
                throw new ResourceException("Failed to make the data access persistent.",
                        exception);
            }
            return data;
        } else {
            return "Policy Restriction!";
        }
    }

    /** {@inheritDoc} */
    @Override
    public ArrayList<Resource> getRequestedResources() {
        return new ArrayList<>(requestedResources.values());
    }

    public Optional<String> isValidResource(RequestedResource resource) {
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

    private void storeResource(RequestedResource resource) throws ResourceException {
        final var result = isValidResource(resource);
        if(result.isPresent())
            throw new ResourceException("Not a valid resource. " + result.get());

        requestedResourceRepository.save(resource);
        requestedResources.put(resource.getUuid(), idsUtils.getAsResource(resource));
    }
}
