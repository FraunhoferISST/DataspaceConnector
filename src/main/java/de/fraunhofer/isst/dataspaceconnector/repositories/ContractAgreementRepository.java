package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.ResourceContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * ContractAgreementRepository interface.
 */
@Repository
public interface ContractAgreementRepository extends JpaRepository<ResourceContract, UUID> {
}
