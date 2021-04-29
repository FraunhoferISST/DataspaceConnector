package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import javax.validation.Valid;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractDescription;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.services.resources.BaseEntityService;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
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
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Offers REST-Api endpoints for REST resource handling.
 *
 * @param <T> The type of the resource.
 * @param <D> The type of the resource description expected to be passed with REST calls.
 * @param <V> The type of the view produces by this controller.
 * @param <S> The underlying service for handling the resource logic.
 */
public class BaseResourceController<T extends AbstractEntity, D extends AbstractDescription<T>, V
        extends RepresentationModel<V>, S
        extends BaseEntityService<T, D>> {
    /**
     * The service for the resource logic.
     **/
    @Autowired
    private S service;

    /**
     * The assembler for creating a view from an entity.
     */
    @Autowired
    private RepresentationModelAssembler<T, V> assembler;

    /**
     * The assembler for creating list of views.
     */
    @Autowired
    private PagedResourcesAssembler<T> pagedResourcesAssembler;

    /**
     * The type of the entity used for creating empty pages.
     */
    private final Class<T> resourceType;

    /**
     * Default constructor.
     */
    protected BaseResourceController() {
        final var resolved =
                GenericTypeResolver.resolveTypeArguments(getClass(), BaseResourceController.class);
        resourceType = (Class<T>) resolved[2];
    }

    /**
     * Creates a new resource. Endpoint for POST requests.
     * @param desc The resource description.
     * @return Response with code 201 (Created).
     * @throws IllegalArgumentException if the description is null.
     */
    @PostMapping
    @Operation(summary = "Create a base resource")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Created")})
    public ResponseEntity<V> create(@RequestBody final D desc) {
        final var obj = service.create(desc);
        final var entity = assembler.toModel(obj);

        final var headers = new HttpHeaders();
        headers.setLocation(entity.getLink("self").get().toUri());

        return new ResponseEntity<>(entity, headers, HttpStatus.CREATED);
    }

    /**
     * Get a list of all resources endpoints of this type.
     * Endpoint for GET requests.
     * @param page The page index.
     * @param size The page size.
     * @param sort The sorting applied to the page.
     * @return Response with code 200 (Ok) and the list of all endpoints of this resource type.
     */
    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get a list of base resources with pagination")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
    public ResponseEntity<CollectionModel<V>> getAll(
            @RequestParam(required = false, defaultValue = "0") final Integer page,
            @RequestParam(required = false, defaultValue = "30") final Integer size,
            @RequestParam(required = false) final String sort) {
        final var pageable = Utils.toPageRequest(page, size, sort);
        final var entities = service.getAll(pageable);
        PagedModel<V> model;
        if (entities.hasContent()) {
            model = pagedResourcesAssembler.toModel(entities, assembler);
        } else {
            model = (PagedModel<V>) pagedResourcesAssembler.toEmptyModel(entities, resourceType);
        }

        return ResponseEntity.ok(model);
    }

    /**
     * Get a resource. Endpoint for GET requests.
     * @param resourceId The id of the resource.
     * @return The resource.
     * @throws IllegalArgumentException if the resourceId is null.
     * @throws ResourceNotFoundException if the resourceId is unknown.
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @Operation(summary = "Get a base resource by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
    public ResponseEntity<V> get(@Valid @PathVariable(name = "id") final UUID resourceId) {
        final var view = assembler.toModel(service.get(resourceId));
        return ResponseEntity.ok(view);
    }

    /**
     * Update a resource details. Endpoint for PUT requests.
     *
     * @param resourceId The id of the resource to be updated.
     * @param desc       The new description of the resource.
     * @return Response with code (No_Content) when the resource has been updated or response with
     * code (201) if the resource has been updated and been moved to a new endpoint.
     * @throws IllegalArgumentException if the any of the parameters is null.
     * @throws ResourceNotFoundException if the resourceId is unknown.
     */
    @PutMapping(value = "{id}")
    @Operation(summary = "Update a base resource by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "204", description = "No Content")})
    public ResponseEntity<Object> update(
            @Valid @PathVariable(name = "id") final UUID resourceId, @RequestBody final D desc) {
        final var resource = service.update(resourceId, desc);

        ResponseEntity<Object> response;
        if (resource.getId().equals(resourceId)) {
            // The resource was not moved
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            // The resource has been moved
            final var headers = new HttpHeaders();
            headers.setLocation(assembler.toModel(resource).getLink("self").get().toUri());

            response =
                    new ResponseEntity<>(assembler.toModel(resource), headers, HttpStatus.CREATED);
        }

        return response;
    }

    /**
     * Delete a resource. Endpoint for DELETE requests.
     * @param resourceId The id of the resource to be deleted.
     * @return Response with code 204 (No_Content).
     * @throws IllegalArgumentException if the resourceId is null.
     */
    @DeleteMapping(value = "{id}")
    @Operation(summary = "Delete a base resource by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No Content")})
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
