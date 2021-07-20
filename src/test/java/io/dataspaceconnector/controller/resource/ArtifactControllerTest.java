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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import de.fraunhofer.ids.messaging.protocol.UnexpectedResponseException;
import io.dataspaceconnector.controller.resource.view.ArtifactViewAssembler;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.repository.ArtifactRepository;
import io.dataspaceconnector.repository.AuthenticationRepository;
import io.dataspaceconnector.repository.DataRepository;
import io.dataspaceconnector.service.BlockingArtifactReceiver;
import io.dataspaceconnector.service.HttpService;
import io.dataspaceconnector.service.message.subscription.SubscriberNotificationService;
import io.dataspaceconnector.service.resource.ArtifactService;
import io.dataspaceconnector.service.usagecontrol.DataAccessVerifier;
import io.dataspaceconnector.util.QueryInput;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = { ResourceControllers.ArtifactController.class })
class ArtifactControllerTest {

    @MockBean
    private DataRepository dataRepo;

    @MockBean
    private HttpService httpService;

    @MockBean
    private AuthenticationRepository authRepo;

    @MockBean
    private ArtifactRepository artifactRepo;

    @MockBean
    private ArtifactFactory artifactFactory;

    @MockBean
    private BlockingArtifactReceiver artifactReceiver;

    @MockBean
    private DataAccessVerifier dataAccessVerifier;

    @MockBean
    private ArtifactViewAssembler artifactViewAssembler;

    @MockBean
    private PagedResourcesAssembler<Artifact> artifactPagedResourcesAssembler;

    @MockBean
    private SubscriberNotificationService subscriberNotificationService;

    @SpyBean
    private ArtifactService service;

    @Autowired
    ResourceControllers.ArtifactController controller;

    @Test
    public void putData_newData_willPlace() throws IOException {
        /* ARRANGE */
        final var artifactId = UUID.randomUUID();
        final byte[] data = {0 , 1, 2, 3};


        Mockito.doReturn(null).when(service).setData(eq(artifactId), any());

        /* ACT */
        final var result = controller.putData(artifactId, data);

        /* ASSERT */
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatusCode().value());
    }

    @Test
    public void getData_validIdAndQuery_returnTheData() throws
            IOException,
            UnexpectedResponseException,
            io.dataspaceconnector.exception.UnexpectedResponseException {
        /* ARRANGE */
        final var artifactId = UUID.randomUUID();
        final var queryInput = new QueryInput();
        final byte[] data = {0, 1, 2, 3};
        final var dataStream = new ByteArrayInputStream(data);

        Mockito.doReturn(dataStream).when(service).getData(any(), any(), eq(artifactId), eq(queryInput));

        /* ACT */
        final var result = controller.getData(artifactId, queryInput);

        /* ASSERT */
        assertEquals(HttpStatus.OK.value(), result.getStatusCode().value());
    }

    //    @Test
//    public void getData_null_throwIllegalArgumentException() {
//        /* ARRANGE */
//        Mockito.when(policyVerifier.verify(Mockito.any())).thenReturn(VerificationResult.ALLOWED);
//        Mockito.when(service.getData(policyVerifier, artifactRetriever, Mockito.isNull(), (QueryInput) Mockito.any()))
//                .thenThrow(IllegalArgumentException.class);
//
//        /* ACT && ASSERT */
//        assertThrows(IllegalArgumentException.class, () -> controller.getData(null, new QueryInput()));
//    }
//
//    @Test
//    public void getData_unknownId_throwResourceNotFoundException() {
//        /* ARRANGE */
//        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
//        Mockito.when(service.getData(Mockito.eq(unknownUuid), (QueryInput) Mockito.any()))
//                .thenThrow(ResourceNotFoundException.class);
//
//        /* ACT */
//        assertThrows(ResourceNotFoundException.class, () -> controller.getData(unknownUuid,
//                                                                               new QueryInput()));
//    }
//
//    @Test
//    public void getData_knownId_returnData() {
//        /* ARRANGE */
//        final var expected = "TEST";
//        final var knownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
//        Mockito.when(service.getData(Mockito.eq(knownUuid), (QueryInput) Mockito.any())).thenReturn(expected);
//
//        /* ACT */
//        final var result = controller.getData(knownUuid, new QueryInput());
//
//        /* ASSERT */
//        assertEquals(expected, result.getBody());
//    }
//
//    @Test
//    public void getData_knownId_hasStatusCode200() {
//        /* ARRANGE */
//        final var expected = "TEST";
//        final var knownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
//        Mockito.when(service.getData(Mockito.eq(knownUuid), (QueryInput) Mockito.any())).thenReturn(expected);
//
//        /* ACT */
//        final var result = controller.getData(knownUuid, new QueryInput());
//
//        /* ASSERT */
//        assertEquals(HttpStatus.OK.value(), result.getStatusCodeValue());
//    }
}
