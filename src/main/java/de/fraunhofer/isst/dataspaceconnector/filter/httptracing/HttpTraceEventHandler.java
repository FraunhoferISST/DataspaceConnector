package de.fraunhofer.isst.dataspaceconnector.filter.httptracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Handles the processing of HttpTraces.
 */
@Component
public class HttpTraceEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTraceEventHandler.class);
    private final ApplicationEventPublisher publisher;

    HttpTraceEventHandler(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Async
    @EventListener
    public void handleHttpTraceEvent(HttpTrace trace) {
        LOGGER.info("{}", trace);
    }

    /**
     * Raise a HttpTraceEvent.
     * @param trace The http trace that others should be notified about.
     */
    public void sendHttpTraceEvent(HttpTrace trace) {
        publisher.publishEvent(trace);
    }
}
