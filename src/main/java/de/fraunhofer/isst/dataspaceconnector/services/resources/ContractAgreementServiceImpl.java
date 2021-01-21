package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.ResourceContract;
import de.fraunhofer.isst.dataspaceconnector.repositories.ContractAgreementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * This class implements all methods of {@link ContractAgreementService}.
 * It provides methods for performing CRUD operations on contracts.
 */
@Service
public class ContractAgreementServiceImpl implements ContractAgreementService {

    private final ContractAgreementRepository contractAgreementRepository;

    /**
     * Constructor for ContractAgreementServiceImpl.
     *
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Autowired
    public ContractAgreementServiceImpl(ContractAgreementRepository contractAgreementRepository)
        throws IllegalArgumentException {
        if (contractAgreementRepository == null)
            throw new IllegalArgumentException("The ContractAgreementRepository cannot be null.");

        this.contractAgreementRepository = contractAgreementRepository;
    }

    /**
     * Adds a contract.
     * @param contract the contract.
     */
    @Override
    public void addContract(ResourceContract contract) {
        contractAgreementRepository.save(contract);
    }

    /**
     * Finds a contract by ID.
     * @param uuid ID of the contract.
     * @return the contract.
     */
    @Override
    public ResourceContract getContract(UUID uuid) {
        //noinspection OptionalGetWithoutIsPresent
        return contractAgreementRepository.findById(uuid).get();
    }
}
