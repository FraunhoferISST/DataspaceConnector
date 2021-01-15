package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.ResourceContract;
import de.fraunhofer.isst.dataspaceconnector.repositories.ContractAgreementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContractAgreementServiceImpl implements ContractAgreementService {

    private final ContractAgreementRepository contractAgreementRepository;

    @Autowired
    public ContractAgreementServiceImpl(ContractAgreementRepository contractAgreementRepository)
        throws IllegalArgumentException {
        if (contractAgreementRepository == null)
            throw new IllegalArgumentException("The ContractAgreementRepository cannot be null.");

        this.contractAgreementRepository = contractAgreementRepository;
    }

    @Override
    public void addContract(ResourceContract contract) {
        contractAgreementRepository.save(contract);
    }

    @Override
    public ResourceContract getContract(UUID uuid) {
        return contractAgreementRepository.findById(uuid).get();
    }

    @Override
    public List<ResourceContract> getContracts() {
        return contractAgreementRepository.findAll();
    }
}
