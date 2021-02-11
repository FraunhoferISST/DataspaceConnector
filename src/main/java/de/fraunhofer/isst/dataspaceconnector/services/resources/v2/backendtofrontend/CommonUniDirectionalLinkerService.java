package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceMovedException;
import de.fraunhofer.isst.dataspaceconnector.model.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.BaseUniDirectionalLinkerService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Handles exposing relationship functions between resources to controllers.
 * @param <S> The service for the internal resource logic handling.
 */
public class CommonUniDirectionalLinkerService<
        S extends BaseUniDirectionalLinkerService<?, ?, ?, ?>> {

    /**
     * The service for linking children to an entity.
     **/
    @Autowired
    private S linkerService;

    /**
     * The service for endpoints.
     **/
    @Autowired
    private EndpointService endpointService;

    /**
     * Default constructor.
     */
    protected CommonUniDirectionalLinkerService() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Get all endpoints pointing to child resources of the resource assigned
     * to this endpoint.
     *
     * @param endpointId The endpoint which child resource should be found.
     * @return The list of endpoints pointing to the child resources.
     */
    public Set<EndpointId> get(final EndpointId endpointId) {
        final var endpoint = getResourceEndpoint(endpointId);

        // Get the list of children related to the entity
        final var childrenEntityIds =
                linkerService.get(getInternalId(endpoint));

        // Find all endpoints pointing to any of the children
        final var allChildrenEndpoints = new HashSet<EndpointId>();
        for (final var entityId : childrenEntityIds) {
            allChildrenEndpoints.addAll(endpointService.getByEntity(entityId));
        }

        return allChildrenEndpoints;
    }

    /**
     * Add resources as children to another resource.
     *
     * @param ownerEndpointId     The id of resource that the resources
     *                            should be
     *                            assigned to.
     * @param childrenEndpointIds The id of the resources that should be
     *                            assigned as children.
     */
    public void add(final EndpointId ownerEndpointId,
                    final Set<EndpointId> childrenEndpointIds) {
        // Get the internal ids of children
        final var internalChildIds = findInternalIds(childrenEndpointIds);
        if (internalChildIds.size() == 0) {
            // TODO add logger for information on empty update
            // Do not proceed when no children are left
            return;
        }

        // Update resources
        final var ownerEndpoint = getResourceEndpoint(ownerEndpointId);
        linkerService.add(getInternalId(ownerEndpoint), internalChildIds);
    }


    /**
     * Remove resources as children from another resource.
     *
     * @param ownerEndpointId     The id of resource that the resources
     *                            should be
     *                            removed from.
     * @param childrenEndpointIds The id of the resources that should be
     *                            removed as children.
     */
    public void remove(final EndpointId ownerEndpointId,
                       final Set<EndpointId> childrenEndpointIds) {
        // Get the internal ids of children
        final var internalChildIds = findInternalIds(childrenEndpointIds);
        if (internalChildIds.size() == 0) {
            // TODO add logger for information on empty update
            // Do not proceed when no children are left
            return;
        }

        // Update resources
        final var ownerEndpoint = getResourceEndpoint(ownerEndpointId);
        linkerService.remove(getInternalId(ownerEndpoint), internalChildIds);
    }

    /**
     * Replace resources as children from another resource.
     *
     * @param ownerEndpointId     The id of resource that the resources
     *                            should be
     *                            replaces at.
     * @param childrenEndpointIds The id of the new resources.
     */
    public void replace(final EndpointId ownerEndpointId,
                        final Set<EndpointId> childrenEndpointIds) {
        // Get the internal ids of children
        final var internalChildIds = findInternalIds(childrenEndpointIds);
        if (internalChildIds.size() == 0) {
            // TODO add logger for information on empty update
            // Do not proceed when no children are left
            return;
        }

        // Update resources
        final var ownerEndpoint = getResourceEndpoint(ownerEndpointId);
        linkerService.replace(getInternalId(ownerEndpoint), internalChildIds);
    }

    /**
     * Find the internal ids to a list of endpoint ids.
     * This functions filters out redirections.
     *
     * @param ids The ids of the endpoints.
     * @return The internal ids of entities.
     */
    private Set<UUID> findInternalIds(final Set<EndpointId> ids) {
        final var internalIds = new HashSet<UUID>();

        // Find the internal ids of the children's endpoints
        for (final var id : ids) {
            if (endpointService.doesExist(id)) {
                // Only add endpoints pointing to resources
                final var childEndpoint = endpointService.get(id);
                if (!EndpointService.isRedirect(childEndpoint)) {
                    internalIds.add(getInternalId(childEndpoint));
                }
            }
        }

        return internalIds;
    }

    private UUID getInternalId(final Endpoint endpoint) {
        return endpoint.getInternalId();
    }

    /**
     * Get an resource endpoint.
     *
     * @param endpointId The id of the endpoint.
     * @return The endpoint.
     */
    private Endpoint getResourceEndpoint(final EndpointId endpointId) {
        final var endpoint = endpointService.get(endpointId);
        if (EndpointService.isRedirect(endpoint)) {
            // Reject relationship operations on redirections
            throw new ResourceMovedException(endpoint);
        }

        return endpoint;
    }

}
