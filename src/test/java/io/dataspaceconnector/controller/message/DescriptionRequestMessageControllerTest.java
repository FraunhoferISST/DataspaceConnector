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
import de.fraunhofer.iais.eis.KeyType;
import de.fraunhofer.iais.eis.PublicKeyBuilder;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.exception.MessageException;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.exception.UnexpectedResponseException;
import io.dataspaceconnector.service.ids.DeserializationService;
import io.dataspaceconnector.service.message.type.DescriptionRequestService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {DescriptionRequestMessageController.class})
public class DescriptionRequestMessageControllerTest {

    @MockBean
    private DescriptionRequestService messageService;

    @MockBean
    private DeserializationService deserializationService;

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
        final var result = controller.sendMessage(recipient, null);

        /* ASSERT */
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(connector.toRdf(), result.getBody().toString());

        verify(messageService, times(1)).sendMessage(recipient, null);
        verify(deserializationService, times(1))
                .getInfrastructureComponent(responsePayload);
    }

    @Test
    @SneakyThrows
    public void sendDescriptionRequestMessage_elementIdNull_returnDeserializedResponsePayloadButCannotIdentityPayload() {
        /* ARRANGE */
        final var recipient = URI.create("https://recipient.com");
        final var responsePayload = "{ This is not an infrastructure component. }";
        final var response = getResponse(responsePayload);

        when(messageService.sendMessage(any(), any())).thenReturn(response);
        when(messageService.validateResponse(any())).thenReturn(true);
        Mockito.doThrow(IllegalArgumentException.class).when(deserializationService).getInfrastructureComponent(any());

        /* ACT */
        final var result = controller.sendMessage(recipient, null);

        /* ASSERT */
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(responsePayload, result.getBody().toString());

        verify(messageService, times(1)).sendMessage(recipient, null);
        verify(deserializationService, times(1))
                .getInfrastructureComponent(responsePayload);
    }

    @Test
    @SneakyThrows
    public void sendDescriptionRequestMessage_messageException_respondWithIdsFailed() {
        /* ARRANGE */
        final var recipient = URI.create("https://recipient.com");

        when(messageService.sendMessage(any(), any())).thenThrow(MessageException.class);

        /* ACT */
        final var result = controller.sendMessage(recipient, null);

        /* ASSERT */
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    @SneakyThrows
    public void sendDescriptionRequestMessage_messageException_respondWithInvalidResponse() {
        /* ARRANGE */
        final var recipient = URI.create("https://recipient.com");

        when(messageService.sendMessage(any(), any())).thenThrow(MessageResponseException.class);

        /* ACT */
        final var result = controller.sendMessage(recipient, null);

        /* ASSERT */
        assertEquals(HttpStatus.BAD_GATEWAY, result.getStatusCode());
    }

    @Test
    @SneakyThrows
    public void sendDescriptionRequestMessage_messageException_respondWithUnexpectedResponse() {
        /* ARRANGE */
        final var recipient = URI.create("https://recipient.com");

        when(messageService.sendMessage(any(), any())).thenThrow(UnexpectedResponseException.class);

        /* ACT */
        final var result = controller.sendMessage(recipient, null);

        /* ASSERT */
        assertEquals(HttpStatus.EXPECTATION_FAILED, result.getStatusCode());
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
}
