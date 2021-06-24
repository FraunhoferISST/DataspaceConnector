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

import io.dataspaceconnector.controller.resources.BaseResourceChildController;
import io.dataspaceconnector.controller.resources.BaseResourceController;
import io.dataspaceconnector.model.resources.OfferedResource;
import io.dataspaceconnector.model.routes.Route;
import io.dataspaceconnector.model.routes.RouteDesc;
import io.dataspaceconnector.services.configuration.EntityLinkerService;
import io.dataspaceconnector.services.configuration.RouteService;
import io.dataspaceconnector.view.OfferedResourceView;
import io.dataspaceconnector.view.RouteView;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for route management.
 */
public final class RouteControllers {

    /**
     * Offers the endpoints for managing proxies.
     */
    @RestController
    @RequestMapping("/api/routes")
    @Tag(name = "Route", description = "Endpoints for CRUD operations on routes")
    public static class RouteController
            extends BaseResourceController<Route, RouteDesc, RouteView, RouteService> {

        @Autowired
        private RouteService service;

        @Override
        public ResponseEntity<RouteView> create(@RequestBody final RouteDesc desc) {
            final var x = desc.getStart();
            final var y = desc.getEnd();

            return super.create(desc);
        }


    }

    /**
     * Offers the endpoints for managing steps.
     */
    @RestController
    @RequestMapping("/api/routes/{id}/steps")
    @Tag(name = "Route", description = "Endpoints for linking routes to steps")
    public static class RoutesToSteps
            extends BaseResourceChildController<EntityLinkerService.RouteStepsLinker,
            Route, RouteView> { }

    @RestController
    @RequestMapping("/api/routes/{id}/resources")
    @Tag(name = "Route", description = "Endpoints for linking routes to offered resources")
    public static class RoutesToOfferedResources
            extends BaseResourceChildController<EntityLinkerService.RouteOfferedResourceLinker,
            OfferedResource, OfferedResourceView> { }
}
