package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.ResourceContract;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Interface to the repository containing the contact agreements.
 */
@Repository
public interface ContractAgreementRepository extends JpaRepository<ResourceContract, UUID> {
}
