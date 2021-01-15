package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.ResourceContract;

import java.util.UUID;

public interface ContractAgreementService {

    void addContract(ResourceContract contract);

    ResourceContract getContract(UUID uuid);
}
