package de.fraunhofer.isst.dataspaceconnector.config;

import de.fraunhofer.isst.ids.framework.communication.http.HttpService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.Duration;

/**
 * This class handles connection settings for outgoing http connections.
 */
@Configuration
@RequiredArgsConstructor
public class ConnectionConfiguration {

    /**
     * Global timeout value.
     */
    @Value("${http.timeout.connect}")
    private long connectTimeout;

    /**
     * Read timeout value.
     */
    @Value("${http.timeout.read}")
    private long readTimeout;

    /**
     * Write timeout value.
     */
    @Value("${http.timeout.write}")
    private long writeTimeout;

    /**
     * Call timeout value.
     */
    @Value("${http.timeout.call}")
    private long callTimeout;

    /**
     * Service for http connections.
     */
    private final @NonNull HttpService httpService;

    /**
     * Hand over connection settings from the application.properties to the http client. Either the
     * three values connect, read, and write are used, or the global call timeout.
     */
    @PostConstruct
    public void setTimeouts() {
        if (connectTimeout != 0 && readTimeout != 0 && writeTimeout != 0) {
            httpService.setTimeouts(
                    Duration.ofMillis(connectTimeout),
                    Duration.ofMillis(readTimeout),
                    Duration.ofMillis(writeTimeout),
                    null);
        } else {
            httpService.setTimeouts(null, null, null,
                    Duration.ofMillis(callTimeout));
        }
    }
}
