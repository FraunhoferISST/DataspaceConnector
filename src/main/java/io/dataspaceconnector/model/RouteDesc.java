package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
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
     * The start endpoint of the route.
     */
    private Endpoint startEndpoint;

    /**
     * The last endpoint of the route.
     */
    private Endpoint endpoint;

    /**
     * List of offered resources.
     */
    private List<OfferedResource> offeredResources;
}
