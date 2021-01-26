package de.fraunhofer.isst.dataspaceconnector.services.resources.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseDescription;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseResource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class BaseUniDirectionalLinkerService<
        K extends BaseResource, KDesc extends BaseDescription<K>,
        W extends BaseResource, WDesc extends BaseDescription<W>,
        T extends CommonService<K, KDesc>, X extends CommonService<W, WDesc>> {

    @Autowired
    private T oneService;
    @Autowired
    private X manyService;

    public Set<UUID> get(final EndpointId endpointId) {
        return get(oneService.getEndpoint(endpointId).getInternalId());
    }

    public Set<UUID> get(final UUID ownerId) {
        var owner = oneService.get(ownerId);
        return getInternal(owner).keySet();
    }

    public void add(final EndpointId ownerEndpointId,
                    final Set<UUID> entities) {
        add(getInternalId(ownerEndpointId), entities);
    }

    public void add(final UUID ownerId, final Set<UUID> entities) {
        var owner = oneService.get(ownerId);

        addInternal(owner, entities);

        oneService.persist(owner);
    }

    public void remove(final EndpointId ownerEndpointId,
                       final Set<UUID> entities) {
        remove(getInternalId(ownerEndpointId), entities);
    }

    public void remove(final UUID ownerId,
                       final Set<UUID> entities) {
        var owner = oneService.get(ownerId);

        removeInternal(owner, entities);

        oneService.persist(owner);
    }

    public void replace(final EndpointId ownerEndpointId,
                        final Set<UUID> entities) {
        replace(getInternalId(ownerEndpointId), entities);
    }

    public void replace(final UUID ownerId, final Set<UUID> entities) {
        var owner = oneService.get(ownerId);

        replaceInternal(owner, entities);

        oneService.persist(owner);
    }

    protected abstract Map<UUID, W> getInternal(K owner);

    protected void addInternal(final K owner, final Set<UUID> entities) {
        for (var entityId : entities) {
            Assert.isTrue(manyService.doesExist(entityId),
                    "The resource must exist.");
            final var entity = manyService.get(entityId);
            getInternal(owner).put(entityId, entity);
        }
    }

    protected void removeInternal(final K owner, final Set<UUID> entities) {
        for (var entityId : entities) {
            Assert.isTrue(manyService.doesExist(entityId),
                    "The resource must exist.");
            getInternal(owner).remove(entityId);
        }
    }

    protected void replaceInternal(final K owner, final Set<UUID> entities) {
        getInternal(owner).clear();
        addInternal(owner, entities);
    }

    protected UUID getInternalId(final EndpointId endpointId) {
        return oneService.getEndpoint(endpointId).getInternalId();
    }
}
