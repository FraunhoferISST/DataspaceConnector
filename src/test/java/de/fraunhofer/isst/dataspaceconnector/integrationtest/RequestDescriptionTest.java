package de.fraunhofer.isst.dataspaceconnector.integrationtest;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.isst.dataspaceconnector.services.communication.ConnectorRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.communication.ConnectorRequestServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceRepository;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.util.MultipartStringParser;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * This class tests whether the connector can request and save metadata from other connectors.
 *
 * @author Ronja Quensel
 * @version $Id: $Id
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RequestDescriptionTest {

    private final String requestDescriptionEndpoint = "/admin/api/request/description";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDSHttpService idsHttpService;

    @Autowired
    private RequestedResourceRepository requestedResourceRepository;

    @Autowired
    private ConnectorRequestService connectorRequestService;

    @Autowired
    private Serializer serializer;

    private URI recipient;

    private URI requestedArtifact;

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
    public void requestSelfDescription() throws Exception {
        when(idsHttpService.send(any(RequestBody.class), any(URI.class)))
                .thenReturn(getResponse(getSelfDescriptionMultipart()));

        String response = mockMvc.perform(MockMvcRequestBuilders
                .post(requestDescriptionEndpoint)
                .param("recipient", recipient.toString()))
                .andReturn().getResponse().getContentAsString();

        Map<String, String> multipart = MultipartStringParser.stringToMultipart(response);
        String payload = multipart.get("payload");

        serializer.deserialize(payload, Connector.class);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void requestArtifactDescription_validMetadata() throws Exception {
        if (!requestedResourceRepository.findAll().isEmpty()) {
            requestedResourceRepository.deleteAll();
        }

        when(idsHttpService.send(any(RequestBody.class), any(URI.class)))
                .thenReturn(getResponse(getValidArtifactDescriptionMultipart()));

        mockMvc.perform(MockMvcRequestBuilders
                .post(requestDescriptionEndpoint)
                .param("recipient", recipient.toString())
                .param("requestedArtifact", requestedArtifact.toString()));

        Assert.assertEquals(1, requestedResourceRepository.findAll().size());
        Assert.assertTrue(requestedResourceRepository.findAll().get(0).getData().isEmpty());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void requestArtifactDescription_invalidMetadata() throws Exception {
        if (!requestedResourceRepository.findAll().isEmpty()) {
            requestedResourceRepository.deleteAll();
        }

        when(idsHttpService.send(any(RequestBody.class), any(URI.class)))
                .thenReturn(getResponse(getInvalidArtifactDescriptionMultipart()));

        mockMvc.perform(MockMvcRequestBuilders
                .post(requestDescriptionEndpoint)
                .param("recipient", recipient.toString())
                .param("requestedArtifact", requestedArtifact.toString()));

        Assert.assertEquals(0, requestedResourceRepository.findAll().size());
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

    private String getSelfDescriptionMultipart() {
        return "--6-68GNd1LWhpTA8tVYaMkSDhNKSL67_C_NYQSh\r\n" +
                "Content-Disposition: form-data; name=\"header\"\r\n" +
                "Content-Type: text/plain;charset=UTF-8\r\n" +
                "Content-Length: 2119\r\n" +
                "\r\n" +
                "{\r\n" +
                "  \"@context\" : {\r\n" +
                "    \"ids\" : \"https://w3id.org/idsa/core/\",\r\n" +
                "    \"idsc\" : \"https://w3id.org/idsa/code/\"\r\n" +
                "  },\r\n" +
                "  \"@type\" : \"ids:DescriptionResponseMessage\",\r\n" +
                "  \"@id\" : \"https://w3id.org/idsa/autogen/descriptionResponseMessage/eb7747aa-8cf5-4d97-8c5f-d07e670dc7ec\",\r\n" +
                "  \"ids:modelVersion\" : \"3.1.0\",\r\n" +
                "  \"ids:issued\" : {\r\n" +
                "    \"@value\" : \"2020-10-07T08:22:51.117Z\",\r\n" +
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
                "    \"@id\" : \"https://w3id.org/idsa/autogen/dynamicAttributeToken/5ab6804f-900a-471c-a1d1-7dee49f90695\",\r\n" +
                "    \"ids:tokenValue\" : \"eyJ0eXAiOiJKV1QiLCJraWQiOiJkZWZhdWx0IiwiYWxnIjoiUlMyNTYifQ.eyJpZHNfYXR0cmlidXRlcyI6eyJzZWN1cml0eV9wcm9maWxlIjp7ImF1ZGl0X2xvZ2dpbmciOjB9LCJtZW1iZXJzaGlwIjp0cnVlLCJpZHMtdXJpIjoiaHR0cDovL3NvbWUtdXJpIiwidHJhbnNwb3J0X2NlcnRzX3NoYTI1OCI6ImJhY2I4Nzk1NzU3MzBiYjA4M2YyODNmZDViNjdhOGNiODk2OTQ0ZDFiZTI4YzdiMzIxMTdjZmM3NTdjODFlOTYifSwic2NvcGVzIjpbImlkc19jb25uZWN0b3IiXSwiYXVkIjoiSURTX0Nvbm5lY3RvciIsImlzcyI6Imh0dHBzOi8vZGFwcy5haXNlYy5mcmF1bmhvZmVyLmRlIiwic3ViIjoiQz1ERSxPPUZyYXVuaG9mZXIsT1U9SVNTVCxDTj01ODc3NmViZS1mOGY4LTRhNmYtYjQ0Yi1lZWVmYTQ3ZmMwNGIiLCJuYmYiOjE2MDIxNDQwNzMsImV4cCI6MTYwMjE0NzY3M30.SG1Av3G00ne2tYQMerrJbhg9f24klDMjS5ur1aykIGHrL5AyL2wsLit_5aMhG12DUQ7tPa2o4RHyTCQFAhVKkI9_bwCR9jGBcN6jfVn8vjxQ3mDvNdWOoRURI_3YOAjBlo1TqFLOKBmN3uTsB_ns7LqJDruea07sme5O38NOukHPWxsAnoiH4N9NByxHqxayrFj0buDxJCLKXG3_FQtZBcsGO89geylFec0epehh9pL5QV5nr4xLzVhfrJRgx512KVqr1hNLqfNRWGl0TFoKHyEE5J8IMEihZwF76_4kl_1HZe1HP866yO8ceONfTvRI2sCXmKpP8A02NGhisEF_Mg\",\r\n" +
                "    \"ids:tokenFormat\" : {\r\n" +
                "      \"@id\" : \"idsc:JWT\"\r\n" +
                "    }\r\n" +
                "  },\r\n" +
                "  \"ids:senderAgent\" : {\r\n" +
                "    \"@id\" : \"https://simpleconnector.ids.isst.fraunhofer.de/58776ebe-f8f8-4a6f-b44b-eeefa47fc04b\"\r\n" +
                "  },\r\n" +
                "  \"ids:correlationMessage\" : {\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/descriptionRequestMessage/3d621945-9839-4a2e-8437-4db7d1f959ca\"\r\n" +
                "  }\r\n" +
                "}\r\n" +
                "--6-68GNd1LWhpTA8tVYaMkSDhNKSL67_C_NYQSh\r\n" +
                "Content-Disposition: form-data; name=\"payload\"\r\n" +
                "Content-Type: text/plain;charset=UTF-8\r\n" +
                "Content-Length: 7004\r\n" +
                "\r\n" +
                "{\r\n" +
                "  \"@context\" : {\r\n" +
                "    \"ids\" : \"https://w3id.org/idsa/core/\",\r\n" +
                "    \"idsc\" : \"https://w3id.org/idsa/code/\"\r\n" +
                "  },\r\n" +
                "  \"@type\" : \"ids:BaseConnector\",\r\n" +
                "  \"@id\" : \"58776ebe-f8f8-4a6f-b44b-eeefa47fc04b\",\r\n" +
                "  \"ids:version\" : \"Wed Oct 07 08:22:50 GMT 2020\",\r\n" +
                "  \"ids:publicKey\" : {\r\n" +
                "    \"@type\" : \"ids:PublicKey\",\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/publicKey/11d7c39d-47fd-4888-8996-6481b405324e\",\r\n" +
                "    \"ids:keyType\" : {\r\n" +
                "      \"@id\" : \"idsc:RSA\"\r\n" +
                "    },\r\n" +
                "    \"ids:keyValue\" : \"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuw6mFrdflXZTJgFOA5smDXC09SmpJWoGpyERZNEy31pKdsRGhTipR27j9irmmqihv7gIgzCnx6kIRNGI2u0oFQ5FgvO1xxgzcihdpF0CheOf9INgisPkq5hj8Ae/DYXkvjhQ6c6ak/ZYfj0NpqyEPcJ5MLRmYGexMaMZmTbqDJvJl5JG3+bE3Ya21hTZYOxiSicpfFgJ30kn5aUIAtd05IZy7z1sDiVLtTXlLfe/ZQC4pnjFts+tc12sX9ihImnCkd0Wvz3CTZoyBSsc1TdBkb9m0C5tvg0fQP4QgF/zH2QoZnnrI52uAZ8MomWtY2lt3D0kkpR69pfVDJ7y3vN/ewIDAQAB\"\r\n" +
                "  },\r\n" +
                "  \"ids:description\" : [ {\r\n" +
                "    \"@value\" : \"IDS Connector with static example resources\",\r\n" +
                "    \"@language\" : \"en\"\r\n" +
                "  } ],\r\n" +
                "  \"ids:securityProfile\" : {\r\n" +
                "    \"@id\" : \"idsc:BASE_CONNECTOR_SECURITY_PROFILE\"\r\n" +
                "  },\r\n" +
                "  \"ids:curator\" : {\r\n" +
                "    \"@id\" : \"https://www.isst.fraunhofer.de/\"\r\n" +
                "  },\r\n" +
                "  \"ids:maintainer\" : {\r\n" +
                "    \"@id\" : \"https://www.isst.fraunhofer.de/\"\r\n" +
                "  },\r\n" +
                "  \"ids:catalog\" : {\r\n" +
                "    \"@type\" : \"ids:Catalog\",\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/catalog/7b66495a-f6ab-4b55-aa5b-2c77108f6614\",\r\n" +
                "    \"ids:offer\" : [ {\r\n" +
                "      \"@type\" : \"ids:DataResource\",\r\n" +
                "      \"@id\" : \"https://w3id.org/idsa/autogen/dataResource/7434f738-87f8-45c7-adad-14fdb09bc931\",\r\n" +
                "      \"ids:language\" : [ {\r\n" +
                "        \"@id\" : \"idsc:EN\"\r\n" +
                "      } ],\r\n" +
                "      \"ids:resourceEndpoint\" : [ {\r\n" +
                "        \"@type\" : \"ids:StaticEndpoint\",\r\n" +
                "        \"@id\" : \"https://w3id.org/idsa/autogen/staticEndpoint/981f5e8d-4373-4579-8d48-13bcdff9e113\",\r\n" +
                "        \"ids:path\" : \"resources/7434f738-87f8-45c7-adad-14fdb09bc931\",\r\n" +
                "        \"ids:endpointArtifact\" : {\r\n" +
                "          \"@type\" : \"ids:Artifact\",\r\n" +
                "          \"@id\" : \"https://w3id.org/idsa/autogen/artifact/fd51c117-1e2c-4887-85c2-fdf446574da4\",\r\n" +
                "          \"ids:fileName\" : \"String Resource\",\r\n" +
                "          \"ids:creationDate\" : {\r\n" +
                "            \"@value\" : \"2020-08-07T05:45:26.604Z\",\r\n" +
                "            \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"\r\n" +
                "          },\r\n" +
                "          \"ids:byteSize\" : 12\r\n" +
                "        },\r\n" +
                "        \"ids:endpointHost\" : {\r\n" +
                "          \"@type\" : \"ids:Host\",\r\n" +
                "          \"@id\" : \"https://w3id.org/idsa/autogen/host/c76a6e73-96bc-4d14-9a06-0386fef1f040\",\r\n" +
                "          \"ids:protocol\" : {\r\n" +
                "            \"@id\" : \"idsc:HTTP2\"\r\n" +
                "          },\r\n" +
                "          \"ids:accessUrl\" : {\r\n" +
                "            \"@id\" : \"https://simpleconnector.ids.isst.fraunhofer.de/\"\r\n" +
                "          }\r\n" +
                "        }\r\n" +
                "      } ],\r\n" +
                "      \"ids:keyword\" : [ {\r\n" +
                "        \"@value\" : \"sample\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      }, {\r\n" +
                "        \"@value\" : \"data\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      }, {\r\n" +
                "        \"@value\" : \"string\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      } ],\r\n" +
                "      \"ids:representation\" : [ {\r\n" +
                "        \"@type\" : \"ids:Representation\",\r\n" +
                "        \"@id\" : \"https://w3id.org/idsa/autogen/representation/21c00901-c804-4624-9024-007beada1fc3\",\r\n" +
                "        \"ids:instance\" : [ {\r\n" +
                "          \"@type\" : \"ids:Artifact\",\r\n" +
                "          \"@id\" : \"https://w3id.org/idsa/autogen/artifact/fd51c117-1e2c-4887-85c2-fdf446574da4\",\r\n" +
                "          \"ids:fileName\" : \"String Resource\",\r\n" +
                "          \"ids:creationDate\" : {\r\n" +
                "            \"@value\" : \"2020-08-07T05:45:26.604Z\",\r\n" +
                "            \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"\r\n" +
                "          },\r\n" +
                "          \"ids:byteSize\" : 12\r\n" +
                "        } ],\r\n" +
                "        \"ids:mediaType\" : {\r\n" +
                "          \"@type\" : \"ids:IANAMediaType\",\r\n" +
                "          \"@id\" : \"idsc:TEXT_PLAIN\"\r\n" +
                "        }\r\n" +
                "      } ],\r\n" +
                "      \"ids:description\" : [ {\r\n" +
                "        \"@value\" : \"This is a sample data resource as string.\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      } ],\r\n" +
                "      \"ids:modified\" : {\r\n" +
                "        \"@value\" : \"2020-08-13T06:20:57.442Z\",\r\n" +
                "        \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"\r\n" +
                "      },\r\n" +
                "      \"ids:title\" : [ {\r\n" +
                "        \"@value\" : \"String Resource\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      } ]\r\n" +
                "    }, {\r\n" +
                "      \"@type\" : \"ids:DataResource\",\r\n" +
                "      \"@id\" : \"https://w3id.org/idsa/autogen/dataResource/bffed7f9-2305-4fca-89d6-e3dad6c6c1a7\",\r\n" +
                "      \"ids:language\" : [ {\r\n" +
                "        \"@id\" : \"idsc:EN\"\r\n" +
                "      } ],\r\n" +
                "      \"ids:resourceEndpoint\" : [ {\r\n" +
                "        \"@type\" : \"ids:StaticEndpoint\",\r\n" +
                "        \"@id\" : \"https://w3id.org/idsa/autogen/staticEndpoint/0b9e1da7-f050-4ad7-9adc-37ea8a7676dc\",\r\n" +
                "        \"ids:path\" : \"resources/bffed7f9-2305-4fca-89d6-e3dad6c6c1a7\",\r\n" +
                "        \"ids:endpointArtifact\" : {\r\n" +
                "          \"@type\" : \"ids:Artifact\",\r\n" +
                "          \"@id\" : \"https://w3id.org/idsa/autogen/artifact/25998c93-71b7-45ad-a02b-f410ca324f0d\",\r\n" +
                "          \"ids:fileName\" : \"File Resource\",\r\n" +
                "          \"ids:creationDate\" : {\r\n" +
                "            \"@value\" : \"2020-08-07T06:21:46.614Z\",\r\n" +
                "            \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"\r\n" +
                "          },\r\n" +
                "          \"ids:byteSize\" : 6032\r\n" +
                "        },\r\n" +
                "        \"ids:endpointHost\" : {\r\n" +
                "          \"@type\" : \"ids:Host\",\r\n" +
                "          \"@id\" : \"https://w3id.org/idsa/autogen/host/1d166045-f81e-481b-b327-e9e95085f9fd\",\r\n" +
                "          \"ids:protocol\" : {\r\n" +
                "            \"@id\" : \"idsc:HTTP2\"\r\n" +
                "          },\r\n" +
                "          \"ids:accessUrl\" : {\r\n" +
                "            \"@id\" : \"https://simpleconnector.ids.isst.fraunhofer.de/\"\r\n" +
                "          }\r\n" +
                "        }\r\n" +
                "      } ],\r\n" +
                "      \"ids:keyword\" : [ {\r\n" +
                "        \"@value\" : \"sample\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      }, {\r\n" +
                "        \"@value\" : \"data\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      }, {\r\n" +
                "        \"@value\" : \"image\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      } ],\r\n" +
                "      \"ids:representation\" : [ {\r\n" +
                "        \"@type\" : \"ids:Representation\",\r\n" +
                "        \"@id\" : \"https://w3id.org/idsa/autogen/representation/11ca84ad-204c-4d0a-a1fa-a1b53f2e3f4c\",\r\n" +
                "        \"ids:instance\" : [ {\r\n" +
                "          \"@type\" : \"ids:Artifact\",\r\n" +
                "          \"@id\" : \"https://w3id.org/idsa/autogen/artifact/25998c93-71b7-45ad-a02b-f410ca324f0d\",\r\n" +
                "          \"ids:fileName\" : \"File Resource\",\r\n" +
                "          \"ids:creationDate\" : {\r\n" +
                "            \"@value\" : \"2020-08-07T06:21:46.614Z\",\r\n" +
                "            \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"\r\n" +
                "          },\r\n" +
                "          \"ids:byteSize\" : 6032\r\n" +
                "        } ],\r\n" +
                "        \"ids:mediaType\" : {\r\n" +
                "          \"@type\" : \"ids:IANAMediaType\",\r\n" +
                "          \"@id\" : \"idsc:IMAGE_PNG\"\r\n" +
                "        }\r\n" +
                "      } ],\r\n" +
                "      \"ids:description\" : [ {\r\n" +
                "        \"@value\" : \"This is a sample data resource as base64 encoded file.\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      } ],\r\n" +
                "      \"ids:modified\" : {\r\n" +
                "        \"@value\" : \"2020-08-07T06:22:29.128Z\",\r\n" +
                "        \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"\r\n" +
                "      },\r\n" +
                "      \"ids:title\" : [ {\r\n" +
                "        \"@value\" : \"File Resource\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      } ]\r\n" +
                "    } ]\r\n" +
                "  },\r\n" +
                "  \"ids:outboundModelVersion\" : \"3.1.0\",\r\n" +
                "  \"ids:defaultHost\" : {\r\n" +
                "    \"@type\" : \"ids:Host\",\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/host/057fdf0b-b416-4242-8265-53e46dc16ee6\",\r\n" +
                "    \"ids:protocol\" : {\r\n" +
                "      \"@id\" : \"idsc:HTTP2\"\r\n" +
                "    },\r\n" +
                "    \"ids:accessUrl\" : {\r\n" +
                "      \"@id\" : \"https://simpleconnector.ids.isst.fraunhofer.de/\"\r\n" +
                "    }\r\n" +
                "  },\r\n" +
                "  \"ids:inboundModelVersion\" : [ \"3.1.0\" ],\r\n" +
                "  \"ids:title\" : [ {\r\n" +
                "    \"@value\" : \"Dataspace Connector hosted by the Fraunhofer ISST\",\r\n" +
                "    \"@language\" : \"en\"\r\n" +
                "  } ]\r\n" +
                "}\r\n" +
                "--6-68GNd1LWhpTA8tVYaMkSDhNKSL67_C_NYQSh--";
    }

    private String getInvalidArtifactDescriptionMultipart() {
        return "--6-68GNd1LWhpTA8tVYaMkSDhNKSL67_C_NYQSh\r\n" +
                "Content-Disposition: form-data; name=\"header\"\r\n" +
                "Content-Type: text/plain;charset=UTF-8\r\n" +
                "Content-Length: 2119\r\n" +
                "\r\n" +
                "{\r\n" +
                "  \"@context\" : {\r\n" +
                "    \"ids\" : \"https://w3id.org/idsa/core/\",\r\n" +
                "    \"idsc\" : \"https://w3id.org/idsa/code/\"\r\n" +
                "  },\r\n" +
                "  \"@type\" : \"ids:DescriptionResponseMessage\",\r\n" +
                "  \"@id\" : \"https://w3id.org/idsa/autogen/descriptionResponseMessage/d4957419-abc9-4529-a534-fc44d3940a19\",\r\n" +
                "  \"ids:modelVersion\" : \"3.1.0\",\r\n" +
                "  \"ids:issued\" : {\r\n" +
                "    \"@value\" : \"2020-10-07T08:23:19.938Z\",\n" +
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
                "    \"@id\" : \"https://w3id.org/idsa/autogen/dynamicAttributeToken/5ab6804f-900a-471c-a1d1-7dee49f90695\",\r\n" +
                "    \"ids:tokenValue\" : \"eyJ0eXAiOiJKV1QiLCJraWQiOiJkZWZhdWx0IiwiYWxnIjoiUlMyNTYifQ.eyJpZHNfYXR0cmlidXRlcyI6eyJzZWN1cml0eV9wcm9maWxlIjp7ImF1ZGl0X2xvZ2dpbmciOjB9LCJtZW1iZXJzaGlwIjp0cnVlLCJpZHMtdXJpIjoiaHR0cDovL3NvbWUtdXJpIiwidHJhbnNwb3J0X2NlcnRzX3NoYTI1OCI6ImJhY2I4Nzk1NzU3MzBiYjA4M2YyODNmZDViNjdhOGNiODk2OTQ0ZDFiZTI4YzdiMzIxMTdjZmM3NTdjODFlOTYifSwic2NvcGVzIjpbImlkc19jb25uZWN0b3IiXSwiYXVkIjoiSURTX0Nvbm5lY3RvciIsImlzcyI6Imh0dHBzOi8vZGFwcy5haXNlYy5mcmF1bmhvZmVyLmRlIiwic3ViIjoiQz1ERSxPPUZyYXVuaG9mZXIsT1U9SVNTVCxDTj01ODc3NmViZS1mOGY4LTRhNmYtYjQ0Yi1lZWVmYTQ3ZmMwNGIiLCJuYmYiOjE2MDIxNDQwNzMsImV4cCI6MTYwMjE0NzY3M30.SG1Av3G00ne2tYQMerrJbhg9f24klDMjS5ur1aykIGHrL5AyL2wsLit_5aMhG12DUQ7tPa2o4RHyTCQFAhVKkI9_bwCR9jGBcN6jfVn8vjxQ3mDvNdWOoRURI_3YOAjBlo1TqFLOKBmN3uTsB_ns7LqJDruea07sme5O38NOukHPWxsAnoiH4N9NByxHqxayrFj0buDxJCLKXG3_FQtZBcsGO89geylFec0epehh9pL5QV5nr4xLzVhfrJRgx512KVqr1hNLqfNRWGl0TFoKHyEE5J8IMEihZwF76_4kl_1HZe1HP866yO8ceONfTvRI2sCXmKpP8A02NGhisEF_Mg\",\r\n" +
                "    \"ids:tokenFormat\" : {\r\n" +
                "      \"@id\" : \"idsc:JWT\"\r\n" +
                "    }\r\n" +
                "  },\r\n" +
                "  \"ids:senderAgent\" : {\r\n" +
                "    \"@id\" : \"https://simpleconnector.ids.isst.fraunhofer.de/58776ebe-f8f8-4a6f-b44b-eeefa47fc04b\"\r\n" +
                "  },\r\n" +
                "  \"ids:correlationMessage\" : {\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/descriptionRequestMessage/3d621945-9839-4a2e-8437-4db7d1f959ca\"\r\n" +
                "  }\r\n" +
                "}\r\n" +
                "--6-68GNd1LWhpTA8tVYaMkSDhNKSL67_C_NYQSh\r\n" +
                "Content-Disposition: form-data; name=\"payload\"\r\n" +
                "Content-Type: text/plain;charset=UTF-8\r\n" +
                "Content-Length: 822\r\n" +
                "\r\n" +
                "{\r\n" +
                "  \"@context\" : {\r\n" +
                "    \"ids\" : \"https://w3id.org/idsa/core/\",\r\n" +
                "    \"idsc\" : \"https://w3id.org/idsa/code/\"\r\n" +
                "  },\r\n" +
                "  \"@type\" : \"ids:Resource\",\r\n" +
                "  \"@id\" : \"https://w3id.org/idsa/autogen/resource/4198281f-1c79-4c87-8584-48262432cdc2\",\r\n" +
                "  \"ids:contractOffer\": {\n" +
            "        \"@context\" : {\n" +
                "      \"ids\" : \"https://w3id.org/idsa/core/\"\n" +
                "    },\n" +
                "    \"@type\" : \"ids:ContractOffer\",\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/contractOffer/110e659b-d171-4519-8a65-8a2c297ec296\",\n" +
                "    \"ids:permission\" : [ {\n" +
                "      \"@type\" : \"ids:Permission\",\n" +
                "      \"@id\" : \"https://w3id.org/idsa/autogen/permission/7e1a166d-8c42-492f-afb8-204cea7aacf6\",\n" +
                "      \"ids:description\" : [ {\n" +
                "        \"@value\" : \"provide-access\",\n" +
                "        \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                "      } ],\n" +
                "      \"ids:action\" : [ {\n" +
                "        \"@id\" : \"idsc:USE\"\n" +
                "      } ],\n" +
                "      \"ids:title\" : [ {\n" +
                "        \"@value\" : \"Example Usage Policy\",\n" +
                "        \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                "      } ]\n" +
                "    } ]\n" +
                "  }," +
                "  \"ids:language\" : [ {\r\n" +
                "    \"@id\" : \"idsc:EN\"\r\n" +
                "  } ],\r\n" +
                "  \"ids:version\" : \"v1.0\",\r\n" +
                "  \"ids:description\" : [ {\r\n" +
                "    \"@value\" : \"\",\r\n" +
                "    \"@language\" : \"en\"\r\n" +
                "  } ],\r\n" +
                "  \"ids:title\" : [ {\r\n" +
                "    \"@value\" : \"Test resource\",\r\n" +
                "    \"@language\" : \"en\"\r\n" +
                "  } ],\r\n" +
                "  \"ids:representation\" : [ ],\r\n" +
                "  \"ids:standardLicense\" : {\r\n" +
                "    \"@id\" : \"http://license.com\"\r\n" +
                "  },\r\n" +
                "  \"ids:keyword\" : [ {\r\n" +
                "    \"@value\" : \"test\",\r\n" +
                "    \"@language\" : \"en\"\r\n" +
                "  }, {\r\n" +
                "    \"@value\" : \"resource\",\r\n" +
                "    \"@language\" : \"en\"\r\n" +
                "  } ],\r\n" +
                "  \"ids:publisher\" : {\r\n" +
                "    \"@id\" : \"http://resource-owner.com\"\r\n" +
                "  },\r\n" +
                "  \"ids:resourceEndpoint\" : [ {\r\n" +
                "    \"@type\" : \"ids:ConnectorEndpoint\",\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/connectorEndpoint/e5e2ab04-633a-44b9-87d9-a097ae6da3cf\",\r\n" +
                "    \"ids:accessURL\" : {\r\n" +
                "      \"@id\" : \"/api/ids/data\"\r\n" +
                "    }\r\n" +
                "  } ],\r\n" +
                "  \"ids:policy\" : \"{\\r\\n  \\\"@context\\\" : null,\\r\\n  \\\"@type\\\" : null,\\r\\n  \\\"uid\\\" : null,\\r\\n  \\\"obligation\\\" : null,\\r\\n  \\\"permission\\\" : null,\\r\\n  \\\"prohibition\\\" : null\\r\\n}\"\r\n" +
                "}\r\n" +
                "--6-68GNd1LWhpTA8tVYaMkSDhNKSL67_C_NYQSh--\r\n";
    }

    private String getValidArtifactDescriptionMultipart() {
        return "--6-68GNd1LWhpTA8tVYaMkSDhNKSL67_C_NYQSh\r\n" +
                "Content-Disposition: form-data; name=\"header\"\r\n" +
                "Content-Type: text/plain;charset=UTF-8\r\n" +
                "Content-Length: 2119\r\n" +
                "\r\n" +
                "{\r\n" +
                "  \"@context\" : {\r\n" +
                "    \"ids\" : \"https://w3id.org/idsa/core/\",\r\n" +
                "    \"idsc\" : \"https://w3id.org/idsa/code/\"\r\n" +
                "  },\r\n" +
                "  \"@type\" : \"ids:DescriptionResponseMessage\",\r\n" +
                "  \"@id\" : \"https://w3id.org/idsa/autogen/descriptionResponseMessage/d4957419-abc9-4529-a534-fc44d3940a19\",\r\n" +
                "  \"ids:modelVersion\" : \"3.1.0\",\r\n" +
                "  \"ids:issued\" : {\r\n" +
                "    \"@value\" : \"2020-10-07T08:23:19.938Z\",\n" +
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
                "    \"@id\" : \"https://w3id.org/idsa/autogen/dynamicAttributeToken/5ab6804f-900a-471c-a1d1-7dee49f90695\",\r\n" +
                "    \"ids:tokenValue\" : \"eyJ0eXAiOiJKV1QiLCJraWQiOiJkZWZhdWx0IiwiYWxnIjoiUlMyNTYifQ.eyJpZHNfYXR0cmlidXRlcyI6eyJzZWN1cml0eV9wcm9maWxlIjp7ImF1ZGl0X2xvZ2dpbmciOjB9LCJtZW1iZXJzaGlwIjp0cnVlLCJpZHMtdXJpIjoiaHR0cDovL3NvbWUtdXJpIiwidHJhbnNwb3J0X2NlcnRzX3NoYTI1OCI6ImJhY2I4Nzk1NzU3MzBiYjA4M2YyODNmZDViNjdhOGNiODk2OTQ0ZDFiZTI4YzdiMzIxMTdjZmM3NTdjODFlOTYifSwic2NvcGVzIjpbImlkc19jb25uZWN0b3IiXSwiYXVkIjoiSURTX0Nvbm5lY3RvciIsImlzcyI6Imh0dHBzOi8vZGFwcy5haXNlYy5mcmF1bmhvZmVyLmRlIiwic3ViIjoiQz1ERSxPPUZyYXVuaG9mZXIsT1U9SVNTVCxDTj01ODc3NmViZS1mOGY4LTRhNmYtYjQ0Yi1lZWVmYTQ3ZmMwNGIiLCJuYmYiOjE2MDIxNDQwNzMsImV4cCI6MTYwMjE0NzY3M30.SG1Av3G00ne2tYQMerrJbhg9f24klDMjS5ur1aykIGHrL5AyL2wsLit_5aMhG12DUQ7tPa2o4RHyTCQFAhVKkI9_bwCR9jGBcN6jfVn8vjxQ3mDvNdWOoRURI_3YOAjBlo1TqFLOKBmN3uTsB_ns7LqJDruea07sme5O38NOukHPWxsAnoiH4N9NByxHqxayrFj0buDxJCLKXG3_FQtZBcsGO89geylFec0epehh9pL5QV5nr4xLzVhfrJRgx512KVqr1hNLqfNRWGl0TFoKHyEE5J8IMEihZwF76_4kl_1HZe1HP866yO8ceONfTvRI2sCXmKpP8A02NGhisEF_Mg\",\r\n" +
                "    \"ids:tokenFormat\" : {\r\n" +
                "      \"@id\" : \"idsc:JWT\"\r\n" +
                "    }\r\n" +
                "  },\r\n" +
                "  \"ids:senderAgent\" : {\r\n" +
                "    \"@id\" : \"https://simpleconnector.ids.isst.fraunhofer.de/58776ebe-f8f8-4a6f-b44b-eeefa47fc04b\"\r\n" +
                "  },\r\n" +
                "  \"ids:correlationMessage\" : {\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/descriptionRequestMessage/3d621945-9839-4a2e-8437-4db7d1f959ca\"\r\n" +
                "  }\r\n" +
                "}\r\n" +
                "--6-68GNd1LWhpTA8tVYaMkSDhNKSL67_C_NYQSh\r\n" +
                "Content-Disposition: form-data; name=\"payload\"\r\n" +
                "Content-Type: text/plain;charset=UTF-8\r\n" +
                "Content-Length: 822\r\n" +
                "\r\n" +
                "{\r\n" +
                "  \"@context\" : {\r\n" +
                "    \"ids\" : \"https://w3id.org/idsa/core/\",\r\n" +
                "    \"idsc\" : \"https://w3id.org/idsa/code/\"\r\n" +
                "  },\r\n" +
                "  \"@type\" : \"ids:Resource\",\r\n" +
                "  \"@id\" : \"https://w3id.org/idsa/autogen/resource/4198281f-1c79-4c87-8584-48262432cdc2\",\r\n" +
                "  \"ids:contractOffer\": {\n" +
                "        \"@context\" : {\n" +
                "      \"ids\" : \"https://w3id.org/idsa/core/\"\n" +
                "    },\n" +
                "    \"@type\" : \"ids:ContractOffer\",\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/contractOffer/110e659b-d171-4519-8a65-8a2c297ec296\",\n" +
                "    \"ids:permission\" : [ {\n" +
                "      \"@type\" : \"ids:Permission\",\n" +
                "      \"@id\" : \"https://w3id.org/idsa/autogen/permission/7e1a166d-8c42-492f-afb8-204cea7aacf6\",\n" +
                "      \"ids:description\" : [ {\n" +
                "        \"@value\" : \"provide-access\",\n" +
                "        \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                "      } ],\n" +
                "      \"ids:action\" : [ {\n" +
                "        \"@id\" : \"idsc:USE\"\n" +
                "      } ],\n" +
                "      \"ids:title\" : [ {\n" +
                "        \"@value\" : \"Example Usage Policy\",\n" +
                "        \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n" +
                "      } ]\n" +
                "    } ]\n" +
                "  }," +
                "  \"ids:language\" : [ {\r\n" +
                "    \"@id\" : \"idsc:EN\"\r\n" +
                "  } ],\r\n" +
                "  \"ids:version\" : \"v1.0\",\r\n" +
                "  \"ids:description\" : [ {\r\n" +
                "    \"@value\" : \"\",\r\n" +
                "    \"@language\" : \"en\"\r\n" +
                "  } ],\r\n" +
                "  \"ids:title\" : [ {\r\n" +
                "    \"@value\" : \"Test resource\",\r\n" +
                "    \"@language\" : \"en\"\r\n" +
                "  } ],\r\n" +
                "  \"ids:representation\" : [{\r\n" +
                "        \"@type\" : \"ids:Representation\",\r\n" +
                "        \"@id\" : \"https://w3id.org/idsa/autogen/representation/be7c42fc-fe7b-4010-abd1-47dd5d1d10e4\",\r\n" +
                "        \"ids:instance\" : [ {\r\n" +
                "          \"@type\" : \"ids:Artifact\",\r\n" +
                "          \"@id\" : \"https://w3id.org/idsa/autogen/artifact/be7c42fc-fe7b-4010-abd1-47dd5d1d10e4\",\r\n" +
                "          \"ids:byteSize\" : 105\r\n" +
                "        } ],\r\n" +
                "        \"ids:mediaType\" : {\r\n" +
                "          \"@type\" : \"ids:IANAMediaType\",\r\n" +
                "          \"@id\" : \"https://w3id.org/idsa/autogen/iANAMediaType/f8708e75-a725-4f28-b709-ebfba57f3b4f\",\r\n" +
                "          \"ids:filenameExtension\" : \"application/xml\"\r\n" +
                "        },\r\n" +
                "        \"ids:language\" : {\r\n" +
                "          \"@id\" : \"idsc:EN\"\r\n" +
                "        }\r\n" +
                "      } ],\r\n" +
                "  \"ids:standardLicense\" : {\r\n" +
                "    \"@id\" : \"http://license.com\"\r\n" +
                "  },\r\n" +
                "  \"ids:keyword\" : [ {\r\n" +
                "    \"@value\" : \"test\",\r\n" +
                "    \"@language\" : \"en\"\r\n" +
                "  }, {\r\n" +
                "    \"@value\" : \"resource\",\r\n" +
                "    \"@language\" : \"en\"\r\n" +
                "  } ],\r\n" +
                "  \"ids:publisher\" : {\r\n" +
                "    \"@id\" : \"http://resource-owner.com\"\r\n" +
                "  },\r\n" +
                "  \"ids:resourceEndpoint\" : [ {\r\n" +
                "    \"@type\" : \"ids:ConnectorEndpoint\",\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/connectorEndpoint/e5e2ab04-633a-44b9-87d9-a097ae6da3cf\",\r\n" +
                "    \"ids:accessURL\" : {\r\n" +
                "      \"@id\" : \"/api/ids/data\"\r\n" +
                "    }\r\n" +
                "  } ],\r\n" +
                "  \"ids:policy\" : \"{\\r\\n  \\\"@context\\\" : null,\\r\\n  \\\"@type\\\" : null,\\r\\n  \\\"uid\\\" : null,\\r\\n  \\\"obligation\\\" : null,\\r\\n  \\\"permission\\\" : null,\\r\\n  \\\"prohibition\\\" : null\\r\\n}\"\r\n" +
                "}\r\n" +
                "--6-68GNd1LWhpTA8tVYaMkSDhNKSL67_C_NYQSh--\r\n";
    }

}
