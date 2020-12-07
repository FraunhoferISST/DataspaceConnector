package de.fraunhofer.isst.dataspaceconnector.services.resource;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;

import java.util.ArrayList;
import java.util.UUID;

/**
 * <p>RequestedResourceService interface.</p>
 *
 * @version $Id: $Id
 */
public interface RequestedResourceService {

    /**
     * <p>addResource.</p>
     *
     * @param resourceMetadata a {@link ResourceMetadata} object.
     * @return a {@link java.util.UUID} object.
     * @throws ResourceException - if the resource could not be added.
     */
    UUID addResource(ResourceMetadata resourceMetadata) throws ResourceException;

    /**
     * <p>addData.</p>
     *
     * @param id   a {@link java.util.UUID} object.
     * @param data a {@link java.lang.String} object.
     * @throws ResourceException - if the data could not be added.
     */
    void addData(UUID id, String data) throws ResourceException;

    /**
     * <p>deleteResource.</p>
     *
     * @param id a {@link java.util.UUID} object.
     * @return true if the resource could be found and be deleted.
     */
    boolean deleteResource(UUID id);

    /**
     * <p>getResource.</p>
     *
     * @param id a {@link java.util.UUID} object.
     * @return a {@link RequestedResource} object.
     * @throws ResourceException - if the resource could not be found.
     */
    RequestedResource getResource(UUID id) throws ResourceException;

    /**
     * <p>getMetadata.</p>
     *
     * @param id a {@link java.util.UUID} object.
     * @return a {@link ResourceMetadata} object.
     * @throws ResourceException - if the metadata could not be found.
     */
    ResourceMetadata getMetadata(UUID id) throws ResourceException;

    /**
     * <p>getData.</p>
     *
     * @param id a {@link java.util.UUID} object.
     * @return a {@link java.lang.String} object.
     * @throws ResourceException - if the data could not be received.
     */
    String getData(UUID id) throws ResourceException;

    /**
     * <p>getRequestedResources.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    ArrayList<Resource> getRequestedResources();
}
