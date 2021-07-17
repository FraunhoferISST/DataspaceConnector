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

import io.configmanager.extensions.routes.camel.RouteManager;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.model.endpoint.GenericEndpointDesc;
import io.dataspaceconnector.model.endpoint.GenericEndpointFactory;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.service.ids.builder.IdsAppRouteBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * Service for managing Camel routes.
     */
    private final @NonNull RouteManager routeManager;

    /**
     * Service for creating IDS AppRoutes from routes.
     */
    private final @NonNull IdsAppRouteBuilder appRouteBuilder;

    /**
     * Constructor for injection.
     *
     * @param dataSourceService The data source repository.
     * @param routeRepository the service for managing routes.
     * @param camelRouteManager the Camel route manager.
     * @param idsAppRouteBuilder the AppRoute builder.
     */
    @Autowired
    public GenericEndpointService(final @NonNull DataSourceService dataSourceService,
                                  final @NonNull RouteRepository routeRepository,
                                  final @NonNull RouteManager camelRouteManager,
                                  final @NonNull IdsAppRouteBuilder idsAppRouteBuilder) {
        super();
        this.dataSourceSvc = dataSourceService;
        this.routeRepo = routeRepository;
        this.routeManager = camelRouteManager;
        this.appRouteBuilder = idsAppRouteBuilder;
    }

    /**
     * This method allows to modify the generic endpoint and set a data source.
     * @param endpointId The id of the generic endpoint.
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
            affectedRoutes.forEach(r -> routeManager
                    .createAndDeployXMLRoute(appRouteBuilder.create(r)));
        }

        return super.persist(endpoint);
    }
}
