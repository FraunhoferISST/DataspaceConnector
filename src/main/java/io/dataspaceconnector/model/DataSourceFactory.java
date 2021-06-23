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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates data sources.
 */
@Component
public class DataSourceFactory implements AbstractFactory<DataSource, DataSourceDesc> {

    /**
     * The default string.
     */
    private static final URI DEFAULT_LOCATION = URI.create("");

    private static final DataSourceType DEFAULT_SOURCE_TYPE = DataSourceType.DATABASE;

    /**
     * @param desc The description of the entity.
     * @return The new data source entity.
     */
    @Override
    public DataSource create(final DataSourceDesc desc) {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var dataSource = new DataSource();

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

        final var hasUpdatedRelativePath = updateLocation(dataSource, desc.getLocation());
        final var hasUpdatedDataSourceType = updateDataSourceType(dataSource, desc.getType());
        final var hasUpdatedAdditional = updateAdditional(dataSource, desc.getAdditional());

        return hasUpdatedRelativePath || hasUpdatedDataSourceType || hasUpdatedAdditional;
    }

    /**
     * @param dataSource The data source entity.
     * @param location   The relative path of the data source.
     * @return True, if data source type is updated.
     */
    private boolean updateLocation(final DataSource dataSource, final URI location) {
        final var newRelativePath =
                MetadataUtils.updateUri(dataSource.getLocation(), location, DEFAULT_LOCATION);
        newRelativePath.ifPresent(dataSource::setLocation);

        return newRelativePath.isPresent();
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
     * @param dataSource The entity to be updated.
     * @param additional The updated additional.
     * @return True, if additional is updated.
     */
    private boolean updateAdditional(final DataSource dataSource,
                                     final Map<String, String> additional) {
        final var newAdditional = MetadataUtils.updateStringMap(
                dataSource.getAdditional(), additional, new HashMap<>());
        newAdditional.ifPresent(dataSource::setAdditional);

        return newAdditional.isPresent();
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
