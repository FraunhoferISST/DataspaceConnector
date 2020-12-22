package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.model.ConnectorResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import java.util.List;
import java.util.UUID;

/**
 * RequestedResourceService interface.
 */
public interface ResourceService {

    /**
     * Adds resource.
     *
     * @throws ResourceException - if the resource could not be added.
     */
    UUID addResource(ResourceMetadata metadata) throws ResourceException;

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
    ConnectorResource getResource(UUID id) throws ResourceException;

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
    List<Resource> getResources();

    /**
     * Returns data by representation.
     *
     * @return a {@link java.lang.String} object.
     * @throws ResourceException - if the resource data could not be received.
     */
    String getDataByRepresentation(UUID resourceId, UUID representationId) throws ResourceException;

    /**
     * Returns representation.
     *
     * @return a {@link ResourceRepresentation} object.
     * @throws ResourceException - if the representation could not be received.
     */
    ResourceRepresentation getRepresentation(UUID resourceId, UUID representationId)
        throws ResourceException;
}
