package de.fraunhofer.isst.dataspaceconnector.services.resource;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * <p>RequestedResourceService interface.</p>
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
public interface RequestedResourceService {
    /**
     * <p>addResource.</p>
     *
     * @param resourceMetadata a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata} object.
     * @return a {@link java.util.UUID} object.
     */
    UUID addResource(ResourceMetadata resourceMetadata) throws ResourceException;

    /**
     * <p>addData.</p>
     *
     * @param id a {@link java.util.UUID} object.
     * @param data a {@link java.lang.String} object.
     */
    void addData(UUID id, String data) throws ResourceNotFoundException, InvalidResourceException;

    /**
     * <p>deleteResource.</p>
     *
     * @param id a {@link java.util.UUID} object.
     */
    boolean deleteResource(UUID id);

    /**
     * <p>getResource.</p>
     *
     * @param id a {@link java.util.UUID} object.
     * @return a {@link de.fraunhofer.isst.dataspaceconnector.model.RequestedResource} object.
     */
    RequestedResource getResource(UUID id) throws InvalidResourceException;

    /**
     * <p>getMetadata.</p>
     *
     * @param id a {@link java.util.UUID} object.
     * @return a {@link de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata} object.
     */
    ResourceMetadata getMetadata(UUID id) throws ResourceNotFoundException,
            InvalidResourceException;

    /**
     * <p>getData.</p>
     *
     * @param id a {@link java.util.UUID} object.
     * @return a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    String getData(UUID id) throws  InvalidResourceException, ResourceNotFoundException,
            ResourceException;

    /**
     * <p>getRequestedResources.</p>
     *
     * @return a {@link java.util.ArrayList} object.
     */
    ArrayList<Resource> getRequestedResources();
}
