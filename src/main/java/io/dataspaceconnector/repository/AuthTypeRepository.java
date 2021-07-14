package io.dataspaceconnector.repository;

import io.dataspaceconnector.model.auth.AuthType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthTypeRepository extends JpaRepository<AuthType, Long> {

}
