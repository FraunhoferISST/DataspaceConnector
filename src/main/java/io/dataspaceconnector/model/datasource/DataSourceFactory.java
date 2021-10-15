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
import io.dataspaceconnector.model.util.FactoryUtils;

/**
 * Creates and updates data sources.
 */
public class DataSourceFactory extends AbstractFactory<DataSource, DataSourceDesc> {

    /**
     * @param desc The description of the entity.
     * @return The new data source entity.
     */
    @Override
    protected DataSource initializeEntity(final DataSourceDesc desc) {
        if (desc instanceof DatabaseDataSourceDesc) {
            return new DatabaseDataSource();
        } else {
            return new DataSource();
        }
    }

    /**
     * @param dataSource The data source entity.
     * @param desc       The description of the new entity.
     * @return True, if data source is updated.
     * @throws InvalidEntityException if the desc type does not match the dataSource type or if
     *                                API key auth is used for type database.
     */
    @Override
    protected boolean updateInternal(final DataSource dataSource, final DataSourceDesc desc) {
        checkCorrectType(dataSource, desc);

        final var hasUpdatedAuthentication = updateAuthentication(
                dataSource, desc.getBasicAuth(), desc.getApiKey());

        if (DataSourceType.DATABASE.equals(dataSource.getType())
                && dataSource.getAuthentication() instanceof ApiKey) {
            throw new InvalidEntityException("A data source of type DATABASE"
                    + " cannot have API key authentication.");
        }

        final var hasUpdatedDatabaseProperties = updateDatabaseProperties(dataSource, desc);

        return hasUpdatedAuthentication || hasUpdatedDatabaseProperties;
    }

    private void checkCorrectType(final DataSource dataSource, final DataSourceDesc desc) {
        if (dataSource instanceof DatabaseDataSource && !(desc instanceof DatabaseDataSourceDesc)) {
            throw new InvalidEntityException("Cannot update datasource type.");
        }

        if (desc instanceof DatabaseDataSourceDesc && !(dataSource instanceof DatabaseDataSource)) {
            throw new InvalidEntityException("Cannot update datasource type.");
        }
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

    /**
     * Updates the database specific fields, if applicable.
     *
     * @param dataSource The entity to be updated.
     * @param desc       The updated descritpion.
     * @return whether the entity has been updated.
     */
    private boolean updateDatabaseProperties(final DataSource dataSource,
                                             final DataSourceDesc desc) {
        if (dataSource instanceof DatabaseDataSource && desc instanceof DatabaseDataSourceDesc) {
            final var databaseDataSource = (DatabaseDataSource) dataSource;
            final var databaseDataSourceDesc = (DatabaseDataSourceDesc) desc;

            final var updatedUrl = updateDatabaseUrl(databaseDataSource,
                    databaseDataSourceDesc.getUrl());
            final var updatedDriver = updateDriverClass(databaseDataSource,
                    databaseDataSourceDesc.getDriverClassName());

            return updatedUrl || updatedDriver;
        }

        return false;
    }

    /**
     * Updates the database URL.
     *
     * @param dataSource The entity to be updated.
     * @param url        The updated URL.
     * @return whether the entity has been updated.
     */
    private boolean updateDatabaseUrl(final DatabaseDataSource dataSource,
                                      final String url) {
        final var newUrl = FactoryUtils.updateString(dataSource.getUrl(), url, "");
        if (newUrl.isPresent()) {
            if (newUrl.get().isBlank()) {
                throw new InvalidEntityException("Database datasource must not have a blank URL.");
            }
            dataSource.setUrl(newUrl.get());
            return true;
        }

        return false;
    }

    /**
     * Updates the driver class name.
     *
     * @param dataSource  The entity to be updated.
     * @param driverClass The updated driver class name.
     * @return whether the entity has been updated.
     */
    private boolean updateDriverClass(final DatabaseDataSource dataSource,
                                      final String driverClass) {
        final var newDriver = FactoryUtils
                .updateString(dataSource.getDriverClassName(), driverClass, "");
        if (newDriver.isPresent()) {
            if (newDriver.get().isBlank()) {
                throw new InvalidEntityException("Database datasource must not have a blank "
                        + "driver class name.");
            }
            dataSource.setDriverClassName(newDriver.get());
            return true;
        }

        return false;
    }
}
