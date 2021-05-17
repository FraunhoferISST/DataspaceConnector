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

/**
 * The base factory for factory classes.
 * This class creates and updates an entity by using a supplied description.
 * @param <T> The type of the entity.
 * @param <D> The type of the description.
 */
public interface AbstractFactory<T extends AbstractEntity, D extends AbstractDescription<T>> {
    /**
     * Create a new entity.
     * @param desc The description of the entity.
     * @return The new entity.
     */
    T create(D desc);

    /**
     * Update an entity.
     * @param entity The entity to be updated.
     * @param desc The description of the new entity.
     * @return true if changes where performed.
     */
    boolean update(T entity, D desc);
}
