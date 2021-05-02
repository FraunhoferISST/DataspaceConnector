package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.AbstractConstraint;
import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.ConstraintImpl;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidInputException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.TimeInterval;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyPattern;
import lombok.extern.log4j.Log4j2;

import javax.xml.datatype.DatatypeConfigurationException;
import java.net.URI;
import java.text.ParseException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Log4j2
public final class PolicyUtils {

    /**
     * Constructor without params.
     */
    private PolicyUtils() {
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
        Utils.requireNonNull(contract, ErrorMessages.CONTRACT_NULL);
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
        Utils.requireNonNull(contract, ErrorMessages.CONTRACT_NULL);
        final var rules = new ArrayList<Rule>();

        for (final var permission : contract.getPermission()) {
            final var target = permission.getTarget();
            if (element == target) {
                rules.add(permission);
            }
        }

        for (final var prohibition : contract.getProhibition()) {
            final var target = prohibition.getTarget();
            if (element == target) {
                rules.add(prohibition);
            }
        }

        for (final var obligation : contract.getObligation()) {
            final var target = obligation.getTarget();
            if (element == target) {
                rules.add(obligation);
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
    public static
    List<de.fraunhofer.isst.dataspaceconnector.model.Contract> removeContractsWithInvalidConsumer(
            final List<de.fraunhofer.isst.dataspaceconnector.model.Contract> contracts,
            final URI issuerConnector) {
        Utils.requireNonNull(contracts, ErrorMessages.LIST_NULL);
        Utils.requireNonNull(issuerConnector, ErrorMessages.URI_NULL);

        return contracts.parallelStream()
                .filter(x -> x.getConsumer().equals(issuerConnector) || x.getConsumer()
                        .toString()
                        .isBlank())
                .collect(Collectors.toList());
    }

    /**
     * Check rule for post duties.
     *
     * @param rule The ids rule.
     * @return True if resource should be deleted, false if not.
     * @throws ParseException If the policy could not be checked.
     */
    public static boolean checkRuleForPostDuties(final Rule rule) throws ParseException {
        final var postDuties = ((Permission) rule).getPostDuty();
        if (postDuties != null && !postDuties.isEmpty()) {
            return checkDutiesForDeletion(postDuties);
        }
        return false;
    }

    /**
     * Check duties for deletion.
     *
     * @param duties The post duty list.
     * @return True if resource should be deleted, false if not.
     * @throws ParseException If the policy could not be checked.
     */
    public static boolean checkDutiesForDeletion(final ArrayList<? extends Duty> duties)
            throws ParseException {
        for (final var duty : duties) {
            for (final var action : duty.getAction()) {
                if (action == Action.DELETE) {
                    return checkRuleForDeletion(duty);
                }
            }
        }
        return false;
    }

    /**
     * Checks if the specified duration since resource creation or the specified maximum date for
     * resource access has already been exceeded.
     *
     * @param rule a {@link de.fraunhofer.iais.eis.Rule} object.
     * @return true, if the duration or date has been exceeded; false otherwise.
     * @throws java.text.ParseException if a duration cannot be parsed.
     */
    public static boolean checkRuleForDeletion(final Rule rule) throws ParseException {
        final var max = getDate(rule);
        if (max != null) {
            return checkDate(ZonedDateTime.now(ZoneOffset.UTC), max);
        } else {
            return false;
        }
    }

    /**
     * Checks whether the current date is later than the specified one.
     *
     * @param dateNow   the current date.
     * @param maxAccess the target date.
     * @return true, if the current date is later than the target date; false otherwise.
     */
    public static boolean checkDate(final ZonedDateTime dateNow, final ZonedDateTime maxAccess) {
        return !dateNow.isAfter(maxAccess);
    }

    /**
     * Gets the endpoint value to send notifications to defined in a policy.
     *
     * @param rule The ids rule.
     * @return The endpoint value.
     */
    public static String getEndpoint(final Rule rule) throws NullPointerException {
        final var constraint = rule.getConstraint().get(0);
        return ((ConstraintImpl) constraint).getRightOperand().getValue();
    }

    /**
     * Gets the allowed number of accesses defined in a policy.
     *
     * @param rule the policy rule object.
     * @return the number of allowed accesses.
     */
    public static Integer getMaxAccess(final Rule rule) throws NumberFormatException {
        final var constraint = rule.getConstraint().get(0);
        final var value = ((ConstraintImpl) constraint).getRightOperand().getValue();
        final var operator = ((ConstraintImpl) constraint).getOperator();

        int number;
        try {
            number = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to parse value to integer. [exception=({})]",
                        e.getMessage(), e);
            }
            number = 1;
        }

        switch (operator) {
            case EQ:
            case LTEQ:
                return number;
            case LT:
                return number - 1;
            default:
                return 0;
        }
    }

    /**
     * Gets the time interval defined in a policy.
     *
     * @param rule the policy rule object.
     * @return the time interval.
     */
    public static TimeInterval getTimeInterval(final Rule rule) throws ParseException {
        final var interval = new TimeInterval();

        for (var constraint : rule.getConstraint()) {
            final var operator = ((ConstraintImpl) constraint).getOperator();
            if (operator == BinaryOperator.AFTER) {
                final var value = ((ConstraintImpl) constraint).getRightOperand().getValue();
                final var start = MappingUtils.getDateOf(value);
                interval.setStart(start);
            } else if (operator == BinaryOperator.BEFORE) {
                final var value = ((ConstraintImpl) constraint).getRightOperand().getValue();
                final var end = MappingUtils.getDateOf(value);
                interval.setEnd(end);
            }
        }
        return interval;
    }

    /**
     * Gets the PIP endpoint path value defined in a policy.
     *
     * @param rule the policy rule object.
     * @return the pip endpoint value.
     */
    public static URI getPipEndpoint(final Rule rule) {
        final var constraint = rule.getConstraint().get(0);
        return ((ConstraintImpl) constraint).getPipEndpoint();
    }

    /**
     * Gets the date value defined in a policy.
     *
     * @param rule the policy constraint object.
     * @return the date or null.
     */
    public static ZonedDateTime getDate(final Rule rule) {
        final var constraint = rule.getConstraint().get(0);
        final var date = ((ConstraintImpl) constraint).getRightOperand().getValue();

        return MappingUtils.getDateOf(date);
    }

    /**
     * Gets the duration value defined in a policy.
     *
     * @param rule The ids rule.
     * @return The duration or null.
     * @throws javax.xml.datatype.DatatypeConfigurationException If the duration cannot be parsed.
     */
    public static java.time.Duration getDuration(final Rule rule)
            throws DatatypeConfigurationException {
        final var constraint = rule.getConstraint().get(0);
        final var type = ((ConstraintImpl) constraint).getRightOperand().getType();

        if (type.equals("xsd:duration")) {
            final var duration = ((ConstraintImpl) constraint).getRightOperand().getValue();
            return java.time.Duration.parse(duration);
        } else {
            return null;
        }
    }

    /**
     * Add duration to a date to calculate a new date.
     *
     * @param original The previous date.
     * @param duration The duration to add.
     * @return The new date.
     */
    public static ZonedDateTime getCalculatedDate(
            final ZonedDateTime original, final Duration duration) {
        return original.plus(duration);
    }

    /**
     * Check if each rule contains a target.
     *
     * @param ruleList The ids rule list.
     * @throws InvalidInputException If a target is missing.
     */
    public static void validateRuleTarget(final List<? extends Rule> ruleList)
            throws InvalidInputException {
        for (final var rule : ruleList) {
            final var target = rule.getTarget();
            if (target == null || target.toString().equals("")) {
                throw new InvalidInputException(ErrorMessages.MISSING_TARGET.toString());
            }
        }
    }

    /**
     * Validate if the assigner is the expected one.
     *
     * @param agreement The contract agreement.
     * @throws ContractException If the assigner is not as expected.
     */
    public static void validateRuleAssigner(final ContractAgreement agreement)
            throws ContractException {
        // TODO implement later
        // NOTE: Recipient url might not be the connector id.
    }

    /**
     * Compare two lists of rules with each other.
     *
     * @param oldContract The old contract.
     * @param newContract The new contract.
     * @throws ContractException If a mismatch has been detected.
     */
    public static void validateRuleContent(final Contract oldContract,
                                           final Contract newContract) throws ContractException {
        if (oldContract == null || newContract == null) {
            throw new ContractException(ErrorMessages.EMPTY_CONTRACT.toString());
        }

        if (!comparePermissions(oldContract.getPermission(), newContract.getPermission())) {
            throw new ContractException("Different permissions");
        }

        if (!compareProhibitions(oldContract.getProhibition(), newContract.getProhibition())) {
            throw new ContractException("Different prohibitions.");
        }

        if (!compareObligations(oldContract.getObligation(), newContract.getObligation())) {
            throw new ContractException("Different obligations.");
        }
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

    /**
     * Compare two permission lists to each other.
     *
     * @param lList One list.
     * @param rList The other list.
     * @return True, if the lists are equal, false if not.
     */
    private static boolean comparePermissions(final ArrayList<? extends Permission> lList,
                                              final ArrayList<? extends Permission> rList) {
        return compareDuties(lList, rList) && compareRules(lList, rList);
    }

    /**
     * Compare two prohibition lists to each other.
     *
     * @param lList One list.
     * @param rList The other list.
     * @return True, if the lists are equal, false if not.
     */
    private static boolean compareProhibitions(final ArrayList<? extends Prohibition> lList,
                                               final ArrayList<? extends Prohibition> rList) {
        return compareRules(lList, rList);
    }

    /**
     * Compare two obligation lists to each other.
     *
     * @param lList One list.
     * @param rList The other list.
     * @return True, if the lists are equal, false if not.
     */
    private static boolean compareObligations(final ArrayList<? extends Duty> lList,
                                              final ArrayList<? extends Duty> rList) {
        return compareRules(lList, rList);
    }

    /**
     * Compare the content of two permissions lists.
     *
     * @param lList List of rules from original contract.
     * @param rList List of rules from the contract that should be compared.
     * @return true if both rules are the same.
     */
    private static boolean compareDuties(final ArrayList<? extends Permission> lList,
                                         final ArrayList<? extends Permission> rList) {
        return compareList(lList, rList, PolicyUtils::compareDuties);
    }

    /**
     * Compare the content of two rule lists.
     *
     * @param oldRules List of rules from original contract.
     * @param newRules List of rules from the contract that should be compared.
     * @return true if both rules are the same.
     */
    public static boolean compareRules(final ArrayList<? extends Rule> oldRules,
                                       final ArrayList<? extends Rule> newRules) {
        return compareList(oldRules, newRules, PolicyUtils::compareRule);
    }

    /**
     * Compares the content of two constraint lists.
     *
     * @param lList List of rules from original contract.
     * @param rList List of rules from the contract that should be compared.
     * @return true if both rules are the same.
     */
    private static boolean compareConstraints(
            final ArrayList<? extends AbstractConstraint> lList,
            final ArrayList<? extends AbstractConstraint> rList) {
        return compareList(lList, rList, PolicyUtils::compareConstraint);
    }

    /**
     * Compares the content of two actions lists.
     *
     * @param lList List of rules from original contract.
     * @param rList List of rules from the contract that should be compared.
     * @return true if the actions are the same.
     */
    private static boolean compareActions(final ArrayList<? extends Action> lList,
                                          final ArrayList<? extends Action> rList) {
        return compareList(lList, rList, PolicyUtils::compareAction);
    }

    private static <T extends Permission> boolean compareDuties(final T lObj, final T rObj) {
        return compareRules(lObj.getPreDuty(), rObj.getPreDuty())
                && compareRules(lObj.getPostDuty(), rObj.getPostDuty());
    }

    private static <T extends Rule> boolean compareRule(final T lObj, final T rObj) {
        return compareActions(lObj.getAction(), rObj.getAction())
                && compareConstraints(lObj.getConstraint(), rObj.getConstraint());
    }

    private static <T extends AbstractConstraint> boolean compareConstraint(final T lObj,
                                                                            final T rObj) {
        return lObj.toRdf().equals(rObj.toRdf());
    }

    private static <T extends Action> boolean compareAction(final T lObj, final T rObj) {
        return lObj.equals(rObj);
    }

    private static <T> boolean compareList(final List<? extends T> lList,
                                           final List<? extends T> rList,
                                           final BiFunction<T, T, Boolean> compare) {
        var isSame = true;

        if (isOnlyOneNull(lList, rList)) {
            isSame = false;
        } else if (lList != null /* && rList != null*/) {
            final var lSet = makeUnique(lList, compare);
            final var rSet = makeUnique(rList, compare);

            if (lSet.size() == rSet.size()) {
                for (final var lObj : lSet) {
                    var found = false;
                    for (final var rObj : rSet) {
                        if (compare.apply(lObj, rObj)) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        // At least one element is different
                        isSame = false;
                        break;
                    }
                }
            } else {
                // Two unique sets with different length must have different elements
                isSame = false;
            }
        }

        return isSame;
    }

    private static <T> List<? extends T> makeUnique(final List<? extends T> list,
                                                    final BiFunction<T, T, Boolean> compare) {
        final var output = new ArrayList<>(list);
        for (int x = 0; x < output.size(); x++) {
            final var obj = output.get(x);
            for (int y = x + 1; y < output.size(); y++) {
                if (compare.apply(obj, output.get(y))) {
                    output.remove(y);
                    --y;
                }
            }
        }

        return output;
    }

    private static <T> boolean isOnlyOneNull(final T obj1, final T obj2) {
        return (obj1 == null && obj2 != null) || (obj1 != null && obj2 == null);
    }

    /**
     * Read the properties of an ids rule to automatically recognize the policy pattern.
     *
     * @param rule The ids rule.
     * @return The recognized policy pattern.
     */
    public static PolicyPattern getPatternByRule(final Rule rule) {
        PolicyPattern detectedPattern = null;

        if (rule instanceof Prohibition) {
            detectedPattern = PolicyPattern.PROHIBIT_ACCESS;
        } else if (rule instanceof Permission) {
            final var constraints = rule.getConstraint();
            final var postDuties = ((Permission) rule).getPostDuty();

            if (constraints != null && constraints.get(0) != null) {
                if (constraints.size() > 1) {
                    if (postDuties != null && postDuties.get(0) != null) {
                        detectedPattern = PolicyPattern.USAGE_UNTIL_DELETION;
                    } else {
                        detectedPattern = PolicyPattern.USAGE_DURING_INTERVAL;
                    }
                } else {
                    final var firstConstraint = (ConstraintImpl) constraints.get(0);
                    final var leftOperand = firstConstraint.getLeftOperand();
                    final var operator = firstConstraint.getOperator();
                    if (leftOperand == LeftOperand.COUNT) {
                        detectedPattern = PolicyPattern.N_TIMES_USAGE;
                    } else if (leftOperand == LeftOperand.ELAPSED_TIME) {
                        detectedPattern = PolicyPattern.DURATION_USAGE;
                    } else if (leftOperand == LeftOperand.SYSTEM
                            && operator == BinaryOperator.SAME_AS) {
                        detectedPattern = PolicyPattern.CONNECTOR_RESTRICTED_USAGE;
                    } else {
                        detectedPattern = null;
                    }
                }
            } else {
                if (postDuties != null && postDuties.get(0) != null) {
                    final var action = postDuties.get(0).getAction().get(0);
                    if (action == Action.NOTIFY) {
                        detectedPattern = PolicyPattern.USAGE_NOTIFICATION;
                    } else if (action == Action.LOG) {
                        detectedPattern = PolicyPattern.USAGE_LOGGING;
                    } else {
                        detectedPattern = null;
                    }
                } else {
                    detectedPattern = PolicyPattern.PROVIDE_ACCESS;
                }
            }
        }

        return detectedPattern;
    }

    /**
     * Get current system date.
     *
     * @return The date object.
     */
    public static ZonedDateTime getCurrentDate() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Check if the transfer contract's target matches the requested artifact.
     *
     * @param artifacts         List of artifacts.
     * @param requestedArtifact Id of the requested artifact.
     * @return True if the requested artifact matches the transfer contract's artifacts.
     * @throws ResourceNotFoundException If a resource could not be found.
     */
    public static boolean isMatchingTransferContract(final List<Artifact> artifacts,
                                                     final URI requestedArtifact)
            throws ResourceNotFoundException {
        for (final var artifact : artifacts) {
            final var endpoint = SelfLinkHelper.getSelfLink(artifact);
            if (endpoint.equals(requestedArtifact)) {
                return true;
            }
        }

        // If the requested artifact could not be found in the transfer contract (agreement).
        return false;
    }
}
