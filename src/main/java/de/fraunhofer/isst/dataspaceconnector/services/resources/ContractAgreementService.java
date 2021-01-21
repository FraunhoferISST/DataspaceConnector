package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.ResourceContract;

import java.util.UUID;

/**
 * ContractAgreementService interface. Contains methods for performing CRUD operations on contracts.
 */
public interface ContractAgreementService {

    /**
     * Adds a contract.
     * @param contract the contract.
     */
    void addContract(ResourceContract contract);

    /**
     * Finds a contract by ID.
     * @param uuid ID of the contract.
     * @return the contract.
     */
    ResourceContract getContract(UUID uuid);
}
