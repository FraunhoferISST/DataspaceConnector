package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.IdentityProvider;
import org.springframework.stereotype.Repository;

/**
 * Repository for identity providers.
 */
@Repository
public interface IdentityProviderRepository extends BaseEntityRepository<IdentityProvider> {
}
