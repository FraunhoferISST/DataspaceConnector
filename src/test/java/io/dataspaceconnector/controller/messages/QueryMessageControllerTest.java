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
import java.net.URI;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.ids.messaging.broker.IDSBrokerService;
import de.fraunhofer.ids.messaging.protocol.multipart.mapping.MessageProcessedNotificationMAP;
import de.fraunhofer.ids.messaging.protocol.multipart.mapping.ResultMAP;
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

import javax.xml.datatype.DatatypeFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class QueryMessageControllerTest {

    @MockBean
    private IDSBrokerService brokerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void sendConnectorUpdateMessage_unauthorized_rejectUnauthorized() throws Exception {
        mockMvc.perform(post("/api/ids/query")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_validQuery_returnQueryAnswer() throws Exception {
        /* ARRANGE */
        final var recipient = URI.create("https://someBroker").toString();
        final var query = "QUERY";

        ResultMessage resultMessage = new ResultMessageBuilder()._issuerConnector_(new URI("https://url"))._correlationMessage_(new URI("https://cormessage"))._issued_(DatatypeFactory.newInstance().newXMLGregorianCalendar("2009-05-07T17:05:45.678Z"))._senderAgent_(new URI("https://sender"))._modelVersion_("4.0.0")._securityToken_(new DynamicAttributeTokenBuilder()._tokenValue_("token")._tokenFormat_(TokenFormat.JWT).build()).build();

        final var response =
                new ResultMAP(resultMessage, "Some query result");

        Mockito.doReturn(response).when(brokerService)
               .queryBroker(Mockito.any(),
                            Mockito.any(),
                            Mockito.any(),
                            Mockito.any(),
                            Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                                                   .param("recipient", recipient)
                                                   .content("SOME QUERY"))
                                  .andExpect(status().isOk()).andReturn();

        /* ASSERT */
        assertEquals("Success", result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_someProblem_returnIdsMessageFailed() throws Exception {
        /* ARRANGE */
        final var recipient = URI.create("https://someBroker").toString();

        Mockito.doThrow(IOException.class).when(brokerService)
               .queryBroker(Mockito.any(),
                            Mockito.any(),
                            Mockito.any(),
                            Mockito.any(),
                            Mockito.any());

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/query")
                                                   .param("recipient", recipient)
                                                   .content("SOME QUERY"))
                                  .andExpect(status().isInternalServerError()).andReturn();

        /* ASSERT */
        assertEquals("Ids message handling failed. null",
                     result.getResponse().getContentAsString());
    }
}
