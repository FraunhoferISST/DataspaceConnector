package io.dataspaceconnector.services.resources;

import java.util.UUID;

import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.base.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntityService<T extends Entity, D extends Description> {
    T create(D desc);
    T update(UUID entityId, D desc);
    T get(UUID entityId);
    Page<T> getAll(Pageable pageable);
    boolean doesExist(UUID entityId);
    void delete(UUID entityId);
}
