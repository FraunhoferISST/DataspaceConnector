package de.fraunhofer.isst.dataspaceconnector.services.resource;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.*;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * <p>OfferedResourceService interface.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
public interface OfferedResourceService {
    /**
     * <p>getResourceList.</p>
     *
     * @return a {@link java.util.List} object.
     */
    ArrayList<Resource> getResourceList();

    /**
     * <p>getOfferedResources.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<UUID, Resource> getOfferedResources();

    /**
     * <p>addResource.</p>
     *
     * @param resourceMetadata a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata} object.
     * @return a {@link java.util.UUID} object.
     */
    UUID addResource(ResourceMetadata resourceMetadata) throws ResourceException;

    /**
     * <p>addResourceWithId.</p>
     *
     * @param resourceMetadata a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata} object.
     * @param uuid a {@link java.util.UUID} object.
     */
    void addResourceWithId(ResourceMetadata resourceMetadata, UUID uuid) throws InvalidResourceException, ResourceAlreadyExists;

    /**
     * <p>addData.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @param data a {@link java.lang.String} object.
     */
    void addData(UUID resourceId, String data) throws ResourceException;

    /**
     * <p>updateResource.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @param resourceMetadata a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata} object.
     */
    void updateResource(UUID resourceId, ResourceMetadata resourceMetadata) throws InvalidResourceException, ResourceNotFoundException  ;

    /**
     * <p>updateContract.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @param policy a {@link java.lang.String} object.
     */
    void updateContract(UUID resourceId, String policy) throws ResourceNotFoundException,
            InvalidResourceException;

    /**
     * <p>deleteResource.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     */
    boolean deleteResource(UUID resourceId);

    /**
     * <p>getResource.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @return a {@link de.fraunhofer.isst.dataspaceconnector.model.OfferedResource} object.
     */
    OfferedResource getResource(UUID resourceId) throws InvalidResourceException;

    /**
     * <p>getMetadata.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @return a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata} object.
     */
    ResourceMetadata getMetadata(UUID resourceId) throws ResourceNotFoundException,
            InvalidResourceException;

    /**
     * <p>getData.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @return a {@link java.lang.String} object.
     * @throws java.lang.Exception if any.
     */
    String getData(UUID resourceId) throws ResourceNotFoundException, InvalidResourceException,
            ResourceException;

    /**
     * <p>getDataByRepresentation.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @param representationId a {@link java.util.UUID} object.
     * @return a {@link java.lang.String} object.
     * @throws java.lang.Exception if any.
     */
    String getDataByRepresentation(UUID resourceId, UUID representationId) throws InvalidResourceException, ResourceNotFoundException, ResourceException;

    /**
     * <p>addRepresentation.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @param representation a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation} object.
     * @return a {@link java.util.UUID} object.
     */
    UUID addRepresentation(UUID resourceId, ResourceRepresentation representation) throws
            ResourceNotFoundException, InvalidResourceException, ResourceAlreadyExists;

    /**
     * <p>addRepresentation.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @param representation a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation} object.
     * @param representationId the {@link UUID} that will be used for the new representation
     * @return a {@link java.util.UUID} object.
     */
    UUID addRepresentationWithId(UUID resourceId, ResourceRepresentation representation,
                                 UUID representationId) throws
            ResourceNotFoundException, InvalidResourceException, ResourceAlreadyExists;

    /**
     * <p>updateRepresentation.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @param representationId a {@link java.util.UUID} object.
     * @param representation a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation} object.
     */
    void updateRepresentation(UUID resourceId, UUID representationId,
                              ResourceRepresentation representation) throws
            ResourceNotFoundException, InvalidResourceException;

    /**
     * <p>getRepresentation.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @param representationId a {@link java.util.UUID} object.
     * @return a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation} object.
     */
    ResourceRepresentation getRepresentation(UUID resourceId, UUID representationId) throws ResourceNotFoundException, InvalidResourceException;

    /**
     * <p>deleteRepresentation.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @param representationId a {@link java.util.UUID} object.
     */
    boolean deleteRepresentation(UUID resourceId, UUID representationId) throws ResourceNotFoundException, InvalidResourceException;
}
