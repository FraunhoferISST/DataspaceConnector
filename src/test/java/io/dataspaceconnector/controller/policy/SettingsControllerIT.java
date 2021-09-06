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
package io.dataspaceconnector.controller.policy;

import de.fraunhofer.ids.messaging.core.config.ConfigContainer;
import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.extension.idscp.config.Idscp2Config;
import io.dataspaceconnector.common.ids.DeserializationService;
import net.minidev.json.JSONObject;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class SettingsControllerIT {

    @MockBean
    private Idscp2Config idscp2Config;

    @MockBean
    private SpringTransactionPolicy transactionPolicy;

    @SpyBean
    private ConfigContainer configContainer;

    @SpyBean
    private ConnectorConfig connectorConfig;

    @SpyBean
    private DeserializationService idsService;

    @Autowired
    private MockMvc mockMvc;

    /**
     * setNegotiationStatus
     */

    @Test
    @WithMockUser("ADMIN")
    public void setNegotiationStatus_noStatusParam_return400() throws Exception {
        /* ACT && ASSERT */
        final var result = mockMvc.perform(put("/api/configuration/negotiation"))
                .andExpect(status().is4xxClientError()).andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void setNegotiationStatus_true_returnTrue() throws Exception {
        final var body = new JSONObject();
        body.put("status", true);

        Mockito.doReturn(true).when(connectorConfig).isPolicyNegotiation();

        /* ACT && ASSERT */
        final var result = mockMvc.perform(put("/api/configuration/negotiation")
                .param("status", "true"))
                .andExpect(status().isOk()).andReturn();

        Mockito.verify(connectorConfig, Mockito.atLeastOnce()).setPolicyNegotiation(Mockito.eq(true));

        assertEquals(body.toJSONString(), result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void setNegotiationStatus_false_returnFalse() throws Exception {
        final var body = new JSONObject();
        body.put("status", false);

        Mockito.doReturn(false).when(connectorConfig).isPolicyNegotiation();

        /* ACT && ASSERT */
        final var result = mockMvc.perform(put("/api/configuration/negotiation")
                .param("status", "false"))
                .andExpect(status().isOk()).andReturn();

        Mockito.verify(connectorConfig, Mockito.atLeastOnce()).setPolicyNegotiation(Mockito.eq(false));

        assertEquals(body.toJSONString(), result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void setNegotiationStatus_any_returnJson() throws Exception {
        /* ACT && ASSERT */
        final var result = mockMvc.perform(put("/api/configuration/negotiation")
                .param("status", "false"))
                .andExpect(status().isOk()).andReturn();

        assertEquals("application/json", result.getResponse().getContentType());
    }


    /**
     * getNegotiationStatus
     */

    @Test
    @WithMockUser("ADMIN")
    public void getNegotiationStatus_isTrue_returnTrue() throws Exception {
        final var body = new JSONObject();
        body.put("status", true);

        Mockito.doReturn(true).when(connectorConfig).isPolicyNegotiation();

        /* ACT && ASSERT */
        final var result = mockMvc.perform(get("/api/configuration/negotiation"))
                .andExpect(status().isOk()).andReturn();

        assertEquals(body.toJSONString(), result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void getNegotiationStatus_isFalse_returnFalse() throws Exception {
        final var body = new JSONObject();
        body.put("status", false);

        Mockito.doReturn(false).when(connectorConfig).isPolicyNegotiation();

        /* ACT && ASSERT */
        final var result = mockMvc.perform(get("/api/configuration/negotiation"))
                .andExpect(status().isOk()).andReturn();

        assertEquals(body.toJSONString(), result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void getNegotiationStatus_any_returnJson() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(false).when(connectorConfig).isPolicyNegotiation();

        /* ACT && ASSERT */
        final var result = mockMvc.perform(get("/api/configuration/negotiation"))
                .andExpect(status().isOk()).andReturn();

        assertEquals("application/json", result.getResponse().getContentType());
    }

    /**
     * setPatternStatus
     */

    @Test
    @WithMockUser("ADMIN")
    public void setPatternStatus_noStatusParam_return401() throws Exception {
        /* ACT && ASSERT */
        final var result = mockMvc.perform(put("/api/configuration/pattern"))
                .andExpect(status().is4xxClientError()).andReturn();

        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void setPatternStatus_true_returnTrue() throws Exception {
        final var body = new JSONObject();
        body.put("status", true);

        Mockito.doReturn(true).when(connectorConfig).isAllowUnsupported();

        /* ACT && ASSERT */
        final var result = mockMvc.perform(put("/api/configuration/pattern")
                .param("status", "true"))
                .andExpect(status().isOk()).andReturn();

        Mockito.verify(connectorConfig, Mockito.atLeastOnce()).setAllowUnsupported(Mockito.eq(true));

        assertEquals(body.toJSONString(), result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void setPatternStatus_false_returnFalse() throws Exception {
        final var body = new JSONObject();
        body.put("status", false);

        Mockito.doReturn(false).when(connectorConfig).isAllowUnsupported();

        /* ACT && ASSERT */
        final var result = mockMvc.perform(put("/api/configuration/pattern")
                .param("status", "false"))
                .andExpect(status().isOk()).andReturn();

        Mockito.verify(connectorConfig, Mockito.atLeastOnce()).setAllowUnsupported(Mockito.eq(false));

        assertEquals(body.toJSONString(), result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void setPatternStatus_any_returnJson() throws Exception {
        /* ACT && ASSERT */
        final var result = mockMvc.perform(put("/api/configuration/pattern")
                .param("status", "false"))
                .andExpect(status().isOk()).andReturn();

        assertEquals("application/json", result.getResponse().getContentType());
    }

    /**
     * getPatternStatus
     */

    @Test
    @WithMockUser("ADMIN")
    public void getPatternStatus_isTrue_returnTrue() throws Exception {
        final var body = new JSONObject();
        body.put("status", true);

        Mockito.doReturn(true).when(connectorConfig).isAllowUnsupported();

        /* ACT && ASSERT */
        final var result = mockMvc.perform(get("/api/configuration/pattern"))
                .andExpect(status().isOk()).andReturn();

        assertEquals(body.toJSONString(), result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void getPatternStatus_isFalse_returnFalse() throws Exception {
        final var body = new JSONObject();
        body.put("status", false);

        Mockito.doReturn(false).when(connectorConfig).isAllowUnsupported();

        /* ACT && ASSERT */
        final var result = mockMvc.perform(get("/api/configuration/negotiation"))
                .andExpect(status().isOk()).andReturn();

        assertEquals(body.toJSONString(), result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void getPatternStatus_any_returnJson() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(false).when(connectorConfig).isAllowUnsupported();

        /* ACT && ASSERT */
        final var result = mockMvc.perform(get("/api/configuration/negotiation"))
                .andExpect(status().isOk()).andReturn();

        assertEquals("application/json", result.getResponse().getContentType());
    }
}
