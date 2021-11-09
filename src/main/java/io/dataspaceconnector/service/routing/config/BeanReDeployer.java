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
package io.dataspaceconnector.service.routing.config;

import io.dataspaceconnector.model.datasource.DatabaseDataSource;
import io.dataspaceconnector.repository.DataSourceRepository;
import io.dataspaceconnector.service.routing.BeanManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Re-creates beans for persisted data sources and adds them to the application context on
 * application start.
 */
@Component("beanReDeployer")
@RequiredArgsConstructor
@Log4j2
public class BeanReDeployer {

    /**
     * Creates and removes datasource beans.
     */
    private final @NonNull BeanManager beanManager;

    /**
     * Repository for data sources.
     */
    private final @NonNull DataSourceRepository repository;

    /**
     * Re-creates the datasource beans for all persisted DataSources.
     */
    @PostConstruct
    public void recreateDataSourceBeans() {
        final var dataSources = repository.findAll();
        for (var dataSource : dataSources) {
            if (dataSource instanceof DatabaseDataSource) {
                try {
                    beanManager.createDataSourceBean((DatabaseDataSource) dataSource);
                    if (log.isDebugEnabled()) {
                        log.debug("Added datasource bean to the application context."
                                + " [id=({})]", dataSource.getId());
                    }
                } catch (BeanCreationException exception) {
                    if (log.isWarnEnabled()) {
                        log.warn("Failed to recreate datasource bean. Some routes might not"
                                + " work correctly. [exception=({})]", exception.getMessage());
                    }
                }
            }
        }
    }

}
