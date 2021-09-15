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
package io.dataspaceconnector.service;

import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.app.AppImpl;
import io.dataspaceconnector.model.endpoint.AppEndpoint;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.repository.AppRepository;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.service.appstore.portainer.PortainerRequestService;
import io.dataspaceconnector.service.resource.relation.AppEndpointLinker;
import io.dataspaceconnector.service.routing.RouteHelper;
import io.dataspaceconnector.service.routing.RouteManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.CamelContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Start and Stop Apps depending on their App Endpoints.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AppRouteResolver {

    /**
     * The camel context.
     */
    private final CamelContext camelContext;

    /**
     * The app repository.
     */
    private final AppRepository appRepository;

    /**
     * The app endpoint linker service.
     */
    private final AppEndpointLinker appEndpointLinker;

    /**
     * The portainer request service.
     */
    private final PortainerRequestService portainerRequestService;

    /**
     * Repo for finding top level routes.
     */
    private final RouteRepository routeRepository;

    /**
     * Deploy and Delete routes.
     */
    private final RouteHelper routeHelper;

    /**
     * The route manager.
     */
    private final RouteManager routeManager;

    /**
     * Stop all apps which use the stopped App.
     *
     * @param endpointId Endpoint of app that was stopped.
     */
    public final void stopRelatedRoutes(final UUID endpointId) {
        var affectedRoutes = routeRepository
                .findTopLevelRoutesByEndpoint(endpointId);
        affectedRoutes.forEach(routeHelper::delete);
    }

    /**
     * Start all apps for which all AppEndpoints are now active.
     *
     * @param endpointId Endpoint of app that was started.
     */
    public final void startRelatedRoutes(final UUID endpointId) {
        var allRoutes = routeRepository.findAllTopLevelRoutes();
        var startableRoutes = allRoutes.stream()
                .filter(route -> {
                    try {
                        return allEndpointsActive(route);
                    } catch (IOException e) {
                        if (log.isDebugEnabled()) {
                            log.debug(
                                    "Failed to check if endpoints of "
                                            + route.getId()
                                            + " are active. [exception=({})]",
                                    e.getMessage());
                        }
                        return false;
                    }
                })
                .collect(Collectors.toList());
        startableRoutes.forEach(routeHelper::deploy);
    }

    /**
     * Get all running app endpoints.
     *
     * @return List of all running App Endpoints.
     * @throws IOException when portainer is not reachable.
     */
    public final List<AppEndpoint> getRunningAppEndpoints() throws IOException {
        var apps = appRepository.findAll();
        var runningEndpoints = new ArrayList<AppEndpoint>();
        for (var app : apps) {
            var appImpl = (AppImpl) app;
            var containerId = appImpl.getContainerId();
            if (portainerRequestService.validateContainerRunning(containerId)) {
                runningEndpoints.addAll(app.getEndpoints());
            }
        }
        return runningEndpoints;
    }

    /**
     * Check if all given app endpoints are running.
     *
     * @param appEndpoints List of app endpoints to check.
     * @return true, if all app endpoints are active.
     * @throws IOException if portainer is not reachable.
     */
    public final boolean requiredEndpointsRunning(final List<AppEndpoint> appEndpoints)
            throws IOException {
        return getRunningAppEndpoints().containsAll(appEndpoints);
    }

    /**
     * Check if app is used by any deployed camel route.
     *
     * @param app app for which usage is checked.
     * @return routeID, if some app endpoint of app is used by an active camel route.
     *              Empty if it is not used.
     */
    public final Optional<String> isAppUsed(final App app) {
        var endpoints = app.getEndpoints();
        for (var endpoint : endpoints) {
            var routes = routeRepository.findTopLevelRoutesByEndpoint(endpoint.getId());
            for (var route : routes) {
                if (camelContext.getRoute("app-route_" + route.getId()) != null) {
                    return Optional.of("app-route_" + route.getId());
                }
            }
        }
        return Optional.empty();
    }

    private boolean allEndpointsActive(final Route route) throws IOException {
        var appEndpoints = getAllEndpoints(route).stream()
                .filter(endpoint -> endpoint instanceof AppEndpoint)
                .collect(Collectors.toList());

        for (var appEndpoint : appEndpoints) {
            if (!checkIfAppIsRunning(appEndpoint.getId())) {
                return false;
            }
        }
        return true;
    }

    private List<Endpoint> getAllEndpoints(final Route route) {
        var endpoints = new ArrayList<Endpoint>();
        endpoints.add(route.getStart());
        endpoints.add(route.getEnd());
        route.getSteps().forEach(step -> endpoints.addAll(getAllEndpoints(step)));
        return endpoints;
    }

    private boolean checkIfAppIsRunning(final UUID appEndpointId) throws IOException {
        final var listOfApps = appRepository.findAll();

        for (var app : listOfApps) {
            final var appImpl = (AppImpl) app;
            final var endpointList = appEndpointLinker.getInternal(app);
            if (endpointList.stream().map(AppEndpoint::getId)
                    .anyMatch(uuid -> uuid.equals(appEndpointId))) {
                return portainerRequestService.validateContainerRunning(appImpl.getContainerId());
            }
        }
        return false;
    }
}
