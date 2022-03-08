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
package io.dataspaceconnector.service.routing;

import io.dataspaceconnector.common.exception.RouteCreationException;
import io.dataspaceconnector.common.exception.RouteDeletionException;
import io.dataspaceconnector.model.configuration.DeployMethod;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.service.resource.ids.builder.IdsAppRouteBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Helper class for deploying Camel routes from {@link Route} objects.
 */
@Component
@RequiredArgsConstructor
public class RouteHelper {

    /**
     * Service for managing Camel routes.
     */
    private final @NonNull RouteManager routeManager;

    /**
     * Service for creating IDS AppRoutes from routes.
     */
    private final @NonNull IdsAppRouteBuilder appRouteBuilder;

    /**
     * Tries to deploy a Camel route from a route object. Maps the route to an ids
     * {@link de.fraunhofer.iais.eis.AppRoute} and calls the {@link RouteManager}. Creates Camel
     * routes only if the route deploy method is CAMEL and start and end of the route are defined.
     *
     * @param route the route.
     * @throws RouteCreationException if the Camel route cannot be created or deployed.
     */
    public void deploy(final Route route) throws RouteCreationException {
        if (shouldDeploy(route)) {
            routeManager.createAndDeployXMLRoute(appRouteBuilder.create(route));
        } else {
            // If a route should not be deployed after update, it might still be deployed in Camel.
            delete(route);
        }
    }

    /**
     * Checks whether a route should be deployed via Camel. If both start and end of the route are
     * undefined, the route should not be deployed. If the end of the route is null, the route
     * should only be deployed if it is linked to an artifact.
     *
     * @param route The route.
     * @return True, if the route should be deployed; false otherwise.
     */
    private boolean shouldDeploy(final Route route) {
        if (!DeployMethod.CAMEL.equals(route.getDeploy())) {
            return false;
        }

        if (route.getEnd() == null) {
            if (route.getStart() == null) {
                return false;
            }

            return route.getOutput() != null;
        }

        return true;
    }

    /**
     * Deletes the Camel route associated with a route.
     *
     * @param route the route.
     * @throws RouteDeletionException if the Camel route cannot be deleted.
     */
    public void delete(final Route route) throws RouteDeletionException {
        routeManager.deleteRoute(route);
    }

}
