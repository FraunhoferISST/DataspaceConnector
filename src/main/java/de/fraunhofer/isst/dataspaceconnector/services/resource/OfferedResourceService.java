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
 * <p>OfferedResourceService interface.</p>
 *
 * @version $Id: $Id
 */
public interface OfferedResourceService {

    /**
     * <p>getResourceList.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<Resource> getResourceList();

    /**
     * <p>getOfferedResources.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<UUID, Resource> getOfferedResources();

    /**
     * <p>addResource.</p>
     *
     * @param resourceMetadata a {@link ResourceMetadata} object.
     * @return a {@link java.util.UUID} object.
     * @throws ResourceException - if the resource could not be added.
     */
    UUID addResource(ResourceMetadata resourceMetadata) throws ResourceException;

    /**
     * <p>addResourceWithId.</p>
     *
     * @param resourceMetadata a {@link ResourceMetadata} object.
     * @param uuid             a {@link java.util.UUID} object.
     * @throws ResourceException - if the resource could not be added.
     */
    void addResourceWithId(ResourceMetadata resourceMetadata, UUID uuid) throws ResourceException;

    /**
     * <p>addData.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @param data       a {@link java.lang.String} object.
     * @throws ResourceException - if the resource data could not be added.
     */
    void addData(UUID resourceId, String data) throws ResourceException;

    /**
     * <p>updateResource.</p>
     *
     * @param resourceId       a {@link java.util.UUID} object.
     * @param resourceMetadata a {@link ResourceMetadata} object.
     * @throws ResourceException - if the resource could not be updated.
     */
    void updateResource(UUID resourceId, ResourceMetadata resourceMetadata)
        throws ResourceException;

    /**
     * <p>updateContract.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @param policy     a {@link java.lang.String} object.
     * @throws ResourceException - if the contract could not be updated.
     */
    void updateContract(UUID resourceId, String policy) throws ResourceException;

    /**
     * <p>deleteResource.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @return true if the resource was found and deleted.
     */
    boolean deleteResource(UUID resourceId);

    /**
     * <p>getResource.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @return a {@link OfferedResource} object.
     * @throws ResourceException - if the resource could not be received.
     */
    OfferedResource getResource(UUID resourceId) throws ResourceException;

    /**
     * <p>getMetadata.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @return a {@link ResourceMetadata} object.
     * @throws ResourceException - if the metadata could not be received.
     */
    ResourceMetadata getMetadata(UUID resourceId) throws ResourceException;

    /**
     * <p>getData.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @return a {@link java.lang.String} object.
     * @throws ResourceException - if the resource data could not be received.
     */
    String getData(UUID resourceId) throws ResourceException;

    /**
     * <p>getDataByRepresentation.</p>
     *
     * @param resourceId       a {@link java.util.UUID} object.
     * @param representationId a {@link java.util.UUID} object.
     * @return a {@link java.lang.String} object.
     * @throws ResourceException - if the resource data could not be received.
     */
    String getDataByRepresentation(UUID resourceId, UUID representationId) throws ResourceException;

    /**
     * <p>addRepresentation.</p>
     *
     * @param resourceId     a {@link java.util.UUID} object.
     * @param representation a {@link ResourceRepresentation} object.
     * @return a {@link java.util.UUID} object.
     * @throws ResourceException - if the representation could not be added.
     */
    UUID addRepresentation(UUID resourceId, ResourceRepresentation representation)
        throws ResourceException;

    /**
     * <p>addRepresentation.</p>
     *
     * @param resourceId       a {@link java.util.UUID} object.
     * @param representation   a {@link ResourceRepresentation} object.
     * @param representationId the {@link UUID} that will be used for the new representation
     * @return a {@link java.util.UUID} object.
     * @throws ResourceException - if the representation could not be added.
     */
    UUID addRepresentationWithId(UUID resourceId, ResourceRepresentation representation,
        UUID representationId) throws ResourceException;

    /**
     * <p>updateRepresentation.</p>
     *
     * @param resourceId       a {@link java.util.UUID} object.
     * @param representationId a {@link java.util.UUID} object.
     * @param representation   a {@link ResourceRepresentation} object.
     * @throws ResourceException - if the representation could not be updated.
     */
    void updateRepresentation(UUID resourceId, UUID representationId,
        ResourceRepresentation representation) throws ResourceException;

    /**
     * <p>getRepresentation.</p>
     *
     * @param resourceId       a {@link java.util.UUID} object.
     * @param representationId a {@link java.util.UUID} object.
     * @return a {@link ResourceRepresentation} object.
     * @throws ResourceException - if the representation could not be received.
     */
    ResourceRepresentation getRepresentation(UUID resourceId, UUID representationId)
        throws ResourceException;

    /**
     * <p>deleteRepresentation.</p>
     *
     * @param resourceId       a {@link java.util.UUID} object.
     * @param representationId a {@link java.util.UUID} object.
     * @throws ResourceException - if the resource could not be added.
     */
    boolean deleteRepresentation(UUID resourceId, UUID representationId) throws ResourceException;
}
