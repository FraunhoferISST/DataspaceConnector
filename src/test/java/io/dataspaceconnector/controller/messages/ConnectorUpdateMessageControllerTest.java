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

import java.io.IOException;

import de.fraunhofer.isst.ids.framework.communication.broker.IDSBrokerService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import io.dataspaceconnector.services.ids.ConnectorService;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ConnectorUpdateMessageControllerTest {

    @MockBean
    private IDSBrokerService brokerService;

    @MockBean
    private ConnectorService connectorService;

    @Autowired
    private MockMvc mockMvc;

    private final String recipient = "https://someURL";

    @Test
    public void sendConnectorUpdateMessage_unauthorized_returnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/ids/connector/update")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_noRecipient_throws400()
            throws Exception {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/connector/update")).andExpect(status().isBadRequest()).andReturn();

        /* ASSERT */
        assertTrue(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_failUpdateConfigModel_throws500()
            throws Exception {
        /* ARRANGE */
        Mockito.doThrow(ConfigurationUpdateException.class).when(connectorService).updateConfigModel();

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/connector/update")
                                                   .param("recipient", recipient))
                                  .andExpect(status().isInternalServerError()).andReturn();

        /* ASSERT */
        assertEquals("Failed to update configuration.", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_failUpdateAtBroker_throws500()
            throws Exception {
        /* ARRANGE */
        Mockito.doThrow(IOException.class).when(brokerService).updateSelfDescriptionAtBroker(Mockito.eq(recipient));

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/connector/update")
                                                   .param("recipient", recipient))
                                  .andExpect(status().isInternalServerError()).andReturn();

        /* ASSERT */
        assertEquals("Ids message handling failed. null", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_brokerEmptyResponseBody_throws500()
            throws Exception {
        /* ARRANGE */
        final var response =
                new Response.Builder().request(new Request.Builder().url(recipient).build())
                                      .protocol(Protocol.HTTP_1_1).code(200).message("").build();
        Mockito.doReturn(response).when(brokerService).updateSelfDescriptionAtBroker(Mockito.eq(recipient));

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/connector/update")
                                                   .param("recipient", recipient))
                                  .andExpect(status().isInternalServerError()).andReturn();

        /* ASSERT */
        assertEquals("Ids message handling failed. null", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_validRequest_returnsBrokerResponse()
            throws Exception {
        /* ARRANGE */
        final var response =
                new Response.Builder().request(new Request.Builder().url(recipient).build())
                                      .protocol(
                                              Protocol.HTTP_1_1).code(200).message("")
                                      .body(ResponseBody.create("ANSWER", MediaType
                                              .parse("application/text")))
                                      .build();

        Mockito.doReturn(response).when(brokerService).updateSelfDescriptionAtBroker(Mockito.eq(recipient));

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/connector/update")
                                                   .param("recipient", recipient))
                                  .andExpect(status().isOk()).andReturn();

        /* ASSERT */
        assertEquals("ANSWER", result.getResponse().getContentAsString());
    }
}
