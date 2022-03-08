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
package io.dataspaceconnector.controller.resource.type;

import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.controller.resource.base.CRUDController;
import io.dataspaceconnector.controller.resource.base.exception.MethodNotAllowed;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.endpoint.EndpointViewAssemblerProxy;
import io.dataspaceconnector.controller.resource.view.endpoint.EndpointViewProxy;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.dataspaceconnector.model.endpoint.AppEndpoint;
import io.dataspaceconnector.model.endpoint.AppEndpointDesc;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.endpoint.EndpointDesc;
import io.dataspaceconnector.service.resource.type.EndpointServiceProxy;
import io.dataspaceconnector.service.resource.type.GenericEndpointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

/**
 * Offers the endpoints for managing different endpoints.
 */
@RestController
@RequiredArgsConstructor
@ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
        description = ResponseDescription.UNAUTHORIZED)
@RequestMapping(BasePath.ENDPOINTS)
@Tag(name = ResourceName.ENDPOINTS, description = ResourceDescription.ENDPOINTS)
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class EndpointController implements CRUDController<Endpoint, EndpointDesc, Object> {

    /**
     * Service for generic endpoint.
     */
    private final @NonNull GenericEndpointService genericEndpointService;

    /**
     * Service proxy for endpoints.
     */
    private final @NonNull EndpointServiceProxy service;

    /**
     * Assembler for pagination.
     */
    private final @NonNull PagedResourcesAssembler<Endpoint> pagedAssembler;

    /**
     * Assembler for the EndpointView.
     */
    private final @NonNull EndpointViewAssemblerProxy assemblerProxy;

    /**
     * Respond with created endpoint.
     *
     * @param obj The endpoint object.
     * @return response entity.
     */
    private ResponseEntity<Object> respondCreated(final Endpoint obj) {
        final RepresentationModel<?> entity = assemblerProxy.toModel(obj);
        final var headers = new HttpHeaders();
        headers.setLocation(entity.getRequiredLink("self").toUri());

        return new ResponseEntity<>(entity, headers, HttpStatus.CREATED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Object> create(final EndpointDesc desc) {
        if (isAppEndpoint(desc)) {
            throw new MethodNotAllowed();
        }
        return respondCreated(service.create(desc));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public PagedModel<Object> getAll(final Integer page, final Integer size) {
        final var pageable = Utils.toPageRequest(page, size);
        final var entities = service.getAll(pageable);
        final PagedModel<?> model;
        if (entities.hasContent()) {
            model = pagedAssembler.toModel(entities, assemblerProxy);
        } else {
            model = pagedAssembler.toEmptyModel(entities, EndpointViewProxy.class);
        }

        return (PagedModel<Object>) model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object get(final UUID resourceId) {
        return assemblerProxy.toModel(service.get(resourceId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Object> update(final UUID resourceId, final EndpointDesc desc) {
        if (isAppEndpoint(desc)) {
            throw new MethodNotAllowed();
        }
        final var resource = service.update(resourceId, desc);

        if (resource.getId().equals(resourceId)) {
            // The resource was not moved.
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return respondCreated(resource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<Void> delete(final UUID resourceId) {
        if (service.get(resourceId) instanceof AppEndpoint) {
            throw new MethodNotAllowed();
        }
        service.delete(resourceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoints for creating a start endpoint for a route.
     *
     * @param genericEndpointId The id of the generic endpoint.
     * @param dataSourceId      The id of the data source.
     * @return response status OK if data source is created at generic endpoint.
     */
    @PutMapping("{id}/datasource/{dataSourceId}")
    @Operation(summary = "Creates start endpoint for a route.")
    @ApiResponse(responseCode = ResponseCode.NO_CONTENT,
            description = ResponseDescription.NO_CONTENT)
    public final ResponseEntity<Void> linkDataSource(
            @Valid @PathVariable(name = "id") final UUID genericEndpointId,
            @Valid @PathVariable(name = "dataSourceId") final UUID dataSourceId) {
        genericEndpointService.setGenericEndpointDataSource(genericEndpointId, dataSourceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private boolean isAppEndpoint(final EndpointDesc desc) {
        return desc instanceof AppEndpointDesc;
    }
}
