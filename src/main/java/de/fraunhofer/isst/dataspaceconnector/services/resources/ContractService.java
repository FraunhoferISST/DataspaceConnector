package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.repositories.ContractRepository;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Handles the basic logic for contracts.
 */
@Service
@NoArgsConstructor
public class ContractService extends BaseEntityService<Contract, ContractDesc> {

    /**
     * Finds all contracts applicable for a specific artifact.
     *
     * @param artifactId ID of the artifact
     * @return list of contracts applicable for the artifact
     */
    public List<Contract> getAllByArtifactId(final UUID artifactId) {
        return ((ContractRepository) getRepository()).findAllByArtifactId(artifactId);
    }

}
