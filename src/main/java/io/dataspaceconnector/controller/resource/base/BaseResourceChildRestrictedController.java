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

import io.dataspaceconnector.controller.resource.base.exception.MethodNotAllowed;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.service.resource.base.RelationService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Offers methods for resource relation handling. Restricts access to modifying endpoints.
 *
 * @param <S> The service type for handling the relations logic.
 * @param <T> The type of the entity operated on.
 * @param <V> The type of the view model produces.
 */
@ApiResponses(value = {
        @ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                description = ResponseDescription.METHOD_NOT_ALLOWED),
        @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                description = ResponseDescription.UNAUTHORIZED)})
public class BaseResourceChildRestrictedController<S extends RelationService<?, ?, ?, ?>,
        T extends Entity, V extends RepresentationModel<V>>
        extends BaseResourceChildController<S, T, V> {

    /**
     * {@inheritDoc}
     */
    @Hidden
    @Override
    public PagedModel<V> addResources(
            @Valid @PathVariable(name = "id") final UUID ownerId,
            @Valid @RequestBody final List<URI> resources) {
        throw new MethodNotAllowed();
    }

    /**
     * {@inheritDoc}
     */
    @Hidden
    @Override
    public HttpEntity<Void> replaceResources(@Valid @PathVariable(name = "id") final UUID ownerId,
                                             @Valid @RequestBody final List<URI> resources) {
        throw new MethodNotAllowed();
    }

    /**
     * {@inheritDoc}
     */
    @Hidden
    @Override
    public HttpEntity<Void> removeResources(@Valid @PathVariable(name = "id") final UUID ownerId,
                                            @Valid @RequestBody final List<URI> resources) {
        throw new MethodNotAllowed();
    }
}
