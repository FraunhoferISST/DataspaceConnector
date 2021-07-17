package io.dataspaceconnector.service.usagecontrol;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;

import io.dataspaceconnector.service.ids.ConnectorService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * Builds log messages for ids communication.
 */
@Component
@RequiredArgsConstructor
public class LogBuilder {

    /**
     * Service for the current connector configuration.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Build a log information object.
     * @param target The accessed element.
     * @return The log line.
     */
    public String buildLog(final URI target) {
        final var id = connectorService.getConnectorId();

        final var output = new HashMap<String, Object>();
        output.put("target", target);
        output.put("issuerConnector", id);
        output.put("accessed", ZonedDateTime.now(ZoneOffset.UTC));

        return output.toString();
    }
}
