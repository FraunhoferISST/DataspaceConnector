package io.dataspaceconnector.repositories;

import java.util.UUID;

import io.dataspaceconnector.model.Agreement;

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
    @Query("UPDATE Agreement a "
            + "SET a.confirmed = true "
            + "WHERE a.id = :entityId "
            + "AND a.archived = false "
            + "AND a.deleted = false")
    void confirmAgreement(UUID entityId);
}
