package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.keystore.Keystore;
import org.springframework.stereotype.Repository;

@Repository
public interface KeystoreRepository extends BaseEntityRepository<Keystore> {
}
