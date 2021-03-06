package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.config.ConnectorConfiguration;
import de.fraunhofer.isst.dataspaceconnector.exceptions.DataAccessLoggingFailedException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.DataAccessNotificationFailedException;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.NotificationMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

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
     * Service for handling requested resources.
     */
    private final @NonNull ResourceService<RequestedResource, ?> resourceService;

    /**
     * The current connector configuration.
     */
    private final @NonNull ConfigurationContainer configurationContainer;

    /**
     * Service for configuring policy settings.
     */
    private final @NonNull ConnectorConfiguration connectorConfiguration;

    /**
     * Service for ids notification messages.
     */
    private final @NonNull NotificationMessageService messageService;

    /**
     * Delete a resource by its id.
     *
     * @param resourceId The resource id.
     */
    public void deleteResource(final UUID resourceId) {
        resourceService.delete(resourceId);
    }

    /**
     * Send a message to the clearing house. Allow the access only if that operation was successful.
     * TODO: Validate response in more detail (dev merge)
     *
     * @param element The accessed element.
     * @throws DataAccessLoggingFailedException If the access could not be successfully logged.
     *                                          Automatically handled by TODO
     */
    public void logDataAccess(final URI element) throws DataAccessLoggingFailedException {
        final var recipient = connectorConfiguration.getClearingHouse();
        final var log = buildLog(element);

        Map<String, String> response;
        try {
            response = messageService.sendLogMessage(URI.create(recipient), log);
        } catch (Exception exception) {
            LOGGER.debug("Unsuccessful logging. [exception=({})]", exception.getMessage());
            throw new DataAccessLoggingFailedException("Log message could not be sent.");
        }

        if (response == null) {
            LOGGER.debug("No response received. [response=({})]", response);
            throw new DataAccessLoggingFailedException("Log message has no valid response.");
        }
    }

    /**
     * Send a message to the clearing house. Allow the access only if that operation was successful.
     * TODO: Validate response in more detail (dev merge)
     *
     * @param rule    The ids rule.
     * @param element The accessed element.
     * @throws DataAccessNotificationFailedException If the notification has not been successful.
     *                                               Automatically handled by TODO
     */
    public void reportDataAccess(final Rule rule, final URI element) throws DataAccessNotificationFailedException {
        final var recipient = PolicyUtils.getEndpoint(rule);
        final var log = buildLog(element);

        Map<String, String> response;
        try {
            response = messageService.sendNotificationMessage(URI.create(recipient), log);
        } catch (Exception exception) {
            LOGGER.debug("Notification message not sent. [exception=({})]", exception.getMessage());
            throw new DataAccessNotificationFailedException("Notification was not successful.");
        }

        if (response == null) {
            LOGGER.debug("No response received. [response=({})]", response);
            throw new DataAccessNotificationFailedException("Notification has no valid response.");
        }
    }

    /**
     * Build a log information object. TODO Format this?
     *
     * @param element The accessed element.
     * @return The log line.
     */
    private String buildLog(final URI element) {
        // Get a local copy of the current connector.
        final var connector = configurationContainer.getConnector();

        return Map.of(
                "target", element.toString(),
                "issuerConnector", connector.getId().toString(),
                "accessed", new Date().toString()).toString();
    }
}
