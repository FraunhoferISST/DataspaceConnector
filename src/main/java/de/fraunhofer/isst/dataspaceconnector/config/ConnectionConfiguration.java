package de.fraunhofer.isst.dataspaceconnector.config;

import de.fraunhofer.isst.ids.framework.communication.http.HttpService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
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

    private final HttpService httpService;

    @Autowired
    public ConnectionConfiguration(HttpService httpService) {
        this.httpService = httpService;
    }

    @PostConstruct
    public void setTimeouts() {
        if (connectTimeout != 0 && readTimeout != 0 && writeTimeout != 0) {
            httpService.setTimeouts(
                    Duration.ofMillis(connectTimeout),
                    Duration.ofMillis(readTimeout),
                    Duration.ofMillis(writeTimeout),
                    null);
        } else {
            httpService.setTimeouts(
                    null,
                    null,
                    null,
                    Duration.ofMillis(callTimeout));
        }
    }
}
