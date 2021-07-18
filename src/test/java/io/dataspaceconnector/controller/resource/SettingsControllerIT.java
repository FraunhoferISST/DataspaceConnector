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
package io.dataspaceconnector.controller.resource;

import de.fraunhofer.iais.eis.ConfigurationModelBuilder;
import de.fraunhofer.iais.eis.ConnectorDeployMode;
import de.fraunhofer.iais.eis.ConnectorStatus;
import de.fraunhofer.iais.eis.LogLevel;
import de.fraunhofer.ids.messaging.core.config.ConfigContainer;
import io.dataspaceconnector.config.ConnectorConfiguration;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class SettingsControllerIT {

    @SpyBean
    private ConfigContainer configContainer;

    @SpyBean
    private ConnectorConfiguration connectorConfig;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void updateConfiguration_unauthorized_return401() throws Exception {
        /* ARRANGE */
        final var model = new ConfigurationModelBuilder()
                ._connectorDeployMode_(ConnectorDeployMode.TEST_DEPLOYMENT)
                ._configurationModelLogLevel_(LogLevel.MINIMAL_LOGGING)
                ._connectorStatus_(ConnectorStatus.CONNECTOR_OFFLINE)
                .build();

        /* ACT && ASSERT */
        mockMvc.perform(put("/api/configuration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(model.toRdf()))
                .andExpect(status().isUnauthorized());

        Mockito.verify(configContainer, Mockito.never()).updateConfiguration(Mockito.any());
    }

    /**
     * getConfiguration
     */

    @Test
    public void getConfiguration_unauthorized_return401() throws Exception {
        /* ACT && ASSERT */
        mockMvc.perform(get("/api/configuration"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("ADMIN")
    public void getConfiguration_noConfig_return404() throws Exception {
        /* ARRANGE */
        Mockito.doReturn(null).when(configContainer).getConfigurationModel();

        /* ACT && ASSERT */
        mockMvc.perform(get("/api/configuration"))
                .andExpect(status().isNotFound());
    }

    /**
     * setNegotiationStatus
     */

    @Test
    public void setNegotiationStatus_unauthorized_return401() throws Exception {
        /* ACT && ASSERT */
        mockMvc.perform(put("/api/configuration/negotiation")
                .param("status", "true"))
                .andExpect(status().isUnauthorized());
    }

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
    public void getNegotiationStatus_unauthorized_return401() throws Exception {
        /* ACT && ASSERT */
        mockMvc.perform(get("/api/configuration/negotiation"))
                .andExpect(status().isUnauthorized());
    }

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
    public void setPatternStatus_unauthorized_return401() throws Exception {
        /* ACT && ASSERT */
        mockMvc.perform(put("/api/configuration/pattern")
                .param("status", "true"))
                .andExpect(status().isUnauthorized());
    }

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
    public void getPatternStatus_unauthorized_return401() throws Exception {
        /* ACT && ASSERT */
        mockMvc.perform(get("/api/configuration/pattern"))
                .andExpect(status().isUnauthorized());
    }

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
