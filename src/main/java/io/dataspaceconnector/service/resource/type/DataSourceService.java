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
package io.dataspaceconnector.service.resource.type;

import java.util.UUID;

import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.datasource.DataSource;
import io.dataspaceconnector.model.datasource.DataSourceDesc;
import io.dataspaceconnector.model.datasource.DatabaseDataSource;
import io.dataspaceconnector.repository.BaseEntityRepository;
import io.dataspaceconnector.service.resource.base.BaseEntityService;
import io.dataspaceconnector.service.routing.BeanManager;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.BeanCreationException;

/**
 * Service class for data sources.
 */
@Log4j2
public class DataSourceService extends BaseEntityService<DataSource, DataSourceDesc> {

    /**
     * Creates and removes datasource beans.
     */
    private final @NonNull BeanManager beanManager;

    /**
     * Constructor.
     *
     * @param repository The dataSource repository.
     * @param factory    The dataSource factory.
     * @param manager    The manager for datasource beans.
     */
    public DataSourceService(final BaseEntityRepository<DataSource> repository,
                             final AbstractFactory<DataSource, DataSourceDesc> factory,
                             final @NonNull BeanManager manager) {
        super(repository, factory);
        this.beanManager = manager;
    }

    /**
     * Persists a DataSource. If the DataSource is of type database, a corresponding bean is also
     * created.
     *
     * @param dataSource The DataSource.
     * @return the persisted DataSource.
     */
    @Override
    protected DataSource persist(final DataSource dataSource) {
        final var persisted = super.persist(dataSource);
        if (dataSource instanceof DatabaseDataSource) {
            // Remove bean (if exists) and create new, in case any access information has changed.
            if (dataSource.getId() != null) {
                beanManager.removeDataSourceBean(dataSource.getId());
            }
            try {
                beanManager.createDataSourceBean((DatabaseDataSource) dataSource);
            } catch (BeanCreationException exception) {
                if (log.isDebugEnabled()) {
                    log.debug("Failed to create datasource bean. [exception=({})]",
                            exception.getMessage());
                }
                super.delete(persisted.getId());
                throw new InvalidEntityException("Failed to create datasource bean.");
            }
        }

        return persisted;
    }

    /**
     * Deletes a DataSource. Before deleting the entity, the corresponding bean is deleted.
     *
     * @param dataSourceId ID of the datasource to delete.
     */
    @Override
    public void delete(final UUID dataSourceId) {
        beanManager.removeDataSourceBean(dataSourceId);
        super.delete(dataSourceId);
    }
}
