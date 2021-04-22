package de.fraunhofer.isst.dataspaceconnector.repositories;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RemoteEntityRepository<T extends AbstractEntity> extends BaseEntityRepository<T> {
    /*
        NOTE: Maybe return the complete object? Depends on the general usage and the bandwidth needed
        for the Object
     */
    @Query("SELECT a.id FROM #{#entityName} a WHERE a.remoteId = :remoteId")
    Optional<UUID> identifyByRemoteId(URI remoteId);
}
