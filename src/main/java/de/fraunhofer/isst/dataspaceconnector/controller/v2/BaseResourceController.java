package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractDescription;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.BaseEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpEntity;
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
import java.util.UUID;

/**
 * Offers REST-Api endpoints for resource handling.
 *
 * @param <T> The type of the resource.
 * @param <D> The type of the resource description expected to be passed with
 *            REST calls.
 * @param <S> The underlying service for handling the resource logic.
 */
public class BaseResourceController<T extends AbstractEntity, D extends AbstractDescription<T>,
        V extends RepresentationModel<V>, S extends BaseEntityService<T, D>> {
    /**
     * The service for the resource logic.
     **/
    @Autowired
    private S service;

    @Autowired
    private RepresentationModelAssembler<T, V> assembler;

    private Class<T> tClass;

    @Autowired
    private EntityLinks entityLinks;

    @Autowired
    private PagedResourcesAssembler<T> pagedResourcesAssembler;

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
    public HttpEntity<V> create(@RequestBody final D desc) {
        final var entity = assembler.toModel(service.create(desc));

        final var headers = new HttpHeaders();
        headers.setLocation(entity.getLink("self").get().toUri());

        return new ResponseEntity<>(entity, headers, HttpStatus.CREATED);
    }

    /**
     * Get a list of all resources endpoints of this type.
     * Endpoint for GET requests.
     *
     * @return Response with code 200 (Ok) and the list of all endpoints of this
     * resource type.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public HttpEntity<CollectionModel<V>> get(final Pageable pageable) {
        final var entities = service.getAll(pageable);
        final var model = pagedResourcesAssembler.toModel(entities, assembler);

        return ResponseEntity.ok(model);
    }

    /**
     * Get a resource. Endpoint for GET requests.
     *
     * @param resourceId The id of the resource.
     * @return The resource.
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public HttpEntity<V> get(@Valid @PathVariable(name = "id") final UUID resourceId) {
        final var resource = assembler.toModel(service.get(resourceId));
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
    @PutMapping(value = "{id}")
    public ResponseEntity<Void> update(
            @Valid @PathVariable(name = "id") final UUID resourceId, @RequestBody final D desc) {
        final var resource = service.update(resourceId, desc);

        ResponseEntity<Void> response;
        if (resource.getId().equals(resourceId)) {
            // The resource was not moved
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            // The resource has been moved
            final var headers = new HttpHeaders();
            headers.setLocation(assembler.toModel(resource).getLink("self").get().toUri());

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
    @DeleteMapping(value = "{id}")
    public ResponseEntity<Void> delete(@Valid @PathVariable(name = "id") final UUID resourceId) {
        service.delete(resourceId);
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
