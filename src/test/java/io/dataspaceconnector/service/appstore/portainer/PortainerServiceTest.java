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
import java.util.ArrayList;
import java.util.Map;

@SpringBootTest
class PortainerServiceTest {

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
    public PortainerService portainerService;

    @MockBean
    public HttpService httpService;

    @MockBean
    public PortainerConfig portainerConfig;

    private MockWebServer mockWebServer;

    @BeforeEach
    public void setupPortainerMock() throws IOException {

        mockWebServer = new MockWebServer();
        mockWebServer.start();
        Mockito.when(portainerConfig.getScheme()).thenReturn("http");
        Mockito.when(portainerConfig.getHost()).thenReturn(mockWebServer.getHostName());
        Mockito.when(portainerConfig.getPort()).thenReturn(mockWebServer.getPort());
        Mockito.when(portainerConfig.getUsername()).thenReturn("user");
        Mockito.when(portainerConfig.getPassword()).thenReturn("pass");
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

        Assertions.assertEquals(RESPONSE_STRING, portainerService.authenticate());
        Assertions.assertEquals(200, portainerService.startContainer("id").code());
        Assertions.assertEquals(200, portainerService.stopContainer("id").code());
        Assertions.assertEquals(200, portainerService.deleteContainer("id").code());
        Assertions.assertEquals(200, portainerService.getDescriptionByContainerId("id").code());
        Assertions.assertEquals(200, portainerService.getRegistries().code());
        Assertions.assertEquals(200, portainerService.getContainers().code());
        Assertions.assertEquals(200, portainerService.getImages().code());
        Assertions.assertEquals(200, portainerService.getNetworks().code());
        Assertions.assertEquals(200, portainerService.getVolumes().code());
        Assertions.assertEquals(200, portainerService.deleteImage("").code());
        Assertions.assertEquals(200, portainerService.deleteNetwork("").code());
        Assertions.assertEquals(200, portainerService.deleteVolume("").code());
        Assertions.assertEquals(200, portainerService.pullImage(TEMPLATE).code());
        Assertions.assertEquals(200, portainerService.disconnectContainerFromNetwork("id", "id", true).code());
        Assertions.assertEquals("1", portainerService.createContainer(TEMPLATE, Map.of(),
                new ArrayList<>()));
        Assertions.assertDoesNotThrow(() -> portainerService.deleteRegistry(1));
        Assertions.assertDoesNotThrow(() -> portainerService.deleteUnusedVolumes());
        Assertions.assertDoesNotThrow(() -> portainerService.createVolumes(TEMPLATE, "id"));
    }

    @Test
    public void testPortainer_createRegistry() throws Exception {
        var arrayResponse = new MockResponse().setResponseCode(200).setBody(ARRAY_RESPONSE);
        var objResponse = new MockResponse().setResponseCode(200).setBody(RESPONSE_STRING);

        mockWebServer.enqueue(objResponse);
        mockWebServer.enqueue(arrayResponse);
        mockWebServer.enqueue(objResponse);

        Assertions.assertEquals(1, portainerService.createRegistry(TEMPLATE));
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

        Assertions.assertTrue(portainerService.validateContainerRunning("1"));
        Assertions.assertEquals(1, portainerService.registryExists("https://someurl"));
        Assertions.assertDoesNotThrow(() -> portainerService.createEndpointId());
        Assertions.assertEquals("1", portainerService.getNetworkId("testdata"));
    }
}
