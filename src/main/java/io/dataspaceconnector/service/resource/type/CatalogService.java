/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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

import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.catalog.CatalogDesc;
import io.dataspaceconnector.repository.BaseEntityRepository;
import io.dataspaceconnector.service.resource.base.BaseEntityService;

/**
 * Handles the basic logic for catalogs.
 */
public class CatalogService extends BaseEntityService<Catalog, CatalogDesc> {

    /**
     * Constructor.
     *
     * @param repository The catalog repository.
     * @param factory    The catalog logic.
     */
    public CatalogService(final BaseEntityRepository<Catalog> repository,
                          final AbstractFactory<Catalog, CatalogDesc> factory) {
        super(repository, factory);
    }
}
