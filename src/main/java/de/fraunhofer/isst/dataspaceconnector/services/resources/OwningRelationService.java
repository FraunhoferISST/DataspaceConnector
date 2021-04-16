package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;

/**
 * Creates a parent-children relationship between two types of resources.
 * Implements the owning side of a relationship.
 * @param <K> The type of the parent resource. (The owning side)
 * @param <W> The type of the child resource.
 * @param <T> The service type for the parent resource.
 * @param <X> The service type for the child resource.
 */
public abstract class OwningRelationService<
        K extends AbstractEntity, W extends AbstractEntity, T extends BaseEntityService<K, ?>, X
                extends BaseEntityService<W, ?>> extends AbstractRelationService<K, W, T, X> {

    @Override
    protected final void addInternal(final UUID ownerId, final Set<UUID> entities) {
        final var owner = getOneService().get(ownerId);
        addInternal(owner, entities);
        getOneService().persist(owner);
    }

    @Override
    protected final void removeInternal(final UUID ownerId, final Set<UUID> entities) {
        final var owner =  getOneService().get(ownerId);
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
