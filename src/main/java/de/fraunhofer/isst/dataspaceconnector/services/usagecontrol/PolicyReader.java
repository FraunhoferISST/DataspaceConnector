package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.Rule;
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
 * This class reads the content of the policy rules and returns required information to the {@link
 * de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyVerifier}.
 */
@Component
public class PolicyReader {

    /**
     * Gets the allowed number of accesses defined in a policy.
     *
     * @param rule the policy rule object.
     * @return the number of allowed accesses.
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
     * Gets the time interval defined in a policy.
     *
     * @param rule the policy rule object.
     * @return the time interval.
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
     * Gets the endpoint value to send notifications to defined in a policy.
     *
     * @param rule the policy rule object.
     * @return the endpoint value.
     */
    public String getEndpoint(Rule rule) {
        Constraint constraint = rule.getConstraint().get(0);
        return constraint.getRightOperand().getValue();
    }

    /**
     * Gets the PIP endpoint path value defined in a policy.
     *
     * @param rule the policy rule object.
     * @return the pip endpoint value.
     */
    public URI getPipEndpoint(Rule rule) {
        Constraint constraint = rule.getConstraint().get(0);
        return constraint.getPipEndpoint();
    }

    /**
     * Gets the date value defined in a policy.
     *
     * @param rule the policy constraint object.
     * @return the date or null.
     * @throws java.text.ParseException if the date cannot be parsed.
     */
    public Date getDate(Rule rule) throws ParseException {
        Constraint constraint = rule.getConstraint().get(0);
        String date = constraint.getRightOperand().getValue();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(Calendar.getInstance().getTimeZone());
        return sdf.parse(date);
    }

    /**
     * Gets the duration value defined in a policy.
     *
     * @param rule the policy constraint object.
     * @return the duration or null.
     * @throws javax.xml.datatype.DatatypeConfigurationException if the duration cannot be parsed.
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
     * Returns the right operand value as an URI.
     *
     * @param rule the policy constraint object
     * @return the URI of the connector
     */
    public URI getAllowedConnector(Rule rule) {
        Constraint constraint = rule.getConstraint().get(0);
        String allowedConnectorAsString = constraint.getRightOperand().getValue();
        return URI.create(allowedConnectorAsString);
    }

    /**
     * Inner class for a time interval format.
     */
    public static class TimeInterval {

        private final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

        private Date start;
        private Date end;

        /**
         * Constructor for TimeInterval.
         */
        public TimeInterval() {
        }

        public Date getStart() {
            return start;
        }

        /**
         * Sets the start of a time interval.
         * @param start string containing a date formatted as specified in {@link TimeInterval#DATE_FORMAT_PATTERN}
         */
        public void setStart(String start) {
            try {
                this.start = new SimpleDateFormat(DATE_FORMAT_PATTERN).parse(start);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public Date getEnd() {
            return end;
        }

        /**
         * Sets the end of a time interval.
         * @param end string containing a date formatted as specified in {@link TimeInterval#DATE_FORMAT_PATTERN}
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
