package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class RouteDesc extends AbstractDescription<Route>{

    private DeployMethod deployMethod;

    private List<Route> subRoutes;

    private String routeConfiguration;

    private Endpoint startEndpoint;

    private Endpoint endpoint;

    private List<OfferedResource> offeredResources;
}
