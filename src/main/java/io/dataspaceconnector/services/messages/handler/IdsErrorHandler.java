package io.dataspaceconnector.services.messages.handler;

import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdsErrorHandler {

    @Bean(name = "Error")
    public DeadLetterChannelBuilder getErrorHandler() {
        final var builder = new DeadLetterChannelBuilder();
        builder.setDeadLetterUri("direct:error");

        return builder;
    }
}
