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
package io.dataspaceconnector.controller.messages;

import io.dataspaceconnector.services.messages.GlobalMessageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.net.URI;
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

    @Test
    public void sendQueryMessage_unauthorized_rejectUnauthorized() throws Exception {
        mockMvc.perform(post("/api/ids/query")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendQueryMessage_validQuery_returnQueryAnswer() throws Exception {
        /* ARRANGE */
        final var recipient = URI.create("https://someBroker").toString();
        final var response = Optional.of("Some query result.");

        Mockito.doReturn(response).when(messageService).sendQueryMessage(Mockito.any(), Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", recipient).content("SOME QUERY"))
                .andExpect(status().isOk()).andReturn();

        /* ASSERT */
        assertEquals("Some query result.", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendQueryMessage_validQuery_returnBadGateway() throws Exception {
        /* ARRANGE */
        final var recipient = URI.create("https://someBroker").toString();
        final var response = Optional.empty();

        Mockito.doReturn(response).when(messageService).sendQueryMessage(Mockito.any(), Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", recipient)
                .content("SOME QUERY"))
                .andExpect(status().isBadGateway()).andReturn();

        /* ASSERT */
        assertEquals(502, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendQueryMessage_someProblem_returnIdsMessageFailed() throws Exception {
        /* ARRANGE */
        final var recipient = URI.create("https://someBroker").toString();

        Mockito.doThrow(IOException.class).when(messageService).sendQueryMessage(Mockito.any(), Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", recipient).content("SOME QUERY"))
                .andExpect(status().isInternalServerError()).andReturn();

        /* ASSERT */
        assertEquals("Ids message handling failed. null",
                result.getResponse().getContentAsString());
    }

    @Test
    public void sendSearchMessage_unauthorized_rejectUnauthorized() throws Exception {
        mockMvc.perform(post("/api/ids/search")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendSearchMessage_validSearchTerm_returnResponse() throws Exception {
        /* ARRANGE */
        final var recipient = URI.create("https://someBroker").toString();
        final var response = Optional.of("Some search result.");

        Mockito.doReturn(response).when(messageService).sendFullTextSearchQueryMessage(
                Mockito.any(), Mockito.any(), Mockito.anyInt(), Mockito.anyInt());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/search")
                .param("recipient", recipient).param("limit", "50")
                .param("offset", "0").content("SOME QUERY"))
                .andExpect(status().isOk()).andReturn();

        /* ASSERT */
        assertEquals("Some search result.", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendSearchMessage_validSearchTerm_returnBadGateway() throws Exception {
        /* ARRANGE */
        final var recipient = URI.create("https://someBroker").toString();
        final var response = Optional.empty();

        Mockito.doReturn(response).when(messageService).sendQueryMessage(Mockito.any(), Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                .param("recipient", recipient)
                .content("SOME QUERY"))
                .andExpect(status().isBadGateway()).andReturn();

        /* ASSERT */
        assertEquals(502, result.getResponse().getStatus());
    }
}
