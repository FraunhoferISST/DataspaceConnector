package de.fraunhofer.isst.dataspaceconnector.integrationtest;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.isst.dataspaceconnector.message.ArtifactMessageHandler;
import de.fraunhofer.isst.dataspaceconnector.model.BackendSource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import de.fraunhofer.isst.ids.framework.util.MultipartStringParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * This class tests the correct handling of ArtifactRequestMessages.
 *
 * @author Ronja Quensel
 * @version $Id: $Id
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ArtifactRequestMessageHandlingTest {

    private final String idsMessageEndpoint = "/api/ids/data";

    private final String HEADER_MULTIPART_NAME = "header";

    private final String PAYLOAD_MULTIPART_NAME = "payload";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OfferedResourceService offeredResourceService;

    @Autowired
    private Serializer serializer;

    @MockBean
    private PolicyHandler policyHandler;

    @Autowired
    private ArtifactMessageHandler artifactMessageHandler;

    @Autowired
    private TokenProvider tokenProvider;

    private final UUID representationUUID = UUID.fromString("f7f69b0e-0930-11eb-adc1-0242ac120002");

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);

        Field policyHandlerField = ArtifactMessageHandler.class.getDeclaredField("policyHandler");
        policyHandlerField.setAccessible(true);
        policyHandlerField.set(artifactMessageHandler, policyHandler);
    }

    @Test
    @Transactional
    public void requestArtifact_validId_provisionAllowed() throws Exception {
        when(policyHandler.onDataProvision(any())).thenReturn(true);

        String data = "Hi, I'm data!";
        UUID resourceId = offeredResourceService.addResource(getResourceMetadata());
        offeredResourceService.addData(resourceId, data);

        MockPart header =
                new MockPart(HEADER_MULTIPART_NAME, getArtifactRequestMessageHeader(representationUUID).getBytes());
        MockPart payload = new MockPart(PAYLOAD_MULTIPART_NAME, "".getBytes());

        String response = mockMvc.perform(MockMvcRequestBuilders
                .multipart(idsMessageEndpoint)
                .part(header, payload))
                .andReturn().getResponse().getContentAsString();

        Map<String, String> multipart = MultipartStringParser.stringToMultipart(response);
        String responseHeader = multipart.get(HEADER_MULTIPART_NAME);
        String responsePayload = multipart.get(PAYLOAD_MULTIPART_NAME);

        serializer.deserialize(responseHeader, ArtifactResponseMessage.class);
        Assert.assertEquals(data, responsePayload);

        offeredResourceService.deleteResource(resourceId);
    }

    @Test
    @Transactional
    public void requestArtifact_validId_provisionInhibited() throws Exception {
        when(policyHandler.onDataProvision(any())).thenReturn(false);

        UUID resourceId = offeredResourceService.addResource(getResourceMetadata());
        offeredResourceService.addData(resourceId, "data");

        MockPart header =
                new MockPart(HEADER_MULTIPART_NAME, getArtifactRequestMessageHeader(representationUUID).getBytes());
        MockPart payload = new MockPart(PAYLOAD_MULTIPART_NAME, "".getBytes());

        String response = mockMvc.perform(MockMvcRequestBuilders
                .multipart(idsMessageEndpoint)
                .part(header, payload))
                .andReturn().getResponse().getContentAsString();

        Map<String, String> multipart = MultipartStringParser.stringToMultipart(response);
        String responseHeader = multipart.get(HEADER_MULTIPART_NAME);

        RejectionMessage rejectionMessage = serializer.deserialize(responseHeader, RejectionMessage.class);
        Assert.assertEquals(RejectionReason.NOT_AUTHORIZED, rejectionMessage.getRejectionReason());

        offeredResourceService.deleteResource(resourceId);
    }

    @Test
    public void requestArtifact_invalidId() throws Exception {
        MockPart header =
                new MockPart(HEADER_MULTIPART_NAME, getArtifactRequestMessageHeader(representationUUID).getBytes());
        MockPart payload = new MockPart(PAYLOAD_MULTIPART_NAME, "".getBytes());

        String response = mockMvc.perform(MockMvcRequestBuilders
                .multipart(idsMessageEndpoint)
                .part(header, payload))
                .andReturn().getResponse().getContentAsString();

        Map<String, String> multipart = MultipartStringParser.stringToMultipart(response);
        String responseHeader = multipart.get(HEADER_MULTIPART_NAME);

        RejectionMessage rejectionMessage = serializer.deserialize(responseHeader, RejectionMessage.class);
        Assert.assertEquals(RejectionReason.NOT_FOUND, rejectionMessage.getRejectionReason());
    }

    private ResourceMetadata getResourceMetadata() {
        ResourceRepresentation representation = new ResourceRepresentation(representationUUID, "text/plain",
                123, ResourceRepresentation.SourceType.LOCAL, new BackendSource(URI.create("http://uri.com"),
                "userName", "pasword", "system"));
        representation.setUuid(representationUUID);
        return new ResourceMetadata("Test resource", "", Arrays.asList("test", "resource"), "policy",
                URI.create("http://resource-owner.com"), URI.create("http://license.com"), "v1.0",
                Collections.singletonList(representation));
    }

    private String getArtifactRequestMessageHeader(UUID requestedArtifact) {
        return "{\n" +
                "   \"@context\":\"https://w3id.org/idsa/contexts/2.0.0/context.jsonld\",\r\n" +
                "   \"@type\":\"ids:ArtifactRequestMessage\",\r\n" +
                "   \"ids:modelVersion\":\"3.1.0\",\r\n" +
                "   \"ids:issued\":\"2019-12-10T10:31:28.119+01:00\",\r\n" +
                "   \"ids:issuerConnector\":\"https://simpleconnector.ids.isst.fraunhofer.de/\",\r\n" +
                "   \"ids:requestedArtifact\":\"https://w3id.org/idsa/autogen/dataResource/" + requestedArtifact + "\",\r\n" +
                "   \"ids:securityToken\":{\r\n" +
                "      \"@type\":\"ids:DynamicAttributeToken\",\r\n" +
                "      \"ids:tokenValue\":\"" + tokenProvider.provideDapsToken() + "\",\r\n" +
                "      \"referingConnector\":\"https://divaconnector.isst.fraunhofer.de\",\r\n" +
                "      \"aud\":\"asd\",\r\n" +
                "      \"iss\":\"adas\",\r\n" +
                "      \"sub\":\"sadas\",\r\n" +
                "      \"nbf\":\"asdasd\",\r\n" +
                "      \"exp\":\"1789722984\",\r\n" +
                "      \"ids:tokenFormat\":{\r\n" +
                "         \"@id\":\"https://w3id.org/idsa/code/JWT\"\r\n" +
                "      },\r\n" +
                "      \"@id\":\"https://w3id.org/idsa/autogen/dynamicAttributeToken/260ce6a6-3738-4cd2-9705-113301fdb111\"\r\n" +
                "   },\r\n" +
                "   \"ids:contentVersion\":\"3.1.0\",\r\n" +
                "   \"@id\":\"https://w3id.org/idsa/autogen/artifactRequestMessage/3d621945-9839-4a2e-8437-4db7d1f959ca\"\r\n" +
                "}\r\n";
    }

}
