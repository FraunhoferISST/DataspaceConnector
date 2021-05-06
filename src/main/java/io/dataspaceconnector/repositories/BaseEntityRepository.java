package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

/**
 * The base repository for all entities of type {@link AbstractEntity}.
 * @param <T> The entity type.
 */
@NoRepositoryBean
public interface BaseEntityRepository<T extends AbstractEntity> extends JpaRepository<T, UUID> {
}
