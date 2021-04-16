package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.util.Set;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RelationService<K extends AbstractEntity, W extends AbstractEntity,
        T extends BaseEntityService<K, ?>, X extends BaseEntityService<W, ?>> {
    Page<W> get(final UUID ownerId, final Pageable pageable);
    void add(final UUID ownerId, final Set<UUID> entities);
    void remove(final UUID ownerId, final Set<UUID> entities);
    void replace(final UUID ownerId, final Set<UUID> entities);
}
