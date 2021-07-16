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

import javax.validation.Valid;
import java.util.UUID;

import io.dataspaceconnector.controller.resource.BaseResourceChildController;
import io.dataspaceconnector.controller.resource.BaseResourceController;
import io.dataspaceconnector.controller.resource.swagger.response.ResponseCodes;
import io.dataspaceconnector.controller.resource.swagger.response.ResponseDescriptions;
import io.dataspaceconnector.controller.resource.view.ArtifactView;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.model.route.RouteDesc;
import io.dataspaceconnector.service.configuration.EntityLinkerService;
import io.dataspaceconnector.service.configuration.RouteService;
import io.dataspaceconnector.view.route.RouteView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public static final class RouteController
            extends BaseResourceController<Route, RouteDesc, RouteView, RouteService> {

        /**
         * @param routeId The id of the route.
         * @param endpointId The id of the endpoint.
         * @return response status OK, if start endpoint is created.
         */
        @PutMapping("{id}/endpoint/start")
        @Operation(summary = "Creates start endpoint for the route")
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCodes.NO_CONTENT,
                description = ResponseDescriptions.NO_CONTENT),
                @ApiResponse(responseCode = ResponseCodes.UNAUTHORIZED,
                        description = ResponseDescriptions.UNAUTHORIZED)})
        public ResponseEntity<String> createStartEndpoint(
                @Valid @PathVariable(name = "id") final UUID routeId,
                @RequestBody final UUID endpointId) {
            getService().setStartEndpoint(routeId, endpointId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        /**
         * @param routeId The id of the route.
         * @return response status OK, if start endpoint is deleted.
         */
        @DeleteMapping("{id}/endpoint/start")
        @Operation(summary = "Deletes the start endpoint of the route")
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCodes.NO_CONTENT,
                description = ResponseDescriptions.NO_CONTENT),
                @ApiResponse(responseCode = ResponseCodes.UNAUTHORIZED,
                        description = ResponseDescriptions.UNAUTHORIZED)})
        public ResponseEntity<String> deleteStartEndpoint(
                @Valid @PathVariable(name = "id") final UUID routeId) {
            getService().removeStartEndpoint(routeId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        /**
         * @param routeId The id of the route.
         * @param endpointId The id of the endpoint.
         * @return response status OK, if last endpoint is created.
         */
        @PutMapping("{id}/endpoint/end")
        @Operation(summary = "Creates last endpoint for the route")
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCodes.NO_CONTENT,
                description = ResponseDescriptions.NO_CONTENT),
                @ApiResponse(responseCode = ResponseCodes.UNAUTHORIZED,
                        description = ResponseDescriptions.UNAUTHORIZED)})
        public ResponseEntity<String> createLastEndpoint(
                @Valid @PathVariable(name = "id") final UUID routeId,
                @RequestBody final UUID endpointId) {
            getService().setLastEndpoint(routeId, endpointId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        /**
         * @param routeId The id of the route
         * @return response status OK, if last endpoint is deleted.
         */
        @DeleteMapping("{id}/endpoint/end")
        @Operation(summary = "Deletes the start endpoint of the route")
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCodes.NO_CONTENT,
                description = ResponseDescriptions.NO_CONTENT),
                @ApiResponse(responseCode = ResponseCodes.UNAUTHORIZED,
                        description = ResponseDescriptions.UNAUTHORIZED)})
        public ResponseEntity<String> deleteLastEndpoint(
                @Valid @PathVariable(name = "id") final UUID routeId) {
            getService().removeLastEndpoint(routeId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
            Route, RouteView> {
    }

    /**
     * Offers the endpoint for managing route artifacts.
     */
    @RestController
    @RequestMapping("/api/routes/{id}/outputs")
    @Tag(name = "Route", description = "Endpoints for linking routes to offered resources")
    public static class RoutesToArtifacts
            extends BaseResourceChildController<EntityLinkerService.RouteArtifactsLinker,
            Artifact, ArtifactView> {
    }
}
