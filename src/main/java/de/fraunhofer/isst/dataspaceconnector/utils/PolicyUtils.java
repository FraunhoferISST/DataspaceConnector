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
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidContractException;
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
import java.util.List;

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
    public static List<? extends Rule> extractRulesFromContract(final Contract contract) {
        final var permissionList = contract.getPermission();
        final var ruleList = new ArrayList<Rule>(permissionList);

        final var prohibitionList = contract.getProhibition();
        ruleList.addAll(prohibitionList);

        final var obligationList = contract.getObligation();
        ruleList.addAll(obligationList);

        return ruleList;
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
     * @throws InvalidContractException If a target is missing.
     */
    public static void validateRuleTarget(final List<? extends Rule> ruleList) throws InvalidContractException {
        for (final var rule : ruleList) {
            final var target = rule.getTarget();
            if (target == null || target.toString().equals("")) {
                throw new InvalidContractException(ErrorMessages.MISSING_TARGET.toString());
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
        // TODO
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
    private static void compareDuties(final ArrayList<? extends Permission> oldRules,
                                      final ArrayList<? extends Permission> newRules) throws ContractException {
        final var oldSize = oldRules.size();
        final var newSize = newRules.size();

        if (oldSize != newSize) {
            throw new ContractException(ErrorMessages.CONTRACT_MISMATCH.toString());
        }

        for (int i = 0; i < oldSize; i++) {
            final var oldPermissions = oldRules.get(i);
            final var newPermissions = newRules.get(i);

            final var oldPostDuties = oldPermissions.getPostDuty();
            final var newPostDuties = newPermissions.getPostDuty();
            compareRules(oldPostDuties, newPostDuties);

            final var oldPreDuties = oldPermissions.getPreDuty();
            final var newPreDuties = newPermissions.getPreDuty();
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
    private static void compareRules(final ArrayList<? extends Rule> oldRules,
                                     final ArrayList<? extends Rule> newRules) throws ContractException {
        final var oldSize = oldRules.size();
        final var newSize = newRules.size();

        if (oldSize != newSize) {
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
     * Compares the content of two actions lists.
     *
     * @param oldAction List of rules from original contract.
     * @param newAction List of rules from the contract that should be compared.
     * @throws ContractException If a mismatch has been detected.
     */
    private static void compareActions(final ArrayList<? extends Action> oldAction,
                                       final ArrayList<? extends Action> newAction) throws ContractException {
        final var oldSize = oldAction.size();
        final var newSize = newAction.size();

        if (oldSize != newSize) {
            throw new ContractException(ErrorMessages.CONTRACT_MISMATCH.toString());
        }

        for (int j = 0; j < oldSize; j++) {
            final var oldActionAsRdf = oldAction.get(j).toRdf();
            final var newActionAsRdf = newAction.get(j).toRdf();
            if (!oldActionAsRdf.equals(newActionAsRdf)) {
                throw new ContractException(ErrorMessages.CONTRACT_MISMATCH.toString());
            }
        }
    }

    /**
     * Compares the content of two constraint lists.
     *
     * @param oldConstraint List of rules from original contract.
     * @param newConstraint List of rules from the contract that should be compared.
     * @throws ContractException If a mismatch has been detected.
     */
    private static void compareConstraints(final ArrayList<? extends Constraint> oldConstraint,
                                           final ArrayList<? extends Constraint> newConstraint) throws ContractException {
        final var oldSize = oldConstraint.size();
        final var newSize = newConstraint.size();

        if (oldSize != newSize) {
            throw new ContractException(ErrorMessages.CONTRACT_MISMATCH.toString());
        }

        for (int j = 0; j < oldSize; j++) {
            final var oldConstraintAsRdf = oldConstraint.get(j).toRdf();
            final var newConstraintAsRdf = newConstraint.get(j).toRdf();
            if (!oldConstraintAsRdf.equals(newConstraintAsRdf)) {
                throw new ContractException(ErrorMessages.CONTRACT_MISMATCH.toString());
            }
        }
    }
}
