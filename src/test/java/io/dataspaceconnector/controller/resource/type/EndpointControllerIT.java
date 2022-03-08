/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class EndpointControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser("ADMIN")
    void create_validInput_returnNew() throws Exception {
        mockMvc.perform(post("/api/endpoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\": \"GENERIC\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser("ADMIN")
    void create_appEndpoint_notAllowed() throws Exception {
        mockMvc.perform(post("/api/endpoints")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\": \"APP\"}"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithMockUser("ADMIN")
    void getAll_validInput_returnObj() throws Exception {
        for(int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/endpoints")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"type\": \"GENERIC\"}"))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/endpoints")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("ADMIN")
    void get_validInput_returnObj() throws Exception {
        final var newObject =
                mockMvc.perform(post("/api/endpoints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"GENERIC\"}"))
                        .andExpect(status().isCreated()).andReturn();

        final var newObj = newObject.getResponse().getHeader("Location");

        mockMvc.perform(get(URI.create(newObj).getPath())).andExpect(status().isOk());
    }

    @Test
    @WithMockUser("ADMIN")
    void update_validInput_returnAcknowledge() throws Exception {
        final var newObject =
                mockMvc.perform(post("/api/endpoints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"GENERIC\"}"))
                        .andExpect(status().isCreated()).andReturn();

        final var newObj = newObject.getResponse().getHeader("Location");

        mockMvc.perform(put(URI.create(newObj).getPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"type\": \"GENERIC\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser("ADMIN")
    void delete_validInput_returnAcknowledge() throws Exception {
        final var newObject =
                mockMvc.perform(post("/api/endpoints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"GENERIC\"}"))
                        .andExpect(status().isCreated()).andReturn();

        final var newObj = newObject.getResponse().getHeader("Location");

        mockMvc.perform(delete(URI.create(newObj).getPath()))
                .andExpect(status().is(204));
    }
}
