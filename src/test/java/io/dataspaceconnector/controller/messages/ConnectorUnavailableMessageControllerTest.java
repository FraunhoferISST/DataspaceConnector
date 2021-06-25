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

import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessage;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.ids.messaging.broker.IDSBrokerService;
import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import de.fraunhofer.ids.messaging.protocol.multipart.mapping.MessageProcessedNotificationMAP;
import io.dataspaceconnector.bootstrap.BootstrapConfiguration;
import io.dataspaceconnector.services.ids.ConnectorService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.xml.datatype.DatatypeFactory;
import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ConnectorUnavailableMessageControllerTest {

    @MockBean
    private IDSBrokerService brokerService;

    @MockBean
    private ConnectorService connectorService;

    @MockBean
    private BootstrapConfiguration bootstrapConfiguration;

    @Autowired
    private MockMvc mockMvc;

    private final String recipient = "https://someURL";

    @Test
    public void sendConnectorUpdateMessage_unauthorized_returnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/ids/connector/unavailable")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_noRecipient_throws400()
            throws Exception {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/connector/unavailable")).andExpect(status().isBadRequest()).andReturn();

        /* ASSERT */
        assertTrue(result.getResponse().getContentAsString().isEmpty());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendConnectorUpdateMessage_failUpdateConfigModel_throws500()
            throws Exception {
        /* ARRANGE */
        Mockito.doThrow(ConfigUpdateException.class).when(connectorService).updateConfigModel();

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/connector/unavailable")
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
        Mockito.doThrow(IOException.class).when(brokerService).unregisterAtBroker(Mockito.eq(URI.create(recipient)));

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/connector/unavailable")
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

        Mockito.doThrow(IOException.class).when(brokerService).unregisterAtBroker(Mockito.eq(URI.create(recipient)));

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/connector/unavailable")
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

        MessageProcessedNotificationMessage message = new MessageProcessedNotificationMessageBuilder()._issuerConnector_(new URI("https://url"))._correlationMessage_(new URI("https://cormessage"))._issued_(DatatypeFactory.newInstance().newXMLGregorianCalendar("2009-05-07T17:05:45.678Z"))._senderAgent_(new URI("https://sender"))._modelVersion_("4.0.0")._securityToken_(new DynamicAttributeTokenBuilder()._tokenValue_("token")._tokenFormat_(TokenFormat.JWT).build()).build();

        final var response =
                new MessageProcessedNotificationMAP(message);

        Mockito.doReturn(response).when(brokerService).unregisterAtBroker(Mockito.eq(URI.create(recipient)));

        /* ACT */
        final var result = mockMvc.perform(post("/api/ids/connector/unavailable")
                                                   .param("recipient", "https://someURL"))
                                  .andExpect(status().isOk()).andReturn();

        /* ASSERT */
        assertEquals("", result.getResponse().getContentAsString());
    }
}
