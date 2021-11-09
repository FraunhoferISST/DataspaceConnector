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
package io.dataspaceconnector.controller.resource.type;

import io.dataspaceconnector.common.exception.PortainerNotConfigured;
import io.dataspaceconnector.controller.resource.base.exception.MethodNotAllowed;
import io.dataspaceconnector.model.app.AppDesc;
import io.dataspaceconnector.model.app.AppImpl;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.service.AppRouteResolver;
import io.dataspaceconnector.service.appstore.portainer.PortainerRequestService;
import io.dataspaceconnector.service.resource.type.AppService;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
class AppControllerTest {

    @MockBean
    private AppService appService;

    @MockBean
    private PortainerRequestService portainerRequestService;

    @MockBean
    private AppRouteResolver appRouteResolver;

    @Autowired
    private AppController appController;

    @BeforeEach
    public void prepare() throws IOException, NoSuchFieldException, IllegalAccessException {
        //prepare mocked app
        var returnedApp = new AppImpl();
        var idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(returnedApp, UUID.randomUUID());
        var containerField = AppImpl.class.getDeclaredField("containerId");
        containerField.setAccessible(true);
        containerField.set(returnedApp, "mocked");

        //prepare mock response
        var returnedResponse = createResponseWithCode(200);

        //prepare mockito
        Mockito.when(appService.get(Mockito.any(UUID.class))).thenReturn(returnedApp);
        Mockito.when(appService.getDataFromInternalDB(Mockito.any())).thenReturn(InputStream.nullInputStream());
        Mockito.when(portainerRequestService.createRegistry(Mockito.any())).thenReturn(1);
        Mockito.when(portainerRequestService.startContainer(Mockito.any())).thenReturn(returnedResponse);
        Mockito.when(portainerRequestService.pullImage(Mockito.any())).thenReturn(returnedResponse);
        Mockito.when(portainerRequestService.createVolumes(Mockito.any(), Mockito.any())).thenReturn(Map.of());
        Mockito.when(portainerRequestService.createContainer(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn("Mocked");
        Mockito.when(portainerRequestService.getNetworkId(Mockito.any())).thenReturn("NetworkID");
        Mockito.when(portainerRequestService.joinNetwork(Mockito.any(), Mockito.any())).thenReturn(returnedResponse);
        Mockito.when(portainerRequestService.stopContainer(Mockito.any())).thenReturn(returnedResponse);
        Mockito.when(portainerRequestService.deleteContainer(Mockito.any())).thenReturn(returnedResponse);
        Mockito.doNothing().when(portainerRequestService).deleteRegistry(Mockito.any());
        Mockito.doNothing().when(appService).setContainerIdForApp(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(appService).deleteContainerIdFromApp(Mockito.any());
        Mockito.when(portainerRequestService.validateContainerRunning(Mockito.any())).thenReturn(false);
        Mockito.when(portainerRequestService.getDescriptionByContainerId(Mockito.any())).thenReturn(returnedResponse);
    }

    @Test
    public void createAppWithNull_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(MethodNotAllowed.class, () -> appController.create(null));
    }

    @Test
    public void createApp_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(MethodNotAllowed.class, () -> appController.create(new AppDesc()));
    }

    @Test
    public void updateApp_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(MethodNotAllowed.class, () -> appController.update(null, null));
    }

    @Test
    public void testContainerManagement() {
        assertEquals(HttpStatus.OK, appController.containerManagement(UUID.randomUUID(), "START").getStatusCode());
        assertEquals(HttpStatus.OK, appController.containerManagement(UUID.randomUUID(), "STOP").getStatusCode());
        assertEquals(HttpStatus.OK, appController.containerManagement(UUID.randomUUID(), "DELETE").getStatusCode());
        assertEquals(HttpStatus.OK, appController.containerManagement(UUID.randomUUID(), "DESCRIBE").getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, appController.containerManagement(UUID.randomUUID(), "TEST").getStatusCode());
    }

    @Test
    public void testContainerManagementExceptions() throws PortainerNotConfigured, IOException {
        doThrow(new PortainerNotConfigured()).when(portainerRequestService).createEndpointId();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, appController.containerManagement(UUID.randomUUID(),
                "START").getStatusCode());

        doThrow(new IOException()).when(portainerRequestService).createEndpointId();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, appController.containerManagement(UUID.randomUUID(),
                "START").getStatusCode());


    }

    @Test
    public void testDeployApp() throws NoSuchFieldException, IllegalAccessException {
        //prepare mocked app with container field null
        var returnedApp = new AppImpl();
        var idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(returnedApp, UUID.randomUUID());
        var containerField = AppImpl.class.getDeclaredField("containerId");
        containerField.setAccessible(true);
        containerField.set(returnedApp, null);

        Mockito.when(appService.get(Mockito.any(UUID.class))).thenReturn(returnedApp);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, appController.containerManagement(UUID.randomUUID(),
                "START").getStatusCode());
    }

    @Test
    public void testReadResponse() throws IOException {
        //prepare mock response
        var returnedNotModifiedResponse = createResponseWithCode(304);
        var returnedNotFoundResponse = createResponseWithCode(404);
        var returnedBadRequestResponse = createResponseWithCode(400);
        var returnedConflictResponse = createResponseWithCode(409);
        var returnedUnauthorizedResponse = createResponseWithCode(401);


        Mockito.when(portainerRequestService.startContainer(Mockito.any()))
                .thenReturn(returnedNotFoundResponse);
        assertEquals(HttpStatus.BAD_REQUEST,
                appController.containerManagement(UUID.randomUUID(),
                        "START").getStatusCode());

        Mockito.when(portainerRequestService.startContainer(Mockito.any()))
                .thenReturn(returnedNotModifiedResponse);
        assertEquals(HttpStatus.BAD_REQUEST,
                appController.containerManagement(UUID.randomUUID(),
                        "START").getStatusCode());

        Mockito.when(portainerRequestService.startContainer(Mockito.any()))
                .thenReturn(returnedBadRequestResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                appController.containerManagement(UUID.randomUUID(),
                        "START").getStatusCode());

        Mockito.when(portainerRequestService.startContainer(Mockito.any()))
                .thenReturn(returnedConflictResponse);
        assertEquals(HttpStatus.BAD_REQUEST,
                appController.containerManagement(UUID.randomUUID(),
                        "START").getStatusCode());

        Mockito.when(portainerRequestService.startContainer(Mockito.any()))
                .thenReturn(returnedUnauthorizedResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                appController.containerManagement(UUID.randomUUID(),
                        "START").getStatusCode());


    }

    private Response createResponseWithCode(int statusCode) {
        return new Response.Builder()
                .request(new Request.Builder().url("https://test").get().build())
                .code(statusCode)
                .body(ResponseBody.create("a", MediaType.parse("text/plain")))
                .protocol(Protocol.HTTP_1_1)
                .message("Success mocked!")
                .build();
    }


}
