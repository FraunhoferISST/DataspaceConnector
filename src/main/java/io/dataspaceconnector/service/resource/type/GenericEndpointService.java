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
package io.dataspaceconnector.service.resource.type;

import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.model.endpoint.GenericEndpointDesc;
import io.dataspaceconnector.model.endpoint.GenericEndpointFactory;
import io.dataspaceconnector.repository.BaseEntityRepository;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.service.routing.RouteHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service class for generic endpoints.
 */
@Service
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.NONE)
@Transactional
public class GenericEndpointService
        extends EndpointService<GenericEndpoint, GenericEndpointDesc> {

    /**
     * Data source repository.
     */
    private final @NonNull DataSourceService dataSourceSvc;

    /**
     * Constructor.
     *
     * @param repository        Generic endpoint repository.
     * @param factory           The generic endpoint factory.
     * @param routeRepository   the service for managing routes.
     * @param camelRouteHelper  The helper class for Camel routes.
     * @param dataSourceService The datasource service.
     */
    public GenericEndpointService(
            final BaseEntityRepository<GenericEndpoint> repository,
            final AbstractFactory<GenericEndpoint, GenericEndpointDesc> factory,
            final RouteRepository routeRepository,
            final RouteHelper camelRouteHelper,
            final DataSourceService dataSourceService) {
        super(repository, factory, routeRepository, camelRouteHelper);
        this.dataSourceSvc = dataSourceService;
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
}
