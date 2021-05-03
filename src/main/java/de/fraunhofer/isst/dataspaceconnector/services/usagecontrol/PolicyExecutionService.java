package de.fraunhofer.isst.dataspaceconnector.services.usagecontrol;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.config.ConnectorConfiguration;
import de.fraunhofer.isst.dataspaceconnector.exceptions.PolicyExecutionException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RdfBuilderException;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.LogMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.NotificationService;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
public class PolicyExecutionService {

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
     * Send contract agreement to clearing house.
     *
     * @param agreement The ids contract agreement.
     */
    public void sendAgreement(final ContractAgreement agreement) {
        try {
            final var rdf = IdsUtils.toRdf(agreement);
            final var recipient = connectorConfig.getClearingHouse();
            logMessageService.sendMessage(recipient, rdf);
        } catch (PolicyExecutionException | RdfBuilderException exception) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to send contract agreement to clearing house. "
                        + "[exception=({})]", exception.getMessage());
            }
        }
    }

    /**
     * Send a message to the clearing house. Allow the access only if that operation was successful.
     *
     * @param target The target object.
     * @throws PolicyExecutionException if the access could not be successfully logged.
     */
    public void logDataAccess(final URI target) throws PolicyExecutionException {
        final var recipient = connectorConfig.getClearingHouse();
        final var logItem = buildLog(target).toString();

        logMessageService.sendMessage(recipient, logItem);
    }

    /**
     * Send a message to the clearing house. Allow the access only if that operation was successful.
     *
     * @param rule    The ids rule.
     * @param element The accessed element.
     * @throws PolicyExecutionException If the notification has not been successful.
     */
    public void reportDataAccess(final Rule rule, final URI element)
            throws PolicyExecutionException {
        final var recipient = PolicyUtils.getEndpoint(rule);
        final var logItem = buildLog(element).toString();

        notificationService.sendMessage(URI.create(recipient), logItem);
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
