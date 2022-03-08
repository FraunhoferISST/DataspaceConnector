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
import io.dataspaceconnector.model.endpoint.AppEndpointDesc;
import io.dataspaceconnector.model.endpoint.AppEndpointFactory;
import io.dataspaceconnector.model.endpoint.AppEndpointImpl;
import io.dataspaceconnector.repository.BaseEntityRepository;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.service.routing.RouteHelper;

/**
 * Service class for app endpoints.
 */
public class AppEndpointService extends EndpointService<AppEndpointImpl, AppEndpointDesc> {
    /**
     * Constructor for injection.
     *
     * @param repository The app endpoint repository.
     * @param factory The app endpoint factory.
     * @param routeRepository  the service for managing routes.
     * @param camelRouteHelper The helper class for Camel routes.
     */
    public AppEndpointService(
            final BaseEntityRepository<AppEndpointImpl> repository,
            final AbstractFactory<AppEndpointImpl, AppEndpointDesc> factory,
            final RouteRepository routeRepository,
            final RouteHelper camelRouteHelper) {
        super(repository, factory, routeRepository, camelRouteHelper);
    }

    /**
     * Sets the external exposed ports of an AppEndpoint.
     *
     * @param appEndpoint The app endpoint.
     * @param externalPort The external port.
     */
    public void setExternalEndpoint(final AppEndpointImpl appEndpoint,
                                    final int externalPort) {
        final var updatedAppEndpoint = ((AppEndpointFactory) getFactory())
                .setExternalPort(appEndpoint, externalPort);
        getRepository().save(updatedAppEndpoint);
    }

    /**
     * Sets the location of an AppEndpoint.
     *
     * @param appEndpoint The app endpoint.
     * @param location The location URI (access URL) of the AppEndpoint.
     */
    public void setLocation(final AppEndpointImpl appEndpoint, final String location) {
        final var updatedAppEndpoint = ((AppEndpointFactory) getFactory())
                .setLocation(appEndpoint, location);
        getRepository().save(updatedAppEndpoint);
    }
}
