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
package io.dataspaceconnector.service.resource.type;

import io.dataspaceconnector.common.net.HttpService;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.routing.RouteDataDispatcher;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.repository.ArtifactRepository;
import io.dataspaceconnector.repository.AuthenticationRepository;
import io.dataspaceconnector.repository.DataRepository;
import io.dataspaceconnector.service.DataRetriever;
import io.dataspaceconnector.service.MultipartArtifactRetriever;
import io.dataspaceconnector.common.usagecontrol.AllowAccessVerifier;
import io.dataspaceconnector.service.resource.relation.ArtifactRouteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ArtifactService.class, ArtifactFactory.class, ArtifactRepository.class,
        DataRepository.class, HttpService.class, MultipartArtifactRetriever.class})
public class RestrictedArtifactServiceTest {

    @MockBean
    private ArtifactRepository artifactRepository;

    @MockBean
    private AuthenticationRepository authenticationRepository;

    @MockBean
    private DataRepository dataRepository;

    @MockBean
    private HttpService httpService;

    @MockBean
    private ArtifactFactory artifactFactory;

    @MockBean
    private ArtifactRouteService artifactRouteService;

    @MockBean
    private DataRetriever dataRetriever;

    @MockBean
    private RouteDataDispatcher routeDataDispatcher;

    @MockBean
    private MultipartArtifactRetriever artifactReceiver;

    @SpyBean
    private ArtifactService service;

    @Test
    public void getData_restrictedData_allowAccessAndDownloadDataAndReturnData() throws IOException {
        /* ARRANGE */
        final var verifier = new AllowAccessVerifier();
        final var artifactId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
        final var agreements = List.of(URI.create("https://someAgreements"));
        final var dataString = "hello".getBytes(StandardCharsets.UTF_8);
        final var data = new ByteArrayInputStream(dataString);
        final var localData = new LocalData();
        ReflectionTestUtils.setField(localData, "value", dataString);
        final var artifact = new ArtifactImpl();
        ReflectionTestUtils.setField(artifact, "additional", new HashMap<>());
        ReflectionTestUtils.setField(artifact, "remoteAddress", URI.create("https://remoteAddress"));
        ReflectionTestUtils.setField(artifact, "data", localData);

        Mockito.doReturn(agreements).when(artifactRepository).findRemoteOriginAgreements(artifactId);
        Mockito.doReturn(artifact).when(service).get(artifactId);
        Mockito.doReturn(data).when(artifactReceiver).retrieve(artifactId, artifact.getRemoteAddress(), agreements.get(0), null);

        /* ACT */
        final var result = service.getData(verifier, artifactReceiver, artifactId,
                (QueryInput) null, null);

        /* ASSERT */
        final var resultBytes = result.readAllBytes();
        assertTrue(Arrays.compare(dataString, resultBytes) == 0);
    }
}
