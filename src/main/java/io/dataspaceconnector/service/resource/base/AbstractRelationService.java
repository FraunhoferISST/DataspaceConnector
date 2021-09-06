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
package io.dataspaceconnector.service.resource.base;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.base.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * Creates a parent-children relationship between two types of resources.
 *
 * @param <K> The type of the parent resource.
 * @param <W> The type of the child resource.
 * @param <T> The service type for the parent resource.
 * @param <X> The service type for the child resource.
 */
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public abstract class AbstractRelationService<K extends Entity, W extends Entity,
        T extends BaseEntityService<K, ?>, X extends EntityService<W, ?>>
        implements RelationService<K, W, T, X> {

    /*
        NOTE: Pretty much all functions will throw a ResourceNotFoundException but they are not
        added to the function signature. The basic idea here was, that a request to an missing
        entity can only come from an user and in most cases will be handled by an
        ResourceNotFoundExceptionHandler. By handling the exception this way the calling controller
        does not need to known (and thus not care for the case) how an invalid request should be
        handled.
     */

    /**
     * The service for the entity whose relations are modified.
     **/
    @Autowired
    private T oneService;

    /**
     * The service for the children.
     **/
    @Autowired
    private X manyService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<W> get(final UUID ownerId, final Pageable pageable) {
        Utils.requireNonNull(ownerId, ErrorMessage.ENTITYID_NULL);
        Utils.requireNonNull(pageable, ErrorMessage.PAGEABLE_NULL);

        final var owner = oneService.get(ownerId);
        return getInternal(owner, pageable);
    }

    /**
     * Receives the list of children assigned to the entity.
     *
     * @param owner The entity whose children should be received.
     * @return The children assigned to the entity.
     */
    protected abstract List<W> getInternal(K owner);

    /**
     * Receives a page of children assigned to the entity.
     *
     * @param owner    The entity whose children should be received.
     * @param pageable The children assigned to the entity.
     * @return The page of the children entities.
     */
    protected Page<W> getInternal(final K owner, final Pageable pageable) {
        final var entities = getInternal(owner);
        return Utils.toPage(entities, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(final UUID ownerId, final Set<UUID> entities) {
        Utils.requireNonNull(ownerId, ErrorMessage.ENTITYID_NULL);
        Utils.requireNonNull(entities, ErrorMessage.ENTITYSET_NULL);

        if (entities.isEmpty()) {
            // Prevent read call to database for the owner.
            return;
        }

        throwIfEntityDoesNotExist(entities);

        addInternal(ownerId, entities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(final UUID ownerId, final Set<UUID> entities) {
        Utils.requireNonNull(ownerId, ErrorMessage.ENTITYID_NULL);
        Utils.requireNonNull(entities, ErrorMessage.ENTITYSET_NULL);

        if (entities.isEmpty()) {
            // Prevent read call to database for the owner.
            return;
        }

        throwIfEntityDoesNotExist(entities);

        removeInternal(ownerId, entities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replace(final UUID ownerId, final Set<UUID> entities) {
        Utils.requireNonNull(ownerId, ErrorMessage.ENTITYID_NULL);
        Utils.requireNonNull(entities, ErrorMessage.ENTITYSET_NULL);
        throwIfEntityDoesNotExist(entities);

        replaceInternal(ownerId, entities);
    }

    /**
     * Check if all entities in a set are known to the children's service.
     *
     * @param entities The set of entities to be checked.
     * @throws ResourceNotFoundException if any of the entities is unknown.
     */
    private void throwIfEntityDoesNotExist(final Set<UUID> entities) {
        if (!doesExist(entities, (x) -> manyService.doesExist(x))) {
            throw new ResourceNotFoundException("Could not find resource.");
        }
    }

    /**
     * Check if all entities in a set are known.
     *
     * @param entities         The set of entities to be checked.
     * @param doesElementExist The function that evaluates if an entity does exist.
     * @return true if all entities are known.
     */
    private boolean doesExist(
            final Set<UUID> entities, final Function<UUID, Boolean> doesElementExist) {
        for (final var entity : entities) {
            if (!doesElementExist.apply(entity)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Adds a list of children to an entity.
     *
     * @param ownerId ID of the owning entity.
     * @param entities list of the children's IDs.
     */
    protected abstract void addInternal(UUID ownerId, Set<UUID> entities);

    /**
     * Removes a list of children from an entity.
     *
     * @param ownerId ID of the owning entity.
     * @param entities list of the children's IDs.
     */
    protected abstract void removeInternal(UUID ownerId, Set<UUID> entities);

    /**
     * Replaces the list of children for an entity.
     *
     * @param ownerId ID of the owning entity.
     * @param entities list of the children's IDs.
     */
    protected abstract void replaceInternal(UUID ownerId, Set<UUID> entities);
}
