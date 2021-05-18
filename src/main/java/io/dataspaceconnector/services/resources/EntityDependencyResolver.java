/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.services.resources;

import java.net.URI;
import java.util.List;

import io.dataspaceconnector.model.Agreement;
import io.dataspaceconnector.model.Artifact;
import io.dataspaceconnector.model.Contract;
import io.dataspaceconnector.model.ContractRule;
import io.dataspaceconnector.utils.EndpointUtils;
import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.Utils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * This service offers methods for finding entities related to another given entity.
 */
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
     * Gets all contracts applicable for a specific artifact.
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
