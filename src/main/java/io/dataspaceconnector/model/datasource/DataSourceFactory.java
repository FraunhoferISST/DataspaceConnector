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
package io.dataspaceconnector.model.datasource;

import io.dataspaceconnector.model.auth.Authentication;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.utils.MetadataUtils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

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
        final var hasUpdatedLocation = updateLocation(dataSource, desc.getLocation());
        final var hasUpdatedAuthentication = updateAuthentication(dataSource, desc.getAuthentication());
        final var hasUpdatedDataSourceType = updateDataSourceType(dataSource, desc.getType());

        return hasUpdatedLocation || hasUpdatedAuthentication || hasUpdatedDataSourceType;
    }

    /**
     * @param dataSource The data source entity.
     * @param location   The relative path of the data source.
     * @return True, if data source type is updated.
     */
    private boolean updateLocation(final DataSource dataSource, final URI location) {
        final var newLocation =
                MetadataUtils.updateUri(dataSource.getLocation(), location, DEFAULT_NAME);
        newLocation.ifPresent(dataSource::setLocation);

        return newLocation.isPresent();
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
    public boolean updateAuthentication(final DataSource dataSource,
                                        final Authentication authentication) {
        if (dataSource.getAuthentication() == null && authentication == null){
            return false;
        }

        if(dataSource.getAuthentication()!=null && authentication == null){
            dataSource.setAuthentication(null);
            return true;
        }

        dataSource.setAuthentication(authentication);
        return true;
    }

    public void removeAuthentication(final DataSource dataSource) {
        dataSource.setAuthentication(null);
    }
}
