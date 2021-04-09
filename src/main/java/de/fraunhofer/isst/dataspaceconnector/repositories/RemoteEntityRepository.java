package de.fraunhofer.isst.dataspaceconnector.repositories;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RemoteEntityRepository<T extends AbstractEntity> extends BaseEntityRepository<T> {
    @Query("SELECT a FROM #{#entityName} a WHERE a.remoteId = :remoteId")
    Optional<UUID> identifyByRemoteId(URI remoteId);
}
