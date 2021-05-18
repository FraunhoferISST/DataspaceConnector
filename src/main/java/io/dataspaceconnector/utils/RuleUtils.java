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
package io.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.AbstractConstraint;
import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.ConstraintImpl;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.DutyImpl;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionImpl;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.exceptions.ContractException;
import io.dataspaceconnector.exceptions.InvalidInputException;
import io.dataspaceconnector.model.TimeInterval;
import io.dataspaceconnector.services.usagecontrol.PolicyPattern;
import lombok.extern.log4j.Log4j2;

import java.net.URI;
import java.text.ParseException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains utility methods for validating the content of ids rules.
 */
@Log4j2
public final class RuleUtils {

    /**
     * Constructor without params.
     */
    private RuleUtils() {
        // not used
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
     * Check rule for post duties.
     *
     * @param rule The ids rule.
     * @return True if resource should be deleted, false if not.
     * @throws DateTimeParseException If the policy could not be checked.
     */
    public static boolean checkRuleForPostDuties(final Rule rule) throws DateTimeParseException {
        if (rule instanceof PermissionImpl || rule instanceof DutyImpl) {
            final var postDuties = ((Permission) rule).getPostDuty();
            if (postDuties != null && !postDuties.isEmpty()) {
                return checkDutiesForDeletion(postDuties);
            }
        }

        return false;
    }

    /**
     * Check duties for deletion.
     *
     * @param duties The post duty list.
     * @return True if resource should be deleted, false if not.
     * @throws DateTimeParseException If the policy could not be checked.
     */
    public static boolean checkDutiesForDeletion(final ArrayList<? extends Duty> duties)
            throws DateTimeParseException {
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
     * @throws DateTimeParseException if a duration cannot be parsed.
     */
    public static boolean checkRuleForDeletion(final Rule rule) throws DateTimeParseException {
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
            throw e;
        }

        if (number < 0) {
            number = 0;
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
    public static ZonedDateTime getDate(final Rule rule) throws DateTimeParseException {
        final var constraint = rule.getConstraint().get(0);
        final var date = ((ConstraintImpl) constraint).getRightOperand().getValue();

        return MappingUtils.getDateOf(date);
    }

    /**
     * Gets the duration value defined in a policy.
     *
     * @param rule The ids rule.
     * @return The duration or null.
     * @throws DateTimeParseException If the duration cannot be parsed.
     */
    public static java.time.Duration getDuration(final Rule rule)
            throws DateTimeParseException {
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
     * Compare two permission lists to each other.
     *
     * @param lList One list.
     * @param rList The other list.
     * @return True, if the lists are equal, false if not.
     */
    public static boolean comparePermissions(final ArrayList<? extends Permission> lList,
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
    public static boolean compareProhibitions(final ArrayList<? extends Prohibition> lList,
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
    public static boolean compareObligations(final ArrayList<? extends Duty> lList,
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
        return Utils.compareList(lList, rList, RuleUtils::compareDuties);
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
        return Utils.compareList(oldRules, newRules, RuleUtils::compareRule);
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
        return Utils.compareList(lList, rList, RuleUtils::compareConstraint);
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
        return Utils.compareList(lList, rList, RuleUtils::compareAction);
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

    /**
     * Get current system date.
     *
     * @return The date object.
     */
    public static ZonedDateTime getCurrentDate() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }

}
