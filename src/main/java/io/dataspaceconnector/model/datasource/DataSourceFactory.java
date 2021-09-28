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

import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.model.auth.ApiKey;
import io.dataspaceconnector.model.auth.AuthenticationDesc;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.model.base.AbstractFactory;

/**
 * Creates and updates data sources.
 */
public class DataSourceFactory extends AbstractFactory<DataSource, DataSourceDesc> {

    /**
     * The default data source type.
     */
    public static final DataSourceType DEFAULT_SOURCE_TYPE = DataSourceType.DATABASE;

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
        final var hasUpdatedAuthentication = updateAuthentication(
                dataSource, desc.getBasicAuth(), desc.getApiKey());
        final var hasUpdatedDataSourceType = updateDataSourceType(
                dataSource, desc.getType());

        if (DataSourceType.DATABASE.equals(dataSource.getType())
                && dataSource.getAuthentication() instanceof ApiKey) {
            throw new InvalidEntityException("A data source of type DATABASE"
                    + " cannot have API key authentication.");
        }

        return hasUpdatedAuthentication || hasUpdatedDataSourceType;
    }

    /**
     * @param dataSource     The data source entity.
     * @param dataSourceType The type of the data source.
     * @return True, if data source type is updated.
     */
    private boolean updateDataSourceType(final DataSource dataSource,
                                         final DataSourceType dataSourceType) {
        final var tmp = dataSourceType == null ? DEFAULT_SOURCE_TYPE : dataSourceType;
        if (dataSource.getType() != null && dataSource.getType().equals(tmp)) {
            return false;
        }

        dataSource.setType(tmp);
        return true;
    }

    /**
     * @param dataSource     The entity to be updated.
     * @param basicAuth      The updated basic auth.
     * @param apiKey         The updates api key auth.
     * @return updated entity.
     */
    public boolean updateAuthentication(final DataSource dataSource,
                                        final AuthenticationDesc basicAuth,
                                        final AuthenticationDesc apiKey) {
        if (dataSource.getAuthentication() == null && basicAuth == null && apiKey == null) {
            return false;
        }

        if (basicAuth != null) {
            final var auth = new BasicAuth(basicAuth);
            if (auth.equals(dataSource.getAuthentication())) {
                return false;
            }
            dataSource.setAuthentication(auth);
            return true;
        } else if (apiKey != null) {
            final var auth = new ApiKey(apiKey.getKey(), apiKey.getValue());
            if (auth.equals(dataSource.getAuthentication())) {
                return false;
            }
            dataSource.setAuthentication(auth);
            return true;
        } else if (dataSource.getAuthentication() != null) {
            dataSource.setAuthentication(null);
            return true;
        }

        return false;
    }
}
