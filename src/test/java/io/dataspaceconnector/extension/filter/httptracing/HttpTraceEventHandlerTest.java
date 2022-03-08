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

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

@SpringBootTest(classes = {HttpTraceEventHandler.class})
public class HttpTraceEventHandlerTest {

    @MockBean
    private ApplicationEventPublisher publisher;

    @Autowired
    private HttpTraceEventHandler handler;

    @Test
    public void sendHttpTraceEvent_validTrace_publishHttpTraceEvent() {
        /* ARRANGE */
        final var trace = new HttpTrace();
        trace.setBody("HELLO");

        /* ACT */
        handler.sendHttpTraceEvent(trace);

        /* ASSERT */
        Mockito.verify(publisher, Mockito.atMostOnce()).publishEvent(Mockito.eq(trace));
    }

    @Test
    public void sendHttpTraceEvent_null_dontPublishEvent() {
        /* ARRANGE */
        /* ACT */
        handler.sendHttpTraceEvent(null);

        /* ASSERT */
        Mockito.verify(publisher, Mockito.never()).publishEvent(Mockito.any());
    }

    @Test
    public void handleHttpTraceEvent_validTrace_logIt() {
        /* ARRANGE */
        final var trace = new HttpTrace();
        trace.setBody("HELLO");

        /* ACT */
        handler.handleHttpTraceEvent(trace);

        /* ASSERT */
    }
}
