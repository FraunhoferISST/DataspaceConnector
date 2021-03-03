package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceAlreadyExistsException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.handler.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.repositories.EndpointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Offers base logic for endpoints such lookup for internal ids.
 */
@Service
public final class EndpointService {

    /**
     * Persists all endpoints.
     **/
    @Autowired
    private EndpointRepository endpointRepository;

    /**
     * Default constructor.
     */
    protected EndpointService() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Creates a new endpoint pointing to an internal resource
     * This function does not validate the resource existence.
     *
     * @param endpointId The id for the new endpoint.
     * @param resourceId The id of the resource.
     * @return The new persistent endpoint.
     */
    public Endpoint create(final EndpointId endpointId, final UUID resourceId) {
        if (doesExist(endpointId)) {
            throw new ResourceAlreadyExistsException(endpointId.toString());
        }

        final var endpoint = new Endpoint();
        endpoint.setId(endpointId);
        endpoint.setInternalId(resourceId);

        return persist(endpoint);
    }

    /**
     * Update an existing endpoint.
     * If the endpoint points to a resource the endpoint will point to the
     * passed resource.
     * If the endpoint is redirecting the endpoint will then point to the passed
     * resource.
     * This function does not validate the resource existent.
     *
     * @param endpointId The id of the endpoint to be updated.
     * @param resourceId The id of the resource the endpoint should point to.
     */
    public void update(final EndpointId endpointId, final UUID resourceId) {
        if (doesExist(endpointId)) {
            final var endpoint = get(endpointId);

            if (directToResource(endpoint, resourceId)) {
                // Only persist if something changed
                persist(endpoint);
            }
        } else {
            // Cannot update endpoint that does not exist
            // Handle with global exception handler
            throw new ResourceNotFoundException(endpointId.toString());
        }
    }

    /**
     * This function will change the redirection target of the endpoint.
     * If the endpoint points to a resource the endpoint will redirect to a
     * different endpoint.
     * If the endpoint already redirects the target endpoint will be changed.
     *
     * @param endpointId  The id of the endpoint that should be updated.
     * @param newLocation The id of the redirection target endpoint.
     */
    public void update(final EndpointId endpointId,
                       final EndpointId newLocation) {
        if (doesExist(endpointId)) {
            // Change where the redirect points to
            final var endpoint = get(endpointId);

            if (redirectToEndpoint(endpoint, get(newLocation))) {
                // Only persist if something changed
                persist(endpoint);
            }

            persist(endpoint);
        } else {
            // Cannot update endpoint that does not exist
            // Handle with global exception handler
            throw new ResourceNotFoundException(endpointId.toString());
        }
    }

    /**
     * Try to delete an endpoint. This will delete all endpoints redirecting
     * to this endpoint.
     *
     * @param endpointId The id of the endpoint to be deleted.
     */
    public void delete(final EndpointId endpointId) {
        endpointRepository.deleteById(endpointId);

        // TODO cleanup the redirects
    }

    /**
     * Receive an endpoint.
     *
     * @param endpointId The id of the endpoint to be received.
     * @return The endpoint.
     */
    public Endpoint get(final EndpointId endpointId) {
        final var endpoint = endpointRepository.findById(endpointId);

        if (endpoint.isEmpty()) {
            // Handle with global exception handler
            throw new ResourceNotFoundException(endpointId.toString());
        }

        return endpoint.get();
    }

    /**
     * Get all endpoints.
     *
     * @return All endpoints.
     */
    public Set<EndpointId> getAll() {
        return endpointRepository.getAllIds();
    }

    /**
     * Get all entities pointing to this internal id.
     *
     * @param entityId The internal id.
     * @return The endpoints pointing directly to this id.
     */
    public Set<EndpointId> getByEntity(final UUID entityId) {
        // TODO Improve query
        final var allEndpoints = endpointRepository.findAll();
        final var foundEndpoints = new HashSet<EndpointId>();

        for (final var endpoint : allEndpoints) {
            if (!isRedirect(endpoint)
                    && getInternalId(endpoint).equals(entityId)) {
                foundEndpoints.add(endpoint.getId());
            }
        }

        return foundEndpoints;
    }

    /**
     * Check if a endpoint with the given id exists.
     *
     * @param endpointId The id of the endpoint.
     * @return True if the resource already exists.
     */
    public boolean doesExist(final EndpointId endpointId) {
        return endpointRepository.findById(endpointId).isPresent();
    }

    /**
     * Check if the endpoint is redirecting.
     *
     * @param endpoint The endpoint.
     * @return True if the endpoint if redirecting.
     */
    public static boolean isRedirect(final Endpoint endpoint) {
        return endpoint.getInternalId() == null
                && endpoint.getNewLocation() != null;
    }

    /**
     * Persist an endpoint.
     *
     * @param endpoint The endpoint to be persisted.
     * @return The endpoint after persisting.
     */
    private Endpoint persist(final Endpoint endpoint) {
        return endpointRepository.saveAndFlush(endpoint);
    }

    /**
     * Make an endpoint redirect to another endpoint.
     *
     * @param endpoint The endpoint to be changed.
     * @param target   The target endpoint.
     * @return True if the endpoint has been modified.
     */
    private static boolean redirectToEndpoint(final Endpoint endpoint,
                                              final Endpoint target) {
        final var updateInternalId = getInternalId(endpoint) != null;
        final var updateLocation = endpoint.getNewLocation() == null ||
                !endpoint.getNewLocation().equals(endpoint);

        if (updateInternalId) {
            endpoint.setInternalId(null);
        }

        if (updateLocation) {
            endpoint.setNewLocation(target);
        }

        return updateInternalId || updateLocation;
    }

    /**
     * Make an endpoint direct to a resource.
     *
     * @param endpoint   The endpoint to be changed.
     * @param resourceId The internal resource id.
     * @return True if the endpoint has been modified.
     */
    private static boolean directToResource(final Endpoint endpoint,
                                            final UUID resourceId) {
        final var internalId = getInternalId(endpoint);
        final var updateInternalId = internalId != null
                && !internalId.equals(resourceId);
        final var updateLocation = endpoint.getNewLocation() != null;

        if (updateInternalId) {
            endpoint.setInternalId(resourceId);
        }

        if (updateLocation) {
            endpoint.setNewLocation(null);
        }

        return updateInternalId || updateLocation;
    }

    /**
     * Receive the internal id of an endpoint.
     *
     * @param endpoint The endpoint.
     * @return The internal id.
     */
    private static UUID getInternalId(final Endpoint endpoint) {
        return endpoint.getInternalId();
    }
}
