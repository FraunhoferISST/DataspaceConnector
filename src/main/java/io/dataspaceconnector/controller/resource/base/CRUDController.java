/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.controller.resource.base;

import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.dataspaceconnector.model.base.Description;
import io.dataspaceconnector.model.base.Entity;
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

import javax.validation.Valid;
import java.util.UUID;

/**
 * Defines a controller for CRUD operations.
 *
 * @param <T> The resource type.
 * @param <D> Input type consumed by controller.
 * @param <V> Output type produced by the controller.
 */
@ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
        description = ResponseDescription.UNAUTHORIZED)
public interface CRUDController<T extends Entity, D extends Description, V> {

    /**
     * Creates a new resource. Endpoint for POST requests.
     *
     * @param desc The resource description.
     * @return Response with code 201 (Created).
     * @throws IllegalArgumentException if the description is null.
     */
    @PostMapping
    @Operation(summary = "Create a base resource.")
    @ApiResponse(responseCode = ResponseCode.CREATED, description = ResponseDescription.CREATED)
    ResponseEntity<V> create(@RequestBody D desc);

    /**
     * Get a list of all resources endpoints of this type. Endpoint for GET requests.
     *
     * @param page The page index.
     * @param size The page size.
     * @return Response with code 200 (Ok) and the list of all endpoints of this resource type.
     */
    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get a list of base resources with pagination.")
    @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK)
    PagedModel<V> getAll(@RequestParam(required = false, defaultValue = "0") Integer page,
                         @RequestParam(required = false, defaultValue = "30") Integer size);

    /**
     * Get a resource. Endpoint for GET requests.
     *
     * @param resourceId The id of the resource.
     * @return The resource.
     * @throws IllegalArgumentException if the resourceId is null.
     * @throws io.dataspaceconnector.common.exception.ResourceNotFoundException if the resourceId is
     * unknown.
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @Operation(summary = "Get a base resource by id.")
    @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK)
    V get(@Valid @PathVariable(name = "id") UUID resourceId);

    /**
     * Update a resource details. Endpoint for PUT requests.
     *
     * @param id   The id of the resource to be updated.
     * @param desc The new description of the resource.
     * @return Response with code (No_Content) when the resource has been updated or response with
     * code (201) if the resource has been updated and been moved to a new endpoint.
     * @throws IllegalArgumentException if the any of the parameters is null.
     * @throws io.dataspaceconnector.common.exception.ResourceNotFoundException if the resourceId is
     * unknown.
     */
    @PutMapping("{id}")
    @Operation(summary = "Update a base resource by id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.CREATED,
                    description = ResponseDescription.CREATED),
            @ApiResponse(responseCode = ResponseCode.NO_CONTENT,
                    description = ResponseDescription.NO_CONTENT)})
    ResponseEntity<V> update(@Valid @PathVariable(name = "id") UUID id, @RequestBody D desc);

    /**
     * Delete a resource. Endpoint for DELETE requests.
     *
     * @param id The id of the resource to be deleted.
     * @return Response with code 204 (No_Content).
     * @throws IllegalArgumentException if the resourceId is null.
     */
    @DeleteMapping("{id}")
    @Operation(summary = "Delete a base resource by id.")
    @ApiResponse(responseCode = ResponseCode.NO_CONTENT,
            description = ResponseDescription.NO_CONTENT)
    ResponseEntity<Void> delete(@Valid @PathVariable(name = "id") UUID id);
}
