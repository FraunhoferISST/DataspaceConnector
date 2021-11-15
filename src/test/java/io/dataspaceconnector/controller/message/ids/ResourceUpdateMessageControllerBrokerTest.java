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
package io.dataspaceconnector.controller.message.ids;

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.ids.messaging.broker.IDSBrokerService;
import de.fraunhofer.ids.messaging.requests.MessageContainer;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.service.message.handler.dto.Response;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ResourceUpdateMessageControllerBrokerTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @MockBean
    private IDSBrokerService brokerService;

    @MockBean
    private ConnectorService connectorService;

    @MockBean
    private ProducerTemplate producerTemplate;

    @SpyBean
    private ConnectorConfig connectorConfig;

    @Autowired
    private MockMvc mockMvc;

    private final UUID resourceId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
    private final URI resourceURI = URI.create(resourceId.toString());
    private final Resource resource = getResource();
    private final String recipient = "https://someURL";
    private final DynamicAttributeToken token = new DynamicAttributeTokenBuilder()
            ._tokenValue_("token")
            ._tokenFormat_(TokenFormat.JWT)
            .build();

    @BeforeEach
    public void init() {
        Mockito.doReturn("6.0.0").when(connectorConfig).getDefaultVersion();
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_noRecipient_throws400() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/resource/update")
                .param("resourceId", resourceId.toString()))
                .andReturn();

        /* ASSERT */
        assertNotNull(result.getResponse());
        assertTrue(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_noResourceId_throws400() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/resource/update")
                .param("recipient", "https://someUrl"))
                .andReturn();

        /* ASSERT */
        assertNotNull(result.getResponse());
        assertTrue(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_resourceNotFound_throws404() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(Optional.empty()).when(connectorService).getOfferedResourceById(resourceURI);

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/resource/update")
                .param("recipient", "https://someURL")
                .param("resourceId", resourceId.toString())
                .param("protocol", "MULTIPART"))
                .andReturn();

        /* ASSERT */
        assertNotNull(result.getResponse());
        assertEquals(404, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_failUpdateAtBroker_throws500() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(Optional.of(resource)).when(connectorService).getOfferedResourceById(Mockito.eq(resourceURI));
        Mockito.doThrow(IOException.class).when(brokerService).updateResourceAtBroker(Mockito.any(),
                Mockito.eq(resource));

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/resource/update")
                .param("recipient", "https://someURL")
                .param("resourceId", resourceId.toString())
                .param("protocol", "MULTIPART"))
                .andReturn();

        /* ASSERT */
        assertNotNull(result.getResponse());
        assertEquals(500, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_brokerEmptyResponseBody_throws500() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(Optional.of(resource)).when(connectorService).getOfferedResourceById(Mockito.eq(resourceURI));
        Mockito.doThrow(IOException.class).when(brokerService).updateResourceAtBroker(Mockito.any(),
                Mockito.eq(resource));

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/resource/update")
                .param("recipient", recipient)
                .param("resourceId", resourceId.toString())
                .param("protocol", "MULTIPART"))
                .andReturn();

        /* ASSERT */
        assertNotNull(result.getResponse());
        assertEquals(500, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_validRequest_returnsBrokerResponse() throws Exception {
        /* ARRANGE */
        final var message = new MessageProcessedNotificationMessageBuilder()
                ._issuerConnector_(new URI("https://url"))
                ._correlationMessage_(new URI("https://cormessage"))
                ._issued_(DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar("2009-05-07T17:05:45.678Z"))
                ._senderAgent_(new URI("https://sender"))
                ._modelVersion_("4.0.0")
                ._securityToken_(token)
                .build();

        final var response = new MessageContainer<>(message, "EMPTY");

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(Optional.of(resource)).when(connectorService).getOfferedResourceById(Mockito.eq(resourceURI));
        Mockito.doReturn(response).when(brokerService).updateResourceAtBroker(Mockito.any(),
                Mockito.eq(resource));

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/resource/update")
                .param("recipient", recipient)
                .param("resourceId", resourceId.toString())
                .param("protocol", "MULTIPART"))
                .andReturn();

        /* ASSERT */
        assertNotNull(result.getResponse());
        assertEquals("EMPTY", result.getResponse().getContentAsString());
    }

    @Test
    @SneakyThrows
    @WithMockUser("ADMIN")
    public void sendMessage_protocolIdscp_parseResponseFromRoute() {
        /* ARRANGE */
        final var response = new Response(getMessage(), "body");

        Mockito.doReturn(token).when(connectorService).getCurrentDat();

        when(producerTemplate.send(anyString(), any(Exchange.class))).thenReturn(exchange);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(Response.class)).thenReturn(response);
        when(connectorConfig.isIdscpEnabled()).thenReturn(true);

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/resource/update")
                .param("recipient", recipient)
                .param("resourceId", resourceId.toString()))
                .andReturn();

        /* ASSERT */
        assertNotNull(result.getResponse());
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    @Test
    @SneakyThrows
    @WithMockUser("ADMIN")
    public void sendMessage_protocolIdscp_returnResponseEntityFromErrorRoute() {
        /* ARRANGE */
        final var errorMessage = "Error message.";
        final var response = new ResponseEntity<Object>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.doReturn(token).when(connectorService).getCurrentDat();

        when(producerTemplate.send(anyString(), any(Exchange.class))).thenReturn(exchange);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(ResponseEntity.class)).thenReturn(response);
        when(connectorConfig.isIdscpEnabled()).thenReturn(true);

        /* ACT */
        final var mvcResult = mockMvc.perform(post("/api/ids/resource/update")
                .param("recipient", recipient)
                .param("resourceId", resourceId.toString()))
                .andReturn();

        /* ASSERT */
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), mvcResult.getResponse().getStatus());
        assertEquals(errorMessage, mvcResult.getResponse().getContentAsString());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

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

    private Resource getResource() {
        return new ResourceBuilder(URI.create(resourceId.toString())).build();
    }
}
