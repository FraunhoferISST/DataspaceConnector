package de.fraunhofer.isst.dataspaceconnector.services.resources.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseDescription;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class BaseUniDirectionalLinkerService<
        K extends BaseResource, KDesc extends BaseDescription<K>,
        W extends BaseResource, WDesc extends BaseDescription<W>,
        T extends BaseService<K, KDesc>, X extends BaseService<W, WDesc>> {

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
    }

    /**
     * Get all children of an entity.
     *
     * @param ownerId The id of the entity whose children should be received.
     * @return The ids of the children.
     */
    public Set<UUID> get(final UUID ownerId) {
        final var owner = oneService.get(ownerId);
        return getInternal(owner).keySet();
    }

    /**
     * Add a list of children to an entity.
     * The children must exist.
     *
     * @param ownerId  The id of the entity that the children should be added
     *                to.
     * @param entities The children to be added.
     */
    public void add(final UUID ownerId, final Set<UUID> entities) {
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
     */
    public void remove(final UUID ownerId,
                       final Set<UUID> entities) {
        final var owner = oneService.get(ownerId);

        removeInternal(owner, entities);

        oneService.persist(owner);
    }

    /**
     * Replace the children of an entity.
     *
     * @param ownerId  The id of the entity whose children should be replaced.
     * @param entities The new children for the entity.
     */
    public void replace(final UUID ownerId, final Set<UUID> entities) {
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
    protected abstract Map<UUID, W> getInternal(K owner);

    /**
     * Adds children to an entity.
     *
     * @param owner    The entity that the children should be assigned to.
     * @param entities The children added to the entity.
     */
    protected void addInternal(final K owner, final Set<UUID> entities) {
        for (final var entityId : entities) {
            Assert.isTrue(manyService.doesExist(entityId),
                    "The resource must exist.");
            final var entity = manyService.get(entityId);
            getInternal(owner).put(entityId, entity);
        }
    }

    /**
     * Remove children from an entity.
     *
     * @param owner    The entity that the children should be removed from.
     * @param entities The children to be removed.
     */
    protected void removeInternal(final K owner, final Set<UUID> entities) {
        for (final var entityId : entities) {
            Assert.isTrue(manyService.doesExist(entityId),
                    "The resource must exist.");
            getInternal(owner).remove(entityId);
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
}
