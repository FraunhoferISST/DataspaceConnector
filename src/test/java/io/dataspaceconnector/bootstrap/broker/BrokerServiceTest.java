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
package io.dataspaceconnector.bootstrap.broker;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.isst.ids.framework.communication.broker.IDSBrokerService;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import io.dataspaceconnector.services.ids.DeserializationService;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {SerializerProvider.class, DeserializationService.class, BrokerService.class})
class BrokerServiceTest {

    @MockBean
    private IDSBrokerService idsBrokerSvc;

    @Autowired
    private BrokerService service;

    @Test
    void registerAtBroker_validInput_returnTrue() throws IOException {
        /* ARRANGE */
        final var properties = new Properties();
        properties.put("broker.register.https://someBroker", "https://someBroker");
        final var idsResources = new HashMap<URI, Resource>();
        idsResources.put(URI.create("https://someBroker"), new ResourceBuilder().build());

        final var response =
                new Response.Builder().request(new Request.Builder().url("https://someBroker").build())
                        .protocol(Protocol.HTTP_1_1).code(200).message("").body(
                        ResponseBody.create(getBrokerRegisterResponse(), MediaType
                                .parse("multipart/form-data"))).build();
        final var resourceResponse =
                new Response.Builder().request(new Request.Builder().url("https://someBroker").build())
                        .protocol(Protocol.HTTP_1_1).code(200).message("").body(
                        ResponseBody.create(getBrokerRegisterResponse(), MediaType
                                .parse("multipart/form-data"))).build();

        Mockito.doReturn(response).when(idsBrokerSvc).updateSelfDescriptionAtBroker(Mockito.eq(
                "https://someBroker"));

        Mockito.doReturn(resourceResponse).when(idsBrokerSvc)
                .updateResourceAtBroker(Mockito.any(), Mockito.any());

        /* ACT */
        final var result = service.registerAtBroker(properties, idsResources);

        /* ASSERT */
        assertTrue(result);
    }

    public static String getBrokerRegisterResponse() {
        return "Success: true\r\n" +
                "Body: --WIYDyVfIpHRCN9WIgaUOLPHIQ9Uu107L7k5qbQ\r\n" +
                "Content-Disposition: form-data; name=\"header\"\r\n" +
                "Content-Type: text/plain;charset=UTF-8\r\n" +
                "Content-Length: 2446\r\n" +
                "\r\n" +
                "{\r\n" +
                "  \"@context\" : {\r\n" +
                "    \"ids\" : \"https://w3id.org/idsa/core/\",\r\n" +
                "    \"idsc\" : \"https://w3id.org/idsa/code/\"\r\n" +
                "  },\r\n" +
                "  \"@type\" : \"ids:MessageProcessedNotificationMessage\",\r\n" +
                "  \"@id\" : \"https://w3id.org/idsa/autogen/descriptionResponseMessage/becc83e1" +
                "-5c22-4069-8c7d-289686d3d775\",\r\n" +
                "  \"ids:modelVersion\" : \"4.0.0\",\r\n" +
                "  \"ids:issued\" : {\r\n" +
                "    \"@value\" : \"2020-12-14T08:09:41.872Z\",\r\n" +
                "    \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"\r\n" +
                "  },\r\n" +
                "  \"ids:issuerConnector\" : {\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/baseConnector/7b934432-a85e-41c5" +
                "-9f65-669219dde4ea\"\r\n" +
                "  },\r\n" +
                "  \"ids:recipientConnector\" : [ {\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/baseConnector/7b934432-a85e-41c5" +
                "-9f65-669219dde4ea\"\r\n" +
                "  } ],\r\n" +
                "  \"ids:securityToken\" : {\r\n" +
                "    \"@type\" : \"ids:DynamicAttributeToken\",\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/dynamicAttributeToken/1491affb" +
                "-ae6d-4903-acb9-b886acf11eae\",\r\n" +
                "    \"ids:tokenValue\" : " +
                "\"eyJ0eXAiOiJKV1QiLCJraWQiOiJkZWZhdWx0IiwiYWxnIjoiUlMyNTYifQ" +
                ".eyJzZWN1cml0eVByb2ZpbGUiOiJpZHNjOkJBU0VfQ09OTkVDVE9SX1NFQ1VSSVRZX1BST0ZJTEUiLCJyZWZlcnJpbmdDb25uZWN0b3IiOiJodHRwOi8vc2ltcGxlaXNzdC5kZW1vIiwiQHR5cGUiOiJpZHM6RGF0UGF5bG9hZCIsIkBjb250ZXh0IjoiaHR0cHM6Ly93M2lkLm9yZy9pZHNhL2NvbnRleHRzL2NvbnRleHQuanNvbmxkIiwidHJhbnNwb3J0Q2VydHNTaGEyNTYiOiIxM2JkYjg2YTZkOTVkZGI0NGMwNGI4ZmU1ZTI5ZTM4N2M2Y2JhNmM5ZjllNWM2NTM3Yjg0MzU5NzM1M2VjYzRlIiwic2NvcGVzIjpbImlkc2M6SURTX0NPTk5FQ1RPUl9BVFRSSUJVVEVTX0FMTCJdLCJhdWQiOiJpZHNjOklEU19DT05ORUNUT1JTX0FMTCIsImlzcyI6Imh0dHBzOi8vZGFwcy5haXNlYy5mcmF1bmhvZmVyLmRlIiwic3ViIjoiNjk6RDE6MzE6OEU6NTY6RjY6MkM6OEM6MjU6NDM6Njc6OUQ6RkU6RjY6NjA6MDU6Mzk6RDc6MDk6MkM6a2V5aWQ6Q0I6OEM6Qzc6QjY6ODU6Nzk6QTg6MjM6QTY6Q0I6MTU6QUI6MTc6NTA6MkY6RTY6NjU6NDM6NUQ6RTgiLCJuYmYiOjE2MDc5MzMzODEsImlhdCI6MTYwNzkzMzM4MSwianRpIjoiTlRRd05qVXdNemcxT0RNME16RTNOVEF4T0E9PSIsImV4cCI6MTYwNzkzNjk4MX0.KxOuUlBFBol8X4CDpNZ3EOiHxza0PaROm_MMKWfBavIRn8LdCj9RHPagdCpNZY7B10k647ri0JokKhgXmLKIxYXhRplNzgCvgoWIFiOoY4qCsk4kMCbN5AAv0vY8Mps2yghL_slkjoLPKQGVByFU6Q7ERq0J28teMvR5bOTwNubO98ZN9kz8Dv6KQImHpDUg-eh_txO_uvg-6N_91iKpx86yNT6umzKg80gmsFX_2mCu48SwXeU6ejukDq6JsXhChUSR2cqCIOya8K4cAqsVeSrsxlDUEfnaGa9o61Akd6UNr1X4F9ihq_XZSeuLr23u70AN50qlC4gNlo_V5LEhgA\",\r\n" +
                "    \"ids:tokenFormat\" : {\r\n" +
                "      \"@id\" : \"idsc:JWT\"\r\n" +
                "    }\r\n" +
                "  },\r\n" +
                "  \"ids:senderAgent\" : {\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/baseConnector/7b934432-a85e-41c5" +
                "-9f65-669219dde4ea\"\r\n" +
                "  },\r\n" +
                "  \"ids:correlationMessage\" : {\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/descriptionRequestMessage/325e0d79" +
                "-1c6f-4a9a-9be3-cf3da43ab111\"\r\n" +
                "  }\r\n" +
                "}\r\n" +
                "--WIYDyVfIpHRCN9WIgaUOLPHIQ9Uu107L7k5qbQ\r\n" +
                "Content-Disposition: form-data; name=\"payload\"\r\n" +
                "Content-Type: text/plain;charset=UTF-8\r\n" +
                "Content-Length: 1963\r\n" +
                "\r\n" +
                "{\r\n" +
                "  \"@context\" : {\r\n" +
                "    \"ids\" : \"https://w3id.org/idsa/core/\",\r\n" +
                "    \"idsc\" : \"https://w3id.org/idsa/code/\"\r\n" +
                "  },\r\n" +
                "  \"@type\" : \"ids:BaseConnector\",\r\n" +
                "  \"@id\" : \"https://w3id.org/idsa/autogen/baseConnector/7b934432-a85e-41c5" +
                "-9f65-669219dde4ea\",\r\n" +
                "  \"ids:version\" : \"v3.0.0\",\r\n" +
                "  \"ids:curator\" : {\r\n" +
                "    \"@id\" : \"https://www.isst.fraunhofer.de/\"\r\n" +
                "  },\r\n" +
                "  \"ids:inboundModelVersion\" : [ \"4.0.0\" ],\r\n" +
                "  \"ids:outboundModelVersion\" : \"4.0.0\",\r\n" +
                "  \"ids:title\" : [ {\r\n" +
                "    \"@value\" : \"Dataspace Connector\",\r\n" +
                "    \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\r\n" +
                "  } ],\r\n" +
                "  \"ids:resourceCatalog\" : [ {\r\n" +
                "    \"@type\" : \"ids:ResourceCatalog\",\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/resourceCatalog/2883e22f-7f26-4bdf" +
                "-a236-3c69ea2b410f\",\r\n" +
                "    \"ids:offeredResource\" : [ {\r\n" +
                "      \"@type\" : \"ids:Resource\",\r\n" +
                "      \"@id\" : \"https://w3id.org/idsa/autogen/resource/abe6fb3d-3e76-49fc-ad0a" +
                "-69392f85cc33\",\r\n" +
                "      \"ids:language\" : [ {\r\n" +
                "        \"@id\" : \"idsc:EN\"\r\n" +
                "      } ],\r\n" +
                "      \"ids:description\" : [ {\r\n" +
                "        \"@value\" : \"Order status resource for user " + 123 + " .\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      } ],\r\n" +
                "      \"ids:title\" : [ {\r\n" +
                "        \"@value\" : \"Order status resource\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      } ],\r\n" +
                "      \"ids:modified\" : {\r\n" +
                "        \"@value\" : \"2020-12-14T08:14:15.558Z\",\r\n" +
                "        \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"\r\r\n" +
                "      },\r\n" +
                "      \"ids:representation\" : [ {\r\n" +
                "        \"@type\" : \"ids:Representation\",\r\n" +
                "        \"@id\" : \"https://w3id" +
                ".org/idsa/autogen/representation/4dbac513-5724-4163-a855-cdd4d05d6bea\",\r\n" +
                "        \"ids:instance\" : [ {\r\n" +
                "          \"@type\" : \"ids:Artifact\",\r\n" +
                "          \"@id\" : \"https://w3id" +
                ".org/idsa/autogen/artifact/4dbac513-5724-4163-a855-cdd4d05d6bea\",\r\n" +
                "          \"ids:byteSize\" : 123\r\n" +
                "        } ],\r\n" +
                "        \"ids:mediaType\" : {\r\n" +
                "          \"@type\" : \"ids:IANAMediaType\",\r\n" +
                "          \"@id\" : \"https://w3id" +
                ".org/idsa/autogen/iANAMediaType/5c2f0699-5848-4752-be7c-4bb4991b846e\",\r\n" +
                "          \"ids:filenameExtension\" : \"application/json\"\r\n" +
                "        },\r\n" +
                "        \"ids:language\" : {\r\n" +
                "          \"@id\" : \"idsc:EN\"\r\n" +
                "        }\r\n" +
                "      } ],\r\n" +
                "      \"ids:created\" : {\r\n" +
                "        \"@value\" : \"2020-12-14T08:14:15.558Z\",\r\n" +
                "        \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTimeStamp\"\r\n" +
                "      },\r\n" +
                "      \"ids:keyword\" : [ {\r\n" +
                "        \"@value\" : \"" + 123 + "\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      }, {\r\n" +
                "        \"@value\" : \"" + "2020-12-14T08:14:15.558Z" + "\",\r\n" +
                "        \"@language\" : \"en\"\r\n" +
                "      } ],\r\n" +
                "      \"ids:contractOffer\" : [ {\r\n" +
                "        \"@type\" : \"ids:ContractOffer\",\r\n" +
                "        \"@id\" : \"https://w3id" +
                ".org/idsa/autogen/contractOffer/08eb624e-47a9-4550-ba27-66fed1fa1630\",\r\n" +
                "        \"ids:permission\" : [ {\r\n" +
                "          \"@type\" : \"ids:Permission\",\r\n" +
                "          \"@id\" : \"https://w3id" +
                ".org/idsa/autogen/permission/e1f2dc40-e396-40a9-8338-71b3c0d32cbd\",\r\n" +
                "          \"ids:action\" : [ {\r\n" +
                "            \"@id\" : \"idsc:USE\"\r\n" +
                "          } ],\r\n" +
                "          \"ids:description\" : [ {\r\n" +
                "            \"@value\" : \"provide-access\",\r\n" +
                "            \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\r\n" +
                "          } ],\r\n" +
                "          \"ids:title\" : [ {\r\n" +
                "            \"@value\" : \"Example Usage Policy\",\r\n" +
                "            \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\r\n" +
                "          } ]\r\n" +
                "        } ]\r\n" +
                "      } ],\r\n" +
                "      \"ids:resourceEndpoint\" : [ {\r\n" +
                "        \"@type\" : \"ids:ConnectorEndpoint\",\r\n" +
                "        \"@id\" : \"https://w3id" +
                ".org/idsa/autogen/connectorEndpoint/e5e2ab04-633a-44b9-87d9-a097ae6da3cf\",\r\n" +
                "        \"ids:accessURL\" : {\r\n" +
                "          \"@id\" : \"/api/ids/data\"\r\n" +
                "        }\r\n" +
                "      } ]\r\n" +
                "    } ],\r\n" +
                "    \"ids:requestedResource\" : [ ]\r\n" +
                "  } ],\r\n" +
                "  \"ids:hasDefaultEndpoint\" : {\r\n" +
                "    \"@type\" : \"ids:ConnectorEndpoint\",\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/connectorEndpoint/e5e2ab04-633a" +
                "-44b9-87d9-a097ae6da3cf\",\r\n" +
                "    \"ids:accessURL\" : {\r\n" +
                "      \"@id\" : \"/api/ids/data\"\r\n" +
                "    }\r\n" +
                "  },\r\n" +
                "  \"ids:securityProfile\" : {\r\n" +
                "    \"@id\" : \"idsc:BASE_SECURITY_PROFILE\"\r\n" +
                "  },\r\n" +
                "  \"ids:maintainer\" : {\r\n" +
                "    \"@id\" : \"https://www.isst.fraunhofer.de/\"\r\n" +
                "  },\r\n" +
                "  \"ids:description\" : [ {\r\n" +
                "    \"@value\" : \"IDS Connector with static example resources hosted by the " +
                "Fraunhofer ISST\",\r\n" +
                "    \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\r\n" +
                "  } ],\r\n" +
                "  \"ids:publicKey\" : {\r\n" +
                "    \"@type\" : \"ids:PublicKey\",\r\n" +
                "    \"@id\" : \"https://w3id.org/idsa/autogen/publicKey/78eb73a3-3a2a-4626-a0ff" +
                "-631ab50a00f9\",\r\n" +
                "    \"ids:keyType\" : {\r\n" +
                "      \"@id\" : \"idsc:RSA\"\r\n" +
                "    },\r\n" +
                "    \"ids:keyValue\" : " +
                "\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuw6mFrdflXZTJgFOA5smDXC09SmpJWoGpyERZNEy31pKdsRGhTipR27j9irmmqihv7gIgzCnx6kIRNGI2u0oFQ5FgvO1xxgzcihdpF0CheOf9INgisPkq5hj8Ae/DYXkvjhQ6c6ak/ZYfj0NpqyEPcJ5MLRmYGexMaMZmTbqDJvJl5JG3+bE3Ya21hTZYOxiSicpfFgJ30kn5aUIAtd05IZy7z1sDiVLtTXlLfe/ZQC4pnjFts+tc12sX9ihImnCkd0Wvz3CTZoyBSsc1TdBkb9m0C5tvg0fQP4QgF/zH2QoZnnrI52uAZ8MomWtY2lt3D0kkpR69pfVDJ7y3vN/ewIDAQAB\"\r\n" +
                "  }\r\n" +
                "}\r\n" +
                "--WIYDyVfIpHRCN9WIgaUOLPHIQ9Uu107L7k5qbQ--\r\n";
    }
}
