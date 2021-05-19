package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.Connector;
import org.springframework.stereotype.Repository;

/**
 * Repository for the connector.
 */
@Repository
public interface ConnectorRepository extends BaseEntityRepository<Connector> {
}
