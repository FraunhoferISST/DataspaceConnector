package de.fraunhofer.isst.dataspaceconnector.config;

import de.fraunhofer.isst.ids.framework.communication.http.HttpService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ConnectionConfiguration {

    @Value("${http.timeout.connect}")
    private long connectTimeout;

    @Value("${http.timeout.read}")
    private long readTimeout;

    @Value("${http.timeout.write}")
    private long writeTimeout;

    @Value("${http.timeout.call}")
    private long callTimeout;

    @Autowired
    public ConnectionConfiguration(HttpService httpService) {
        httpService.setTimeouts(
                Duration.ofMillis(connectTimeout),
                Duration.ofMillis(readTimeout),
                Duration.ofMillis(writeTimeout),
                Duration.ofMillis(callTimeout));
    }
}
