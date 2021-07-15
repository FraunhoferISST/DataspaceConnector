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
package io.dataspaceconnector.service.configuration;

import java.util.UUID;

import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.model.route.RouteDesc;
import io.dataspaceconnector.model.route.RouteFactory;
import io.dataspaceconnector.repository.EndpointRepository;
import io.dataspaceconnector.service.resource.BaseEntityService;
import io.dataspaceconnector.service.resource.EndpointServiceProxy;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for routes.
 */
@Service
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.NONE)
public class RouteService extends BaseEntityService<Route, RouteDesc> {

    /**
     * Repository for storing data.
     **/
    private final @NonNull EndpointRepository endpointRepo;

    /**
     * Service for all endpoints.
     */
    private final @NonNull EndpointServiceProxy endpointService;

    /**
     * Constructor for route service.
     * @param endpointRepository The endpoint repository.
     * @param endpointServiceProxy The endpoint service.
     */
    @Autowired
    public RouteService(final @NonNull EndpointRepository endpointRepository,
                        final @NonNull EndpointServiceProxy endpointServiceProxy) {
        super();
        this.endpointRepo = endpointRepository;
        this.endpointService = endpointServiceProxy;
    }

    @Override
    protected final Route persist(final Route route) {
        if (route.getStart() != null) {
            endpointRepo.save(route.getStart());
        }
        if (route.getEnd() != null) {
            endpointRepo.save(route.getEnd());
        }
        return super.persist(route);
    }

    /**
     * Set the start point of a route.
     * @param routeId The route id.
     * @param endpointId The endpoint id.
     */
    public void setStartEndpoint(final UUID routeId, final UUID endpointId) {
        final var routeTmp = get(routeId);
        final var endpoint = endpointService.get(endpointId);
        persist(((RouteFactory) getFactory()).setStartEndpoint(routeTmp, endpoint));
    }

    /**
     * Remove the start point of a route.
     * @param routeId The route id.
     */
    public void removeStartEndpoint(final UUID routeId) {
        final var routeTmp = get(routeId);
        persist(((RouteFactory) getFactory()).deleteStartEndpoint(routeTmp));
    }

    /**
     * Set the last point of a route.
     * @param routeId The route id.
     * @param endpointId The endpoint id.
     */
    public void setLastEndpoint(final UUID routeId, final UUID endpointId) {
        final var routeTmp = get(routeId);
        final var endpoint = endpointService.get(endpointId);
        persist(((RouteFactory) getFactory()).setLastEndpoint(routeTmp, endpoint));
    }

    /**
     * Remove the last point of a route.
     * @param routeId The route id.
     */
    public void removeLastEndpoint(final UUID routeId) {
        final var routeTmp = get(routeId);
        persist(((RouteFactory) getFactory()).deleteStartEndpoint(routeTmp));
    }
}
