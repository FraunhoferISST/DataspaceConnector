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
package io.dataspaceconnector.controller.configurations;

import java.util.UUID;

import io.dataspaceconnector.controller.base.CRUDController;
import io.dataspaceconnector.model.endpoint.AppEndpoint;
import io.dataspaceconnector.model.endpoint.ConnectorEndpoint;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.endpoint.EndpointDesc;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.services.resources.EndpointServiceProxy;
import io.dataspaceconnector.utils.Utils;
import io.dataspaceconnector.view.AppEndpointViewAssembler;
import io.dataspaceconnector.view.ConnectorEndpointViewAssembler;
import io.dataspaceconnector.view.GenericEndpointViewAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for management of different endpoints.
 */
public final class EndpointControllers {

    /**
     * Offers the endpoints for managing different endpoints.
     */
    @RestController
    @RequestMapping("/api/endpoints")
    @RequiredArgsConstructor
    @Tag(name = "Endpoints", description = "Endpoints for CRUD operations on endpoints")
    public static class GenericEndpointController
            implements CRUDController<Endpoint, EndpointDesc, Object> {

        @Autowired
        private final EndpointServiceProxy service;

        @Autowired
        private final GenericEndpointViewAssembler genericAssembler;

        @Autowired
        private final AppEndpointViewAssembler appAssembler;

        @Autowired
        private final ConnectorEndpointViewAssembler connectorAssembler;

        @Autowired
        private final PagedResourcesAssembler<Endpoint> pagedAssembler;

        private <K> RepresentationModel<?> toView(final K endpoint) {
            if (AppEndpoint.class.equals(endpoint.getClass())) {
                return appAssembler.toModel((AppEndpoint) endpoint);
            }

            if (ConnectorEndpoint.class.equals(endpoint.getClass())) {
                return connectorAssembler.toModel((ConnectorEndpoint) endpoint);
            }

            return genericAssembler.toModel((GenericEndpoint) endpoint);
        }

        private PagedModel<?> toView(final Pageable pageable) {
            final var objs = service.getAll(pageable);
            if(objs.hasContent()){
                    return pagedAssembler.toModel(objs);
            }
            return PagedModel.empty();
        }

        private ResponseEntity<Object> respondCreated(final Endpoint obj) {
            final RepresentationModel<?> entity = toView(obj);
            final var headers = new HttpHeaders();
            headers.setLocation(entity.getRequiredLink("self").toUri());

            return new ResponseEntity<>(entity, headers, HttpStatus.CREATED);
        }

        @Override
        public ResponseEntity<Object> create(final EndpointDesc desc) {
            return respondCreated(service.create(desc));
        }

        @Override
        public PagedModel<Object> getAll(final Integer page, final Integer size) {
            final var pageable = Utils.toPageRequest(page, size);
            return (PagedModel<Object>) toView(pageable);
        }

        @Override
        public Object get(final UUID resourceId) {
            return toView(service.get(resourceId));
        }

        @Override
        public ResponseEntity<Object> update(final UUID resourceId, final EndpointDesc desc) {
            final var resource = service.update(resourceId, desc);

            ResponseEntity<Object> response;
            if (resource.getId().equals(resourceId)) {
                // The resource was not moved
                response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                // The resource has been moved
                response = respondCreated(resource);
            }

            return response;
        }

        @Override
        public ResponseEntity<Void> delete(final UUID resourceId) {
            service.delete(resourceId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
