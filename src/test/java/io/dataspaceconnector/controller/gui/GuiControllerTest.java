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

import io.dataspaceconnector.controller.gui.util.GuiUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the GuiUtilController class.
 */
@SpringBootTest(classes = {GuiUtils.class, GuiController.class})
@AutoConfigureMockMvc
class GuiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthorizedGetEnum() throws Exception {
        mockMvc.perform(get("/api/configmanager/enum/loglevel")).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    @WithMockUser("ADMIN")
    void badRequestGetEnum() throws Exception {
        mockMvc.perform(get("/api/configmanager/enum/null")).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    @WithMockUser("ADMIN")
    void getSpecificEnum() throws Exception {
        mockMvc.perform(
                get("/api/configmanager/enum/loglevel"))
                .andExpect(status().isOk()).andReturn();
        mockMvc.perform(
                get("/api/configmanager/enum/connectorstatus"))
                .andExpect(status().isOk()).andReturn();
        mockMvc.perform(
                get("/api/configmanager/enum/connectordeploymode"))
                .andExpect(status().isOk()).andReturn();
        mockMvc.perform(
                get("/api/configmanager/enum/language"))
                .andExpect(status().isOk()).andReturn();
        mockMvc.perform(
                get("/api/configmanager/enum/deploymethod"))
                .andExpect(status().isOk()).andReturn();
        mockMvc.perform(
                get("/api/configmanager/enum/brokerstatus"))
                .andExpect(status().isOk()).andReturn();
        mockMvc.perform(
                get("/api/configmanager/enum/securityprofile"))
                .andExpect(status().isOk()).andReturn();
    }
}
