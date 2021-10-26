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
package io.dataspaceconnector.controller.resource.type;

import java.net.URI;

import de.fraunhofer.ids.messaging.core.config.ConfigContainer;
import io.dataspaceconnector.common.runtime.ServiceResolver;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc(addFilters = false)
public class ConfigurationControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ServiceResolver svcResolver;

    @Test
    @WithMockUser("ADMIN")
    public void create_validInput_returnNew() throws Exception {
        mockMvc.perform(post("/api/configurations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
               .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("ADMIN")
     public void getAll_validInput_returnObj() throws Exception {
        for(int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/configurations")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{}"))
                   .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/configurations")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("ADMIN")
     public void get_validInput_returnObj() throws Exception {
        final var newObject =
        mockMvc.perform(post("/api/configurations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
               .andExpect(status().isCreated()).andReturn();

        final var newObj = newObject.getResponse().getHeader("Location");

        mockMvc.perform(get(URI.create(newObj).getPath())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("ADMIN")
     public void update_validInput_returnAcknowledge() throws Exception {
        final var newObject =
                mockMvc.perform(post("/api/configurations")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{}"))
                       .andExpect(status().isCreated()).andReturn();

        final var newObj = newObject.getResponse().getHeader("Location");

        mockMvc.perform(put(URI.create(newObj).getPath())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
               .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser("ADMIN")
     public void delete_validInput_returnAcknowledge() throws Exception {
        final var newObject =
                mockMvc.perform(post("/api/configurations")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content("{}"))
                       .andExpect(status().isCreated()).andReturn();

        final var newObj = newObject.getResponse().getHeader("Location");

        mockMvc.perform(delete(URI.create(newObj).getPath()))
               .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser("ADMIN")
     public void get_validInput_returnDSCRepresentation() throws Exception {
        final var newObject =
                mockMvc.perform(get("/api/configurations/active")
                                        .accept("application/hal+json"))
                       .andExpect(status().isOk()).andReturn();

        assertEquals(HttpStatus.OK.value(), newObject.getResponse().getStatus());
    }

    @Test
    @WithMockUser("ADMIN")
    @Transactional
    public void setConfiguration_invalidConfigInput_rollBack() throws Exception {
        //--1. INITIAL CONFIG--
        //get initial active config
        final var getActive =
                mockMvc.perform(get("/api/configurations/active"))
                        .andExpect(status().isOk()).andReturn();

        //get UUID of inital active config
        final var activeConfig = new JSONObject(getActive.getResponse().getContentAsString());
        final var links = new JSONObject(activeConfig.get("_links").toString());
        final var self = new JSONObject(links.get("self").toString());
        final var validUUID = self.get("href");

        //get KeyAlias of keyStore in Messaging-Service of initial config
        final var configContainer = svcResolver.getService(ConfigContainer.class);
        final var configBean = configContainer.get();
        final var keyStoreManager = configBean.getKeyStoreManager();
        final var keyAlias = keyStoreManager.getClass().getDeclaredField("keyAlias");
        keyAlias.setAccessible(true);
        final var keyAliasContent = keyAlias.get(keyStoreManager);

        //--2. ADD AND ACTIVATE INVALID CONFIG--
        //add invalid config to dsc db
        final var invalidConfig =
                mockMvc.perform(post("/api/configurations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                        .andExpect(status().isCreated()).andReturn();

        //activate invalid config - will cause rollback to old active config
        final var invalidConfigPath = invalidConfig.getResponse().getHeader("Location");
        final var invalidConfigURI = URI.create(invalidConfigPath).getPath() + "/active";
        mockMvc.perform(put(invalidConfigURI)).andExpect(status().isNoContent()).andReturn();

        //--3. GET ACTIVE CONFIG AFTER ROLLBACK--
        //get active config again after possible rollback
        final var getActive2 =
                mockMvc.perform(get("/api/configurations/active"))
                        .andExpect(status().isOk()).andReturn();

        //get UUID of now active config after rollback
        final var activeConfig2 = new JSONObject(getActive2.getResponse().getContentAsString());
        final var links2 = new JSONObject(activeConfig2.get("_links").toString());
        final var self2 = new JSONObject(links2.get("self").toString());
        final var validUUID2 = self2.get("href");

        //get KeyAlias of now active keyStore in Messaging-Service after rollback
        final var configContainer2 = svcResolver.getService(ConfigContainer.class);
        final var configBean2 = configContainer2.get();
        final var keyStoreManager2 = configBean2.getKeyStoreManager();
        final var keyAlias2 = keyStoreManager2.getClass().getDeclaredField("keyAlias");
        keyAlias2.setAccessible(true);
        final var keyAliasContent2 = keyAlias2.get(keyStoreManager2);

        //--4. TEST IF ROLLBACK ON ACTIVATING INVALID CONFIG DID THE JOB--
        //1. test dsc db holding old config as active again
        assertEquals(validUUID, validUUID2);
        //2. test messaging-services KeyStoreManager keystore alias same as before rollback
        assertEquals(keyAliasContent, keyAliasContent2);
    }
}
