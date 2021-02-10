package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.model.ConnectorResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;

import java.util.List;
import java.util.UUID;

/**
 * RequestedResourceService interface. Contains methods for performing CRUD operations on resources.
 */
public interface ResourceService {

    /**
     * Adds resource.
     *
     * @throws ResourceException if the resource could not be added.
     */
    UUID addResource(ResourceMetadata metadata) throws ResourceException;

    /**
     * Adds data.
     *
     * @throws ResourceException if the data could not be added.
     */
    void addData(UUID id, String data) throws ResourceException;

    /**
     * Deletes resource.
     *
     * @return true if the resource could be found and be deleted.
     */
    boolean deleteResource(UUID id);

    /**
     * Finds resource by ID
     *
     * @throws ResourceException if the resource could not be found.
     */
    ConnectorResource getResource(UUID id) throws ResourceException;

    /**
     * Finds metadata by ID.
     *
     * @throws ResourceException if the metadata could not be found.
     */
    ResourceMetadata getMetadata(UUID id) throws ResourceException;

    /**
     * Finds data by ID.
     *
     * @throws ResourceException if the data could not be retrieved.
     */
    String getData(UUID id) throws ResourceException;

    /**
     * Returns all resources as list.
     *
     * @return a list of resources.
     */
    List<Resource> getResources();

    /**
     * Returns data by representation.
     *
     * @param resourceId ID of the resource.
     * @param representationId ID of the representation.
     * @param queryInput Header and params for data request from backend.
     * @return resource data as string.
     * @throws ResourceException if the resource data could not be retrieved.
     */
    String getDataByRepresentation(UUID resourceId, UUID representationId, QueryInput queryInput)
            throws ResourceException;

    /**
     * Finds representation by ID.
     *
     * @param resourceId ID of the resource.
     * @param representationId ID of the representation.
     * @return a {@link ResourceRepresentation} object.
     * @throws ResourceException if the representation could not be retrieved.
     */
    ResourceRepresentation getRepresentation(UUID resourceId, UUID representationId)
        throws ResourceException;
}
