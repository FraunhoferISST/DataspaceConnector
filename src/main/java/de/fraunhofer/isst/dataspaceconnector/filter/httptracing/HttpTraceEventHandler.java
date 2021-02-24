package de.fraunhofer.isst.dataspaceconnector.filter.httptracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Handles the processing of HttpTraces.
 */
@Component
@RequiredArgsConstructor
public class HttpTraceEventHandler {
    /**
     * The class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpTraceEventHandler.class);

    /**
     * The channel for publishing HttpTrace events.
     */
    private final transient @NonNull ApplicationEventPublisher eventPublisher;

    /**
     * Processes raised HttpTraceEvents.
     *
     * @param trace The HttpTrace that needs to be processed.
     */
    @Async
    @EventListener
    public void handleHttpTraceEvent(final HttpTrace trace) {
        LOGGER.info("{}", trace);
    }

    /**
     * Raise an HttpTraceEvent.
     *
     * @param trace The http trace that others should be notified about.
     */
    public void sendHttpTraceEvent(final HttpTrace trace) {
        eventPublisher.publishEvent(trace);
    }
}
