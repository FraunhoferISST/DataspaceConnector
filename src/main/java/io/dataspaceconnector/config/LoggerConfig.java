package io.dataspaceconnector.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerConfig {

    @Bean("route-logger")
    public Logger getCamelRouteLogger() {
        return LoggerFactory.getLogger("camel-route-logger");
    }
}
