package de.fraunhofer.isst.dataspaceconnector.repositories;

import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * The repository containing all objects of type {@link
 * de.fraunhofer.isst.dataspaceconnector.model.Agreement}.
 */
public interface AgreementRepository extends BaseEntityRepository<Agreement> {
    @Modifying
    @Query("update Agreement a set a.confirmed = ?1 where a.id = :entityId")
    int confirmAgreement(UUID entityId);
}
