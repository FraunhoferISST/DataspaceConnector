package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseDescription;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseResource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.BaseView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.FrontFacingService;
import de.fraunhofer.isst.dataspaceconnector.services.utils.EndpointUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

/**
 * Offers REST-Api endpoints for resource handling.
 *
 * @param <T> The type of the resource.
 * @param <D> The type of the resource description expected to be passed with
 *            REST calls.
 * @param <V> The type of the resource view expected to be returned with the
 *            REST calls.
 * @param <S> The underlying service for handling the resource logic.
 */
public class BaseResourceController<T extends BaseResource,
        D extends BaseDescription<T>, V extends BaseView<T>,
        S extends FrontFacingService<T, D, V>> {

    /**
     * The service for the resource logic.
     **/
    @Autowired
    private S service;

    /**
     * Default constructor.
     */
    protected BaseResourceController() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Creates a new resource. Endpoint for POST requests.
     *
     * @param desc The resource description.
     * @return Response with code 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody final D desc) {
        final var endpointId = service.create(
                EndpointUtils.getCurrentBasePath().toString(), desc);

        final var headers = new HttpHeaders();
        headers.setLocation(endpointId.toUri());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    /**
     * Get a list of all resources endpoints of this type.
     * Endpoint for GET requests.
     *
     * @return Response with code 200 (Ok) and the list of all endpoints of this
     * resource type.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Set<EndpointId>> get() {
        final var resources = service.getAll();
        return ResponseEntity.ok(resources);
    }

    /**
     * Get a resource. Endpoint for GET requests.
     *
     * @param resourceId The id of the resource.
     * @return The resource.
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<V> get(
            @Valid @PathVariable(name = "id") final UUID resourceId) {
        final var currentEndpoint =
                EndpointUtils.getCurrentEndpoint(resourceId);
        final var resource = service.get(currentEndpoint);
        return ResponseEntity.ok(resource);
    }

    /**
     * Update a resource details. Endpoint for PUT requests.
     *
     * @param resourceId The id of the resource to be updated.
     * @param desc       The new description of the resource.
     * @return Response with code (No_Content) when the resource has been
     * updated or response with code (201) if the resource has been updated
     * and been moved to a new endpoint.
     */
    @PutMapping
    public ResponseEntity<Void> update(
            @Valid @PathVariable(name = "id") final UUID resourceId,
            @RequestBody final D desc) {
        final var currentEndpoint =
                EndpointUtils.getCurrentEndpoint(resourceId);
        final var newEndpoint = service.update(currentEndpoint, desc);

        ResponseEntity<Void> response;
        if (newEndpoint.equals(currentEndpoint)) {
            // The resource was not moved
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            // The resource has been moved
            final var headers = new HttpHeaders();
            headers.setLocation(newEndpoint.toUri());

            response = new ResponseEntity<>(headers, HttpStatus.CREATED);
        }

        return response;
    }

    /**
     * Delete a resource. Endpoint for DELETE requests.
     *
     * @param resourceId The id of the resource to be deleted.
     * @return Response with code 204 (No_Content).
     */
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @Valid @PathVariable(name = "id") final UUID resourceId) {
        service.delete(EndpointUtils.getCurrentEndpoint(resourceId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Get the service responsible for the resource's logic handling.
     *
     * @return The service.
     */
    protected S getService() {
        return service;
    }
}
