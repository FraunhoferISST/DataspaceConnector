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
package io.configmanager.extensions.routes.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.AppRoute;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import io.configmanager.extensions.routes.api.RoutesApi;
import io.configmanager.extensions.routes.api.service.RoutesService;
import io.configmanager.util.enums.RouteDeployMethod;
import io.configmanager.util.json.JsonUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * The api class implements the AppRouteApi and offers the possibilities to manage
 * the app routes in the configuration manager.
 */
@Slf4j
@RestController
@RequestMapping("/api/configmanager")
@Tag(name = "ConfigManager: Routes")
public class RoutesController implements RoutesApi {
    /**
     * Max. amount of route errors to be logged.
     */
    public static final int MAX_ERROR_LOG = 100;

    /**
     * The Service class for the Route-APIs.
     */
    private final RoutesService routesService;

    /**
     * The infomodel-serializer.
     */
    private final Serializer serializer;

    /**
     * Objectmapper used for mapping values to strings.
     */
    private final ObjectMapper objectMapper;

    /**
     * The temp. storage of route errors.
     */
    private final LinkedList<String> routeErrors = new LinkedList<>();

    /**
     * Constuctor of RoutesController.
     * @param routesService The Service class for the Route-APIs.
     * @param serializer The infomodel-serializer.
     * @param objectMapper Objectmapper used for mapping values to strings.
     */
    @Autowired
    public RoutesController(final RoutesService routesService,
                            final Serializer serializer,
                            final ObjectMapper objectMapper) {
        this.routesService = routesService;
        this.serializer = serializer;
        this.objectMapper = objectMapper;
    }

    /**
     * This method creates an app route.
     *
     * @param description description of the app route
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> createAppRoute(final String description) {
        ResponseEntity<String> response;

        final var appRoute = routesService.createAppRoute(description);

        if (appRoute != null) {
            final var jsonObject = new JSONObject();
            jsonObject.put("id", appRoute.getId().toString());
            jsonObject.put("message", "Created a new app route successfully");

            if (log.isInfoEnabled()) {
                log.info("Created app route successfully");
            }
            response = ResponseEntity.ok(jsonObject.toJSONString());
        } else {
            if (log.isInfoEnabled()) {
                log.info("Could not create app route");
            }
            response = ResponseEntity.badRequest().body("Can not create an app route");
        }

        return response;
    }

    /**
     * This method deletes an app route.
     *
     * @param routeId id of the app route
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> deleteAppRoute(final URI routeId) {
        ResponseEntity<String> response;

        final boolean deleted = routesService.deleteAppRoute(routeId);

        if (deleted) {
            if (log.isInfoEnabled()) {
                log.info("App route with id: " + routeId + " is deleted.");
            }

            response = ResponseEntity.ok(JsonUtils.jsonMessage("message",
                    "App route with id: " + routeId + " is deleted."));
        } else {
            if (log.isInfoEnabled()) {
                log.info("Could not delete app route with id: " + routeId);
            }

            response = ResponseEntity
                    .badRequest()
                    .body("Could not delete app route with id: " + routeId);
        }

        return response;
    }

    /**
     * This method returns a specific app route.
     *
     * @param routeId id of the app route
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> getAppRoute(final URI routeId) {
        ResponseEntity<String> response;

        final var appRoute = routesService.getAppRoute(routeId);

        if (appRoute != null) {
            try {
                final var appRouteString = serializer.serialize(appRoute);
                if (log.isInfoEnabled()) {
                    log.info("Returning app route");
                }
                response = ResponseEntity.ok(appRouteString);
            } catch (IOException e) {
                if (log.isErrorEnabled()) {
                    log.error("Problem while serializing app route!");
                }
                response = ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Could not serialize app route to jsonld");
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("Could not get app route with id: " + routeId);
            }
            response = ResponseEntity
                    .badRequest()
                    .body("Could not get app route with id: " + routeId);
        }

        return response;
    }

    /**
     * This method returns a list of app routes.
     *
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> getAppRoutes() {
        ResponseEntity<String> response;

        final var appRouteList = routesService.getAppRoutes();

        try {
            if (appRouteList == null) {
                if (log.isInfoEnabled()) {
                    log.info("Returning empty list since no app routes are present");
                }
                response = ResponseEntity.ok(serializer.serialize(new ArrayList<AppRoute>()));
            } else {
                if (log.isInfoEnabled()) {
                    log.info("Returning list of app routes");
                }
                response = ResponseEntity.ok(serializer.serialize(appRouteList));
            }
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Problem while serializing app routes list!");
                log.error(e.getMessage(), e);
            }
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * This method creates a route step for an app route with the given parameters.
     *
     * @param routeId          id of the route
     * @param startId          id of the start endpoint
     * @param startCoordinateX x coordinate of the start endpoint
     * @param startCoordinateY y coordinate of the start endpoint
     * @param endID            id of the last endpoint
     * @param endCoordinateX   x coordinate of the last endpoint
     * @param endCoordinateY   y coordinate of the last endpoint
     * @param resourceId       id of the resource
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> createAppRouteStep(final URI routeId,
                                                     final URI startId,
                                                     final int startCoordinateX,
                                                     final int startCoordinateY,
                                                     final URI endID,
                                                     final int endCoordinateX,
                                                     final int endCoordinateY,
                                                     final URI resourceId) {
        ResponseEntity<String> response;

        final var routeStep = routesService.createAppRouteStep(routeId, startId,
                endID, resourceId
                , startCoordinateX, startCoordinateY, endCoordinateX, endCoordinateY);

        if (routeStep != null) {
            final var jsonObject = new JSONObject();
            jsonObject.put("routeStepId", routeStep.getId().toString());
            jsonObject.put("message", "Successfully created the route step");

            if (log.isInfoEnabled()) {
                log.info("Successfully created the route step");
            }
            response = ResponseEntity.ok(jsonObject.toJSONString());
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Could not create the route step");
            }
            response = ResponseEntity.badRequest().body("Could not create the route step");
        }

        return response;
    }

    /**
     * This method returns information regarding the endpoint.
     *
     * @param routeId    id of the app route
     * @param endpointId id of the endpoint
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> getEndpointInformation(final URI routeId, final URI endpointId) {
        ResponseEntity<String> response;

        final var endpointInformation = routesService.getEndpointInformation(routeId, endpointId);

        if (endpointInformation != null) {
            try {
                final var endpointInfo = objectMapper.writeValueAsString(endpointInformation);
                if (log.isInfoEnabled()) {
                    log.info("Returning endpoint information");
                }
                response = ResponseEntity.ok(endpointInfo);
            } catch (JsonProcessingException e) {
                if (log.isErrorEnabled()) {
                    log.error("Could not parse endpoint Information to JSON!");
                    log.error(e.getMessage(), e);
                }
                response = ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Could not parse endpoint information to JSON");
            }
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Endpoint Information is null");
            }
            response = ResponseEntity.badRequest().body("Could not get endpoint information");
        }

        return response;
    }

    /**
     * This method updates the route deploy method of all app route and route steps.
     *
     * @param routeDeployMethod route deploy method
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> updateRouteDeployMethod(
            final RouteDeployMethod routeDeployMethod) {
        //TODO: set in DB (in Service-Class)
        return null;
    }

    /**
     * This method returns the route deploy method.
     *
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> getRouteDeployMethod() {
        //TODO: Get from DB (in Service-Class)
        return null;
    }

    /**
     * This method saves occuring route errors.
     *
     * @param routeError The route error to be saved
     * @return The response code for the API.
     */
    @Override
    public ResponseEntity<String> setRouteError(final String routeError) {
        if (routeErrors.size() >= MAX_ERROR_LOG) {
            routeErrors.remove(0);
        }

        routeErrors.add(routeError);

        return ResponseEntity.ok("Saved Route-Error in ConfigManager-backend.");
    }

    /**
     * This method returns all saved Route-Errors for the GET-API.
     *
     * @return Response-Code and all logged Route-Errors.
     */
    @Override
    public ResponseEntity<String> getRouteErrors() {
        return ResponseEntity.ok(routeErrors.stream().collect(Collectors.joining(",", "[", "]")));
    }

    /**
     * This method creates a generic endpoint with the given parameters.
     *
     * @param accessURL access url of the parameter
     * @param sourceType source type of the endpoint
     * @param username  username for the authentication
     * @param password  password for the authentication
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> createGenericEndpoint(final URI accessURL,
                                                        final String sourceType,
                                                        final String username,
                                                        final String password) {
        final var genericEndpoint = routesService
                .createGenericEndpoint(accessURL,
                        sourceType,
                        username,
                        password);
        final var jsonObject = new JSONObject();

        jsonObject.put("id", genericEndpoint.getId().toString());
        jsonObject.put("message", "Created a new generic endpoint");

        return ResponseEntity.ok(jsonObject.toJSONString());
    }

    /**
     * This method returns a list of generic endpoints.
     *
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> getGenericEndpoints() {
        ResponseEntity<String> response;

        final var endpoints = routesService.getGenericEndpoints();

        try {
            response = ResponseEntity.ok(serializer.serialize(endpoints));
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * This method deletes a generic endpoint.
     *
     * @param endpointId id of the generic endpoint
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> deleteGenericEndpoint(final URI endpointId) {
        ResponseEntity<String> response;

        final var deleted = routesService.deleteGenericEndpoint(endpointId);

        if (deleted) {
            response = ResponseEntity
                    .ok("Deleted the generic endpoint with id: " + endpointId);
        } else {
            response = ResponseEntity
                    .badRequest()
                    .body("Could not delete the generic endpoint with id: " + endpointId);
        }

        return response;
    }

    /**
     * This method updates a generic endpoint with the given parameters.
     *
     * @param endpointId id of the generic endpoint
     * @param accessURL  access url of the endpoint
     * @param sourceType source type of the endpoint
     * @param username   username for authentication
     * @param password   password for authentication
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> updateGenericEndpoint(final URI endpointId,
                                                        final URI accessURL,
                                                        final String sourceType,
                                                        final String username,
                                                        final String password) {
        ResponseEntity<String> response;

        final var updated = routesService
                .updateGenericEndpoint(endpointId,
                        accessURL,
                        sourceType,
                        username,
                        password);

        if (updated) {
            response = ResponseEntity.ok("Updated the generic endpoint with id: " + endpointId);
        } else {
            response = ResponseEntity
                    .badRequest()
                    .body("Could not update the generic endpoint with id: " + endpointId);
        }

        return response;
    }

    /**
     * This method creates a connector endpoint with given parameters.
     *
     * @param accessUrl access url of the endpoint
     * @param sourceType source type of the endpoint
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> createConnectorEndpoint(
            final URI accessUrl,
            final String sourceType) {
        //TODO: Get ConfigModel from DB (updated and maintained via GUI -> DSC)
//        final var configModelImpl = (ConfigurationModelImpl) configModelService.getConfigModel();
//        final var baseConnector = (BaseConnectorImpl) configModelImpl.getConnectorDescription();
//
//        if (baseConnector.getHasEndpoint() == null) {
//            baseConnector.setHasEndpoint(new ArrayList<>());
//        }
//
//        final var connectorEndpoints = (ArrayList<ConnectorEndpoint>) baseConnector
//        .getHasEndpoint();
//        final var connectorEndpoint = new ConnectorEndpointBuilder()
//        ._accessURL_(accessUrl).build();
//
//        connectorEndpoint.setProperty("ids:sourceType", sourceType);
//
//        connectorEndpoints.add(connectorEndpoint);
//
//        final var jsonObject = new JSONObject();
//
//        jsonObject.put("connectorEndpointId", connectorEndpoint.getId().toString());
//        jsonObject.put("message", "Created a new connector endpoint for the connector");
//
//        return ResponseEntity.ok(jsonObject.toJSONString());
        return null;
    }
}
