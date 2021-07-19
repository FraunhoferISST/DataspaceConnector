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
package io.dataspaceconnector.controller.message;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.KeyType;
import de.fraunhofer.iais.eis.PublicKeyBuilder;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.controller.util.CommunicationProtocol;
import io.dataspaceconnector.service.ids.DeserializationService;
import io.dataspaceconnector.service.message.type.DescriptionRequestService;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {DescriptionRequestMessageController.class, DefaultCamelContext.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DescriptionRequestMessageControllerTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @MockBean
    private DescriptionRequestService messageService;

    @MockBean
    private DeserializationService deserializationService;

    @MockBean
    private ProducerTemplate producerTemplate;

    @Autowired
    private DescriptionRequestMessageController controller;

    @Test
    @SneakyThrows
    public void sendDescriptionRequestMessage_elementIdNull_returnDeserializedResponsePayload() {
        /* ARRANGE */
        final var recipient = URI.create("https://recipient.com");
        final var responsePayload = "Connector self description";
        final var response = getResponse(responsePayload);
        final var connector = getConnector();

        when(messageService.sendMessage(any(), any())).thenReturn(response);
        when(messageService.validateResponse(any())).thenReturn(true);
        when(deserializationService.getInfrastructureComponent(any())).thenReturn(connector);

        /* ACT */
        final var result = controller
                .sendMessage(recipient, null, CommunicationProtocol.MULTIPART);

        /* ASSERT */
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(connector.toRdf(), result.getBody().toString());

        verify(messageService, times(1)).sendMessage(recipient, null);
        verify(deserializationService, times(1))
                .getInfrastructureComponent(responsePayload);
    }

//    @Test
//    @SneakyThrows
//    public void sendDescriptionRequestMessage_invalidResponse_returnResponsePayloadWithCode417() {
//        /* ARRANGE */
//        final var recipient = URI.create("https://recipient.com");
//        final var responsePayload = "some payload";
//        final var response = getResponse(responsePayload);
//        final var responseContent = getResponseContent(responsePayload);
//
//        when(messageService.sendMessage(any(), any())).thenReturn(response);
//        when(messageService.validateResponse(any())).thenReturn(false);
//        when(messageService.getResponseContent(any())).thenReturn(responseContent);
//
//        /* ACT */
//        final var result = controller.sendMessage(recipient, null);
//
//        /* ASSERT */
//        assertEquals(HttpStatus.EXPECTATION_FAILED, result.getStatusCode());
//        assertNotNull(result.getBody());
//        assertEquals(responseContent.toString(), result.getBody().toString());
//    }

    @Test
    public void sendMessage_protocolIdscp_parseResponseFromRoute() {
        /* ARRANGE */
        final var connector = getConnector();
        final var payload = connector.toRdf();
        final var response = new Response(getMessage(), payload);

        when(producerTemplate.send(anyString(), any(Exchange.class))).thenReturn(exchange);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(Response.class)).thenReturn(response);
        when(deserializationService.getInfrastructureComponent(any())).thenReturn(connector);

        /* ACT */
        final var responseEntity = controller
                .sendMessage(URI.create("https://recipient.com"),
                        null, CommunicationProtocol.IDSCP2);

        /* ASSERT */
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(payload, responseEntity.getBody());
    }

    @Test
    public void sendMessage_protocolIdscp_returnResponseEntityFromErrorRoute() {
        /* ARRANGE */
        final var errorMessage = "Error message.";
        final var response = new ResponseEntity<Object>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);

        when(producerTemplate.send(anyString(), any(Exchange.class))).thenReturn(exchange);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(ResponseEntity.class)).thenReturn(response);

        /* ACT */
        final var responseEntity = controller
                .sendMessage(URI.create("https://recipient.com"),
                        null, CommunicationProtocol.IDSCP2);

        /* ASSERT */
        assertEquals(response, responseEntity);
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private Map<String, String> getResponse(final String payload) {
        final var response = new HashMap<String, String>();
        response.put("header", "IDS message header");
        response.put("payload", payload);
        return response;
    }

    private Connector getConnector() {
        return new BaseConnectorBuilder(URI.create("https://connector-id.com"))
                ._maintainer_(URI.create("https://example.com"))
                ._curator_(URI.create("https://example.com"))
                ._securityProfile_(SecurityProfile.BASE_SECURITY_PROFILE)
                ._outboundModelVersion_("4.0.0")
                ._inboundModelVersion_(Util.asList("4.0.0"))
                ._title_(Util.asList(new TypedLiteral("Dataspace Connector")))
                ._description_(Util.asList(new TypedLiteral(
                        "Test Connector")))
                ._version_("v3.0.0")
                ._publicKey_(new PublicKeyBuilder()
                        ._keyType_(KeyType.RSA)
                        ._keyValue_("something".getBytes())
                        .build()
                )
                ._hasDefaultEndpoint_(new ConnectorEndpointBuilder()
                        ._accessURL_(URI.create("/api/ids/data"))
                        .build())
                .build();
    }

    private Map<String, Object> getResponseContent(final String payload) {
        final var map = new HashMap<String, Object>();
        map.put("type", RejectionMessage.class);
        map.put("reason", RejectionReason.INTERNAL_RECIPIENT_ERROR);
        map.put("payload", payload);
        return map;
    }

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
