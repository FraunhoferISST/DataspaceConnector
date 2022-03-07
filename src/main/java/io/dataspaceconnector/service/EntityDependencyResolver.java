/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.service;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import io.dataspaceconnector.service.resource.type.ContractService;
import io.dataspaceconnector.service.resource.type.RuleService;
import io.dataspaceconnector.common.net.EndpointUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

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
        Utils.requireNonNull(artifactId, ErrorMessage.ENTITYID_NULL);
        return contractService.getAllByArtifactId(uuid);
    }

    /**
     * Finds all rules in a specific contract.
     *
     * @param contract the contract
     * @return list of all rules in the contract
     */
    public List<ContractRule> getRulesByContractOffer(final Contract contract) {
        Utils.requireNonNull(contract, ErrorMessage.ENTITY_NULL);
        return ruleService.getAllByContract(contract.getId());
    }

    /**
     * Gets all artifacts referenced in a specific agreement.
     *
     * @param agreement the agreement
     * @return list of all artifacts referenced in the agreement
     */
    public List<Artifact> getArtifactsByAgreement(final Agreement agreement) {
        Utils.requireNonNull(agreement, ErrorMessage.ENTITY_NULL);
        return artifactService.getAllByAgreement(agreement.getId());
    }
}
