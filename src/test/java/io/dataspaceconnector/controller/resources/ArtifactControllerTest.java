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
//package io.dataspaceconnector.controller.resources;
//
//import java.net.URI;
//import java.util.UUID;
//
//import io.dataspaceconnector.exceptions.ResourceNotFoundException;
//import io.dataspaceconnector.model.Artifact;
//import io.dataspaceconnector.model.QueryInput;
//import io.dataspaceconnector.services.ArtifactRetriever;
//import io.dataspaceconnector.services.BlockingArtifactReceiver;
//import io.dataspaceconnector.services.resources.ArtifactService;
//import io.dataspaceconnector.services.usagecontrol.PolicyVerifier;
//import io.dataspaceconnector.services.usagecontrol.VerificationResult;
//import io.dataspaceconnector.view.ArtifactViewAssembler;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.web.PagedResourcesAssembler;
//import org.springframework.http.HttpStatus;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@SpringBootTest(classes = {ResourceControllers.ArtifactController.class})
//class ArtifactControllerTest {
//    @MockBean
//    ArtifactService service;
//
//    @MockBean
//    private ArtifactViewAssembler assembler;
//
//    @MockBean
//    private PagedResourcesAssembler<Artifact> pagedAssembler;
//
//    @MockBean
//    private PolicyVerifier<URI> policyVerifier;
//
//    @MockBean
//    private ArtifactRetriever artifactRetriever;
//
//    @Autowired
//    @InjectMocks
//    ResourceControllers.ArtifactController controller;
//
//    @Test
//    public void getData_null_throwIllegalArgumentException() {
//        /* ARRANGE */
//        Mockito.when(policyVerifier.verify(Mockito.any())).thenReturn(VerificationResult.ALLOWED);
//        Mockito.when(service.getData(policyVerifier, artifactRetriever, Mockito.isNull(), (QueryInput) Mockito.any()))
//                .thenThrow(IllegalArgumentException.class);
//
//        /* ACT && ASSERT */
//        assertThrows(IllegalArgumentException.class, () -> controller.getData(null,
//                new QueryInput()));
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
//                new QueryInput()));
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
//}
