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

import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import io.dataspaceconnector.controller.resource.view.ArtifactView;
import io.dataspaceconnector.model.Artifact;
import io.dataspaceconnector.service.BlockingArtifactReceiver;
import io.dataspaceconnector.service.ids.ConnectorService;
import io.dataspaceconnector.service.message.subscription.SubscriberNotificationService;
import io.dataspaceconnector.service.resource.ArtifactService;
import io.dataspaceconnector.service.usagecontrol.DataAccessVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = {ResourceControllers.ArtifactController.class})
class ArtifactControllerTest {

    @Autowired
    ResourceControllers.ArtifactController controller;

    @MockBean
    private ConnectorService connectorService;

    @MockBean
    ArtifactService service;

    @MockBean
    BlockingArtifactReceiver dataReceiver;

    @MockBean
    DataAccessVerifier accessVerifier;

    @MockBean
    SubscriberNotificationService subscriberNotificationSvc;

    @MockBean
    RepresentationModelAssembler<Artifact, ArtifactView> modelAssembler;

    @MockBean
    PagedResourcesAssembler<Artifact> pagedResourcesAssembler;

    @MockBean
    private MockMvc mockMvc;

    private final DynamicAttributeToken token = new DynamicAttributeTokenBuilder()
            ._tokenValue_("token")
            ._tokenFormat_(TokenFormat.JWT)
            .build();

//    @SneakyThrows
//    @Test
//    public void update_validInput_throwIllegalArgumentException() {
//        /* ARRANGE */
//        Mockito.doReturn(token).when(connectorService).getCurrentDat();
//        final var desc = new ArtifactDesc();
//
//        /* ACT */
//        final var result = mockMvc.perform(put("/api/artifacts")
//                .param("id", String.valueOf(UUID.randomUUID()))
//                .content(String.valueOf(desc))).andReturn();
//
//        /* ASSERT */
//        assertEquals(500, result.getResponse().getStatus());
//    }
}
