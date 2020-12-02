package de.fraunhofer.isst.dataspaceconnector.integrationtest;

import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.services.communication.ConnectorRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.communication.ConnectorRequestServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceRepository;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import okhttp3.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * This class tests whether the connecter can request and save data from other connectors.
 *
 * @author Ronja Quensel
 * @version $Id: $Id
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RequestArtifactTest {

    private final String requestArtifactEndpoint = "/admin/api/request/artifact";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDSHttpService idsHttpService;

    @Autowired
    private RequestedResourceRepository requestedResourceRepository;

    @Autowired
    private ConnectorRequestService connectorRequestService;

    private URI recipient;

    private URI requestedArtifact;

    private final String data = "Hi, I'm data!";

    @Before
    public void init() throws Exception {
        recipient = new URI("http://recipient-uri.com");
        requestedArtifact = new URI("https://w3id.org/idsa/autogen/dataResource/7434f738-87f8-45c7-adad-14fdb09bc931");

        MockitoAnnotations.initMocks(this);

        Field idsHttpServiceField = ConnectorRequestServiceImpl.class.getDeclaredField("idsHttpService");
        idsHttpServiceField.setAccessible(true);
        idsHttpServiceField.set(connectorRequestService, this.idsHttpService);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void requestArtifact() throws Exception {
        requestedResourceRepository.deleteAll();

        UUID key = requestedResourceRepository.save(getRequestedResource()).getUuid();
        Assert.assertTrue(requestedResourceRepository.findAll().get(0).getData().isEmpty());

        when(idsHttpService.send(any(RequestBody.class), any(URI.class)))
                .thenReturn(getResponse(getArtifactResponseMultipart()));

        mockMvc.perform(MockMvcRequestBuilders
                .post(requestArtifactEndpoint)
                .param("recipient", recipient.toString())
                .param("requestedArtifact", requestedArtifact.toString())
                .param("key", key.toString()));

        Assert.assertEquals(1, requestedResourceRepository.findAll().size());
        Assert.assertEquals(data, requestedResourceRepository.findAll().get(0).getData());
    }

    private Response getResponse(String multipartPayload) {
        MediaType MEDIA_TYPE_MULTIPART =
                MediaType.parse("multipart/form-data; boundary=6-68GNd1LWhpTA8tVYaMkSDhNKSL67_C_NYQSh; charset=UTF-8");

        ResponseBody responseBody = ResponseBody.create(multipartPayload, MEDIA_TYPE_MULTIPART);
        return new Response.Builder()
                .code(0)
                .request(new Request.Builder().url("http://recipient-uri.com").build())
                .protocol(Protocol.HTTP_2)
                .message("")
                .body(responseBody)
                .build();
    }

    private RequestedResource getRequestedResource() {
        return new RequestedResource(new Date(), new Date(), getResourceMetadata(), "", 0);
    }

    private ResourceMetadata getResourceMetadata() {
        return new ResourceMetadata("Test resource", "", Arrays.asList("test", "resource"), "policy",
                URI.create("http://resource-owner.com"), URI.create("http://license.com"), "v1.0",
                new HashMap<>());
    }

    private String getArtifactResponseMultipart() {
        return "--6-68GNd1LWhpTA8tVYaMkSDhNKSL67_C_NYQSh\r\n" +
                "Content-Disposition: form-data; name=\"header\"\r\n" +
                "Content-Type: text/plain;charset=UTF-8\r\n" +
                "Content-Length: 2110\r\n" +
                "\r\n" +
                "{\r\n" +
                "  \"@context\" : {\r\n" +
                "    \"ids\" : \"https://w3id.org/idsa/core/\",\r\n" +
                "    \"idsc\" : \"https://w3id.org/idsa/code/\"\r\n" +
                "  },\r\n" +
                "  \"@type\" : \"ids:ArtifactResponseMessage\",\r\n" +
                "  \"@id\" : \"https://w3id.org/idsa/autogen/artifactResponseMessage/3291aeda-cc13-407e-98ff-4a661eda0618\",\r\n" +
                "  \"ids:modelVersion\" : \"3.1.0\",\r\n" +
                "  \"ids:issued\" : {\r\n" +
                "    \"@value\" : \"2020-10-07T10:51:49.782Z\",\r\n" +
                "    \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"\r\n" +
                "  },\r\n" +
                "  \"ids:issuerConnector\" : {\r\n" +
                "    \"@id\" : \"https://simpleconnector.ids.isst.fraunhofer.de/58776ebe-f8f8-4a6f-b44b-eeefa47fc04b\"\r\n" +
                "  },\r\n" +
                "  \"ids:recipientConnector\" : [ {\r\n" +
                "    \"@id\" : \"https://simpleconnector.ids.isst.fraunhofer.de/\"\r\n" +
                "  } ],\r\n" +
                "  \"ids:securityToken\" : {\r\n" +
                "    \"@type\" : \"ids:DynamicAttributeToken\",\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/dynamicAttributeToken/6ce8a1a3-6a79-4545-b0ef-35029bf23656\",\r\n" +
                "    \"ids:tokenValue\" : \"eyJ0eXAiOiJKV1QiLCJraWQiOiJkZWZhdWx0IiwiYWxnIjoiUlMyNTYifQ.eyJpZHNfYXR0cmlidXRlcyI6eyJzZWN1cml0eV9wcm9maWxlIjp7ImF1ZGl0X2xvZ2dpbmciOjB9LCJtZW1iZXJzaGlwIjp0cnVlLCJpZHMtdXJpIjoiaHR0cDovL3NvbWUtdXJpIiwidHJhbnNwb3J0X2NlcnRzX3NoYTI1OCI6ImJhY2I4Nzk1NzU3MzBiYjA4M2YyODNmZDViNjdhOGNiODk2OTQ0ZDFiZTI4YzdiMzIxMTdjZmM3NTdjODFlOTYifSwic2NvcGVzIjpbImlkc19jb25uZWN0b3IiXSwiYXVkIjoiSURTX0Nvbm5lY3RvciIsImlzcyI6Imh0dHBzOi8vZGFwcy5haXNlYy5mcmF1bmhvZmVyLmRlIiwic3ViIjoiQz1ERSxPPUZyYXVuaG9mZXIsT1U9SVNTVCxDTj01ODc3NmViZS1mOGY4LTRhNmYtYjQ0Yi1lZWVmYTQ3ZmMwNGIiLCJuYmYiOjE2MDIxNDQwNzMsImV4cCI6MTYwMjE0NzY3M30.SG1Av3G00ne2tYQMerrJbhg9f24klDMjS5ur1aykIGHrL5AyL2wsLit_5aMhG12DUQ7tPa2o4RHyTCQFAhVKkI9_bwCR9jGBcN6jfVn8vjxQ3mDvNdWOoRURI_3YOAjBlo1TqFLOKBmN3uTsB_ns7LqJDruea07sme5O38NOukHPWxsAnoiH4N9NByxHqxayrFj0buDxJCLKXG3_FQtZBcsGO89geylFec0epehh9pL5QV5nr4xLzVhfrJRgx512KVqr1hNLqfNRWGl0TFoKHyEE5J8IMEihZwF76_4kl_1HZe1HP866yO8ceONfTvRI2sCXmKpP8A02NGhisEF_Mg\",\r\n" +
                "    \"ids:tokenFormat\" : {\r\n" +
                "      \"@id\" : \"idsc:JWT\"\r\n" +
                "    }\r\n" +
                "  },\r\n" +
                "  \"ids:senderAgent\" : {\r\n" +
                "    \"@id\" : \"https://simpleconnector.ids.isst.fraunhofer.de/58776ebe-f8f8-4a6f-b44b-eeefa47fc04b\"\r\n" +
                "  },\r\n" +
                "  \"ids:correlationMessage\" : {\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/artifactRequestMessage/3d621945-9839-4a2e-8437-4db7d1f959ca\"\r\n" +
                "  }\r\n" +
                "}\r\n" +
                "--6-68GNd1LWhpTA8tVYaMkSDhNKSL67_C_NYQSh\r\n" +
                "Content-Disposition: form-data; name=\"payload\"\r\n" +
                "Content-Type: text/plain;charset=UTF-8\r\n" +
                "Content-Length: 12\r\n" +
                "\r\n" +
                data + "\r\n" +
                "--6-68GNd1LWhpTA8tVYaMkSDhNKSL67_C_NYQSh--\r\n";
    }

}
