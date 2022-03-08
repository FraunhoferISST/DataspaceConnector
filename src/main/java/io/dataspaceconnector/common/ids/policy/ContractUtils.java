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
package io.dataspaceconnector.common.ids.policy;

import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.common.exception.ContractException;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.util.Utils;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.dataspaceconnector.common.ids.policy.RuleUtils.compareObligations;
import static io.dataspaceconnector.common.ids.policy.RuleUtils.comparePermissions;
import static io.dataspaceconnector.common.ids.policy.RuleUtils.compareProhibitions;

/**
 * Contains utility methods for creating and validation ids contracts.
 */
public final class ContractUtils {

    /**
     * Constructor without params.
     */
    private ContractUtils() {
        // not used
    }

    /**
     * Return all contract rules as one list.
     *
     * @param contract The ids contract.
     * @return A list of ids rules.
     * @throws IllegalArgumentException If the message is null.
     */
    public static List<Rule> extractRulesFromContract(final Contract contract) {
        Utils.requireNonNull(contract, ErrorMessage.CONTRACT_NULL);
        final var permissionList = contract.getPermission();
        final var ruleList = permissionList == null ? new ArrayList<Rule>()
                : new ArrayList<Rule>(permissionList);

        final var prohibitionList = contract.getProhibition();
        if (prohibitionList != null) {
            ruleList.addAll(prohibitionList);
        }

        final var obligationList = contract.getObligation();
        if (obligationList != null) {
            ruleList.addAll(obligationList);
        }

        return ruleList;
    }

    /**
     * Iterate over all rules of a contract and add the ones with the element as their target to a
     * rule list.
     *
     * @param contract The contract.
     * @param element  The requested element.
     * @return List of ids rules.
     * @throws IllegalArgumentException If the message is null.
     */
    public static List<? extends Rule> getRulesForTargetId(final Contract contract,
                                                           final URI element) {
        Utils.requireNonNull(contract, ErrorMessage.CONTRACT_NULL);
        final var rules = new ArrayList<Rule>();

        if (contract.getPermission() != null) {
            for (final var permission : contract.getPermission()) {
                final var target = permission.getTarget();
                if (target != null && target.equals(element)) {
                    rules.add(permission);
                }
            }
        }

        if (contract.getProhibition() != null) {
            for (final var prohibition : contract.getProhibition()) {
                final var target = prohibition.getTarget();
                if (target != null && target.equals(element)) {
                    rules.add(prohibition);
                }
            }
        }

        if (contract.getObligation() != null) {
            for (final var obligation : contract.getObligation()) {
                final var target = obligation.getTarget();
                if (target != null && target.equals(element)) {
                    rules.add(obligation);
                }
            }
        }

        return rules;
    }

    /**
     * Extract targets and save them together with the respective rules to a map.
     *
     * @param rules List of ids rules.
     * @return Map with targets and matching rules.
     */
    public static Map<URI, List<Rule>> getTargetRuleMap(final List<Rule> rules) {
        final var targetRuleMap = new HashMap<URI, List<Rule>>();

        // Iterate over all rules.
        for (final var rule : rules) {
            // Get target of rule.
            final var target = rule.getTarget();

            // If the target is already in the map, add the rule to the value list.
            if (targetRuleMap.containsKey(target)) {
                final var value = targetRuleMap.get(target);
                value.add(rule);
            } else {
                // If not, create a target-rule-entry to the map.
                final var value = new ArrayList<Rule>();
                value.add(rule);
                targetRuleMap.put(target, value);
            }
        }

        return targetRuleMap;
    }

    /**
     * Check if contract offer has a restricted consumer. If the value does not match the issuer
     * connector, remove the contract from the list.
     *
     * @param issuerConnector The requesting consumer.
     * @param contracts       List of contracts.
     * @return Cleaned list of contracts.
     * @throws IllegalArgumentException if any of the arguments is null.
     */
    public static List<io.dataspaceconnector.model.contract.Contract>
    removeContractsWithInvalidConsumer(
            final List<io.dataspaceconnector.model.contract.Contract> contracts,
            final URI issuerConnector) {
        Utils.requireNonNull(contracts, ErrorMessage.LIST_NULL);
        Utils.requireNonNull(issuerConnector, ErrorMessage.URI_NULL);

        return contracts.parallelStream()
                .filter(x -> x.getConsumer().equals(issuerConnector) || x.getConsumer()
                        .toString()
                        .isBlank())
                .collect(Collectors.toList());
    }

    /**
     * Checks whether contracts are valid according to their start and end date. If the current
     * time is before a contract's start date or after a contract's end data, the contract is
     * removed from the list.
     *
     * @param contracts The list of contracts to filter.
     * @return The filtered list.
     */
    public static List<io.dataspaceconnector.model.contract.Contract>
    removeContractsWithInvalidDates(
            final List<io.dataspaceconnector.model.contract.Contract> contracts) {
        Utils.requireNonNull(contracts, ErrorMessage.LIST_NULL);

        final var now = ZonedDateTime.now();
        return contracts
                .parallelStream()
                .filter(x -> now.isAfter(x.getStart()) && now.isBefore(x.getEnd()))
                .collect(Collectors.toList());
    }

    /**
     * Validate if the assigner is the expected one.
     *
     * @param agreement The contract agreement.
     * @throws ContractException If the assigner is not as expected.
     */
    public static void validateRuleAssigner(final ContractAgreement agreement)
            throws ContractException {
        // NOTE: Recipient url might not be the connector id.
    }

    /**
     * Compare two contract agreements to each other.
     *
     * @param consumer The consumer agreement.
     * @param provider The provider agreement.
     * @return True if both agreements are equal.
     * @throws ContractException If both objects do not match.
     */
    public static boolean compareContractAgreements(final ContractAgreement consumer,
                                                    final ContractAgreement provider) {
        return consumer.getId().equals(provider.getId())
                && comparePermissions(consumer.getPermission(), provider.getPermission())
                && compareProhibitions(consumer.getProhibition(), provider.getProhibition())
                && compareObligations(consumer.getObligation(), provider.getObligation());
    }
}
