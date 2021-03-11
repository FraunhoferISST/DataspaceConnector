package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    /**
     * The current connector configuration.
     */
    private final @NonNull ConfigurationContainer configContainer;

    public URI extractIdFromConnector() {
        // Get a local copy of the current connector.
        final var connector = configContainer.getConnector();

        return connector.getId();
    }

    public String extractOutboundModelVersionFromConnector() {
        // Get a local copy of the current connector.
        final var connector = configContainer.getConnector();

        return connector.getOutboundModelVersion();
    }
}
