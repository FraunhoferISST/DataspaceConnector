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

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.base.Description;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.repository.BaseEntityRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * The base service implements base logic for persistent entities.
 *
 * @param <T> The entity type.
 * @param <D> The description for the passed entity type.
 */
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
@RequiredArgsConstructor
public class BaseEntityService<T extends Entity, D extends Description>
    implements EntityService<T, D> {
    /**
     * Persists all entities of type T.
     **/
    private final @NonNull BaseEntityRepository<T> repository;

    /**
     * Contains creation and update logic for entities of type T.
     **/
    private final @NonNull AbstractFactory<T, D> factory;

    /**
     * Creates a new persistent entity.
     *
     * @param desc The description of the new entity.
     * @return The new entity.
     * @throws IllegalArgumentException if the desc is null.
     */
    @Override
    public T create(final D desc) {
        Utils.requireNonNull(desc, ErrorMessage.DESC_NULL);

        return persist(factory.create(desc));
    }

    /**
     * Updates an existing entity.
     *
     * @param entityId The id of the entity.
     * @param desc     The new description of the entity.
     * @return The updated entity.
     * @throws IllegalArgumentException  if any of the passed arguments is null.
     * @throws ResourceNotFoundException if the entity is unknown.
     */
    @Override
    public T update(final UUID entityId, final D desc) {
        Utils.requireNonNull(entityId, ErrorMessage.ENTITYID_NULL);
        Utils.requireNonNull(desc, ErrorMessage.DESC_NULL);

        var entity = get(entityId);

        if (factory.update(entity, desc)) {
            entity = persist(entity);
        }

        return entity;
    }

    /**
     * Get the entity for a given id.
     *
     * @param entityId The id of the entity.
     * @return The entity.
     * @throws IllegalArgumentException  if the passed id is null.
     * @throws ResourceNotFoundException if the entity is unknown.
     */
    @Override
    public T get(final UUID entityId) {
        Utils.requireNonNull(entityId, ErrorMessage.ENTITYID_NULL);

        final var entity = repository.findById(entityId);

        if (entity.isEmpty()) {
            // Handle with global exception handler.
            throw new ResourceNotFoundException(this.getClass().getSimpleName() + ": " + entityId);
        }

        return entity.get();
    }

    /**
     * Get a list of all entities with of the same type.
     *
     * @param pageable Range selection of the complete data set.
     * @return The id list of all entities.
     * @throws IllegalArgumentException if the passed pageable is null.
     */
    @Override
    public Page<T> getAll(final Pageable pageable) {
        Utils.requireNonNull(pageable, ErrorMessage.PAGEABLE_NULL);
        return repository.findAll(pageable);
    }

    /**
     * Checks if a entity exists for a given id.
     *
     * @param entityId The id of entity.
     * @return True if the entity exists.
     * @throws IllegalArgumentException if the passed id is null.
     */
    @Override
    public boolean doesExist(final UUID entityId) {
        Utils.requireNonNull(entityId, ErrorMessage.ENTITYID_NULL);
        return repository.findById(entityId).isPresent();
    }

    /**
     * Delete an entity with the given id.
     *
     * @param entityId The id of the entity.
     * @throws IllegalArgumentException if the passed id is null.
     */
    @Override
    public void delete(final UUID entityId) {
        Utils.requireNonNull(entityId, ErrorMessage.ENTITYID_NULL);
        repository.deleteById(entityId);
    }

    /**
     * Persists an entity.
     *
     * @param entity The entity.
     * @return The persisted entity.
     */
    protected T persist(final T entity) {
        return repository.saveAndFlush(entity);
    }
}
