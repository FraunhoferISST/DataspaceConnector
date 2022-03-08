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
package io.dataspaceconnector.service.resource.base;

import io.dataspaceconnector.model.base.Description;
import io.dataspaceconnector.model.base.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Interface for entities.
 * @param <T> The type of the entity.
 * @param <D> The type of the description.
 */
public interface EntityService<T extends Entity, D extends Description> {
    /**
     * @param desc The description of the entity.
     * @return entity
     */
    T create(D desc);

    /**
     * @param entityId The id of the entity.
     * @param desc The description of the entity.
     * @return entity
     */
    T update(UUID entityId, D desc);

    /**
     * @param entityId The id of the entity.
     * @return entity
     */
    T get(UUID entityId);

    /**
     * @param pageable Holds the page request.
     * @return page
     */
    Page<T> getAll(Pageable pageable);

    /**
     * @param entityId The id of the entity.
     * @return true, if entity does exist.
     */
    boolean doesExist(UUID entityId);

    /**
     * @param entityId The id of the entity.
     */
    void delete(UUID entityId);
}
