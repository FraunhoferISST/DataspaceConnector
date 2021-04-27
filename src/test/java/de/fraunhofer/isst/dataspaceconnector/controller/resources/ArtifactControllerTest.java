//package de.fraunhofer.isst.dataspaceconnector.controller.resources;
//
//import java.net.URI;
//import java.util.UUID;
//
//import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
//import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
//import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
//import de.fraunhofer.isst.dataspaceconnector.services.ArtifactRetriever;
//import de.fraunhofer.isst.dataspaceconnector.services.BlockingArtifactReceiver;
//import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
//import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyVerifier;
//import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.VerificationResult;
//import de.fraunhofer.isst.dataspaceconnector.view.ArtifactViewAssembler;
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
