package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for entities identifiable by remote id.
 *
 * @param <T> Type of the entity.
 */
@NoRepositoryBean
public interface RemoteEntityRepository<T extends AbstractEntity> extends BaseEntityRepository<T> {
    /*
        NOTE: Maybe return the complete object? Depends on the general usage and the bandwidth
        needed for the Object.
     */

    /**
     * Find an entity id by its remote id.
     *
     * @param remoteId The remote id.
     * @return The id of the entity.
     */
    @Query("SELECT a.id FROM #{#entityName} a WHERE a.remoteId = :remoteId")
    Optional<UUID> identifyByRemoteId(URI remoteId);
}
