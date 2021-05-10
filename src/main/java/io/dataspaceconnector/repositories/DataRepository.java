package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * The repository containing all objects of type {@link Data}.
 */
@Repository
public interface DataRepository extends JpaRepository<Data, Long> {
    /**
     * Set new local data for an entity.
     *
     * @param entityId The entity id.
     * @param data     The new data.
     */
    @Modifying
    @Query("UPDATE LocalData a "
            + "SET a.value = :data "
            + "WHERE a.id = :entityId")
    void setLocalData(Long entityId, byte[] data);
}
