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
package io.dataspaceconnector.controller.gui;

import org.apache.commons.codec.CharEncoding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the GuiUtilController class.
 */
@SpringBootTest
class GuiControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void unauthorizedGetEnum() throws Exception {
        mockMvc.perform(get("/api/utils/enum")).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void missingRequestBody() throws Exception {
        mockMvc.perform(post("/api/utils/enum")).andExpect(status().isInternalServerError()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void badRequestGetEnum() throws Exception {
        mockMvc.perform(post("/api/utils/enum")
                .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(CharEncoding.UTF_8)
                        .content("\" TEST \""))
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getSpecificEnum() throws Exception {
        mockMvc.perform(post("/api/utils/enum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(CharEncoding.UTF_8)
                        .content("\" LOG_LEVEL \""))
                .andExpect(status().isOk()).andReturn();
        mockMvc.perform(post("/api/utils/enum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(CharEncoding.UTF_8)
                        .content("\" CONNECTOR_STATUS \""))
                .andExpect(status().isOk()).andReturn();
        mockMvc.perform(post("/api/utils/enum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(CharEncoding.UTF_8)
                        .content("\" CONNECTOR_DEPLOY_MODE \""))
                .andExpect(status().isOk()).andReturn();
        mockMvc.perform(post("/api/utils/enum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(CharEncoding.UTF_8)
                        .content("\" LANGUAGE \""))
                .andExpect(status().isOk()).andReturn();
        mockMvc.perform(post("/api/utils/enum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(CharEncoding.UTF_8)
                        .content("\" DEPLOY_METHOD \""))
                .andExpect(status().isOk()).andReturn();
        mockMvc.perform(post("/api/utils/enum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(CharEncoding.UTF_8)
                        .content("\" BROKER_STATUS \""))
                .andExpect(status().isOk()).andReturn();
        mockMvc.perform(post("/api/utils/enum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(CharEncoding.UTF_8)
                        .content("\" SECURITY_PROFILE \""))
                .andExpect(status().isOk()).andReturn();
        mockMvc.perform(post("/api/utils/enum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(CharEncoding.UTF_8)
                        .content("\" PAYMENT_METHOD \""))
                .andExpect(status().isOk()).andReturn();
    }
}
