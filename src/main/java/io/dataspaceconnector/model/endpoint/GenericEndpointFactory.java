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
package io.dataspaceconnector.model.endpoint;

import io.dataspaceconnector.model.datasource.DataSource;
import org.springframework.stereotype.Component;

/**
 * Creates and updates generic endpoints.
 */
@Component
public class GenericEndpointFactory extends EndpointFactory<GenericEndpoint, GenericEndpointDesc> {

    /**
     * @param desc The description passed to the factory.
     * @return The app endpoint entity.
     */
    @Override
    protected GenericEndpoint initializeEntity(final GenericEndpointDesc desc) {
        return new GenericEndpoint();
    }

    /**
     * @param genericEndpoint The generic endpoint.
     * @param dataSource      The data source which is added to the endpoint.
     * @return generic endpoint
     */
    public GenericEndpoint setDataSourceToGenericEndpoint(final GenericEndpoint genericEndpoint,
                                                          final DataSource dataSource) {
        genericEndpoint.setDataSource(dataSource);
        return genericEndpoint;
    }

    /**
     * @param genericEndpoint The generic endpoint.
     * @return generic endpoint
     */
    public GenericEndpoint removeDataSource(final GenericEndpoint genericEndpoint) {
        genericEndpoint.setDataSource(null);
        return genericEndpoint;
    }
}
