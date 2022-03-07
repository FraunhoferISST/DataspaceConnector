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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Creates a parent-children relationship between two types of resources.
 * Implements the owning side of a relationship.
 *
 * @param <K> The type of the parent resource. (The owning side)
 * @param <W> The type of the child resource.
 * @param <T> The service type for the parent resource.
 * @param <X> The service type for the child resource.
 */
public abstract class OwningRelationService<
        K extends Entity, W extends Entity, T extends BaseEntityService<K, ?>, X
        extends EntityService<W, ?>> extends AbstractRelationService<K, W, T, X> {

    @Override
    protected final void addInternal(final UUID ownerId, final Set<UUID> entities) {
        final var owner = getOneService().get(ownerId);
        addInternal(owner, entities);
        getOneService().persist(owner);
    }

    @Override
    protected final void removeInternal(final UUID ownerId, final Set<UUID> entities) {
        final var owner = getOneService().get(ownerId);
        final var existingEntities = getInternal(owner);

        for (final var entityId : entities) {
            existingEntities.removeIf(x -> x.getId().equals(entityId));
        }
        getOneService().persist(owner);
    }

    @Override
    protected final void replaceInternal(final UUID ownerId, final Set<UUID> entities) {
        final var owner = getOneService().get(ownerId);
        getInternal(owner).clear();
        addInternal(owner, entities);
        getOneService().persist(owner);
    }

    /**
     * Adds children to an entity.
     *
     * @param owner    The entity that the children should be assigned to.
     * @param entities The children added to the entity.
     */
    protected void addInternal(final K owner, final Set<UUID> entities) {
        final var existingEntities = getInternal(owner);
        final var existingIds =
                existingEntities.parallelStream().map(W::getId).collect(Collectors.toSet());

        final var toBeAdded = new HashSet<>(entities);
        toBeAdded.removeAll(existingIds);

        for (final var entityId : toBeAdded) {
            final var entity = getManyService().get(entityId);
            existingEntities.add(entity);
        }
    }
}
