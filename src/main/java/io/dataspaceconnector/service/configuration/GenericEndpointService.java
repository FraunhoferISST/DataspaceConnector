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

import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.model.endpoint.GenericEndpointDesc;
import io.dataspaceconnector.model.endpoint.GenericEndpointFactory;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.service.configuration.util.RouteHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service class for generic endpoints.
 */
@Service
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.NONE)
public class GenericEndpointService
        extends EndpointService<GenericEndpoint, GenericEndpointDesc> {

    /**
     * Data source repository.
     */
    private final @NonNull DataSourceService dataSourceSvc;

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
     * @param dataSourceService The data source repository.
     * @param routeRepository The service for managing routes.
     * @param camelRouteHelper The helper class for Camel routes.
     */
    @Autowired
    public GenericEndpointService(final @NonNull DataSourceService dataSourceService,
                                  final @NonNull RouteRepository routeRepository,
                                  final @NonNull RouteHelper camelRouteHelper) {
        super();
        this.dataSourceSvc = dataSourceService;
        this.routeRepo = routeRepository;
        this.routeHelper = camelRouteHelper;
    }

    /**
     * This method allows to modify the generic endpoint and set a data source.
     *
     * @param endpointId   The id of the generic endpoint.
     * @param dataSourceId The new data source of the generic endpoint.
     */
    public void setGenericEndpointDataSource(final UUID endpointId,
                                             final UUID dataSourceId) {
        final var updated = ((GenericEndpointFactory) getFactory())
                .setDataSourceToGenericEndpoint(get(endpointId), dataSourceSvc.get(dataSourceId));
        persist(updated);
    }

    /**
     * Persists a generic endpoint. If an already existing endpoint is updated, the Camel routes
     * for all routes referencing the endpoint are recreated.
     *
     * @param endpoint the endpoint to persist.
     * @return the persisted endpoint.
     */
    @Override
    protected final GenericEndpoint persist(final GenericEndpoint endpoint) {
        if (endpoint.getId() != null) {
            final var affectedRoutes = routeRepo.findTopLevelRoutesByEndpoint(endpoint.getId());
            affectedRoutes.forEach(routeHelper::deploy);
        }

        return super.persist(endpoint);
    }
}
