package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * The repository containing all objects of type {@link ContractRule}.
 */
@Repository
public interface RuleRepository extends BaseEntityRepository<ContractRule> {

    /**
     * Finds all rules in a specific contract.
     *
     * @param contractId ID of the contract
     * @return list of all rules in the contract
     */
    @Query("SELECT r FROM ContractRule r INNER JOIN Contract c ON r MEMBER OF c.rules "
            + "WHERE c.id = :contractId")
    List<ContractRule> findAllByContract(UUID contractId);

}
