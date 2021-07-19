package io.dataspaceconnector.camel.processor.controller.helper;

import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import io.dataspaceconnector.service.ids.ConnectorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Updates the connector configuration in preparation for sending a ConnectorUpdate- or
 * ConnectorUnavailableMessage.
 */
@Component("ConfigurationUpdater")
@RequiredArgsConstructor
public
class ConfigurationUpdater extends IdsHelperProcessor {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Updates the connector configuration.
     * @param exchange the exchange.
     * @throws ConfigUpdateException if updating the configuration fails.
     */
    @Override
    protected void processInternal(final Exchange exchange) throws ConfigUpdateException {
        // Update the config model.
        connectorService.updateConfigModel();
    }

}
