package de.fraunhofer.isst.dataspaceconnector.repositories;

import java.util.List;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

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
    @Query("select t.id from #{#entityName} t")
    List<UUID> getAllIds();
}
