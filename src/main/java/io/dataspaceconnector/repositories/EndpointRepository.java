package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.Endpoint;
import org.springframework.stereotype.Repository;

/**
 * Repository for endpoints.
 */
@Repository
public interface EndpointRepository extends BaseEntityRepository<Endpoint> {
}
