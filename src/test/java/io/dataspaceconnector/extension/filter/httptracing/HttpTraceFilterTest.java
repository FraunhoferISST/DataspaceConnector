/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;

import io.dataspaceconnector.extension.filter.httptracing.internal.RequestWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class HttpTraceFilterTest {

    @Mock
    HttpTraceEventHandler eventHandler;

    @Mock
    MockHttpServletRequest request;

    @Mock
    MockHttpServletResponse response;

    @Mock
    MockFilterChain filterChain;

    @Captor
    private ArgumentCaptor<HttpTrace> traceCaptor;

    @InjectMocks
    private HttpTraceFilter filter;

    @Test
    public void doFilterInternal_validRequest_captureRequest() throws Exception {
        /* ARRANGE */
        final var now = ZonedDateTime.now();
        final var headers = new HashMap<>();
        headers.put("SOME", "HEADER");

        final var headerNames = Collections.enumeration(headers.keySet());

        Mockito.doReturn(headerNames).when(request).getHeaderNames();
        Mockito.doReturn("/URI").when(request).getRequestURI();
        Mockito.doReturn("METHOD").when(request).getMethod();
        Mockito.doReturn("CLIENT").when(request).getRemoteAddr();

        final var params = new HashMap<>();
        params.put("OTHER", "PARAMETER");

        final var parameterNames = Collections.enumeration(params.keySet());
        Mockito.doReturn(parameterNames).when(request).getParameterNames();

        final var customServletIS =
                RequestWrapper.class.getDeclaredClasses()[0].getDeclaredConstructor(byte[].class);
        customServletIS.setAccessible(true);
        final var requestBody =
                customServletIS.newInstance("BODY".getBytes(StandardCharsets.UTF_8));

        Mockito.doReturn(requestBody).when(request).getInputStream();
        Mockito.doReturn(StandardCharsets.UTF_8.name()).when(request).getCharacterEncoding();

        /* ACT */
        filter.doFilter(request, response, filterChain);

        /* ASSERT */
        Mockito.verify(eventHandler, Mockito.times(2)).sendHttpTraceEvent(traceCaptor.capture());
        final var traces = traceCaptor.getAllValues();

        assertEquals(2, traces.size());

        final var requestTrace = traces.get(0);
        assertNotNull(requestTrace.getTraceId());
        assertTrue(requestTrace.getTimestamp().isAfter(now));
        assertEquals("/URI", requestTrace.getUrl());
        assertEquals("METHOD", requestTrace.getMethod());
        assertEquals("CLIENT", requestTrace.getClient());

        final var responseTrace = traces.get(1);
        assertEquals(responseTrace.getTraceId(), requestTrace.getTraceId());
        assertTrue(responseTrace.getTimestamp().isAfter(requestTrace.getTimestamp()));
        assertNull(responseTrace.getMethod());
        assertNull(responseTrace.getUrl());
        assertEquals("ERROR", responseTrace.getBody());
        assertEquals(0, responseTrace.getHeaders().size());
        assertEquals(0, responseTrace.getStatus());
        assertNull(responseTrace.getClient());
        assertNull(responseTrace.getParameterMap());
    }
}
