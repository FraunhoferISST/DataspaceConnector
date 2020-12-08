package de.fraunhofer.isst.dataspaceconnector.services.resource;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.*;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * OfferedResourceService interface.
 */
public interface OfferedResourceService {

    /**
     * Returns resource list.
     *
     * @return a {@link java.util.List} object.
     */
    List<Resource> getResourceList();

    /**
     * Returns offered resources.
     *
     * @return a {@link java.util.Map} object.
     */
    Map<UUID, Resource> getOfferedResources();

    /**
     * Adds resource.
     *
     * @return a {@link java.util.UUID} object.
     * @throws ResourceException - if the resource could not be added.
     */
    UUID addResource(ResourceMetadata resourceMetadata) throws ResourceException;

    /**
     * Adds resource with id.
     *
     * @throws ResourceException - if the resource could not be added.
     */
    void addResourceWithId(ResourceMetadata resourceMetadata, UUID uuid) throws ResourceException;

    /**
     * Adds data.
     *
     * @throws ResourceException - if the resource data could not be added.
     */
    void addData(UUID resourceId, String data) throws ResourceException;

    /**
     * Updates resource.
     *
     * @throws ResourceException - if the resource could not be updated.
     */
    void updateResource(UUID resourceId, ResourceMetadata resourceMetadata)
        throws ResourceException;

    /**
     * Updates contract.
     *
     * @throws ResourceException - if the contract could not be updated.
     */
    void updateContract(UUID resourceId, String policy) throws ResourceException;

    /**
     * Deletes resource.
     *
     * @return true if the resource was found and deleted.
     */
    boolean deleteResource(UUID resourceId);

    /**
     * Returns resource.
     *
     * @return a {@link OfferedResource} object.
     * @throws ResourceException - if the resource could not be received.
     */
    OfferedResource getResource(UUID resourceId) throws ResourceException;

    /**
     * Gets metadata.
     *
     * @return a {@link ResourceMetadata} object.
     * @throws ResourceException - if the metadata could not be received.
     */
    ResourceMetadata getMetadata(UUID resourceId) throws ResourceException;

    /**
     * Returns data.
     *
     * @return a {@link java.lang.String} object.
     * @throws ResourceException - if the resource data could not be received.
     */
    String getData(UUID resourceId) throws ResourceException;

    /**
     * Returns data by representation.
     *
     * @return a {@link java.lang.String} object.
     * @throws ResourceException - if the resource data could not be received.
     */
    String getDataByRepresentation(UUID resourceId, UUID representationId) throws ResourceException;

    /**
     * Adds representation.
     *
     * @return a {@link java.util.UUID} object.
     * @throws ResourceException - if the representation could not be added.
     */
    UUID addRepresentation(UUID resourceId, ResourceRepresentation representation)
        throws ResourceException;

    /**
     * Adds representation.
     *
     * @return a {@link java.util.UUID} object.
     * @throws ResourceException - if the representation could not be added.
     */
    UUID addRepresentationWithId(UUID resourceId, ResourceRepresentation representation,
        UUID representationId) throws ResourceException;

    /**
     * Updates representation.
     *
     * @throws ResourceException - if the representation could not be updated.
     */
    void updateRepresentation(UUID resourceId, UUID representationId,
        ResourceRepresentation representation) throws ResourceException;

    /**
     * Returns representation.
     *
     * @return a {@link ResourceRepresentation} object.
     * @throws ResourceException - if the representation could not be received.
     */
    ResourceRepresentation getRepresentation(UUID resourceId, UUID representationId)
        throws ResourceException;

    /**
     * Deletes representation.
     *
     * @throws ResourceException - if the resource could not be added.
     */
    boolean deleteRepresentation(UUID resourceId, UUID representationId) throws ResourceException;
}
