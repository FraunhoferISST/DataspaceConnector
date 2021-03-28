package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidInputException;
import de.fraunhofer.isst.dataspaceconnector.model.TimeInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PolicyUtils {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyUtils.class);

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
     */
    public static List<Rule> extractRulesFromContract(final Contract contract) {
        final var permissionList = contract.getPermission();
        final var ruleList = new ArrayList<Rule>(permissionList);

        final var prohibitionList = contract.getProhibition();
        ruleList.addAll(prohibitionList);

        final var obligationList = contract.getObligation();
        ruleList.addAll(obligationList);

        return ruleList;
    }

    /**
     * Iterate over all rules of a contract and add the ones with the element as their target to a
     * rule list.
     *
     * @param contract The contract.
     * @param element  The requested element.
     * @return List of ids rules.
     */
    public static List<? extends Rule> getRulesForTargetId(final Contract contract,
                                                           final URI element) {
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
     */
    public static List<de.fraunhofer.isst.dataspaceconnector.model.Contract> removeContractsWithInvalidConsumer(
            final List<de.fraunhofer.isst.dataspaceconnector.model.Contract> contracts,
            final URI issuerConnector) {
        for (final var contract : contracts) {
            final var consumer = contract.getConsumer();
            if (!consumer.equals(issuerConnector)) {
                contracts.remove(contract);
            }
        }
        return contracts;
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
    public static boolean checkDutiesForDeletion(final ArrayList<? extends Duty> duties) throws ParseException {
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
            return checkDate(new Date(), max);
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
    public static boolean checkDate(final Date dateNow, final Date maxAccess) {
        return !dateNow.after(maxAccess);
    }

    /**
     * Gets the endpoint value to send notifications to defined in a policy.
     *
     * @param rule The ids rule.
     * @return The endpoint value.
     */
    public static String getEndpoint(final Rule rule) throws NullPointerException {
        final var constraint = rule.getConstraint().get(0);
        return constraint.getRightOperand().getValue();
    }

    /**
     * Gets the allowed number of accesses defined in a policy.
     *
     * @param rule the policy rule object.
     * @return the number of allowed accesses.
     */
    public static Integer getMaxAccess(final Rule rule) throws NumberFormatException {
        final var constraint = rule.getConstraint().get(0);
        final var value = constraint.getRightOperand().getValue();
        final var operator = constraint.getOperator();

        int number;
        try {
            number = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGGER.debug("Failed to parse value to integer. [exception=({})]", e.getMessage());
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
            final var operator = constraint.getOperator();
            if (operator == BinaryOperator.AFTER) {
                final var value = constraint.getRightOperand().getValue();
                final var start = IdsUtils.getDateOf(value);
                interval.setStart(start);
            } else if (operator == BinaryOperator.BEFORE) {
                final var value = constraint.getRightOperand().getValue();
                final var end = IdsUtils.getDateOf(value);
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
        return constraint.getPipEndpoint();
    }

    /**
     * Gets the date value defined in a policy.
     *
     * @param rule the policy constraint object.
     * @return the date or null.
     * @throws java.text.ParseException if the date cannot be parsed.
     */
    public static Date getDate(final Rule rule) throws ParseException {
        final var constraint = rule.getConstraint().get(0);
        final var date = constraint.getRightOperand().getValue();

        return IdsUtils.getDateOf(date);
    }

    /**
     * Gets the duration value defined in a policy.
     *
     * @param rule The ids rule.
     * @return The duration or null.
     * @throws javax.xml.datatype.DatatypeConfigurationException If the duration cannot be parsed.
     */
    public static Duration getDuration(final Rule rule) throws DatatypeConfigurationException {
        final var constraint = rule.getConstraint().get(0);
        final var type = constraint.getRightOperand().getType();

        if (type.equals("xsd:duration")) {
            final var duration = constraint.getRightOperand().getValue();
            return DatatypeFactory.newInstance().newDuration(duration);
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
    public static Date getCalculatedDate(final Date original, final Duration duration) {
        final var cal = Calendar.getInstance();
        cal.setTime(original);
        cal.add(Calendar.SECOND, duration.getSeconds());
        cal.add(Calendar.MINUTE, duration.getMinutes());
        cal.add(Calendar.HOUR_OF_DAY, duration.getHours());
        cal.add(Calendar.DAY_OF_MONTH, duration.getDays());
        cal.add(Calendar.MONTH, duration.getMonths());
        cal.add(Calendar.YEAR, duration.getYears());

        return cal.getTime();
    }

    /**
     * Check if each rule contains a target.
     *
     * @param ruleList The ids rule list.
     * @throws InvalidInputException If a target is missing.
     */
    public static void validateRuleTarget(final List<? extends Rule> ruleList) throws InvalidInputException {
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
    public static void validateRuleAssigner(final ContractAgreement agreement) throws ContractException {
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

        final var oldPermissions = oldContract.getPermission();
        final var newPermissions = newContract.getPermission();
        if (oldPermissions != null && newPermissions != null) {
            compareDuties(oldPermissions, newPermissions);
            compareRules(oldPermissions, oldPermissions);
        }

        final var oldProhibitions = oldContract.getProhibition();
        final var newProhibitions = newContract.getProhibition();
        if (oldProhibitions != null && newProhibitions != null) {
            compareRules(oldProhibitions, newProhibitions);
        }

        final var oldObligations = oldContract.getObligation();
        final var newObligations = newContract.getObligation();
        if (oldObligations != null && newObligations != null) {
            compareRules(oldObligations, newObligations);
        }
    }

    /**
     * Compares the content of two permissions lists.
     *
     * @param oldRules List of rules from original contract.
     * @param newRules List of rules from the contract that should be compared.
     * @throws ContractException If a mismatch has been detected.
     */
    public static void compareDuties(final ArrayList<? extends Permission> oldRules,
                                     final ArrayList<? extends Permission> newRules) throws ContractException {
        final var oldSize = oldRules.size();
        final var newSize = newRules.size();

        if (oldSize != newSize) {
            LOGGER.debug("Size mismatch. [oldRules=({}), newRules=({})]", oldRules, newRules);
            throw new ContractException(ErrorMessages.CONTRACT_MISMATCH.toString());
        }

        for (int i = 0; i < oldSize; i++) {
            final var oldRule = oldRules.get(i);
            final var newRule = newRules.get(i);

            final var oldPostDuties = oldRule.getPostDuty();
            final var newPostDuties = newRule.getPostDuty();
            compareRules(oldPostDuties, newPostDuties);

            final var oldPreDuties = oldRule.getPreDuty();
            final var newPreDuties = newRule.getPreDuty();
            compareRules(oldPreDuties, newPreDuties);
        }
    }

    /**
     * Compares the content of two rule lists.
     *
     * @param oldRules List of rules from original contract.
     * @param newRules List of rules from the contract that should be compared.
     * @throws ContractException If a mismatch has been detected.
     */
    public static void compareRules(final ArrayList<? extends Rule> oldRules,
                                    final ArrayList<? extends Rule> newRules) throws ContractException {
        final var oldSize = oldRules.size();
        final var newSize = newRules.size();

        if (oldSize != newSize) {
            LOGGER.debug("Size mismatch. [oldRules=({}), newRules=({})]", oldRules, newRules);
            throw new ContractException(ErrorMessages.CONTRACT_MISMATCH.toString());
        }

        for (int i = 0; i < oldSize; i++) {
            final var oldRule = oldRules.get(i);
            final var newRule = newRules.get(i);

            final var oldConstraints = oldRule.getConstraint();
            final var newConstraints = newRule.getConstraint();
            compareConstraints(oldConstraints, newConstraints);

            final var oldAction = oldRule.getAction();
            final var newAction = newRule.getAction();
            compareActions(oldAction, newAction);
        }
    }

    /**
     * Compares the content of two constraint lists.
     *
     * @param oldConstraints List of rules from original contract.
     * @param newConstraints List of rules from the contract that should be compared.
     * @throws ContractException If a mismatch has been detected.
     */
    private static void compareConstraints(final ArrayList<? extends Constraint> oldConstraints,
                                           final ArrayList<? extends Constraint> newConstraints) throws ContractException {
        final var oldSize = oldConstraints.size();
        final var newSize = newConstraints.size();

        if (oldSize != newSize) {
            LOGGER.debug("Size mismatch. [oldConstraints=({}), newConstraints=({})]",
                    oldConstraints, newConstraints);
            throw new ContractException(ErrorMessages.CONTRACT_MISMATCH.toString());
        }

        for (int j = 0; j < oldSize; j++) {
            final var oldConstraint = oldConstraints.get(j);
            final var newConstraint = newConstraints.get(j);
            final var oldConstraintAsRdf = oldConstraint.toRdf();
            final var newConstraintAsRdf = newConstraint.toRdf();
            if (!oldConstraintAsRdf.equals(newConstraintAsRdf)) {
                LOGGER.debug("Invalid constraint. [oldConstraint=({}), newConstraint=({})]",
                        oldConstraint, newConstraint);
                throw new ContractException(ErrorMessages.CONTRACT_MISMATCH.toString());
            }
        }
    }

    /**
     * Compares the content of two actions lists.
     *
     * @param oldActions List of rules from original contract.
     * @param newActions List of rules from the contract that should be compared.
     * @throws ContractException If a mismatch has been detected.
     */
    private static void compareActions(final ArrayList<? extends Action> oldActions,
                                       final ArrayList<? extends Action> newActions) throws ContractException {
        final var oldSize = oldActions.size();
        final var newSize = newActions.size();

        if (oldSize != newSize) {
            LOGGER.debug("Size mismatch. [oldRules=({}), newRules=({})]", oldActions, newActions);
            throw new ContractException(ErrorMessages.CONTRACT_MISMATCH.toString());
        }

        for (int j = 0; j < oldSize; j++) {
            final var oldAction = oldActions.get(j).toRdf();
            final var newAction = newActions.get(j).toRdf();
            if (!oldAction.equals(newAction)) {
                LOGGER.debug("Invalid action. [oldAction=({}), newAction=({})]", oldAction,
                        newAction);
                throw new ContractException(ErrorMessages.CONTRACT_MISMATCH.toString());
            }
        }
    }

    /**
     * Compare two contract agreements to each other.
     *
     * @param consumer The consumer agreement.
     * @param provider The provider agreement.
     * @throws ContractException If both objects do not match.
     */
    public static void compareContractAgreements(final ContractAgreement consumer,
                                                 final ContractAgreement provider) throws ContractException {
        if (!consumer.equals(provider)) {
            LOGGER.debug("Invalid agreement. [consumer=({}), provider=({})]", consumer, provider);
            throw new ContractException("Contract Agreement does not match the cached one.");
        }
    }
}
