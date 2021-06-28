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
package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.endpoints.GenericEndpoint;
import io.dataspaceconnector.model.endpoints.GenericEndpointDesc;
import io.dataspaceconnector.repositories.DataSourceRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for generic endpoints.
 */
@Service
@NoArgsConstructor
public class GenericEndpointService
        extends EndpointService<GenericEndpoint, GenericEndpointDesc> {

    /**
     * Data source repository.
     */
    private DataSourceRepository dataSourceRepository;

    /**
     * Constructor for injection.
     * @param repository The data source repository.
     */
    @Autowired
    public GenericEndpointService(final DataSourceRepository repository) {
        this.dataSourceRepository = repository;
    }
//
//    /**
//     * This method allows to modify the generic endpoint and set a data source.
//     *
//     * @param genericEndpointId The id of the generic endpoint.
//     * @param dataSourceId      The new data source of the generic endpoint.
//     * @throws IOException Exception occurs, if data source can not be set at generic endpoint.
//     */
//    @Transactional
//    public void setGenericEndpointDataSource(final UUID genericEndpointId,
//                                             final UUID dataSourceId) throws IOException {
//        final var genericEndpointRepository = getRepository();
//        final var genericEndpoint = genericEndpointRepository
//                .findById(genericEndpointId)
//                .orElse(null);
//        final var dataSource = dataSourceRepository.findById(dataSourceId).orElse(null);
//        if (genericEndpoint != null && dataSource != null) {
//            final var updatedGenericEndpoint = ((GenericEndpointFactory) getFactory())
//                    .updateDataSource(genericEndpoint, dataSource);
//            genericEndpointRepository.saveAndFlush(updatedGenericEndpoint);
//        } else {
//            throw new IOException("Failed to update the generic endpoint");
//        }
//    }
//
//    /**
//     * This method allows to delete the data source from a generic endpoint.
//     *
//     * @param genericEndpointId The id of the data source.
//     * @throws IOException Exception occurs, if data source can not be deleted.
//     */
//    @Transactional
//    public void deleteGenericEndpointDataSource(final UUID genericEndpointId) throws IOException {
//        final var genericEndpointRepository = getRepository();
//        final var genericEndpoint = genericEndpointRepository
//                .findById(genericEndpointId)
//                .orElse(null);
//        if (genericEndpoint != null) {
//            final var updatedGenericEndpoint = ((GenericEndpointFactory) getFactory())
//                    .deleteDataSource(genericEndpoint);
//            genericEndpointRepository.saveAndFlush(updatedGenericEndpoint);
//        } else {
//            throw new IOException("Failed to delete the authentication from the data source.");
//        }
//    }
}
