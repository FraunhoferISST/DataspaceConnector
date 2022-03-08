/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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

import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.dataspaceconnector.model.base.Description;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.service.resource.base.EntityService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

/**
 * Offers REST-Api endpoints for REST resource handling.
 *
 * @param <T> The type of the resource.
 * @param <D> The type of the resource description expected to be passed with REST calls.
 * @param <V> The type of the view produces by this controller.
 * @param <S> The underlying service for handling the resource logic.
 */
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
@ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
        description = ResponseDescription.UNAUTHORIZED)
public class BaseResourceController<T extends Entity, D extends Description, V
        extends RepresentationModel<V>, S extends EntityService<T, D>>
        implements CRUDController<T, D, V> {
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
    private PagedResourcesAssembler<T> pagedAssembler;

    /**
     * The type of the entity used for creating empty pages.
     */
    private final Class<T> resourceType;

    /**
     * Default constructor.
     */
    @SuppressWarnings("unchecked")
    protected BaseResourceController() {
        final var resolved =
                GenericTypeResolver.resolveTypeArguments(getClass(), BaseResourceController.class);
        assert resolved != null;
        resourceType = (Class<T>) resolved[2];
    }

    private ResponseEntity<V> respondCreated(final T obj) {
        final var entity = assembler.toModel(obj);
        final var headers = new HttpHeaders();
        headers.setLocation(entity.getRequiredLink("self").toUri());

        return new ResponseEntity<>(entity, headers, HttpStatus.CREATED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<V> create(final D desc) {
        return respondCreated(service.create(desc));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PagedModel<V> getAll(final Integer page, final Integer size) {
        final var pageable = Utils.toPageRequest(page, size);
        final var entities = service.getAll(pageable);
        return getPagedModel(entities);
    }

    /**
     * Create a PagedModel from a page.
     *
     * @param entities The entities.
     * @return The pagemodel.
     */
    @SuppressWarnings("unchecked")
    protected PagedModel<V> getPagedModel(final Page<T> entities) {
        PagedModel<V> model;
        if (entities.hasContent()) {
            model = pagedAssembler.toModel(entities, assembler);
        } else {
            model = (PagedModel<V>) pagedAssembler.toEmptyModel(entities, resourceType);
        }

        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(final UUID resourceId) {
        return assembler.toModel(service.get(resourceId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<V> update(final UUID resourceId, final D desc) {
        final var resource = service.update(resourceId, desc);

        if (resource.getId().equals(resourceId)) {
            // The resource was not moved
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            // The resource has been moved
            return respondCreated(resource);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Void> delete(final UUID resourceId) {
        service.delete(resourceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
