package io.dataspaceconnector.services.resources;

import java.util.UUID;

import io.dataspaceconnector.model.base.AbstractEntity;
import io.dataspaceconnector.model.base.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntityService<T extends AbstractEntity, D extends Description> {
    T create(final D desc);
    T update(final UUID entityId, final D desc);
    T get(final UUID entityId);
    Page<T> getAll(final Pageable pageable);
    boolean doesExist(final UUID entityId);
    void delete(final UUID entityId);
}
