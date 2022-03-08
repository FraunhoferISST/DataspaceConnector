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
package io.dataspaceconnector.service.routing.processor;

import java.util.HashMap;

import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.routing.ParameterUtils;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HeaderProcessorTest {

    private final HeaderProcessor processor = new HeaderProcessor();

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @Test
    @SneakyThrows
    void process_queryInputNull_doNothing() {
        /* ARRANGE */
        when(exchange.getProperty(ParameterUtils.QUERY_INPUT_PARAM, QueryInput.class))
                .thenReturn(null);

        /* ACT */
        processor.process(exchange);

        /* ASSERT */
        verify(in, never()).setHeader(any(), any());
    }

    @Test
    @SneakyThrows
    void process_headersNull_doNothing() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        queryInput.setHeaders(null);

        when(exchange.getProperty(ParameterUtils.QUERY_INPUT_PARAM, QueryInput.class))
                .thenReturn(queryInput);

        /* ACT */
        processor.process(exchange);

        /* ASSERT */
        verify(in, never()).setHeader(any(), any());
    }

    @Test
    @SneakyThrows
    void process_headersEmpty_doNothing() {
        /* ARRANGE */
        final var queryInput = new QueryInput();

        when(exchange.getProperty(ParameterUtils.QUERY_INPUT_PARAM, QueryInput.class))
                .thenReturn(queryInput);

        /* ACT */
        processor.process(exchange);

        /* ASSERT */
        verify(in, never()).setHeader(any(), any());
    }

    @Test
    @SneakyThrows
    void process_withHeaders_addCamelHeaders() {
        /* ARRANGE */
        final var headers = new HashMap<String, String>() {{
            put("key1", "value1");
            put("key2", "value2");
        }};
        final var queryInput = new QueryInput();
        queryInput.setHeaders(headers);

        when(exchange.getProperty(ParameterUtils.QUERY_INPUT_PARAM, QueryInput.class))
                .thenReturn(queryInput);
        when(exchange.getIn()).thenReturn(in);

        /* ACT */
        processor.process(exchange);

        /* ASSERT */
        headers.forEach((k, v) -> verify(in, times(1)).setHeader(k, v));
    }

}
