package de.fraunhofer.isst.dataspaceconnector.services.resources.v2;

import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseDescription;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseFactory;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseResource;
import de.fraunhofer.isst.dataspaceconnector.repositories.v2.BaseResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

/**
 * The base service implements base logic for persistent entities.
 *
 * @param <T> The entity type.
 * @param <D> The description for the passed entity type.
 */
public class BaseService<T extends BaseResource,
        D extends BaseDescription<T>> {

    /** Persists all entities of type T. **/
    @Autowired
    private BaseResourceRepository<T> repository;

    /** Contains creation and update logic for entities of type T. **/
    @Autowired
    private BaseFactory<T, D> factory;

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
     * @param id   The id of the entity.
     * @param desc The new description of the entity.
     * @return The updated entity.
     */
    public T update(final UUID id, final D desc) {
        var entity = get(id);

        if (factory.update(entity, desc)) {
            entity = persist(entity);
        }

        return entity;
    }

    /**
     * Get the entity for a given id.
     *
     * @param id The id of the entity.
     * @return The entity.
     */
    public T get(final UUID id) {
        final var entity = repository.findById(id);

        if (entity.isEmpty()) {
            // Handle with global exception handler
            throw new ResourceNotFoundException(id.toString());
        }

        return entity.get();
    }

    /**
     * Get a list of all entities with of the same type.
     *
     * @return The id list of all entities.
     */
    public List<UUID> getAll() {
        return repository.getAllIds();
    }

    /**
     * Checks if a entity exists for a given id.
     *
     * @param id The id of entity.
     * @return True if the entity exists.
     */
    public boolean doesExist(final UUID id) {
        return repository.findById(id).isPresent();
    }

    /**
     * Delete an entity with the given id.
     *
     * @param id The id of the entity.
     */
    public void delete(final UUID id) {
        final var entity = get(id);
        repository.deleteById(id);
    }

    /**
     * Persists an entity.
     *
     * @param entity The entity.
     * @return The persisted entity.
     */
    T persist(final T entity) {
        return repository.saveAndFlush(entity);
    }
}
