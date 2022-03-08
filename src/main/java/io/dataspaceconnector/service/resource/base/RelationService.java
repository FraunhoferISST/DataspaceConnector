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

import io.dataspaceconnector.model.base.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

/**
 * Creates and modifies relations between two entity types.
 *
 * @param <K> Type of owner entity.
 * @param <W> Type of children entity.
 * @param <T> Type of owning entity service.
 * @param <X> Type of child entity service.
 */
public interface RelationService<K extends Entity, W extends Entity,
        T extends BaseEntityService<K, ?>, X extends EntityService<W, ?>> {

    /**
     * Get all children of an entity.
     *
     * @param ownerId  The id of the entity whose children should be received.
     * @param pageable The {@link Pageable} object for getting only a page of objects.
     * @return The ids of the children.
     * @throws IllegalArgumentException  if any of the passed arguments is null.
     * @throws io.dataspaceconnector.common.exception.ResourceNotFoundException
     *         if the ownerId entity does not exists.
     */
    Page<W> get(UUID ownerId, Pageable pageable);

    /**
     * Add a list of children to an entity. The children must exist.
     *
     * @param ownerId  The id of the entity that the children should be added to.
     * @param entities The children to be added.
     * @throws IllegalArgumentException  if any of the passed arguments is null.
     * @throws io.dataspaceconnector.common.exception.ResourceNotFoundException
     *         if any of the entities does not exists.
     */
    void add(UUID ownerId, Set<UUID> entities);

    /**
     * Remove a list of children from an entity.
     *
     * @param ownerId  The id of the entity that the children should be removed from.
     * @param entities The children to be removed.
     * @throws IllegalArgumentException  if any of the passed arguments is null.
     * @throws io.dataspaceconnector.common.exception.ResourceNotFoundException
     *         if any of the entities does not exists.
     */
    void remove(UUID ownerId, Set<UUID> entities);

    /**
     * Replace the children of an entity.
     *
     * @param ownerId  The id of the entity whose children should be replaced.
     * @param entities The new children for the entity.
     * @throws IllegalArgumentException  if any of the passed arguments is null.
     * @throws io.dataspaceconnector.common.exception.ResourceNotFoundException
     *         if any of the entities does not exists.
     */
    void replace(UUID ownerId, Set<UUID> entities);
}
