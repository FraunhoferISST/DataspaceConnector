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
package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Creates and updates data sources.
 */
@Component
public class DataSourceFactory implements AbstractFactory<DataSource, DataSourceDesc> {

    /**
     * The default string.
     */
    private static final String DEFAULT_RELATIVE_PATH = "relativePath";

    /**
     * @param desc The description of the entity.
     * @return The new data source entity.
     */
    @Override
    public DataSource create(final DataSourceDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var dataSource = new DataSource();

        dataSource.setGenericEndpoint(new ArrayList<>());
        dataSource.setAuthentication(null);

        update(dataSource, desc);

        return dataSource;
    }

    /**
     * @param dataSource The data source entity.
     * @param desc       The description of the new entity.
     * @return True, if data source is updated.
     */
    @Override
    public boolean update(final DataSource dataSource, final DataSourceDesc desc) {
        Utils.requireNonNull(dataSource, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedRelativePath = updateRelativPath(dataSource,
                desc.getRelativePath());
        final var hasUpdatedDataSourceType = updateDataSourceType(dataSource,
                desc.getDataSourceType());

        return hasUpdatedRelativePath || hasUpdatedDataSourceType;
    }

    /**
     * @param dataSource   The data source entity.
     * @param relativePath The relative path of the data source.
     * @return True, if data source type is updated.
     */
    private boolean updateRelativPath(final DataSource dataSource, final String relativePath) {
        final var newRelativePath =
                MetadataUtils.updateString(dataSource.getRelativePath(), relativePath,
                        DEFAULT_RELATIVE_PATH);
        newRelativePath.ifPresent(dataSource::setRelativePath);

        return newRelativePath.isPresent();
    }

    /**
     * @param dataSource     The data source entity.
     * @param dataSourceType The type of the data source.
     * @return True, if data source type is updated.
     */
    private boolean updateDataSourceType(final DataSource dataSource,
                                         final DataSourceType dataSourceType) {
        final boolean updated;
        if (dataSource.getDataSourceType().equals(dataSourceType)) {
            updated = false;
        } else {
            dataSource.setDataSourceType(dataSourceType);
            updated = true;
        }
        return updated;
    }
}
