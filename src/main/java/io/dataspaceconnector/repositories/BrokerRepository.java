package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.Broker;
import org.springframework.stereotype.Repository;

/**
 * Repository for brokers.
 */
@Repository
public interface BrokerRepository extends BaseEntityRepository<Broker> {
}
