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
package io.dataspaceconnector.service.resource.relation;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.net.ApiReferenceHelper;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.artifact.Data;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.model.artifact.RemoteData;
import io.dataspaceconnector.model.configuration.DeployMethod;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.service.resource.type.RouteService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ArtifactRouteService.class})
class ArtifactRouteServiceTest {

    @MockBean
    private RouteService routeService;

    @MockBean
    private ApiReferenceHelper apiReferenceHelper;

    @Autowired
    private ArtifactRouteService artifactRouteService;

    private final UUID routeId = UUID.randomUUID();

    private final UUID artifactId = UUID.randomUUID();

    @Test
    @SneakyThrows
    void ensureSingleArtifactPerRoute_noOutputSet_doNothing() {
        /* ARRANGE */
        final var route = getRoute(null);
        final var url = new URL("https://" + routeId);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(true);
        when(routeService.get(any())).thenReturn(route);

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> artifactRouteService.ensureSingleArtifactPerRoute(url, artifactId));
    }

    @Test
    @SneakyThrows
    void ensureSingleArtifactPerRoute_outputAlreadySet_throwInvalidEntityException() {
        /* ARRANGE */
        final var artifact = getArtifact();
        final var route = getRoute(artifact);
        final var url = new URL("https://" + routeId);
        final var newArtifactId = UUID.randomUUID();

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(true);
        when(routeService.get(any())).thenReturn(route);

        /* ACT && ASSERT */
        assertThrows(InvalidEntityException.class,
                () -> artifactRouteService.ensureSingleArtifactPerRoute(url, newArtifactId));
    }

    @Test
    @SneakyThrows
    void ensureSingleArtifactPerRoute_malformedUri_throwInvalidEntityException() {
        /* ARRANGE */
        final var url = new URL("https://{not-allowed-in-uri}" + routeId);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(true);

        /* ACT && ASSERT */
        assertThrows(InvalidEntityException.class,
                () -> artifactRouteService.ensureSingleArtifactPerRoute(url, artifactId));
    }

    @Test
    @SneakyThrows
    void ensureSingleArtifactPerRoute_routeNotFound_throwInvalidEntityException() {
        /* ARRANGE */
        final var url = new URL("https://" + routeId);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(true);
        when(routeService.get(any())).thenThrow(ResourceNotFoundException.class);

        /* ACT && ASSERT */
        assertThrows(InvalidEntityException.class,
                () -> artifactRouteService.ensureSingleArtifactPerRoute(url, artifactId));
    }

    @Test
    @SneakyThrows
    void checkForValidRoute_urlNotRoute_doNothing() {
        /* ARRANGE */
        final var url = new URL("https://" + routeId);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(false);

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> artifactRouteService.checkForValidRoute(url));
    }

    @Test
    @SneakyThrows
    void checkForValidRoute_deployMethodNotCamel_throwInvalidEntityException() {
        /* ARRANGE */
        final var route = getRoute(null);
        ReflectionTestUtils.setField(route, "deploy", DeployMethod.NONE);
        final var url = new URL("https://" + routeId);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(true);
        when(routeService.get(any())).thenReturn(route);

        /* ACT && ASSERT */
        assertThrows(InvalidEntityException.class,
                () -> artifactRouteService.checkForValidRoute(url));
    }

    @Test
    @SneakyThrows
    void checkForValidRoute_malformedUri_throwInvalidEntityException() {
        /* ARRANGE */
        final var url = new URL("https://{not-allowed-in-uri}" + routeId);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(true);

        /* ACT && ASSERT */
        assertThrows(InvalidEntityException.class,
                () -> artifactRouteService.checkForValidRoute(url));
    }

    @Test
    @SneakyThrows
    void checkForValidRoute_startUndefined_throwInvalidEntityException() {
        /* ARRANGE */
        final var route = getRoute(null);
        ReflectionTestUtils.setField(route, "deploy", DeployMethod.CAMEL);
        final var url = new URL("https://" + routeId);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(true);
        when(routeService.get(any())).thenReturn(route);

        /* ACT && ASSERT */
        assertThrows(InvalidEntityException.class,
                () -> artifactRouteService.checkForValidRoute(url));
    }

    @Test
    @SneakyThrows
    void createRouteLink_urlNotRoute_doNothing() {
        /* ARRANGE */
        final var artifact = getArtifact();
        final var url = new URL("https://" + routeId);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(false);

        /* ACT */
        artifactRouteService.createRouteLink(url, artifact);

        /* ASSERT */
        verify(routeService, never()).setOutput(any(), any());
    }

    @Test
    @SneakyThrows
    void createRouteLink_validInput_setOutput() {
        /* ARRANGE */
        final var artifact = getArtifact();
        final var route = getRoute(null);
        ReflectionTestUtils.setField(route, "deploy", DeployMethod.CAMEL);
        final var url = new URL("https://" + routeId);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(true);
        when(routeService.getByOutput(any())).thenReturn(null);
        when(routeService.get(any())).thenReturn(route);

        /* ACT */
        artifactRouteService.createRouteLink(url, artifact);

        /* ASSERT */
        verify(routeService, times(1)).setOutput(routeId, artifactId);
    }

    @Test
    @SneakyThrows
    void createRouteLink_malformedUri_throwInvalidEntityException() {
        /* ARRANGE */
        final var artifact = getArtifact();
        final var url = new URL("https://{not-allowed-in-uri}" + routeId);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(true);
        when(routeService.getByOutput(any())).thenReturn(null);

        /* ACT && ASSERT */
        assertThrows(InvalidEntityException.class,
                () -> artifactRouteService.createRouteLink(url, artifact));
    }

    @Test
    @SneakyThrows
    void checkForRouteLinkUpdate_routeLinkedAndNewUrlDifferentRoute_removeOutput() {
        /* ARRANGE */
        final var artifact = getArtifact();
        final var route = getRoute(artifact);
        final var newRouteId = UUID.randomUUID();
        final var url = new URL("https://" + newRouteId);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(true);
        when(routeService.getByOutput(any())).thenReturn(route);

        /* ACT */
        artifactRouteService.createRouteLink(url, artifact);

        /* ASSERT */
        verify(routeService, times(1)).removeOutput(routeId);
        verify(routeService, times(1)).setOutput(newRouteId, artifactId);
    }


    @Test
    void getAssociatedRoute_noRouteAssociated_returnNull() {
        /* ARRANGE */
        final var artifact = getArtifact();
        when(routeService.getByOutput(any())).thenReturn(null);

        /* ACT */
        final var result = artifactRouteService.getAssociatedRoute(artifact);

        /* ASSERT */
        assertNull(result);
    }

    @Test
    void getAssociatedRoute_routeAssociated_returnNull() {
        /* ARRANGE */
        final var artifact = getArtifact();
        final var route = getRoute(artifact);
        when(routeService.getByOutput(any())).thenReturn(route);

        /* ACT */
        final var result = artifactRouteService.getAssociatedRoute(artifact);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(route, result);
    }

    @Test
    void removeRouteLink_localData_doNothing() {
        /* ARRANGE */
        final var data = getLocalData();
        final var artifact = getArtifact(data);

        /* ACT */
        artifactRouteService.removeRouteLink(artifact);

        /* ASSERT */
        verify(routeService, never()).removeOutput(any());
    }

    @Test
    @SneakyThrows
    void removeRouteLink_urlNotRoute_doNothing() {
        /* ARRANGE */
        final var data = getRemoteData();
        final var artifact = getArtifact(data);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(false);

        /* ACT */
        artifactRouteService.removeRouteLink(artifact);

        /* ASSERT */
        verify(routeService, never()).removeOutput(any());
    }

    @Test
    @SneakyThrows
    void removeRouteLink_malformedUri_doNothing() {
        /* ARRANGE */
        final var url = new URL("https://{not-allowed-in-uri}" + routeId);
        final var data = getRemoteData();
        ReflectionTestUtils.setField(data, "accessUrl", url);
        final var artifact = getArtifact(data);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(true);

        /* ACT */
        artifactRouteService.removeRouteLink(artifact);

        /* ASSERT */
        verify(routeService, never()).removeOutput(any());
    }

    @Test
    @SneakyThrows
    void removeRouteLink_urlRoute_removeOutput() {
        /* ARRANGE */
        final var data = getRemoteData();
        final var artifact = getArtifact(data);

        when(apiReferenceHelper.isRouteReference(any())).thenReturn(true);

        /* ACT */
        artifactRouteService.removeRouteLink(artifact);

        /* ASSERT */
        verify(routeService, times(1)).removeOutput(routeId);
    }

    private Route getRoute(final Artifact output) {
        final var route = new Route();
        ReflectionTestUtils.setField(route, "id", routeId);
        ReflectionTestUtils.setField(route, "output", output);
        return route;
    }

    private ArtifactImpl getArtifact() {
        final var artifact = new ArtifactImpl();
        ReflectionTestUtils.setField(artifact, "id", artifactId);
        return artifact;
    }

    private ArtifactImpl getArtifact(final Data data) {
        final var artifact = getArtifact();
        ReflectionTestUtils.setField(artifact, "data", data);
        return artifact;
    }

    private LocalData getLocalData() {
        final var data = new LocalData();
        ReflectionTestUtils.setField(data, "value", "value".getBytes(StandardCharsets.UTF_8));
        return data;
    }

    @SneakyThrows
    private RemoteData getRemoteData() {
        final var data = new RemoteData();
        ReflectionTestUtils.setField(data, "accessUrl", new URL("https://" + routeId));
        return data;
    }
}
