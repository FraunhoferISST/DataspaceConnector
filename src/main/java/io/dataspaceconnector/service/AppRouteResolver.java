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

import io.dataspaceconnector.model.endpoint.AppEndpoint;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.service.routing.RouteHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Start and Stop Apps depending on their App Endpoints.
 */
@Service
@RequiredArgsConstructor
public class AppRouteResolver {

    /**
     * Repo for finding top level routes.
     */
    private final RouteRepository routeRepository;

    /**
     * Deploy and Delete routes.
     */
    private final RouteHelper routeHelper;

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
                .filter(this::allEndpointsActive)
                .collect(Collectors.toList());
        startableRoutes.forEach(routeHelper::deploy);
    }

    private boolean allEndpointsActive(final Route route) {
        var appEndpoints = getAllEndpoints(route).stream()
                .filter(endpoint -> endpoint instanceof AppEndpoint)
                .collect(Collectors.toList());
        for (var appEndpoint : appEndpoints) {
            //TODO if the app, to which appEndpoint belongs, is not started, return false.
            appEndpoint.getId();
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

}
