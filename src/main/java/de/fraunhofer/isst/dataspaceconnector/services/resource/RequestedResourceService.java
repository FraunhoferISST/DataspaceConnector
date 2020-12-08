package de.fraunhofer.isst.dataspaceconnector.services.resource;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;

import java.util.List;
import java.util.UUID;

/**
 * RequestedResourceService interface.
 */
public interface RequestedResourceService {

    /**
     * Adds resource.
     *
     * @throws ResourceException - if the resource could not be added.
     */
    UUID addResource(ResourceMetadata resourceMetadata) throws ResourceException;

    /**
     * Adds data.
     *
     * @throws ResourceException - if the data could not be added.
     */
    void addData(UUID id, String data) throws ResourceException;

    /**
     * Deletes resource.
     *
     * @return true if the resource could be found and be deleted.
     */
    boolean deleteResource(UUID id);

    /**
     * Returns resource
     *
     * @throws ResourceException - if the resource could not be found.
     */
    RequestedResource getResource(UUID id) throws ResourceException;

    /**
     * Returns metadata.
     *
     * @throws ResourceException - if the metadata could not be found.
     */
    ResourceMetadata getMetadata(UUID id) throws ResourceException;

    /**
     * Returns data.
     *
     * @throws ResourceException - if the data could not be received.
     */
    String getData(UUID id) throws ResourceException;

    /**
     * Returns requested resources as list.
     *
     * @return a list of resources.
     */
    List<Resource> getRequestedResources();
}
