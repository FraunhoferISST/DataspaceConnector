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
package io.dataspaceconnector.controller;

import de.fraunhofer.iais.eis.ConfigurationModelBuilder;
import de.fraunhofer.iais.eis.ConnectorDeployMode;
import de.fraunhofer.iais.eis.ConnectorStatus;
import de.fraunhofer.iais.eis.LogLevel;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import io.dataspaceconnector.config.ConnectorConfiguration;
import io.dataspaceconnector.services.ids.DeserializationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class ConfigurationControllerTest {

    @MockBean
    private ConfigurationContainer configContainer;

    @MockBean
    private ConnectorConfiguration connectorConfig;

    @MockBean
    private DeserializationService idsService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser("ADMIN")
    public void updateConfiguration_validJson_consumesJson() throws Exception {
        /* ARRANGE */
        final var model = new ConfigurationModelBuilder()
                ._connectorDeployMode_(ConnectorDeployMode.TEST_DEPLOYMENT)
                ._configurationModelLogLevel_(LogLevel.MINIMAL_LOGGING)
                ._connectorStatus_(ConnectorStatus.CONNECTOR_OFFLINE)
                .build();

        Mockito.when(idsService.getConfigurationModel(Mockito.eq(model.toRdf()))).thenReturn(model);

        /* ACT && ASSERT */
        mockMvc.perform(put("/api/configuration")
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                   .content(model.toRdf()))
                                  .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("ADMIN")
    public void updateConfiguration_validJsonLd_consumesJsonLd() throws Exception {
        /* ARRANGE */
        final var model = new ConfigurationModelBuilder()
                ._connectorDeployMode_(ConnectorDeployMode.TEST_DEPLOYMENT)
                ._configurationModelLogLevel_(LogLevel.MINIMAL_LOGGING)
                ._connectorStatus_(ConnectorStatus.CONNECTOR_OFFLINE)
                .build();

        Mockito.when(idsService.getConfigurationModel(Mockito.eq(model.toRdf()))).thenReturn(model);

        /* ACT && ASSERT */
        mockMvc.perform(put("/api/configuration")
                                .contentType("application/ld+json")
                                .content(model.toRdf()))
               .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("ADMIN")
    public void updateConfiguration_validJson_producesJsonLd() throws Exception {
        /* ARRANGE */
        final var model = new ConfigurationModelBuilder()
                ._connectorDeployMode_(ConnectorDeployMode.TEST_DEPLOYMENT)
                ._configurationModelLogLevel_(LogLevel.MINIMAL_LOGGING)
                ._connectorStatus_(ConnectorStatus.CONNECTOR_OFFLINE)
                .build();

        Mockito.when(idsService.getConfigurationModel(Mockito.eq(model.toRdf()))).thenReturn(model);


        /* ACT && ASSERT */
        final var result = mockMvc.perform(put("/api/configuration")
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(model.toRdf()))
                                  .andExpect(status().isOk()).andReturn();

        assertEquals("application/ld+json", result.getResponse().getContentType());
    }


    @Test
    @WithMockUser("ADMIN")
    public void updateConfiguration_invalidMediaType_return415() throws Exception {
        /* ARRANGE */
        final var model = new ConfigurationModelBuilder()
                ._connectorDeployMode_(ConnectorDeployMode.TEST_DEPLOYMENT)
                ._configurationModelLogLevel_(LogLevel.MINIMAL_LOGGING)
                ._connectorStatus_(ConnectorStatus.CONNECTOR_OFFLINE)
                .build();

        Mockito.when(idsService.getConfigurationModel(Mockito.eq(model.toRdf()))).thenReturn(model);


        /* ACT && ASSERT */
        final var result = mockMvc.perform(put("/api/configuration")
                                                   .contentType(MediaType.APPLICATION_ATOM_XML)
                                                   .content(model.toRdf()))
                                  .andExpect(status().is4xxClientError()).andReturn();

        assertEquals(415, result.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    public void updateConfiguration_validJson_200AndReturnsNewConfig() throws Exception {
        /* ARRANGE */
        final var model = new ConfigurationModelBuilder()
                ._connectorDeployMode_(ConnectorDeployMode.TEST_DEPLOYMENT)
                ._configurationModelLogLevel_(LogLevel.MINIMAL_LOGGING)
                ._connectorStatus_(ConnectorStatus.CONNECTOR_OFFLINE)
                .build();

        Mockito.when(idsService.getConfigurationModel(Mockito.eq(model.toRdf()))).thenReturn(model);


        /* ACT && ASSERT */
        final var result = mockMvc.perform(put("/api/configuration")
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(model.toRdf()))
                                  .andExpect(status().isOk()).andReturn();

        Mockito.verify(configContainer, Mockito.atLeastOnce()).updateConfiguration(Mockito.any());

        assertEquals(model.toRdf(), result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser("ADMIN")
    public void updateConfiguration_invalidJson_return400() throws Exception {
        /* ARRANGE */
        final var model = new ConfigurationModelBuilder()
                ._connectorDeployMode_(ConnectorDeployMode.TEST_DEPLOYMENT)
                ._configurationModelLogLevel_(LogLevel.MINIMAL_LOGGING)
                ._connectorStatus_(ConnectorStatus.CONNECTOR_OFFLINE)
                .build();

        Mockito.when(idsService.getConfigurationModel(Mockito.eq(model.toRdf()))).thenThrow(IllegalArgumentException.class);


        /* ACT && ASSERT */
        mockMvc.perform(put("/api/configuration")
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .content(model.toRdf()))
                                  .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser("ADMIN")
    public void updateConfiguration_validJsonFailsUpdate_return500() throws Exception {
        /* ARRANGE */
        final var model = new ConfigurationModelBuilder()
                ._connectorDeployMode_(ConnectorDeployMode.TEST_DEPLOYMENT)
                ._configurationModelLogLevel_(LogLevel.MINIMAL_LOGGING)
                ._connectorStatus_(ConnectorStatus.CONNECTOR_OFFLINE)
                .build();

        Mockito.when(idsService.getConfigurationModel(Mockito.eq(model.toRdf()))).thenReturn(model);
        Mockito.doThrow(ConfigurationUpdateException.class).when(configContainer).updateConfiguration(Mockito.eq(model));

        /* ACT && ASSERT */
        mockMvc.perform(put("/api/configuration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(model.toRdf()))
               .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateConfiguration_unauthorized_return500() throws Exception {
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
}
