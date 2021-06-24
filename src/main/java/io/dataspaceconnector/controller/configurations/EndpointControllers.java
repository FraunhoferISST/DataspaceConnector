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

import io.dataspaceconnector.controller.resources.BaseResourceController;
import io.dataspaceconnector.model.endpoints.AppEndpoint;
import io.dataspaceconnector.model.endpoints.AppEndpointDesc;
import io.dataspaceconnector.model.endpoints.ConnectorEndpoint;
import io.dataspaceconnector.model.endpoints.ConnectorEndpointDesc;
import io.dataspaceconnector.model.endpoints.GenericEndpoint;
import io.dataspaceconnector.model.endpoints.GenericEndpointDesc;
import io.dataspaceconnector.services.configuration.AppEndpointService;
import io.dataspaceconnector.services.configuration.ConnectorEndpointService;
import io.dataspaceconnector.services.configuration.GenericEndpointService;
import io.dataspaceconnector.view.AppEndpointView;
import io.dataspaceconnector.view.ConnectorEndpointView;
import io.dataspaceconnector.view.GenericEndpointView;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

/**
 * Controller for management of different endpoints.
 */
public final class EndpointControllers {

    /**
     * Offers the endpoints for managing generic endpoints.
     */
    @RestController
    @RequestMapping("/api/genericendpoints")
    @RequiredArgsConstructor
    @Tag(name = "Generic Endpoint", description = "Endpoints for CRUD operations on"
            + " generic endpoints")
    public static class GenericEndpointController
            extends BaseResourceController<GenericEndpoint, GenericEndpointDesc,
            GenericEndpointView, GenericEndpointService> {

        /**
         * The service managing data sources.
         */
        private final @NonNull GenericEndpointService genericEndpointService;

        /**
         * Replace the data source of a generic endpoint.
         * @param genericEndpointId The generic endpoint whose data source should be replaced.
         * @param dataSourceId      The new data source.
         * @return Http Status ok.
         */
        @PutMapping(value = "{id}/datasource")
        public ResponseEntity<Void> putDataSource(
                @Valid @PathVariable(name = "id") final UUID genericEndpointId,
                @RequestParam(name = "dataSourceId") final UUID dataSourceId) throws IOException {
            genericEndpointService.setGenericEndpointDataSource(genericEndpointId, dataSourceId);
            return ResponseEntity.ok().build();
        }

        /**
         * Delete the data source from a generic endpoint.
         * @param genericEndpointId The generic endpoint whose data source should be deleted.
         * @return Http Status ok.
         */
        @DeleteMapping(value = "{id}/datasource")
        public ResponseEntity<Void> deleteDataSource(
                @Valid @PathVariable(name = "id") final UUID genericEndpointId) throws IOException {
            genericEndpointService.deleteGenericEndpointDataSource(genericEndpointId);
            return ResponseEntity.ok().build();
        }
    }

    /**
     * Offers the endpoints for managing connector endpoints.
     */
    @RestController
    @RequestMapping("/api/connectorendpoints")
    @Tag(name = "Connector Endpoint", description = "Endpoints for CRUD operations on"
            + " connector endpoints")
    public static class ConnectorEndpointController
            extends BaseResourceController<ConnectorEndpoint, ConnectorEndpointDesc,
            ConnectorEndpointView, ConnectorEndpointService> {

    }

    /**
     * Offers the endpoints for managing apps.
     */
    @RestController
    @RequestMapping("/api/appendpoints")
    @Tag(name = "App Endpoints", description = "Endpoints for CRUD operations on app endpoints")
    public static class AppEndpointController
            extends BaseResourceController<AppEndpoint, AppEndpointDesc, AppEndpointView,
            AppEndpointService> {
    }
}
