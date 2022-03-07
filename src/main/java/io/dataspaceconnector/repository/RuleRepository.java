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
package io.dataspaceconnector.repository;

import io.dataspaceconnector.model.rule.ContractRule;
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
     * @param contractId The contract's id.
     * @return A list of all rules in the contract.
     */
    @Query("SELECT r "
            + "FROM ContractRule r INNER JOIN Contract c ON r MEMBER OF c.rules "
            + "WHERE c.id = :contractId "
            + "AND r.deleted = false "
            + "AND c.deleted = false")
    List<ContractRule> findAllByContract(UUID contractId);
}
