package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityDependencyResolver {

    /**
     * Service for persisting and querying contracts.
     */
    private final @NonNull ContractService contractService;

    /**
     * Service for persisting and querying rules.
     */
    private final @NonNull RuleService ruleService;

    /**
     * Service for persisting and querying artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * Gets all contracts applicable for a specific artifact by using the query defined in the
     * {@link de.fraunhofer.isst.dataspaceconnector.repositories.ContractRepository}.
     *
     * @param artifactId The artifact id.
     * @return List of contract offers.
     */
    public List<Contract> getContractOffersByArtifactId(final URI artifactId) {
        final var uuid = EndpointUtils.getUUIDFromPath(artifactId);
        Utils.requireNonNull(artifactId, ErrorMessages.ENTITYID_NULL);
        return contractService.getAllByArtifactId(uuid);
    }

    /**
     * Finds all rules in a specific contract.
     *
     * @param contract the contract
     * @return list of all rules in the contract
     */
    public List<ContractRule> getRulesByContractOffer(final Contract contract) {
        Utils.requireNonNull(contract, ErrorMessages.ENTITY_NULL);
        return ruleService.getAllByContract(contract.getId());
    }

    /**
     * Gets all artifacts referenced in a specific agreement.
     *
     * @param agreement the agreement
     * @return list of all artifacts referenced in the agreement
     */
    public List<Artifact> getArtifactsByAgreement(final Agreement agreement) {
        Utils.requireNonNull(agreement, ErrorMessages.ENTITY_NULL);
        return artifactService.getAllByAgreement(agreement.getId());
    }
}
