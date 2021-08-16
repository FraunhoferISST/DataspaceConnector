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
import de.fraunhofer.iais.eis.ResultMessageImpl;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.DapsTokenManagerException;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartParseException;
import de.fraunhofer.ids.messaging.requests.MessageContainer;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.service.message.GlobalMessageService;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class QueryMessageControllerTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @MockBean
    private GlobalMessageService messageService;

    @MockBean
    private ProducerTemplate producerTemplate;

    @SpyBean
    private ConnectorConfig connectorConfig;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConnectorService connectorService;

    private final String brokerUrl = "https://someBroker";
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
    public void sendQueryMessage_validInput_returnQueryResponse() throws Exception {
        /* ARRANGE */
        final var payload = "Some query result.";
        final var response = new ResponseEntity<>(payload, HttpStatus.OK);
        final var container = getResponse(payload);
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(container).when(messageService)
                .sendQueryMessage(Mockito.any(), Mockito.any());
        Mockito.doReturn(response).when(messageService).validateResponse(container,
                ResultMessageImpl.class);

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", brokerUrl)
                .param("protocol", "MULTIPART")
                .content("SOME QUERY"))
                .andReturn();

        /* ASSERT */
        assertEquals(payload, result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendQueryMessage_throwIOException_returnIdsMessageFailed() throws Exception {
        /* ARRANGE */
        Mockito.doThrow(IOException.class).when(messageService).sendQueryMessage(Mockito.any(),
                Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", brokerUrl)
                .param("protocol", "MULTIPART")
                .content("SOME QUERY"))
                .andReturn();

        /* ASSERT */
        assertEquals(500, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendQueryMessage_throwDapsTokenManagerException_returnIdsMessageFailed() throws Exception {
        /* ARRANGE */
        Mockito.doThrow(DapsTokenManagerException.class).when(messageService).sendQueryMessage(Mockito.any(),
                Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", brokerUrl)
                .param("protocol", "MULTIPART")
                .content("SOME QUERY"))
                .andReturn();

        /* ASSERT */
        assertEquals(500, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendQueryMessage_throwClaimsException_returnIdsMessageFailed() throws Exception {
        /* ARRANGE */
        Mockito.doThrow(ClaimsException.class).when(messageService).sendQueryMessage(Mockito.any(),
                Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", brokerUrl)
                .param("protocol", "MULTIPART")
                .content("SOME QUERY"))
                .andReturn();

        /* ASSERT */
        assertEquals(500, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendQueryMessage_throwMultipartParseException_returnReceivedInvalidResponse() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doThrow(MultipartParseException.class).when(messageService).sendQueryMessage(Mockito.any(), Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", brokerUrl)
                .param("protocol", "MULTIPART")
                .content("SOME QUERY"))
                .andReturn();

        /* ASSERT */
        assertEquals(502, result.getResponse().getStatus());
        final var msg = ErrorMessage.INVALID_MESSAGE.toString();
        assertEquals(msg, result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendQueryMessage_throwSocketTimeoutException_returnConnectionTimedOut() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doThrow(SocketTimeoutException.class).when(messageService).sendQueryMessage(Mockito.any(), Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", brokerUrl)
                .param("protocol", "MULTIPART")
                .content("SOME QUERY"))
                .andReturn();

        /* ASSERT */
        assertEquals(504, result.getResponse().getStatus());
    }

    @Test
    @SneakyThrows
    @WithMockUser("ADMIN")
    public void sendQueryMessage_protocolIdscp_parseResponseFromRoute() {
        /* ARRANGE */
        final var queryResult = "query result";
        final var response = new Response(getMessage(), queryResult);

        Mockito.doReturn(token).when(connectorService).getCurrentDat();

        when(producerTemplate.send(anyString(), any(Exchange.class))).thenReturn(exchange);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(Response.class)).thenReturn(response);
        when(connectorConfig.isIdscpEnabled()).thenReturn(true);

        /* ACT */
        final var mvcResult = mockMvc.perform(post("/api/ids/query")
                .param("recipient", brokerUrl)
                .content("SOME QUERY"))
                .andReturn();

        /* ASSERT */
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(queryResult, mvcResult.getResponse().getContentAsString());
    }

    @Test
    @SneakyThrows
    @WithMockUser("ADMIN")
    public void sendQueryMessage_protocolIdscp_returnResponseEntityFromErrorRoute() {
        /* ARRANGE */
        final var errorMessage = "Error message.";
        final var response = new ResponseEntity<Object>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.doReturn(token).when(connectorService).getCurrentDat();

        when(producerTemplate.send(anyString(), any(Exchange.class))).thenReturn(exchange);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(ResponseEntity.class)).thenReturn(response);
        when(connectorConfig.isIdscpEnabled()).thenReturn(true);

        /* ACT */
        final var mvcResult = mockMvc.perform(post("/api/ids/query")
                .param("recipient", brokerUrl)
                .content("SOME QUERY"))
                .andReturn();

        /* ASSERT */
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), mvcResult.getResponse().getStatus());
        assertEquals(errorMessage, mvcResult.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendSearchMessage_validSearchTerm_returnResponse() throws Exception {
        /* ARRANGE */
        final var payload = "Some search result.";
        final var response = new ResponseEntity<>(payload, HttpStatus.OK);
        final var container = getResponse(payload);
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(getResponse(payload)).when(messageService).sendFullTextSearchMessage(
                Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());
        Mockito.doReturn(response).when(messageService).validateResponse(container,
                ResultMessageImpl.class);

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/search")
                .param("recipient", brokerUrl)
                .param("limit", "50")
                .param("offset", "0")
                .param("protocol", "MULTIPART")
                .content("SOME SEARCH TERM"))
                .andReturn();

        /* ASSERT */
        assertNotNull(result);
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendSearchMessage_throwIOException_returnIdsMessageFailed() throws Exception {
        /* ARRANGE */
        Mockito.doThrow(IOException.class).when(messageService).sendFullTextSearchMessage(
                Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/search")
                .param("recipient", brokerUrl)
                .param("protocol", "MULTIPART")
                .content("SOME SEARCH TERM"))
                .andReturn();

        /* ASSERT */
        assertEquals(500, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendSearchMessage_throwMultipartParseException_returnReceivedInvalidResponse() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doThrow(MultipartParseException.class).when(messageService).sendFullTextSearchMessage(
                Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/search")
                .param("recipient", brokerUrl)
                .param("protocol", "MULTIPART")
                .content("SOME SEARCH TERM"))
                .andReturn();

        /* ASSERT */
        assertEquals(502, result.getResponse().getStatus());
        final var msg = ErrorMessage.INVALID_MESSAGE.toString();
        assertEquals(msg, result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendSearchMessage_throwSocketTimeoutException_returnConnectionTimedOut() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doThrow(SocketTimeoutException.class).when(messageService).sendFullTextSearchMessage(
                Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/search")
                .param("recipient", brokerUrl)
                .param("protocol", "MULTIPART")
                .content("SOME SEARCH TERM"))
                .andReturn();

        /* ASSERT */
        assertEquals(504, result.getResponse().getStatus());
    }

    @Test
    @SneakyThrows
    @WithMockUser("ADMIN")
    public void sendSearchMessage_protocolIdscp_parseResponseFromRoute() {
        /* ARRANGE */
        final var queryResult = "query result";
        final var response = new Response(getMessage(), queryResult);

        Mockito.doReturn(token).when(connectorService).getCurrentDat();

        when(producerTemplate.send(anyString(), any(Exchange.class))).thenReturn(exchange);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(Response.class)).thenReturn(response);
        when(connectorConfig.isIdscpEnabled()).thenReturn(true);

        /* ACT */
        final var mvcResult = mockMvc.perform(post("/api/ids/search")
                .param("recipient", brokerUrl)
                .content("SOME SEARCH TERM"))
                .andReturn();

        /* ASSERT */
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus());
        assertEquals(queryResult, mvcResult.getResponse().getContentAsString());
    }

    @Test
    @SneakyThrows
    @WithMockUser("ADMIN")
    public void sendSearchMessage_protocolIdscp_returnResponseEntityFromErrorRoute() {
        /* ARRANGE */
        final var errorMessage = "Error message.";
        final var response = new ResponseEntity<Object>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);

        Mockito.doReturn(token).when(connectorService).getCurrentDat();

        when(producerTemplate.send(anyString(), any(Exchange.class))).thenReturn(exchange);
        when(exchange.getIn()).thenReturn(in);
        when(in.getBody(ResponseEntity.class)).thenReturn(response);
        when(connectorConfig.isIdscpEnabled()).thenReturn(true);

        /* ACT */
        final var mvcResult = mockMvc.perform(post("/api/ids/search")
                .param("recipient", brokerUrl)
                .content("SOME SEARCH TERM"))
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

    @SneakyThrows
    private Optional<MessageContainer<?>> getResponse(final String result) {
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        final var connectorId = URI.create("https://connector");
        final var modelVersion = "4.0.0";
        final var token = new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();
        final var message = new MessageProcessedNotificationMessageBuilder()
                ._securityToken_(token)
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._correlationMessage_(URI.create("https://message"))
                ._senderAgent_(connectorId)
                ._issued_(xmlCalendar)
                .build();
        return Optional.of(new MessageContainer<>(message, result));
    }
}
