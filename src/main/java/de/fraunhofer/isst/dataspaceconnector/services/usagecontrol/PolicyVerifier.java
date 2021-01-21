package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.LogMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.NotificationMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.utils.HttpUtils;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class provides access permission information for the {@link de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler}
 * depending on the policy content.
 */
@Component
public class PolicyVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyVerifier.class);

    private final PolicyReader policyReader;
    private final NotificationMessageService notificationMessageService;
    private final LogMessageService logMessageService;
    private final HttpUtils httpUtils;

    /**
     * Constructor for PolicyVerifier.
     *
     */
    @Autowired
    public PolicyVerifier(PolicyReader policyReader, LogMessageService logMessageService,
        NotificationMessageService notificationMessageService, HttpUtils httpUtils)
        throws IllegalArgumentException {
        if (policyReader == null)
            throw new IllegalArgumentException("The PolicyReader cannot be null.");

        if (logMessageService == null)
            throw new IllegalArgumentException("The LogMessageService cannot be null.");

        if (notificationMessageService == null)
            throw new IllegalArgumentException("The NotificationMessageService cannot be null.");

        if (httpUtils == null)
            throw new IllegalArgumentException("The HttpUtils cannot be null.");

        this.policyReader = policyReader;
        this.logMessageService = logMessageService;
        this.notificationMessageService = notificationMessageService;
        this.httpUtils = httpUtils;
    }

    /**
     * Allows data access.
     *
     * @return Access allowed.
     */
    @SuppressWarnings("SameReturnValue")
    public boolean allowAccess() {
        return true;
    }

    /**
     * Inhibits data access.
     *
     * @return Access denied.
     */
    @SuppressWarnings("SameReturnValue")
    public boolean inhibitAccess() {
        return false;
    }

    /**
     * Saves the access date into the database.
     * TODO: Validate response in more detail.
     * TODO: Add log message.
     *
     * @return Success or not (access or inhibition).
     */
    public boolean logAccess() {
        Map<String, String> response;
        try {
            response = logMessageService.sendMessage("");
        } catch (Exception exception) {
            LOGGER.warn("Log message could not be sent. [exception=({})]", exception.getMessage());
            return allowAccess();
        }
        if (response != null) {
            return allowAccess();
        } else {
            LOGGER.warn("No response received.");
            return allowAccess();
        }
    }

    /**
     * Notify participant about data access.
     * TODO: Validate response in more detail.
     *
     * @param contract a {@link de.fraunhofer.iais.eis.Contract} object.
     * @return Success or not (access or inhibition).
     */
    public boolean sendNotification(Contract contract) {
        Rule rule = contract.getPermission().get(0).getPostDuty().get(0);
        String recipient = policyReader.getEndpoint(rule);

        Map<String, String> response;
        try {
            notificationMessageService.setRequestParameters(URI.create(recipient));
            response = notificationMessageService.sendMessage("");
        } catch (Exception exception) {
            LOGGER.warn("Notification message could not be sent. [exception=({})]", exception.getMessage());
            return allowAccess();
        }

        if (response != null) {
            return allowAccess();
        } else {
            LOGGER.warn("No response received.");
            return allowAccess();
        }
    }

    /**
     * Checks if the requested access is in the allowed time interval.
     *
     * @param contract a {@link de.fraunhofer.iais.eis.Contract} object.
     * @return If this is the case, access is provided.
     */
    public boolean checkInterval(Contract contract) {
        PolicyReader.TimeInterval timeInterval = policyReader
            .getTimeInterval(contract.getPermission().get(0));
        Date date = new Date();

        if (date.after(timeInterval.getStart()) && date.before(timeInterval.getEnd())) {
            return allowAccess();
        } else {
            return inhibitAccess();
        }
    }

    /**
     * Checks whether the current date is later than the specified one.
     *
     * @param dateNow   The current date.
     * @param maxAccess The target date.
     * @return True if the date has been already exceeded, false if not.
     */
    public boolean checkDate(Date dateNow, Date maxAccess) {
        return dateNow.after(maxAccess);
    }

    /**
     * Adds a duration to a date to get the a date.
     *
     * @param created  The date when the resource was created.
     * @param contract a {@link de.fraunhofer.iais.eis.Contract} object.
     * @return True if the resource should be deleted, false if not.
     */
    public boolean checkDuration(Date created, Contract contract) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(created);
        try {
            Duration duration = policyReader.getDuration(contract.getPermission().get(0));

            cal.add(Calendar.SECOND, duration.getSeconds());
            cal.add(Calendar.MINUTE, duration.getMinutes());
            cal.add(Calendar.HOUR_OF_DAY, duration.getHours());
            cal.add(Calendar.DAY_OF_MONTH, duration.getDays());
            cal.add(Calendar.MONTH, duration.getMonths());
            cal.add(Calendar.YEAR, duration.getYears());

            return !checkDate(new Date(), cal.getTime());
        } catch (DatatypeConfigurationException e) {
            return inhibitAccess();
        }
    }

    /**
     * Checks whether the maximum of access number is already reached.
     *
     * @param contract a {@link de.fraunhofer.iais.eis.Contract} object.
     * @param uuid     a {@link java.util.UUID} object.
     * @return If this is not the case, access is provided. Otherwise, data is deleted and access
     * denied.
     */
    public boolean checkFrequency(Contract contract, UUID uuid) {
        int max = policyReader.getMaxAccess(contract.getPermission().get(0));
        URI pip = policyReader.getPipEndpoint(contract.getPermission().get(0));

        try {
            String accessed = httpUtils
                .sendHttpsGetRequestWithBasicAuth(pip + uuid.toString() + "/access", "admin",
                    "password");
            if (Integer.parseInt(accessed) >= max) {
                return inhibitAccess();
            } else {
                return allowAccess();
            }
        } catch (IOException | RuntimeException e) {
            return inhibitAccess();
        }
    }

    /**
     * Checks if the duration since resource creation or the max date for resource access has been
     * already exceeded.
     *
     * @param rule a {@link de.fraunhofer.iais.eis.Rule} object.
     * @return True if the resource should be deleted, false if not.
     * @throws java.text.ParseException if any.
     */
    public boolean checkForDelete(Rule rule) throws ParseException {
        Date max = policyReader.getDate(rule);
        if (max != null) {
            return checkDate(new Date(), max);
        } else {
            return false;
        }
    }
}
