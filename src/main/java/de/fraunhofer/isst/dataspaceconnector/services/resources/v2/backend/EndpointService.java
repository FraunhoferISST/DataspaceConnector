package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceAlreadyExistsException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.repositories.v2.EndpointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public final class EndpointService {

    /** Persists all endpoints. **/
    @Autowired
    private EndpointRepository endpointRepository;

    /**
     * Creates a new endpoint pointing to an internal resource
     * This function does not validate the resource existent.
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
     * @throws IllegalArgumentException if the resourceId is null.
     */
    public void update(final EndpointId endpointId, final UUID resourceId)
            throws IllegalArgumentException {
        if (resourceId == null) {
            throw new IllegalArgumentException("ResourceId may not be null");
        }

        if (doesExist(endpointId)) {
            final var endpoint = get(endpointId);

            if (isRedirect(endpoint)) {
                // Only point to new resource
                final var oldLocation = endpoint.getNewLocation();
                final var oldInternal = endpoint.getInternalId();

                endpoint.setNewLocation(null);
                endpoint.setInternalId(resourceId);

                if (!(endpoint.getNewLocation().equals(oldLocation)
                        && endpoint.getInternalId().equals(oldInternal))) {
                    // Only persist if something changed
                    persist(endpoint);
                }
            } else {
                // The endpoint points to a resource, change it
                final var oldInternal = endpoint.getInternalId();

                endpoint.setInternalId(resourceId);

                if (!endpoint.getInternalId().equals(oldInternal)) {
                    // Only persist if something changed
                    persist(endpoint);
                }
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
     * @param endpointId The id of the endpoint that should be updated.
     * @param newLocation The id of the redirection target endpoint.
     * @throws IllegalArgumentException if newLocation is null.
     */
    public void update(final EndpointId endpointId,
                       final EndpointId newLocation)
            throws IllegalArgumentException {
        if (newLocation == null) {
            throw new IllegalArgumentException("newLocation may not be null");
        }

        if (doesExist(endpointId)) {
            // Change where the redirect points to
            final var endpoint = get(endpointId);

            final var oldLocation = endpoint.getNewLocation();
            final var oldInternal = endpoint.getInternalId();

            endpoint.setInternalId(null);
            endpoint.setNewLocation(get(newLocation));

            if (!(endpoint.getNewLocation().equals(oldLocation)
                    && endpoint.getInternalId().equals(oldInternal))) {
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
            if (!isRedirect(endpoint)) {
                if (endpoint.getInternalId().equals(entityId)) {
                    foundEndpoints.add(endpoint.getId());
                }
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
     * @param endpointId The id of the endpoint.
     * @return True if the endpoint is redirecting.
     */
    public boolean isRedirect(final EndpointId endpointId) {
        final var endpoint = get(endpointId);
        return isRedirect(endpoint);
    }

    /**
     * Check if the endpoint is redirecting.
     *
     * @param endpoint The endpoint.
     * @return True if the endpoint if redirecting.
     */
    public static boolean isRedirect(final Endpoint endpoint) {
        return endpoint.getInternalId() == null && endpoint.getNewLocation() != null;
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
}
