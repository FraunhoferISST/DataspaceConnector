package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.Data;
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
    @Query("update LocalData a set a.value = :data where a.id = :entityId")
    void setLocalData(Long entityId, byte[] data);
}
