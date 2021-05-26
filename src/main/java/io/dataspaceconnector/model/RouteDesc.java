package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Describing route's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RouteDesc extends AbstractDescription<Route> {

    /**
     * The deploy method of the route.
     */
    private DeployMethod deployMethod;

    /**
     * List of subroutes.
     */
    private List<Route> subRoutes;

    /**
     * The route configuration.
     */
    private String routeConfiguration;

    /**
     * The possible start endpoint of the route.
     */
    private GenericEndpoint startGenericEndpoint;

    /**
     * The possible last endpoint of the route.
     */
    private GenericEndpoint endGenericEndpoint;

    /**
     * The possible start endpoint of the route.
     */
    private IdsEndpoint startIdsEndpoint;

    /**
     * The possible last endpoint of the route.
     */
    private IdsEndpoint endIdsEndpoint;

    /**
     * List of offered resources.
     */
    private List<OfferedResource> offeredResources;
}
