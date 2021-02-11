package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceAlreadyExistsException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceMovedException;
import de.fraunhofer.isst.dataspaceconnector.model.BaseDescription;
import de.fraunhofer.isst.dataspaceconnector.model.BaseResource;
import de.fraunhofer.isst.dataspaceconnector.model.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.view.BaseView;
import de.fraunhofer.isst.dataspaceconnector.model.view.BaseViewer;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.BaseService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.EndpointService;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Handles exposing resource functions to controllers.
 *
 * @param <T> The resource type.
 * @param <D> The description for the passed resource type.
 * @param <V> The view type of the passed resource type.
 */
public class CommonService<T extends BaseResource, D extends BaseDescription<T>,
        V extends BaseView<T>> implements FrontFacingService<T, D, V> {

    /**
     * The service for resources.
     **/
    @Autowired
    private BaseService<T, D> resourceService;

    /**
     * The service for endpoints.
     **/
    @Autowired
    private EndpointService endpointService;

    /**
     * The resource to view converter.
     */
    @Autowired
    private BaseViewer<T, V> viewConverter;

    /**
     * Default constructor.
     **/
    protected CommonService() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Create a resource at a passed resource path.
     *
     * @param basePath The path leading to the resource.
     * @param desc     The resource description.
     * @return The endpoint where the new resource is provided.
     */
    @Override
    public EndpointId create(final String basePath, final D desc) {
        final var resource = resourceService.create(desc);

        return endpointService.create(new EndpointId(basePath,
                generateEndpointResourceId(new EndpointId(basePath,
                        desc.getStaticId()))), resource.getId()).getId();
    }

    /**
     * Update a resource provided at a given endpoint.
     *
     * @param endpointId The endpoint of the resource.
     * @param desc       The new updated resource description.
     * @return The endpoint where the updated resource is provided.
     */
    @Override
    public EndpointId update(final EndpointId endpointId, final D desc) {
        final var resource = getResource(endpointId);

        // Update the underlying resource
        resourceService.update(resource.getId(), desc);

        var outputId = endpointId;

        // Move the resource and create new endpoint if necessary
        if (desc.getStaticId() != null
                && !endpointId.getResourceId().equals(desc.getStaticId())) {
            // The resource needs to be moved.
            final var newEndpoint = endpointService.create(
                    new EndpointId(endpointId.getBasePath(),
                            desc.getStaticId()), resource.getId());

            // Mark the old resource as moved
            endpointService.update(endpointId, newEndpoint.getId());

            outputId = newEndpoint.getId();
        }

        return outputId;
    }

    /**
     * Get the resource at a given endpoint.
     *
     * @param endpointId The endpoint of the resource.
     * @return The resource.
     */
    @Override
    public V get(final EndpointId endpointId) {
        final var resource = getResource(endpointId);
        return viewConverter.create(resource);
    }

    /**
     * Get the resource to an endpoint.
     *
     * @param endpointId The id of the endpoint.
     * @return The resource.
     */
    protected T getResource(final EndpointId endpointId) {
        final var endpoint = getEndpoint(endpointId);

        if (endpoint.getInternalId() == null) {
            // Handle with global exception handler
            throw new ResourceMovedException(endpoint.getNewLocation());
        } else {
            return resourceService.get(endpoint.getInternalId());
        }
    }


    /**
     * Get the endpoint for a given id.
     *
     * @param endpointId The id of the endpoint.
     * @return The endpoint.
     */
    @Override
    public Endpoint getEndpoint(final EndpointId endpointId) {
        // TODO Is this function really needed? Should it be provided?
        return endpointService.get(endpointId);
    }

    /**
     * Get all available endpoints.
     *
     * @return All endpoints.
     */
    @Override
    public Set<EndpointId> getAll() {
        // TODO: Find by the current endpoint call scope. It contains the
        //  basepath making the resource service obsolete

        final var allResources = resourceService.getAll();
        final var allEndpoints = new HashSet<EndpointId>();

        for (final var resource : allResources) {
            allEndpoints.addAll(endpointService.getByEntity(resource));
        }

        return allEndpoints;
    }

    /**
     * Checks if an endpoint exists.
     *
     * @param endpointId The endpoint.
     * @return True if the endpoint exists.
     */
    @Override
    public boolean doesExist(final EndpointId endpointId) {
        return endpointService.doesExist(endpointId);
    }

    /**
     * Delete a resource.
     *
     * @param endpointId The endpoint id of the resource.
     */
    @Override
    public void delete(final EndpointId endpointId) {
        final var endpoint = getEndpoint(endpointId);

        // Remove the endpoint first to prevent further access
        endpointService.delete(endpointId);

        if (!EndpointService.isRedirect(endpoint)) {
            resourceService.delete(endpoint.getInternalId());
        }
    }

    private UUID generateEndpointResourceId(final EndpointId endpointId) {
        // TODO: FIX ME
        // TODO what happends when basePath is not set
        UUID generatedId;
        if (endpointId.getResourceId() == null) {
            // No endpoint hint
            generatedId = UUIDUtils.createUUID(x ->
                    doesExist(new EndpointId(endpointId.getBasePath(), x)));
        } else {
            if (doesExist(endpointId)) {
                throw new ResourceAlreadyExistsException(endpointId.toString());
            }

            // Preferred endpoint available
            generatedId = endpointId.getResourceId();
        }

        return generatedId;
    }

    protected BaseService<T, D>  getService() {
        return resourceService;
    }

    protected EndpointService getEndpointService() {
        return endpointService;
    }
}
