package io.dataspaceconnector.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Contains logging configuration.
 */
@Configuration
public class LoggerConfig {

    /**
     * Configures the logger for Camel.
     * @return the logger.
     */
    @Bean("route-logger")
    public Logger getCamelRouteLogger() {
        return LoggerFactory.getLogger("camel-route-logger");
    }
}
