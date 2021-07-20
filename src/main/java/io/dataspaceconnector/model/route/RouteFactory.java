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
package io.dataspaceconnector.model.route;

import java.util.ArrayList;
import java.util.Objects;

import io.dataspaceconnector.model.configuration.DeployMethod;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.named.AbstractNamedFactory;
import io.dataspaceconnector.util.MetadataUtils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates a route.
 */
@Component
public class RouteFactory extends AbstractNamedFactory<Route, RouteDesc> {

    /**
     * The default string.
     */
    public static final String DEFAULT_CONFIGURATION = "Configuration";

    /**
     * @param desc The description of the entity.
     * @return The new route entity.
     */
    @Override
    protected Route initializeEntity(final RouteDesc desc) {
        final var route = new Route();
        route.setOutput(new ArrayList<>());

        return route;
    }

    /**
     * @param route The route.
     * @param desc  The description of the new entity.
     * @return True, if route is updated.
     */
    @Override
    protected boolean updateInternal(final Route route, final RouteDesc desc) {
        final var hasUpdatedRouteConfig = updateRouteConfiguration(route,
                desc.getConfiguration());
        final var hasUpdatedDeployMethod = updateRouteDeployMethod(route,
                desc.getDeploy());

        return hasUpdatedRouteConfig || hasUpdatedDeployMethod;
    }

    /**
     * @param route        The route.
     * @param deployMethod The deploy method of the route.
     * @return True, if route deploy method is updated.
     */
    private boolean updateRouteDeployMethod(final Route route, final DeployMethod deployMethod) {
        if (route.getDeploy() != null && route.getDeploy() == deployMethod) {
            return false;
        }

        route.setDeploy(Objects.requireNonNullElse(deployMethod, DeployMethod.NONE));
        return true;
    }

    /**
     * @param route              The route.
     * @param routeConfiguration The route configuration.
     * @return True, if route configuration is updated.
     */
    private boolean updateRouteConfiguration(final Route route, final String routeConfiguration) {
        final var newRouteConfig = MetadataUtils.updateString(route.getConfiguration(),
                routeConfiguration, DEFAULT_CONFIGURATION);
        newRouteConfig.ifPresent(route::setConfiguration);

        return newRouteConfig.isPresent();
    }

    /**
     * @param route    The route.
     * @param endpoint The start endpoint
     * @return The route with start endpoint.
     */
    public Route setStartEndpoint(final Route route, final Endpoint endpoint) {
        route.setStart(endpoint);
        return route;
    }

    /**
     * @param route    The route.
     * @param endpoint The last endpoint.
     * @return The route with last endpoint.
     */
    public Route setLastEndpoint(final Route route, final Endpoint endpoint) {
        route.setEnd(endpoint);
        return route;
    }

    /**
     * @param route The route.
     * @return The route without start endpoint.
     */
    public Route deleteStartEndpoint(final Route route) {
        route.setStart(null);
        return route;
    }

    /**
     * @param route The route.
     * @return The route without the last endpoint.
     */
    public Route deleteLastEndpoint(final Route route) {
        route.setEnd(null);
        return route;
    }

    /**
     * @param route The route.
     * @return The route without sub routes.
     */
    public final Route deleteSubroutes(final Route route) {
        route.setSteps(null);
        return route;
    }
}
