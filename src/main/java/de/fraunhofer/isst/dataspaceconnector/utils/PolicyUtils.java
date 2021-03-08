package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.model.TimeInterval;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public final class PolicyUtils {

    /**
     * The date format pattern.
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

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
        Date max = getDate(rule);
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
    public static Integer getMaxAccess(final Rule rule) {
        Constraint constraint = rule.getConstraint().get(0);

        int value = Integer.parseInt(constraint.getRightOperand().getValue());
        switch (constraint.getOperator()) {
            case EQ:
            case LTEQ:
                return value;
            case LT:
                return value - 1;
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
        TimeInterval timeInterval = new TimeInterval();

        for (var constraint : rule.getConstraint()) {
            if (constraint.getOperator() == BinaryOperator.AFTER) {
                final var start = new SimpleDateFormat(DATE_FORMAT)
                        .parse(constraint.getRightOperand().getValue());
                timeInterval.setStart(start);
            } else if (constraint.getOperator() == BinaryOperator.BEFORE) {
                final var end = new SimpleDateFormat(DATE_FORMAT)
                        .parse(constraint.getRightOperand().getValue());
                timeInterval.setEnd(end);
            }
        }
        return timeInterval;
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

        final var sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(Calendar.getInstance().getTimeZone());
        return sdf.parse(date);
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
        if (constraint.getRightOperand().getType().equals("xsd:duration")) {
            String duration = constraint.getRightOperand().getValue();
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
        Calendar cal = Calendar.getInstance();
        cal.setTime(original);
        cal.add(Calendar.SECOND, duration.getSeconds());
        cal.add(Calendar.MINUTE, duration.getMinutes());
        cal.add(Calendar.HOUR_OF_DAY, duration.getHours());
        cal.add(Calendar.DAY_OF_MONTH, duration.getDays());
        cal.add(Calendar.MONTH, duration.getMonths());
        cal.add(Calendar.YEAR, duration.getYears());

        return cal.getTime();
    }
}
