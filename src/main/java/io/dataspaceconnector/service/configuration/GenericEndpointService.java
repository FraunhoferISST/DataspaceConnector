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
import io.dataspaceconnector.repository.DataSourceRepository;
import io.dataspaceconnector.repository.GenericEndpointRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final @NonNull DataSourceRepository dataSourceRepository;

    /**
     * Generic endpoint repository.
     */
    private final @NonNull GenericEndpointRepository genericEndpointRepository;

    /**
     * Constructor for injection.
     * @param genericRepository The generic endpoint repository.
     * @param repository        The data source repository.
     */
    @Autowired
    public GenericEndpointService(final @NonNull DataSourceRepository repository,
                                  final @NonNull GenericEndpointRepository genericRepository) {
        this.dataSourceRepository = repository;
        this.genericEndpointRepository = genericRepository;
    }

    /**
     * This method allows to modify the generic endpoint and set a data source.
     * @param genericEndpointId The id of the generic endpoint.
     * @param dataSourceId      The new data source of the generic endpoint.
     * @throws IOException Exception occurs, if data source can not be set at generic endpoint.
     */
    public void setGenericEndpointDataSource(final UUID genericEndpointId,
                                             final UUID dataSourceId) throws IOException {
        final var genericEndpoint = genericEndpointRepository
                .findById(genericEndpointId)
                .orElse(null);
        final var dataSource = dataSourceRepository.findById(dataSourceId).orElse(null);
        if (genericEndpoint != null && dataSource != null) {
            final var updatedGenericEndpoint = ((GenericEndpointFactory) getFactory())
                    .setDataSourceToGenericEndpoint(genericEndpoint, dataSource);
            genericEndpointRepository.saveAndFlush(updatedGenericEndpoint);
        } else {
            throw new IOException("Failed to set data source at the generic endpoint");
        }
    }

    /**
     * This method allows to delete the data source from a generic endpoint.
     *
     * @param genericEndpointId The id of the data source.
     * @throws IOException Exception occurs, if data source can not be deleted.
     */
    public void deleteGenericEndpointDataSource(final UUID genericEndpointId) throws IOException {
        final var genericEndpoint = genericEndpointRepository
                .findById(genericEndpointId)
                .orElse(null);
        if (genericEndpoint != null) {
            final var updatedGenericEndpoint = ((GenericEndpointFactory) getFactory())
                    .removeDataSource(genericEndpoint);
            genericEndpointRepository.saveAndFlush(updatedGenericEndpoint);
        } else {
            throw new IOException("Failed to delete the data source from the generic endpoint");
        }
    }
}
