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
import io.dataspaceconnector.model.resource.Resource;
import io.dataspaceconnector.model.resource.ResourceDesc;
import io.dataspaceconnector.repository.BaseEntityRepository;
import io.dataspaceconnector.service.resource.base.BaseEntityService;

/**
 * Handles the basic logic for resources.
 *
 * @param <T> The resource type.
 * @param <D> The resource description type.
 */
public class ResourceService<T extends Resource, D extends ResourceDesc>
        extends BaseEntityService<T, D> {

    /**
     * Constructor.
     *
     * @param repository The resource repository.
     * @param factory    The resource factory.
     */
    public ResourceService(final BaseEntityRepository<T> repository,
                           final AbstractFactory<T, D> factory) {
        super(repository, factory);
    }
}
