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
import io.dataspaceconnector.controller.resources.swagger.responses.ResponseCodes;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.model.route.RouteDesc;
import io.dataspaceconnector.model.route.RouteFactory;
import io.dataspaceconnector.repositories.EndpointRepository;
import io.dataspaceconnector.repositories.RouteRepository;
import io.dataspaceconnector.services.configuration.EntityLinkerService;
import io.dataspaceconnector.services.configuration.RouteService;
import io.dataspaceconnector.view.ArtifactView;
import io.dataspaceconnector.view.RouteView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

/**
 * Controller for route management.
 */
public final class RouteControllers {

    /**
     * Offers the endpoints for managing routes.
     */
    @RestController
    @RequestMapping("/api/routes")
    @Tag(name = "Route", description = "Endpoints for CRUD operations on routes")
    @RequiredArgsConstructor
    public static class RouteController
            extends BaseResourceController<Route, RouteDesc, RouteView, RouteService> {

        private final @NonNull RouteRepository routeRepository;
        private final @NonNull EndpointRepository endpointRepository;
        private final @NonNull RouteFactory routeFactory;

        @PutMapping("{id}/endpoint/start")
        @Operation(summary = "Creates start endpoint for the route")
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCodes.OK)})
        public ResponseEntity<String> createStartEndpoint(
                @Valid @PathVariable(name = "id") final UUID routeId,
                @RequestBody final UUID endpointId) {
            final var routeTmp = routeRepository.findById(routeId).orElse(null);
            final var endpoint = endpointRepository.findById(endpointId).orElse(null);
            if (routeTmp != null && endpoint != null) {
                final var updatedRoute = routeFactory.setStartEndpoint(routeTmp, endpoint);
                routeRepository.saveAndFlush(updatedRoute);
                return new ResponseEntity<>("Created the start endpoint for the route",
                        HttpStatus.OK);
            }
            return ResponseEntity.badRequest()
                    .body("Could not create start endpoint for the route");
        }

        @DeleteMapping("{id}/endpoint/start")
        @Operation(summary = "Deletes the start endpoint of the route")
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCodes.OK)})
        public ResponseEntity<String> deleteStartEndpoint(
                @Valid @PathVariable(name = "id") final UUID routeId) {
            final var routeTmp = routeRepository.findById(routeId).orElse(null);
            if (routeTmp != null) {
                final var updatedRoute = routeFactory.deleteStartEndpoint(routeTmp);
                routeRepository.saveAndFlush(updatedRoute);
                return new ResponseEntity<>("Deleted the start endpoint of the route",
                        HttpStatus.OK);
            }
            return ResponseEntity.badRequest()
                    .body("Could not delete the start endpoint of the route");
        }

        @PutMapping("{id}/endpoint/end")
        @Operation(summary = "Creates last endpoint for the route")
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCodes.OK)})
        public ResponseEntity<String> createLastEndpoint(
                @Valid @PathVariable(name = "id") final UUID routeId,
                @RequestBody final UUID endpointId) {
            final var routeTmp = routeRepository.findById(routeId).orElse(null);
            final var endpoint = endpointRepository.findById(endpointId).orElse(null);
            if (routeTmp != null && endpoint != null) {
                final var updatedRoute = routeFactory.setLastEndpoint(routeTmp, endpoint);
                routeRepository.saveAndFlush(updatedRoute);
                return new ResponseEntity<>("Created the last endpoint for the route",
                        HttpStatus.OK);
            }
            return ResponseEntity.badRequest()
                    .body("Could not create last endpoint for the route");
        }

        @DeleteMapping("{id}/endpoint/end")
        @Operation(summary = "Deletes the start endpoint of the route")
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCodes.OK)})
        public ResponseEntity<String> deleteLastEndpoint(
                @Valid @PathVariable(name = "id") final UUID routeId) {
            final var routeTmp = routeRepository.findById(routeId).orElse(null);
            if (routeTmp != null) {
                final var updatedRoute = routeFactory.deleteLastEndpoint(routeTmp);
                routeRepository.saveAndFlush(updatedRoute);
                return new ResponseEntity<>("Deleted the last endpoint of the route",
                        HttpStatus.OK);
            }
            return ResponseEntity.badRequest()
                    .body("Could not delete the last endpoint of the route");
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
    @RequestMapping("/api/routes/{id}/outputs")
    @Tag(name = "Route", description = "Endpoints for linking routes to offered resources")
    public static class RoutesToArtifacts
            extends BaseResourceChildController<EntityLinkerService.RouteArtifactsLinker,
            Artifact, ArtifactView> { }

//    @RestController
//    @RequestMapping("/api/routes/{id}/endpoints/start")
//    @Tag(name = "Route", description = "Endpoints for linking routes to the start endpoint")
//    public static class RoutesToStartEndpoints
//            extends BaseResourceChildController<EntityLinkerService.RouteStartEndpointLinker,
//            Endpoint, EndpointViewProxy> { }
//
//    @RestController
//    @RequestMapping("/api/routes/{id}/endpoints/end")
//    @Tag(name = "Route", description = "Endpoints for linking routes to the last endpoint")
//    public static class RoutesToEndpoints
//            extends BaseResourceChildController<EntityLinkerService.RouteLastEndpointLinker,
//            Endpoint, EndpointViewProxy> { }
}
