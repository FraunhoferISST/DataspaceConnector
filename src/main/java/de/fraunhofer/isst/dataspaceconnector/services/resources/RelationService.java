package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
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
public interface RelationService<K extends AbstractEntity, W extends AbstractEntity,
        T extends BaseEntityService<K, ?>, X extends BaseEntityService<W, ?>> {

    /**
     * Get all children of an entity.
     *
     * @param ownerId  The id of the entity whose children should be received.
     * @param pageable The {@link Pageable} object for getting only a page of objects.
     * @return The ids of the children.
     * @throws IllegalArgumentException  if any of the passed arguments is null.
     * @throws ResourceNotFoundException if the ownerId entity does not exists.
     */
    Page<W> get(UUID ownerId, Pageable pageable);

    /**
     * Add a list of children to an entity. The children must exist.
     *
     * @param ownerId  The id of the entity that the children should be added to.
     * @param entities The children to be added.
     * @throws IllegalArgumentException  if any of the passed arguments is null.
     * @throws ResourceNotFoundException if any of the entities does not exists.
     */
    void add(UUID ownerId, Set<UUID> entities);

    /**
     * Remove a list of children from an entity.
     *
     * @param ownerId  The id of the entity that the children should be removed from.
     * @param entities The children to be removed.
     * @throws IllegalArgumentException  if any of the passed arguments is null.
     * @throws ResourceNotFoundException if any of the entities does not exists.
     */
    void remove(UUID ownerId, Set<UUID> entities);

    /**
     * Replace the children of an entity.
     *
     * @param ownerId  The id of the entity whose children should be replaced.
     * @param entities The new children for the entity.
     * @throws IllegalArgumentException  if any of the passed arguments is null.
     * @throws ResourceNotFoundException if any of the entities does not exists.
     */
    void replace(UUID ownerId, Set<UUID> entities);
}
