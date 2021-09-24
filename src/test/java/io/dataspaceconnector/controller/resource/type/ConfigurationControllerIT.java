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
@AutoConfigureMockMvc(addFilters = false)
public class ConfigurationControllerIT {

    @Autowired
    MockMvc mockMvc;

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
        //Get initial active config
        final var getActive =
                mockMvc.perform(get("/api/configurations/active"))
                        .andExpect(status().isOk()).andReturn();

        //Get UUID of inital active config
        final var activeConfig = new JSONObject(getActive.getResponse().getContentAsString());
        final var links = new JSONObject(activeConfig.get("_links").toString());
        final var self = new JSONObject(links.get("self").toString());
        final var validUUID = self.get("href");

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

        //get active config again after possible rollback
        final var getActive2 =
                mockMvc.perform(get("/api/configurations/active"))
                        .andExpect(status().isOk()).andReturn();

        //get UUID of now active config after rollback
        final var activeConfig2 = new JSONObject(getActive2.getResponse().getContentAsString());
        final var links2 = new JSONObject(activeConfig2.get("_links").toString());
        final var self2 = new JSONObject(links2.get("self").toString());
        final var validUUID2 = self2.get("href");

        //test rollback: test if old config still active after rollback
        assertEquals(validUUID, validUUID2);
    }
}
