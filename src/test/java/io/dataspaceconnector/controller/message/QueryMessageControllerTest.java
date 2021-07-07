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

import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.ids.messaging.core.daps.ClaimsException;
import de.fraunhofer.ids.messaging.core.daps.DapsTokenManagerException;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartParseException;
import io.dataspaceconnector.service.ids.ConnectorService;
import io.dataspaceconnector.service.message.GlobalMessageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class QueryMessageControllerTest {

    @MockBean
    private GlobalMessageService messageService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConnectorService connectorService;

    private final String brokerUrl = "https://someBroker";
    private final DynamicAttributeToken token = new DynamicAttributeTokenBuilder()
            ._tokenValue_("token")
            ._tokenFormat_(TokenFormat.JWT)
            .build();

    @Test
    public void sendQueryMessage_unauthorized_rejectUnauthorized() throws Exception {
        mockMvc.perform(post("/api/ids/query")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendQueryMessage_validInput_returnQueryResponse() throws Exception {
        /* ARRANGE */
        final var response = Optional.of("Some query result.");

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(response).when(messageService).sendQueryMessage(Mockito.any(),
                Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", brokerUrl).content("SOME QUERY"))
                .andExpect(status().isOk()).andReturn();

        /* ASSERT */
        assertEquals("Some query result.", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendQueryMessage_mockConnectionTimeout_returnBadGateway() throws Exception {
        /* ARRANGE */
        final var response = Optional.empty();

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(response).when(messageService).sendQueryMessage(Mockito.any(),
                Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", brokerUrl).content("SOME QUERY")).andReturn();

        /* ASSERT */
        assertEquals(502, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendQueryMessage_throwIOException_returnIdsMessageFailed() throws Exception {
        /* ARRANGE */
        Mockito.doThrow(IOException.class).when(messageService).sendQueryMessage(Mockito.any(),
                Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", brokerUrl).content("SOME QUERY")).andReturn();

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
                .param("recipient", brokerUrl).content("SOME QUERY")).andReturn();

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
                .param("recipient", brokerUrl).content("SOME QUERY")).andReturn();

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
                .param("recipient", brokerUrl).content("SOME QUERY")).andReturn();

        /* ASSERT */
        assertEquals(500, result.getResponse().getStatus());
        assertEquals("Failed to read the ids response message.",
                result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendQueryMessage_throwSocketTimeoutException_returnConnectionTimedOut() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doThrow(SocketTimeoutException.class).when(messageService).sendQueryMessage(Mockito.any(), Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", brokerUrl).content("SOME QUERY")).andReturn();

        /* ASSERT */
        assertEquals(504, result.getResponse().getStatus());
    }

    @Test
    public void sendSearchMessage_unauthorized_rejectUnauthorized() throws Exception {
        mockMvc.perform(post("/api/ids/search")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendSearchMessage_validSearchTerm_returnResponse() throws Exception {
        /* ARRANGE */
        final var response = Optional.of("Some search result.");

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(response).when(messageService).sendFullTextSearchQueryMessage(
                Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/search")
                .param("recipient", brokerUrl).param("limit", "50")
                .param("offset", "0").content("SOME SEARCH TERM")).andReturn();

        /* ASSERT */
        assertEquals("Some search result.", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendSearchMessage_validSearchTerm_returnBadGateway() throws Exception {
        /* ARRANGE */
        final var response = Optional.empty();

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(response).when(messageService).sendFullTextSearchQueryMessage(
                Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/search")
                .param("recipient", brokerUrl)
                .content("SOME SEARCH TERM")).andReturn();

        /* ASSERT */
        assertEquals(502, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendSearchMessage_throwIOException_returnIdsMessageFailed() throws Exception {
        /* ARRANGE */
        Mockito.doThrow(IOException.class).when(messageService).sendFullTextSearchQueryMessage(
                Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/search")
                .param("recipient", brokerUrl).content("SOME SEARCH TERM")).andReturn();

        /* ASSERT */
        assertEquals(500, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendSearchMessage_throwMultipartParseException_returnReceivedInvalidResponse() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doThrow(MultipartParseException.class).when(messageService).sendFullTextSearchQueryMessage(
                Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/search")
                .param("recipient", brokerUrl).content("SOME SEARCH TERM")).andReturn();

        /* ASSERT */
        assertEquals(500, result.getResponse().getStatus());
        assertEquals("Failed to read the ids response message.",
                result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendSearchMessage_throwSocketTimeoutException_returnConnectionTimedOut() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doThrow(SocketTimeoutException.class).when(messageService).sendFullTextSearchQueryMessage(
                Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/search")
                .param("recipient", brokerUrl).content("SOME SEARCH TERM")).andReturn();

        /* ASSERT */
        assertEquals(504, result.getResponse().getStatus());
    }
}
