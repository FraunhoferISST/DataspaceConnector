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
package io.dataspaceconnector.model.route;

import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.configuration.DeployMethod;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.named.AbstractNamedFactory;
import io.dataspaceconnector.model.util.FactoryUtils;

import java.util.Objects;

/**
 * Creates and updates a route.
 */
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
        return new Route();
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

        if (DeployMethod.NONE.equals(deployMethod) && route.getOutput() != null) {
            throw new InvalidEntityException("Route that is linked to an artifact must have deploy "
                    + "method CAMEL.");
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
        final var newRouteConfig = FactoryUtils.updateString(route.getConfiguration(),
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
     * Sets an artifact as a route's output.
     *
     * @param route The route.
     * @param artifact The artifact.
     * @return The route with output.
     */
    public final Route setOutput(final Route route, final Artifact artifact) {
        route.setOutput(artifact);
        return route;
    }

    /**
     * @param route The route.
     * @return The route without start endpoint.
     */
    public Route deleteStartEndpoint(final Route route) {
        if (route.getOutput() != null) {
            throw new InvalidEntityException("Route that is linked to an artifact must not have "
                    + "an undefined start.");
        }
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

    /**
     * Removes a route's output.
     *
     * @param route The route.
     * @return The route without output.
     */
    public final Route deleteOutput(final Route route) {
        route.setOutput(null);
        return route;
    }
}
