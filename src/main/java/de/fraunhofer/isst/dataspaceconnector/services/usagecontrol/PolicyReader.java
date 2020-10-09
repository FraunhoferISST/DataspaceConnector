package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This class reads the content of the policy rules and returns needed information to the {@link de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyVerifier}.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Component
public class PolicyReader {
    /**
     * Constant <code>LOGGER</code>
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(PolicyReader.class);

    /**
     * Gets the access frequency of a policy.
     *
     * @param rule The policy rule object.
     * @return The time frequency.
     */
    public Integer getMaxAccess(Rule rule) {
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
     * Gets the time interval of a policy.
     *
     * @param rule The policy rule object.
     * @return The time interval.
     */
    public TimeInterval getTimeInterval(Rule rule) {
        TimeInterval timeInterval = new TimeInterval();

        for (Constraint constraint : rule.getConstraint()) {
            if (constraint.getOperator() == BinaryOperator.AFTER) {
                timeInterval.setStart(constraint.getRightOperand().getValue());
            } else if (constraint.getOperator() == BinaryOperator.BEFORE) {
                timeInterval.setEnd(constraint.getRightOperand().getValue());
            }
        }
        return timeInterval;
    }

    /**
     * Gets the log path value of a policy.
     *
     * @param rule The policy rule object.
     * @return The found value.
     */
    public String getEndpoint(Rule rule) {
        Constraint constraint = rule.getConstraint().get(0);
        return constraint.getRightOperand().getValue();
    }

    /**
     * Gets the log path value of a policy.
     *
     * @param rule The policy rule object.
     * @return The found value.
     */
    public URI getPipEndpoint(Rule rule) {
        Constraint constraint = rule.getConstraint().get(0);
        return constraint.getPipEndpoint();
    }

    /**
     * Gets the date value of a policy.
     *
     * @param rule The policy constraint object.
     * @return The date or null.
     * @throws java.text.ParseException if any.
     */
    public Date getDate(Rule rule) throws ParseException {
        Constraint constraint = rule.getConstraint().get(0);
        String date = constraint.getRightOperand().getValue();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(Calendar.getInstance().getTimeZone());
        return sdf.parse(date);
    }

    /**
     * Gets the duration value of a policy.
     *
     * @param rule The policy constraint object.
     * @return The duration or null.
     * @throws javax.xml.datatype.DatatypeConfigurationException if any.
     */
    public Duration getDuration(Rule rule) throws DatatypeConfigurationException {
        Constraint constraint = rule.getConstraint().get(0);
        if (constraint.getRightOperand().getType().equals("xsd:duration")) {
            String duration = constraint.getRightOperand().getValue();
            return DatatypeFactory.newInstance().newDuration(duration);
        } else {
            return null;
        }
    }

    /**
     * <p>TimeInterval class.</p>
     *
     * @author Julia Pampus
     * @version $Id: $Id
     */
    public static class TimeInterval {
        private String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

        private Date start;
        private Date end;

        /**
         * <p>Constructor for TimeInterval.</p>
         */
        public TimeInterval() {
        }

        /**
         * <p>Constructor for TimeInterval.</p>
         *
         * @param start a {@link Date} object.
         * @param end   a {@link Date} object.
         */
        public TimeInterval(Date start, Date end) {
            this.start = start;
            this.end = end;
        }

        /**
         * <p>Getter for the field <code>start</code>.</p>
         *
         * @return a {@link Date} object.
         */
        public Date getStart() {
            return start;
        }

        /**
         * <p>Setter for the field <code>start</code>.</p>
         *
         * @param start a {@link String} object.
         */
        public void setStart(String start) {
            try {
                this.start = new SimpleDateFormat(DATE_FORMAT_PATTERN).parse(start);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        /**
         * <p>Getter for the field <code>end</code>.</p>
         *
         * @return a {@link Date} object.
         */
        public Date getEnd() {
            return end;
        }

        /**
         * <p>Setter for the field <code>end</code>.</p>
         *
         * @param end a {@link String} object.
         */
        public void setEnd(String end) {
            try {
                this.end = new SimpleDateFormat(DATE_FORMAT_PATTERN).parse(end);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
