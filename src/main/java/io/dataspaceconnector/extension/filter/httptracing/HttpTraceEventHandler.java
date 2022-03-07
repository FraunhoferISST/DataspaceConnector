/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.extension.filter.httptracing;

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
        if (trace != null) {
            publisher.publishEvent(trace);
        }
    }
}
