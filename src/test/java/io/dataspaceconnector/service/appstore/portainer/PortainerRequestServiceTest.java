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
package io.dataspaceconnector.service.appstore.portainer;

import io.dataspaceconnector.common.net.HttpService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
class PortainerRequestServiceTest {

    private static String RESPONSE_STRING = "{\"Type\":\"1\", \"URL\":\"https://someurl\", \"State\":\"running\", \"Name\":\"testdata\", \"image\":\"ids-binac/03d93c90-d63f-4dc4-a6f3-b06874f5077e:latest\", \"Id\":\"1\", \"Portainer\":{\"ResourceControl\":{\"Id\":1}}}";

    private static String ARRAY_RESPONSE = "[" + RESPONSE_STRING + "]";

    private static String TEMPLATE = "{\n" +
            "    \"type\" : 1,\n" +
            "    \"title\" : \"Docker Representation\",\n" +
            "    \"description\" : \"This is the docker representation for the DataProcessingApp\",\n" +
            "    \"image\" : \"ids-binac/03d93c90-d63f-4dc4-a6f3-b06874f5077e\",\n" +
            "    \"restartPolicy\" : \"always\",\n" +
            "    \"ports\" : [ {\n" +
            "      \"INPUT_ENDPOINT\" : \"5000:5000/tcp\"\n" +
            "    } ],\n" +
            "    \"volumes\" : [ {\n" +
            "      \"container\" : \"/data/temp\",\n" +
            "      \"bind\" : \"/temp\"\n" +
            "    } ],\n" +
            "    \"label\" : [ {\n" +
            "      \"label\" : \"License\",\n" +
            "      \"value\" : \"https://www.apache.org/licenses/LICENSE-2.0\"\n" +
            "    }, {\n" +
            "      \"label\" : \"Author\",\n" +
            "      \"value\" : \"https://fit.fraunhofer.de\"\n" +
            "    } ],\n" +
            "    \"name\" : \"03d93c90-d63f-4dc4-a6f3-b06874f5077e\",\n" +
            "    \"logo\" : \"https://logo_placeholder.example\",\n" +
            "    \"registry\" : \"binac.fit.fraunhofer.de\",\n" +
            "    \"categories\" : [ \"data\", \"processing\", \"fit\" ],\n" +
            "    \"platform\" : \"linux\",\n" +
            "    \"env\" : [ {\n" +
            "      \"name\" : \"Env1\",\n" +
            "      \"label\" : \"Env1\",\n" +
            "      \"default\" : \"environmentvariable\"\n" +
            "    }, {\n" +
            "      \"name\" : \"Env2\",\n" +
            "      \"label\" : \"Env2\",\n" +
            "      \"default\" : \"environmentvariable2\"\n" +
            "    } ],\n" +
            "    \"registryUser\" : {\n" +
            "      \"comment\" : \"IDS APPSTORE USER: user\",\n" +
            "      \"password\" : \"pass\",\n" +
            "      \"username\" : \"user\"\n" +
            "    }\n" +
            "  }";

    @Autowired
    public PortainerRequestService portainerRequestService;

    @MockBean
    public HttpService httpService;

    @MockBean
    public PortainerConfig portainerConfig;

    private MockWebServer mockWebServer;

    @BeforeEach
    public void setupPortainerMock() throws IOException {

        mockWebServer = new MockWebServer();
        mockWebServer.start();
        Mockito.when(portainerConfig.getPortainerHost()).thenReturn(mockWebServer.getHostName());
        Mockito.when(portainerConfig.getPortainerPort()).thenReturn(mockWebServer.getPort());
        Mockito.when(portainerConfig.getPortainerUser()).thenReturn("user");
        Mockito.when(portainerConfig.getPortainerPassword()).thenReturn("pass");
    }

    @Test
    public void testPortainer() throws Exception {
        var mockResponse = new MockResponse()
                .setResponseCode(200)
                .setBody(RESPONSE_STRING);
        var arrayResponse = new MockResponse()
                .setResponseCode(200)
                .setBody("[]");
        for(int i = 0; i < 42; i++) {
            mockWebServer.enqueue(mockResponse);
        }
        mockWebServer.enqueue(arrayResponse);
        mockWebServer.enqueue(mockResponse);
        mockWebServer.enqueue(mockResponse);

        Assertions.assertEquals(RESPONSE_STRING, portainerRequestService.authenticate());
        Assertions.assertEquals(200, portainerRequestService.startContainer("id").code());
        Assertions.assertEquals(200, portainerRequestService.stopContainer("id").code());
        Assertions.assertEquals(200, portainerRequestService.deleteContainer("id").code());
        Assertions.assertEquals(200, portainerRequestService.getDescriptionByContainerId("id").code());
        Assertions.assertEquals(200, portainerRequestService.getRegistries().code());
        Assertions.assertEquals(200, portainerRequestService.getContainers().code());
        Assertions.assertEquals(200, portainerRequestService.getImages().code());
        Assertions.assertEquals(200, portainerRequestService.getNetworks().code());
        Assertions.assertEquals(200, portainerRequestService.getVolumes().code());
        Assertions.assertEquals(200, portainerRequestService.deleteImage("").code());
        Assertions.assertEquals(200, portainerRequestService.deleteNetwork("").code());
        Assertions.assertEquals(200, portainerRequestService.deleteVolume("").code());
        Assertions.assertEquals(200, portainerRequestService.pullImage(TEMPLATE).code());
        Assertions.assertEquals(200, portainerRequestService.disconnectContainerFromNetwork("id", "id", true).code());
        Assertions.assertDoesNotThrow(() -> portainerRequestService.deleteRegistry(1));
        Assertions.assertDoesNotThrow(() -> portainerRequestService.deleteUnusedVolumes());
        Assertions.assertDoesNotThrow(() -> portainerRequestService.createVolumes(TEMPLATE, "id"));
        Assertions.assertEquals("1", portainerRequestService.createContainer(TEMPLATE, Map.of()));
        Assertions.assertEquals(1, portainerRequestService.createRegistry(TEMPLATE));
    }

    @Test
    public void testPortainer_ArrayResponse() throws Exception {
        var mockResponse = new MockResponse()
                .setResponseCode(200)
                .setBody(ARRAY_RESPONSE);
        var objResponse = new MockResponse()
                .setResponseCode(200)
                .setBody(RESPONSE_STRING);
        for(int i = 0; i < 9; i++) {
            mockWebServer.enqueue(mockResponse);
        }
        mockWebServer.enqueue(objResponse);

        Assertions.assertTrue(portainerRequestService.validateContainerRunning("1"));
        Assertions.assertEquals(1, portainerRequestService.registryExists("https://someurl"));
        Assertions.assertDoesNotThrow(() -> portainerRequestService.createEndpointId());
        Assertions.assertEquals("1", portainerRequestService.getNetworkId("testdata"));
    }

}
