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

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.util.UUIDUtils;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.service.resource.base.RelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpEntity;
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

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Offers REST-Api Endpoints for modifying relations between REST resources.
 *
 * @param <S> The service type for handling the relations logic.
 * @param <T> The type of the entity operated on.
 * @param <V> The type of the view model produces.
 */
@ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
        description = ResponseDescription.UNAUTHORIZED)
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class BaseResourceChildController<S extends RelationService<?, ?, ?, ?>,
        T extends Entity, V extends RepresentationModel<V>> {
    /**
     * The linker between two resources.
     **/
    @Autowired
    private S linker;

    /**
     * The assembler for converting entites to views.
     */
    @Autowired
    private RepresentationModelAssembler<T, V> assembler;

    /**
     * The assembler for creating list of views.
     */
    @Autowired
    private PagedResourcesAssembler<T> pagedAssembler;

    /**
     * The type of the service.
     */
    private final Class<S> resourceType;

    /**
     * Default constructor.
     */
    @SuppressWarnings("unchecked")
    protected BaseResourceChildController() {
        final var resolved = GenericTypeResolver
                .resolveTypeArguments(getClass(), BaseResourceChildController.class);
        assert resolved != null;
        resourceType = (Class<S>) resolved[2];
    }

    /**
     * Get all resources of the same type linked to the passed resource. Endpoint for GET requests.
     *
     * @param ownerId The id of the owning resource.
     * @param page    The page index.
     * @param size    The page size.
     * @return The children of the resource.
     * @throws IllegalArgumentException                                         if the ownerId is
     *                                                                          null.
     * @throws io.dataspaceconnector.common.exception.ResourceNotFoundException if the ownerId is
     *                                                                          not known.
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Get all children of a base resource with pagination.")
    @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK)
    public PagedModel<V> getResource(
            @Valid @PathVariable(name = "id") final UUID ownerId,
            @RequestParam(required = false, defaultValue = "0") final Integer page,
            @RequestParam(required = false, defaultValue = "30") final Integer size) {
        final var pageable = Utils.toPageRequest(page, size);
        final var entities = linker.get(ownerId, pageable);

        PagedModel<V> model;
        if (entities.hasContent()) {
            model = pagedAssembler.toModel((Page<T>) entities, assembler);
        } else {
            model = (PagedModel<V>) pagedAssembler.toEmptyModel(entities, resourceType);
        }

        return model;
    }

    /**
     * Add resources as children to a resource. Endpoint for POST requests.
     *
     * @param ownerId   The owning resource.
     * @param resources The children to be added.
     * @return Response with code 200 (Ok) and the new children's list.
     */
    @PostMapping
    @Operation(summary = "Add a list of children to a base resource.")
    @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK)
    public PagedModel<V> addResources(
            @Valid @PathVariable(name = "id") final UUID ownerId,
            @Valid @RequestBody final List<URI> resources) {
        Utils.requireNonNull(resources, ErrorMessage.LIST_NULL);

        linker.add(ownerId, toSet(resources));
        // Send back the list of children after modification.
        // See https://tools.ietf.org/html/rfc7231#section-4.3.3 and
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/200
        return this.getResource(ownerId, null, null);
    }

    /**
     * Replace the children of a resource. Endpoint for PUT requests.
     *
     * @param ownerId   The id of the resource which children should be replaced.
     * @param resources The resources that should be added as children.
     * @return Response with code 204 (No_Content).
     */
    @PutMapping
    @Operation(summary = "Replace the children of a base resource.")
    @ApiResponse(responseCode = ResponseCode.NO_CONTENT,
            description = ResponseDescription.NO_CONTENT)
    public HttpEntity<Void> replaceResources(@Valid @PathVariable(name = "id") final UUID ownerId,
                                             @Valid @RequestBody final List<URI> resources) {
        linker.replace(ownerId, toSet(resources));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Remove a list of children of a resource. Endpoint for DELETE requests.
     *
     * @param ownerId   The id of the resource which children should be removed.
     * @param resources The list of children to be removed.
     * @return Response with code 204 (No_Content).
     */
    @DeleteMapping
    @Operation(summary = "Remove a list of children from a base resource.")
    @ApiResponse(responseCode = ResponseCode.NO_CONTENT,
            description = ResponseDescription.NO_CONTENT)
    public HttpEntity<Void> removeResources(@Valid @PathVariable(name = "id") final UUID ownerId,
                                            @Valid @RequestBody final List<URI> resources) {
        linker.remove(ownerId, toSet(resources));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private static Set<UUID> toSet(final List<URI> uris) {
        return uris.parallelStream().map(UUIDUtils::uuidFromUri).collect(Collectors.toSet());
    }
}
