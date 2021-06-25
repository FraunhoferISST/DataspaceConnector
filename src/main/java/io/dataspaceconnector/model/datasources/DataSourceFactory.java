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
package io.dataspaceconnector.model.datasources;

import java.net.URI;
import java.util.Objects;

import io.dataspaceconnector.model.auth.Authentication;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.utils.MetadataUtils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates data sources.
 */
@Component
public class DataSourceFactory extends AbstractFactory<DataSource, DataSourceDesc> {

    /**
     * The default string.
     */
    private static final URI DEFAULT_NAME = URI.create("");

    private static final DataSourceType DEFAULT_SOURCE_TYPE = DataSourceType.DATABASE;

    /**
     * @param desc The description of the entity.
     * @return The new data source entity.
     */
    @Override
    protected DataSource initializeEntity(final DataSourceDesc desc) {
        return new DataSource();
    }

    /**
     * @param dataSource The data source entity.
     * @param desc       The description of the new entity.
     * @return True, if data source is updated.
     */
    @Override
    protected boolean updateInternal(final DataSource dataSource, final DataSourceDesc desc) {
        final var hasUpdatedName = updateName(dataSource, desc.getName());
        final var hasUpdatedDataSourceType = updateDataSourceType(dataSource, desc.getType());

        return hasUpdatedName || hasUpdatedDataSourceType;
    }

    /**
     * @param dataSource The data source entity.
     * @param name   The relative path of the data source.
     * @return True, if data source type is updated.
     */
    private boolean updateName(final DataSource dataSource, final URI name) {
        final var newName =
                MetadataUtils.updateUri(dataSource.getName(), name, DEFAULT_NAME);
        newName.ifPresent(dataSource::setName);

        return newName.isPresent();
    }

    /**
     * @param dataSource     The data source entity.
     * @param dataSourceType The type of the data source.
     * @return True, if data source type is updated.
     */
    private boolean updateDataSourceType(final DataSource dataSource,
                                         final DataSourceType dataSourceType) {
        dataSource.setType(Objects.requireNonNullElse(dataSourceType, DEFAULT_SOURCE_TYPE));
        return true;
    }

    /**
     * @param dataSource     The entity to be updated.
     * @param authentication The updated authentication.
     * @return updated entity.
     */
    public DataSource updateAuthentication(final DataSource dataSource,
                                           final Authentication authentication) {
        dataSource.setAuthentication(authentication);
        return dataSource;
    }

    /**
     * @param dataSource The entity to be updated.
     * @return updated entity without authentication.
     */
    public DataSource deleteAuthentication(final DataSource dataSource) {
        dataSource.setAuthentication(null);
        return dataSource;
    }
}
