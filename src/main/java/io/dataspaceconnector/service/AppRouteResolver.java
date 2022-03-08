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
package io.dataspaceconnector.service;

import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Start and stop apps depending on their AppEndpoints and use in Camel routes.
 */
@Service
@RequiredArgsConstructor
public class AppRouteResolver {

    /**
     * The camel context.
     */
    private final CamelContext camelContext;

    /**
     * Repo for finding top level routes.
     */
    private final RouteRepository routeRepository;

    /**
     * Check if app is used by any deployed camel route.
     *
     * @param app App for which usage is checked.
     * @return routeID, if some app endpoint of app is used by an active camel route.
     * Empty if it is not used.
     */
    public final Optional<String> isAppUsed(final App app) {
        final var endpoints = app.getEndpoints();
        for (final var endpoint : endpoints) {
            final var routes = routeRepository.findTopLevelRoutesByEndpoint(endpoint.getId());
            for (final var route : routes) {
                if (camelContext.getRoute(route.getId().toString()) != null) {
                    return Optional.of(route.getId().toString());
                }
            }
        }
        return Optional.empty();
    }
}
