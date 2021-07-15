package io.dataspaceconnector.repository;

import io.dataspaceconnector.model.auth.AuthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The repository containing all objects of type {@link AuthType}.
 */
@Repository
public interface AuthTypeRepository extends JpaRepository<AuthType, Long> {

}
