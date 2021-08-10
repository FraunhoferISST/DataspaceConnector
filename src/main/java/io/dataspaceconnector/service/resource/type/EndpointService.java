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
package io.dataspaceconnector.service.resource.type;

import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.endpoint.EndpointDesc;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.service.resource.base.BaseEntityService;
import io.dataspaceconnector.service.routing.RouteHelper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Handles the basic logic for endpoints.
 *
 * @param <T> The endpoint type.
 * @param <D> The endpoint description type.
 */
public class EndpointService<T extends Endpoint, D extends EndpointDesc>
        extends BaseEntityService<T, D> {

    /**
     * Service for managing routes.
     */
    private final @NonNull RouteRepository routeRepo;

    /**
     * Helper class for deploying and deleting Camel routes.
     */
    private final @NonNull RouteHelper routeHelper;

    /**
     * Constructor for injection.
     *
     * @param routeRepository the service for managing routes.
     * @param camelRouteHelper The helper class for Camel routes.
     */
    @Autowired
    public EndpointService(final @NonNull RouteRepository routeRepository,
                           final @NonNull RouteHelper camelRouteHelper) {
        super();
        this.routeRepo = routeRepository;
        this.routeHelper = camelRouteHelper;
    }

    /**
     * Persists a connector endpoint. If an already existing endpoint is updated, the Camel routes
     * for all routes referencing the endpoint are recreated.
     *
     * @param endpoint the endpoint to persist.
     * @return the persisted endpoint.
     */
    @Override
    protected final T persist(final T endpoint) {
        if (endpoint.getId() != null) {
            final var affectedRoutes = routeRepo.findTopLevelRoutesByEndpoint(endpoint.getId());
            affectedRoutes.forEach(routeHelper::deploy);
        }

        return super.persist(endpoint);
    }
}
