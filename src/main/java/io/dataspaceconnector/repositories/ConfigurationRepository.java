package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.Configuration;
import org.springframework.stereotype.Repository;

/**
 * Repository for the configuration
 */
@Repository
public interface ConfigurationRepository extends BaseEntityRepository<Configuration> {
}
