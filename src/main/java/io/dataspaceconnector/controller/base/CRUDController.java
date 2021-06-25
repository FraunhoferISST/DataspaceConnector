package io.dataspaceconnector.controller.base;

import javax.validation.Valid;
import java.util.UUID;

import io.dataspaceconnector.controller.resources.swagger.responses.ResponseCodes;
import io.dataspaceconnector.controller.resources.swagger.responses.ResponseDescriptions;
import io.dataspaceconnector.model.base.AbstractEntity;
import io.dataspaceconnector.model.base.Description;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.hateoas.PagedModel;
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
 * Defines a controller for CRUD operations.
 * @param <T> The resource type.
 * @param <D> Input type consumed by controller.
 * @param <V> Output type produced by the controller.
 */
public interface CRUDController<T extends AbstractEntity, D extends Description, V>  {
    /**
     * Creates a new resource. Endpoint for POST requests.
     * @param desc The resource description.
     * @return Response with code 201 (Created).
     * @throws IllegalArgumentException if the description is null.
     */
    @PostMapping
    @Operation(summary = "Create a base resource")
    @ApiResponses(value = {@ApiResponse(responseCode = ResponseCodes.CREATED,
            description = ResponseDescriptions.CREATED)})
    ResponseEntity<V> create(@RequestBody D desc);

    /**
     * Get a list of all resources endpoints of this type.
     * Endpoint for GET requests.
     * @param page The page index.
     * @param size The page size.
     * @return Response with code 200 (Ok) and the list of all endpoints of this resource type.
     */
    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get a list of base resources with pagination")
    @ApiResponses(value = {@ApiResponse(responseCode = ResponseCodes.OK,
            description = ResponseDescriptions.OK)})
    PagedModel<V> getAll(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "30") Integer size);

    /**
     * Get a resource. Endpoint for GET requests.
     * @param resourceId The id of the resource.
     * @return The resource.
     * @throws IllegalArgumentException if the resourceId is null.
     * @throws io.dataspaceconnector.exceptions.ResourceNotFoundException
     *          if the resourceId is unknown.
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @Operation(summary = "Get a base resource by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
    V get(@Valid @PathVariable(name = "id") UUID resourceId);

    /**
     * Update a resource details. Endpoint for PUT requests.
     *
     * @param resourceId The id of the resource to be updated.
     * @param desc       The new description of the resource.
     * @return Response with code (No_Content) when the resource has been updated or response with
     * code (201) if the resource has been updated and been moved to a new endpoint.
     * @throws IllegalArgumentException if the any of the parameters is null.
     * @throws io.dataspaceconnector.exceptions.ResourceNotFoundException
     *          if the resourceId is unknown.
     */
    @PutMapping("{id}")
    @Operation(summary = "Update a base resource by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCodes.CREATED,
                    description = ResponseDescriptions.CREATED),
            @ApiResponse(responseCode = ResponseCodes.NO_CONTENT,
                    description = ResponseDescriptions.NO_CONTENT)})
    ResponseEntity<Object> update(
            @Valid @PathVariable(name = "id") UUID resourceId, @RequestBody D desc);

    /**
     * Delete a resource. Endpoint for DELETE requests.
     * @param resourceId The id of the resource to be deleted.
     * @return Response with code 204 (No_Content).
     * @throws IllegalArgumentException if the resourceId is null.
     */
    @DeleteMapping("{id}")
    @Operation(summary = "Delete a base resource by id")
    @ApiResponses(value = {@ApiResponse(responseCode = ResponseCodes.NO_CONTENT,
            description = ResponseDescriptions.NO_CONTENT)})
    ResponseEntity<Void> delete(@Valid @PathVariable(name = "id") UUID resourceId);
}
