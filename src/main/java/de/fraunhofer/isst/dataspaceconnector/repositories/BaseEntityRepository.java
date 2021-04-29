package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

/**
 * The base repository for all entities of type {@link AbstractEntity}.
 * @param <T> The entity type.
 */
@NoRepositoryBean
public interface BaseEntityRepository<T extends AbstractEntity> extends JpaRepository<T, UUID> {

    /**
     * Get all ids.
     * @return The ids of all entities.
     */
    @Query("SELECT t.id FROM #{#entityName} t")
    List<UUID> getAllIds();
}
