package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.Authentication;
import org.springframework.stereotype.Repository;

/**
 * Repository for the authentication.
 */
@Repository
public interface AuthenticationRepository extends BaseEntityRepository<Authentication> {
}
