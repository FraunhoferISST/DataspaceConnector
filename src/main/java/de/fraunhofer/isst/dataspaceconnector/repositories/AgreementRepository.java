package de.fraunhofer.isst.dataspaceconnector.repositories;

import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * The repository containing all objects of type {@link Agreement}.
 */
@Repository
public interface AgreementRepository extends BaseEntityRepository<Agreement> {

    /**
     * Set the status of an agreement to confirmed.
     * @param entityId The id of the agreement.
     */
    @Modifying
    @Query("update Agreement a set a.confirmed = true where a.id = :entityId")
    void confirmAgreement(UUID entityId);
}
