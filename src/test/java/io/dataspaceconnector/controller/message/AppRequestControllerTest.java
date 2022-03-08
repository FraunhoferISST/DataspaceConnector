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
package io.dataspaceconnector.controller.message;

import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import io.dataspaceconnector.common.exception.UnexpectedResponseException;
import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.appstore.AppStore;
import io.dataspaceconnector.service.ArtifactDataDownloader;
import io.dataspaceconnector.service.MetadataDownloader;
import io.dataspaceconnector.service.message.AppStoreCommunication;
import io.dataspaceconnector.service.resource.type.AppService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppRequestControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private MetadataDownloader metadataDownloader;

    @MockBean
    private ArtifactDataDownloader artifactDataDownloader;

    @MockBean
    private AppService appService;

    @MockBean
    private AppStoreCommunication appStoreCommunication;

    @MockBean
    private ConnectorService connectorService;

    private final DynamicAttributeToken token = new DynamicAttributeTokenBuilder()
            ._tokenValue_("token")
            ._tokenFormat_(TokenFormat.JWT)
            .build();

    @BeforeEach
    public void setup() throws UnexpectedResponseException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        //Controller needs to be mocked to avoid sending
        // messages to appstore and portainer in unit test.
        Mockito.doReturn(URI.create("https://artifactId")).when(metadataDownloader)
                .downloadAppResource(Mockito.any(URI.class), Mockito.any(URI.class), Mockito.any());
        Mockito.when(appService.identifyByRemoteId(Mockito.any(URI.class))).thenReturn(
                Optional.of(UUID.randomUUID()));
        Mockito.when(appService.get(Mockito.any(UUID.class))).thenReturn(new App());
        Mockito.doNothing().when(artifactDataDownloader)
                .downloadTemplate(Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    @WithMockUser("ADMIN")
    public void sendMessage_validInput_willReturn201() throws Exception {
        /* ARRANGE */
        final var recipient = URI.create("https://someRecipient");
        final var app = URI.create("https://someApp");

        Mockito.doReturn(token).when(connectorService).getCurrentDat();
        Mockito.doReturn(Optional.of(new AppStore())).when(appStoreCommunication).checkInput(recipient);

        /* ACT && ASSERT */
        mockMvc.perform(post("/api/ids/app")
                .param("recipient", recipient.toString())
                .param("appId", app.toString()))
                .andExpect(status().isCreated());
    }
}
