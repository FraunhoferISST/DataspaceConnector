package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.exceptions.controller.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractDescription;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractFactory;
import de.fraunhofer.isst.dataspaceconnector.repositories.BaseEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The base service implements base logic for persistent entities.
 *
 * @param <T> The entity type.
 * @param <D> The description for the passed entity type.
 */
public class BaseEntityService<T extends AbstractEntity, D extends AbstractDescription<T>> {

    /**
     * Persists all entities of type T.
     **/
    @Autowired
    private BaseEntityRepository<T> repository;

    /**
     * Contains creation and update logic for entities of type T.
     **/
    @Autowired
    private AbstractFactory<T, D> factory;

    /**
     * Default constructor.
     */
    protected BaseEntityService() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Creates a new persistent entity.
     *
     * @param desc The description of the new entity.
     * @return The new entity.
     */
    public T create(final D desc) {
        return persist(factory.create(desc));
    }

    /**
     * Updates an existing entity.
     *
     * @param entityId The id of the entity.
     * @param desc     The new description of the entity.
     * @return The updated entity.
     */
    public T update(final UUID entityId, final D desc) {
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
     */
    public T get(final UUID entityId) {
        final var entity = repository.findById(entityId);

        if (entity.isEmpty()) {
            // Handle with global exception handler
            throw new ResourceNotFoundException(entityId.toString());
        }

        return entity.get();
    }

    /**
     * Get a list of all entities with of the same type.
     *
     * @param pageable Range selection of the complete data set.
     * @return The id list of all entities.
     */
    public Page<T> getAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    // public Stream<T> getAll(){return repository.}

    /**
     * Checks if a entity exists for a given id.
     *
     * @param entityId The id of entity.
     * @return True if the entity exists.
     */
    public boolean doesExist(final UUID entityId) {
        return repository.findById(entityId).isPresent();
    }

    /**
     * Delete an entity with the given id.
     *
     * @param entityId The id of the entity.
     */
    public void delete(final UUID entityId) {
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
