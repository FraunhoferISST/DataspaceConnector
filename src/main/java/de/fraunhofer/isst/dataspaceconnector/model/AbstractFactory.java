package de.fraunhofer.isst.dataspaceconnector.model;

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
