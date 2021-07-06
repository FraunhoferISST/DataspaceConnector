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

import io.dataspaceconnector.model.AbstractNamedFactory;
import io.dataspaceconnector.model.configuration.DeployMethod;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.utils.MetadataUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Creates and updates a route.
 */
@Component
public class RouteFactory extends AbstractNamedFactory<Route, RouteDesc> {

    /**
     * The default string.
     */
    private static final String DEFAULT_CONFIGURATION = "Default configuration";

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
        final var hasUpdatedStartEndpoint = updateStartEndpoint(route, desc.getStart());
        final var hasUpdatedLastEndpoint = updateLastEndpoint(route, desc.getEnd());

        return hasUpdatedRouteConfig || hasUpdatedDeployMethod || hasUpdatedStartEndpoint
                || hasUpdatedLastEndpoint;
    }

    private boolean updateLastEndpoint(final Route route, final Endpoint end) {
        if (route.getEnd() == null && end == null) {
            return false;
        }
        if (route.getEnd() != null && end == null) {
            route.setEnd(null);
            return true;
        }
        route.setEnd(end);
        return true;
    }

    private boolean updateStartEndpoint(final Route route, final Endpoint start) {
        if (route.getStart() == null && start == null) {
            return false;
        }
        if (route.getStart() != null && start == null) {
            route.setStart(null);
            return true;
        }
        route.setStart(start);
        return true;
    }

    /**
     * @param route        The route.
     * @param deployMethod The deploy method of the route.
     * @return True, if route deploy method is updated.
     */
    private boolean updateRouteDeployMethod(final Route route, final DeployMethod deployMethod) {
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
}
