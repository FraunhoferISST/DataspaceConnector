package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.fraunhofer.isst.dataspaceconnector.exceptions.handled.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Creates a parent-children relationship between two types of resources.
 *
 * @param <K> The type of the parent resource.
 * @param <W> The type of the child resource.
 * @param <T> The service type for the parent resource.
 * @param <X> The service type for the child resource.
 */
public abstract class BaseUniDirectionalLinkerService<
        K extends AbstractEntity, W extends AbstractEntity,
        T extends BaseEntityService<K, ?>, X extends BaseEntityService<W, ?>> {

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
     * Default constructor.
     */
    protected BaseUniDirectionalLinkerService() {
        // This constructor is intentionally empty. Nothing to do here.
    }

    /**
     * Get all children of an entity.
     *
     * @param ownerId The id of the entity whose children should be received.
     * @param pageable The {@link Pageable} object for getting only a page of objects.
     * @throws IllegalArgumentException if any of the passed arguments is null.
     * @throws ResourceNotFoundException if the ownerId entity does not exists.
     * @return The ids of the children.
     */
    public Page<W> get(final UUID ownerId, final Pageable pageable) {
        Utils.requireNonNull(ownerId, ErrorMessages.ENTITYID_NULL);
        Utils.requireNonNull(pageable, ErrorMessages.PAGEABLE_NULL);

        final var owner = oneService.get(ownerId);
        return getInternal(owner, pageable);
    }

    /**
     * Add a list of children to an entity.
     * The children must exist.
     *
     * @param ownerId  The id of the entity that the children should be added
     *                 to.
     * @param entities The children to be added.
     * @throws IllegalArgumentException if any of the passed arguments is null.
     * @throws ResourceNotFoundException if any of the entities does not exists.
     */
    public void add(final UUID ownerId, final Set<UUID> entities) {
        Utils.requireNonNull(ownerId, ErrorMessages.ENTITYID_NULL);
        Utils.requireNonNull(entities, ErrorMessages.ENTITYSET_NULL);

        if (entities.isEmpty()) {
            // Prevent read call to database for the owner.
            return;
        }

        throwIfEntityDoesNotExist(entities);

        final var owner = oneService.get(ownerId);

        addInternal(owner, entities);

        oneService.persist(owner);
    }

    /**
     * Remove a list of children from an entity.
     *
     * @param ownerId  The id of the entity that the children should be removed
     *                 from.
     * @param entities The children to be removed.
     * @throws IllegalArgumentException if any of the passed arguments is null.
     * @throws ResourceNotFoundException if any of the entities does not exists.
     */
    public void remove(final UUID ownerId,
                       final Set<UUID> entities) {
        Utils.requireNonNull(ownerId, ErrorMessages.ENTITYID_NULL);
        Utils.requireNonNull(entities, ErrorMessages.ENTITYSET_NULL);

        if (entities.isEmpty()) {
            // Prevent read call to database for the owner.
            return;
        }

        throwIfEntityDoesNotExist(entities);

        final var owner = oneService.get(ownerId);

        removeInternal(owner, entities);

        oneService.persist(owner);
    }

    /**
     * Replace the children of an entity.
     *
     * @param ownerId  The id of the entity whose children should be replaced.
     * @param entities The new children for the entity.
     * @throws IllegalArgumentException if any of the passed arguments is null.
     * @throws ResourceNotFoundException if any of the entities does not exists.
     */
    public void replace(final UUID ownerId, final Set<UUID> entities) {
        Utils.requireNonNull(ownerId, ErrorMessages.ENTITYID_NULL);
        Utils.requireNonNull(entities, ErrorMessages.ENTITYSET_NULL);
        throwIfEntityDoesNotExist(entities);

        final var owner = oneService.get(ownerId);

        replaceInternal(owner, entities);

        oneService.persist(owner);
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
     * @param owner The entity whose children should be received.
     * @param pageable The children assigned to the entity.
     * @return The page of the children entities.
     */
    protected Page<W> getInternal(final K owner, final Pageable pageable) {
        final var entities = getInternal(owner);
        return new PageImpl<>(entities, pageable, entities.size());
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
        final var copySet = new HashSet<>(entities);
        copySet.removeAll(existingIds);

        for (final var entityId : copySet) {
            final var entity = manyService.get(entityId);
            existingEntities.add(entity);
        }
    }

    /**
     * Remove children from an entity.
     *
     * @param owner    The entity that the children should be removed from.
     * @param entities The children to be removed.
     */
    protected void removeInternal(final K owner, final Set<UUID> entities) {
        final var existingEntities = getInternal(owner);

        for (final var entityId : entities) {
            existingEntities.removeIf(x -> x.getId().equals(entityId));
        }
    }

    /**
     * Replace the children of an entity.
     *
     * @param owner    The entity whose children should be replaced.
     * @param entities The new children.
     */
    protected void replaceInternal(final K owner, final Set<UUID> entities) {
        getInternal(owner).clear();
        addInternal(owner, entities);
    }

    /**
     * Check if all entities in a set are known to the children's service.
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
     * @param entities The set of entities to be checked.
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
}
