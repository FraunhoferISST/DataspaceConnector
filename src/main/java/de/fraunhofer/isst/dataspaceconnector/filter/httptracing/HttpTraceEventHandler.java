package de.fraunhofer.isst.dataspaceconnector.filter.httptracing;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Handles the processing of HttpTraces.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class HttpTraceEventHandler {

    /**
     * The global event publisher used for pushing the http traces.
     */
    private final @NonNull ApplicationEventPublisher publisher;

    /**
     * Processes raised HttpTraceEvents.
     *
     * @param trace The HttpTrace that needs to be processed
     */
    @Async
    @EventListener
    public void handleHttpTraceEvent(final HttpTrace trace) {
        if (log.isInfoEnabled()) {
            log.info("{}", trace);
        }
    }

    /**
     * Raise an HttpTraceEvent.
     *
     * @param trace The http trace that others should be notified about.
     */
    public void sendHttpTraceEvent(final HttpTrace trace) {
        publisher.publishEvent(trace);
    }
}
