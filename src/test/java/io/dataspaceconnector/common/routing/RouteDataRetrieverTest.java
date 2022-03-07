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
package io.dataspaceconnector.common.routing;

import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import io.dataspaceconnector.common.exception.DataRetrievalException;
import io.dataspaceconnector.common.exception.NotImplemented;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {RouteDataRetriever.class})
class RouteDataRetrieverTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @MockBean
    private ExtendedCamelContext camelContext;

    @MockBean
    private ProducerTemplate producerTemplate;

    @Autowired
    private RouteDataRetriever routeDataRetriever;

    @Test
    @SneakyThrows
    void get_noExceptionInRoute_returnData() {
        /* ARRANGE */
        final var url = new URL("https://" + UUID.randomUUID());
        final var response = "Some data retrieved via Camel";

        when(producerTemplate.send(anyString(), any(Exchange.class))).thenReturn(exchange);
        when(exchange.getIn()).thenReturn(in);
        when(exchange.getException()).thenReturn(null);
        when(in.getBody(String.class)).thenReturn(response);

        /* ACT */
        final var result = routeDataRetriever.get(url, null);

        /* ASSERT */
        assertEquals(response, new String(result.getData().readAllBytes()));
    }

    @Test
    @SneakyThrows
    void get_exceptionInRoute_throwDataRetrievalException() {
        /* ARRANGE */
        final var url = new URL("https://" + UUID.randomUUID());

        when(producerTemplate.send(anyString(), any(Exchange.class))).thenReturn(exchange);
        when(exchange.getIn()).thenReturn(in);
        when(exchange.getException()).thenReturn(new IllegalArgumentException());

        /* ACT && ASSERT */
        assertThrows(DataRetrievalException.class, () -> routeDataRetriever.get(url, null));
    }

    @Test
    @SneakyThrows
    void get_withAuth_throwNotImplementedException() {
        /* ARRANGE */
        final var url = new URL("https://" + UUID.randomUUID());

        /* ACT && ASSERT */
        assertThrows(NotImplemented.class,
                () -> routeDataRetriever.get(url, null, new ArrayList<>()));
    }

}
