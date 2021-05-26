package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Creates and updates a route.
 */
@Component
public class RouteFactory implements AbstractFactory<Route, RouteDesc> {

    private static final String DEFAULT_STRING = "Default configuration";

    /**
     * @param desc The description of the entity.
     * @return The new route entity.
     */
    @Override
    public Route create(final RouteDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var route = new Route();
        route.setStartIdsEndpoint(null);
        route.setStartGenericEndpoint(null);
        route.setEndIdsEndpoint(null);
        route.setEndGenericEndpoint(null);
        route.setSubRoutes(new ArrayList<>());
        route.setOfferedResources(new ArrayList<>());

        update(route, desc);

        return route;
    }

    /**
     * @param route The route.
     * @param desc  The description of the new entity.
     * @return True, if route is updated.
     */
    @Override
    public boolean update(final Route route, final RouteDesc desc) {
        Utils.requireNonNull(route, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedRouteConfig = updateRouteConfiguration(route, desc.getRouteConfiguration());
        final var hasUpatedDeployMethod = updateRouteDeployMethod(route, route.getDeployMethod());

        return hasUpdatedRouteConfig || hasUpatedDeployMethod;
    }

    /**
     * @param route        The route.
     * @param deployMethod The deploy method of the route.
     * @return True, if route deploy method is updated.
     */
    private boolean updateRouteDeployMethod(final Route route, final DeployMethod deployMethod) {
        final boolean updated;
        if (route.getDeployMethod().equals(deployMethod)) {
            updated = false;
        } else {
            route.setDeployMethod(deployMethod);
            updated = true;
        }
        return updated;
    }

    /**
     * @param route              The route.
     * @param routeConfiguration The route configuration.
     * @return True, if route configuration is updated.
     */
    private boolean updateRouteConfiguration(final Route route, final String routeConfiguration) {
        final var newRouteConfig = MetadataUtils.updateString(route.getRouteConfiguration(),
                routeConfiguration, DEFAULT_STRING);
        newRouteConfig.ifPresent(route::setRouteConfiguration);

        return newRouteConfig.isPresent();
    }
}
