package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.truststore.Truststore;
import org.springframework.stereotype.Repository;

@Repository
public interface TruststoreRepository extends BaseEntityRepository<Truststore> {
}
