package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.config.ConnectorConfiguration;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.PolicyExecutionException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.LogMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.NotificationMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.EntityResolver;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.LogMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.NotificationService;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Executes policy conditions. Refers to the ids policy enforcement point (PEP).
 */
@Service
@RequiredArgsConstructor
public class PolicyExecutionService {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyExecutionService.class);

    /**
     * Service for configuring policy settings.
     */
    private final @NonNull ConnectorConfiguration connectorConfig;

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Service for ids notification messages.
     */
    private final @NonNull NotificationService notificationService;

    /**
     * Service for ids log messages.
     */
    private final @NonNull LogMessageService logMessageService;

    /**
     * Service for resolving database entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Delete data by artifact id.
     *
     * @param target The artifact id.
     * @throws ResourceNotFoundException If the artifact update fails.
     */
    public void deleteDataFromArtifact(final URI target) throws ResourceNotFoundException {
        final var entityId = EndpointUtils.getUUIDFromPath(target);

        // Update data for artifact.
        entityResolver.updateDataOfArtifact(entityId, "");
        LOGGER.info("Deleted data from artifact. [target=({})]", target);
    }

    /**
     * Send a message to the clearing house. Allow the access only if that operation was successful.
     *
     * @param element The accessed element.
     * @throws PolicyExecutionException If the access could not be successfully logged.
     */
    public void logDataAccess(final URI element) throws PolicyExecutionException {
        final var url = connectorConfig.getClearingHouse();
        final var log = buildLog(element);

        try {
            final var desc = new LogMessageDesc();
            desc.setRecipient(url);
            final var response = logMessageService.sendMessage(desc, log.toString());

            if (response == null) {
                LOGGER.debug("No response received.");
                throw new PolicyExecutionException("Log message has no valid response.");
            }
        } catch (MessageException exception) {
            LOGGER.debug("Unsuccessful logging. [exception=({})]", exception.getMessage());
            throw new PolicyExecutionException("Log message could not be sent.");
        }
    }

    /**
     * Send a message to the clearing house. Allow the access only if that operation was successful.
     *
     * @param rule    The ids rule.
     * @param element The accessed element.
     * @throws PolicyExecutionException If the notification has not been successful.
     */
    public void reportDataAccess(final Rule rule, final URI element) throws PolicyExecutionException {
        final var recipient = PolicyUtils.getEndpoint(rule);
        final var log = buildLog(element);

        Map<String, String> response;
        try {
            final var desc = new NotificationMessageDesc();
            desc.setRecipient(URI.create(recipient));
            response = notificationService.sendMessage(desc, log.toString());
        } catch (MessageException exception) {
            LOGGER.debug("Notification message not sent. [exception=({})]", exception.getMessage());
            throw new PolicyExecutionException("Notification was not successful.");
        }

        if (response == null) {
            LOGGER.debug("No response received. [response=({})]", response);
            throw new PolicyExecutionException("Notification has no valid response.");
        }
    }

    /**
     * Build a log information object.
     *
     * @param target The accessed element.
     * @return The log line.
     */
    private Map<String, Object> buildLog(final URI target) {
        final var id = connectorService.getConnectorId();

        return new HashMap<>() {{
            put("target", target);
            put("issuerConnector", id);
            put("accessed", new Date());
        }};
    }
}
