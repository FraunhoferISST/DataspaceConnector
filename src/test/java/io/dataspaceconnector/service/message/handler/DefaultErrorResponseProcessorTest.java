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
package io.dataspaceconnector.service.message.handler;

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.dto.Response;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {DefaultErrorResponseProcessor.class})
public class DefaultErrorResponseProcessorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @MockBean
    private ConnectorService connectorService;

    @Autowired
    private DefaultErrorResponseProcessor processor;

    @BeforeEach
    public void init() {
        when(exchange.getIn()).thenReturn(in);
        doReturn(URI.create("https://connector")).when(connectorService).getConnectorId();
        doReturn("4.0.0").when(connectorService).getOutboundModelVersion();
    }

    @Test
    @SneakyThrows
    public void process_requestInBody_logMessageHeader() {
        /* ARRANGE */
        final var message = getMessage();
        final var request = new Request<>(message, null, Optional.empty());

        when(in.getBody(Request.class)).thenReturn(request);

        /* ACT & ASSERT */
        assertDoesNotThrow(() -> processor.process(exchange));
    }

    @Test
    @SneakyThrows
    public void process_responseInBody_logMessageHeader() {
        /* ARRANGE */
        final var message = getMessage();
        final var response = new Response(message, "body");

        when(in.getBody(Response.class)).thenReturn(response);

        /* ACT & ASSERT */
        assertDoesNotThrow(() -> processor.process(exchange));
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private DescriptionRequestMessage getMessage() {
        return new DescriptionRequestMessageBuilder()
                ._issuerConnector_(URI.create("https://connector.com"))
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._securityToken_(new DynamicAttributeTokenBuilder()
                        ._tokenValue_("value")
                        ._tokenFormat_(TokenFormat.JWT)
                        .build())
                ._modelVersion_("version")
                ._senderAgent_(URI.create("https://connector.com"))
                .build();
    }

}
