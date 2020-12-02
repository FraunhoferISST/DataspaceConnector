package de.fraunhofer.isst.dataspaceconnector.integrationtest;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.isst.dataspaceconnector.model.BackendSource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.UUIDUtils;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceRepository;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import de.fraunhofer.isst.ids.framework.util.MultipartStringParser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.*;

/**
 * This class tests the correct handling of DescriptionRequestMessages.
 *
 * @author Ronja Quensel
 * @version $Id: $Id
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DescriptionRequestMessageHandlingTest {

    private final String idsMessageEndpoint = "/api/ids/data";

    private final String HEADER_MULTIPART_NAME = "header";

    private final String PAYLOAD_MULTIPART_NAME = "payload";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OfferedResourceService offeredResourceService;

    @Autowired
    private OfferedResourceRepository offeredResourceRepository;

    @Autowired
    private Serializer serializer;

    @Autowired
    private TokenProvider tokenProvider;

    @Test
    public void requestSelfDescription() throws Exception {
        MockPart header = new MockPart(HEADER_MULTIPART_NAME, getHeaderRequestedElementNull().getBytes());
        MockPart payload = new MockPart(PAYLOAD_MULTIPART_NAME, "".getBytes());

        String response = mockMvc.perform(MockMvcRequestBuilders
                .multipart(idsMessageEndpoint)
                .part(header, payload))
                .andReturn().getResponse().getContentAsString();

        Map<String, String> multipart = MultipartStringParser.stringToMultipart(response);
        String responseHeader = multipart.get(HEADER_MULTIPART_NAME);
        String responsePayload = multipart.get(PAYLOAD_MULTIPART_NAME);

        serializer.deserialize(responseHeader, DescriptionResponseMessage.class);
        serializer.deserialize(responsePayload, Connector.class);
    }

    @Test
    @Transactional
    public void requestArtifactDescription_validId() throws Exception {
        UUID resourceId = offeredResourceService.addResource(getResourceMetadata());
        offeredResourceService.addData(resourceId, "data");

        MockPart header = new MockPart(HEADER_MULTIPART_NAME, getHeaderRequestedElementNotNull(resourceId).getBytes());
        MockPart payload = new MockPart(PAYLOAD_MULTIPART_NAME, "".getBytes());

        String response = mockMvc.perform(MockMvcRequestBuilders
                .multipart(idsMessageEndpoint)
                .part(header, payload))
                .andReturn().getResponse().getContentAsString();

        Map<String, String> multipart = MultipartStringParser.stringToMultipart(response);
        String responseHeader = multipart.get(HEADER_MULTIPART_NAME);
        String responsePayload = multipart.get(PAYLOAD_MULTIPART_NAME);

        serializer.deserialize(responseHeader, DescriptionResponseMessage.class);
        Assert.assertEquals(offeredResourceService.getOfferedResources().get(resourceId).toRdf(), responsePayload);
    }

    @Test
    @Transactional
    public void requestArtifactDescription_invalidId() throws Exception {
        offeredResourceRepository.deleteAll();

        MockPart header = new MockPart(HEADER_MULTIPART_NAME, getHeaderRequestedElementNotNull(UUID.randomUUID()).getBytes());
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
        final var representationId = UUIDUtils.createUUID((UUID x) -> false);
        final var representation = new ResourceRepresentation();
        representation.setUuid(representationId);
        representation.setType("Type");
        representation.setByteSize(1);
        representation.setName("Name");

        final var source = new BackendSource();
        source.setType(BackendSource.Type.LOCAL);

        representation.setSource(source);

        return new ResourceMetadata("Test resource", "", Arrays.asList("test", "resource"),
                "{\n" +
                        "  \"@context\" : {\n" +
                        "    \"ids\" : \"https://w3id.org/idsa/core/\"\n" +
                        "  },\n" +
                        "  \"@type\" : \"ids:ContractOffer\",\n" +
                        "  \"@id\" : \"https://w3id.org/idsa/autogen/contractOffer/110e659b-d171-4519-8a65-8a2c297ec296\",\n" +
                        "  \"ids:permission\" : [ {\n" +
                        "    \"@type\" : \"ids:Permission\",\n" +
                        "    \"@id\" : \"https://w3id.org/idsa/autogen/permission/7e1a166d-8c42-492f-afb8-204cea7aacf6\",\n" +
                        "    \"ids:description\" : [ {\n" +
                        "      \"@value\" : \"provide-access\",\n" +
                        "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                        "    } ],\n" +
                        "    \"ids:action\" : [ {\n" +
                        "      \"@id\" : \"idsc:USE\"\n" +
                        "    } ],\n" +
                        "    \"ids:title\" : [ {\n" +
                        "      \"@value\" : \"Example Usage Policy\",\n" +
                        "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                        "    } ]\n" +
                        "  } ]\n" +
                        "}",
                URI.create("http://resource-owner.com"), URI.create("http://license.com"), "v1.0",
                Collections.singletonMap(representationId, representation));
    }

    private String getHeaderRequestedElementNull() {
        return "{\r\n" +
                "   \"@context\":\"https://w3id.org/idsa/contexts/2.0.0/context.jsonld\",\r\n" +
                "   \"@type\":\"ids:DescriptionRequestMessage\",\r\n" +
                "   \"ids:modelVersion\":\"4.0.0\",\r\n" +
                "   \"ids:issued\":\"2019-12-10T10:31:28.119+01:00\",\r\n" +
                "   \"ids:issuerConnector\":\"https://simpleconnector.ids.isst.fraunhofer.de/\",\r\n" +
                "   \"ids:requestedElement\":null,\r\n" +
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
                "   \"ids:contentVersion\":\"4.0.0\",\r\n" +
                "   \"@id\":\"https://w3id.org/idsa/autogen/descriptionRequestMessage/3d621945-9839-4a2e-8437-4db7d1f959ca\"\r\n" +
                "}\r\n";
    }

    private String getHeaderRequestedElementNotNull(UUID requestedArtifact) {
        return "{\r\n" +
                "   \"@context\":\"https://w3id.org/idsa/contexts/2.0.0/context.jsonld\",\r\n" +
                "   \"@type\":\"ids:DescriptionRequestMessage\",\r\n" +
                "   \"ids:modelVersion\":\"4.0.0\",\r\n" +
                "   \"ids:issued\":\"2019-12-10T10:31:28.119+01:00\",\r\n" +
                "   \"ids:issuerConnector\":\"https://simpleconnector.ids.isst.fraunhofer.de/\",\r\n" +
                "   \"ids:requestedElement\":\"https://w3id.org/idsa/autogen/dataResource/" + requestedArtifact.toString() + "\",\r\n" +
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
                "   \"ids:contentVersion\":\"4.0.0\",\r\n" +
                "   \"@id\":\"https://w3id.org/idsa/autogen/descriptionRequestMessage/3d621945-9839-4a2e-8437-4db7d1f959ca\"\r\n" +
                "}\r\n";
    }

}
