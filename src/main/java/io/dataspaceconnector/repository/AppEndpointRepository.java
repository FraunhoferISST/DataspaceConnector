package io.dataspaceconnector.repository;

import io.dataspaceconnector.model.endpoint.AppEndpoint;
import org.springframework.stereotype.Repository;

/**
 * Repository for app endpoints.
 */
@Repository
public interface AppEndpointRepository extends BaseEntityRepository<AppEndpoint> {
}
