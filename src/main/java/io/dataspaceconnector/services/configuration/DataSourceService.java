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

import io.dataspaceconnector.model.Authentication;
import io.dataspaceconnector.model.datasources.DataSource;
import io.dataspaceconnector.model.datasources.DataSourceDesc;
import io.dataspaceconnector.model.datasources.DataSourceFactory;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.UUID;

/**
 * Service class for data sources.
 */
@Service
@NoArgsConstructor
public class DataSourceService extends BaseEntityService<DataSource, DataSourceDesc> {

    /**
     * This method allows to modify the data source and set an authentication.
     * @param dataSourceId   The id of the data source.
     * @param authentication The new authentication for the data source.
     * @throws IOException Exception occurs, if authentication can not be set at data source.
     */
    @Transactional
    public void setDataSourceAuthentication(final UUID dataSourceId,
                                            final Authentication authentication)
            throws IOException {
        final var dataSourceRepository = getRepository();
        final var dataSource = dataSourceRepository.findById(dataSourceId).orElse(null);
        if (dataSource != null && authentication != null) {
            final var updatedDataSource = ((DataSourceFactory) getFactory())
                    .updateAuthentication(dataSource, authentication);
            dataSourceRepository.saveAndFlush(updatedDataSource);
        } else {
            throw new IOException("Failed to update the data source");
        }
    }

    /**
     *
     * This method allows to delete the authentication from a data source.
     * @param dataSourceId The id of the data source.
     * @throws IOException Exception occurs, if authentication can not be deleted.
     */
    @Transactional
    public void deleteDataSourceAuthentication(final UUID dataSourceId) throws IOException {
        final var dataSourceRepository = getRepository();
        final var dataSource = dataSourceRepository.findById(dataSourceId).orElse(null);
        if (dataSource != null) {
            final var updatedDataSource = ((DataSourceFactory) getFactory())
                    .deleteAuthentication(dataSource);
            dataSourceRepository.saveAndFlush(updatedDataSource);
        } else {
            throw new IOException("Failed to delete the authentication from the data source.");
        }
    }
}
