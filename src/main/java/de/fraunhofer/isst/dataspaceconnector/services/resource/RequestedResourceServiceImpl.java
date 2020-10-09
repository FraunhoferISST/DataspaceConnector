package de.fraunhofer.isst.dataspaceconnector.services.resource;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.services.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
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

    private RequestedResourceRepository requestedResourceRepository;
    private IdsUtils idsUtils;

    private Map<UUID, Resource> requestedResources;
    private PolicyHandler policyHandler;

    @Autowired
    /**
     * <p>Constructor for RequestedResourceServiceImpl.</p>
     *
     * @param requestedResourceRepository a {@link de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceRepository} object.
     * @param idsUtils a {@link de.fraunhofer.isst.dataspaceconnector.services.IdsUtils} object.
     * @param policyHandler a {@link de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler} object.
     */
    public RequestedResourceServiceImpl(RequestedResourceRepository requestedResourceRepository, IdsUtils idsUtils, PolicyHandler policyHandler) {
        this.requestedResourceRepository = requestedResourceRepository;
        this.idsUtils = idsUtils;
        this.policyHandler = policyHandler;

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
    public UUID addResource(ResourceMetadata resourceMetadata) {
        RequestedResource resource = new RequestedResource(new Date(), new Date(), resourceMetadata, "", 0);

        requestedResourceRepository.save(resource);
        requestedResources.put(resource.getUuid(), idsUtils.getAsResource(resource));

        return resource.getUuid();
    }

    /**
     * {@inheritDoc}
     *
     * Publishes the resource data.
     */
    @Override
    public void addData(UUID resourceId, String data) {
        RequestedResource resource = requestedResourceRepository.getOne(resourceId);

        resource.setData(data);
        resource.setModified(new Date());

        requestedResourceRepository.save(resource);
    }

    /**
     * {@inheritDoc}
     *
     * Deletes a resource by id.
     */
    @Override
    public void deleteResource(UUID resourceId) {
        requestedResourceRepository.deleteById(resourceId);
        requestedResources.remove(resourceId);
    }

    /**
     * {@inheritDoc}
     *
     * Gets a resource by id.
     */
    @Override
    public RequestedResource getResource(UUID resourceId) {
        return requestedResourceRepository.getOne(resourceId);
    }

    /**
     * {@inheritDoc}
     *
     * Gets resource metadata by id.
     */
    @Override
    public ResourceMetadata getMetadata(UUID resourceId) {
        return requestedResourceRepository.getOne(resourceId).getResourceMetadata();
    }

    /**
     * {@inheritDoc}
     *
     * Gets resource data by id.
     */
    @Override
    public String getData(UUID resourceId) throws IOException {
        RequestedResource resource = requestedResourceRepository.getOne(resourceId);
        int counter = resource.getAccessed();

        resource.setAccessed(counter + 1);
        requestedResourceRepository.save(resource);

        if (policyHandler.onDataAccess(resource)) {
            return requestedResourceRepository.getOne(resourceId).getData();
        } else {
            return "Policy Restriction!";
        }
    }

    /** {@inheritDoc} */
    @Override
    public ArrayList<Resource> getRequestedResources() {
        return new ArrayList<>(requestedResources.values());
    }
}
