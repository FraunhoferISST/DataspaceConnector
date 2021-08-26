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
package io.dataspaceconnector.controller.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppRequestControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private AppRequestController appRequestController;

    @BeforeEach
    public void setup()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        //Controller needs to be mocked to avoid sending
        // messages to appstore and portainer in unit test.
        Mockito.when(appRequestController.sendMessage(Mockito.any(URI.class), Mockito.any(URI.class)))
                .thenReturn(ResponseEntity.ok().body("Mocked."));
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendMessage_validInput_willReturn200() throws Exception {
        /* ARRANGE */
        final var recipient = URI.create("https://someRecipient");
        final var app = URI.create("https://someApp");

        /* ACT && ASSERT */
        mockMvc.perform(post("/api/ids/app")
                .param("recipient", recipient.toString())
                .param("appId", app.toString()))
                .andExpect(status().isOk());
    }
}
