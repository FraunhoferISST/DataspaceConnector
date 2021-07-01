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

import io.dataspaceconnector.model.datasource.DataSource;
import io.dataspaceconnector.model.datasource.DataSourceDesc;
import io.dataspaceconnector.model.datasource.DataSourceFactory;
import io.dataspaceconnector.repositories.DataSourceRepository;
import io.dataspaceconnector.services.resources.BaseEntityService;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service class for data sources.
 */
@Service
@Getter(AccessLevel.PACKAGE)
@RequiredArgsConstructor
public class DataSourceService extends BaseEntityService<DataSource, DataSourceDesc> {

    private final @NonNull DataSourceRepository dataSourceRepository;

    private final @NonNull DataSourceFactory dataSourceFactory;

    @Override
    public void delete(final UUID entityId) {
        Utils.requireNonNull(entityId, ErrorMessages.ENTITYID_NULL);
        var dataSource = dataSourceRepository.getById(entityId);

        if(dataSource.getAuthentication()!=null){
            dataSourceFactory.removeAuthentication(dataSource);
        }
        dataSourceRepository.delete(dataSource);
    }
}
