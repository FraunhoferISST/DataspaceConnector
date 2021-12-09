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
package io.dataspaceconnector.service.resource.type;

import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.exception.UnexpectedResponseException;
import io.dataspaceconnector.common.exception.UnreachableLineException;
import io.dataspaceconnector.common.net.HttpResponse;
import io.dataspaceconnector.common.net.HttpService;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.routing.RouteDataDispatcher;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.artifact.Data;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.model.artifact.RemoteData;
import io.dataspaceconnector.model.auth.Authentication;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.repository.ArtifactRepository;
import io.dataspaceconnector.repository.AuthenticationRepository;
import io.dataspaceconnector.repository.DataRepository;
import io.dataspaceconnector.service.DataRetriever;
import io.dataspaceconnector.service.resource.relation.ArtifactRouteService;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ArtifactService.class, ArtifactFactory.class, ArtifactRepository.class,
        DataRepository.class, AuthenticationRepository.class, HttpService.class})
class ArtifactServiceTest {

    @MockBean
    private ArtifactRepository artifactRepository;

    @MockBean
    private DataRepository dataRepository;

    @MockBean
    private AuthenticationRepository authenticationRepository;

    @MockBean
    private HttpService httpService;

    @MockBean
    private ArtifactFactory artifactFactory;

    @MockBean
    private ArtifactRouteService artifactRouteService;

    @MockBean
    private DataRetriever dataRetriever;

    @MockBean
    private RouteDataDispatcher routeDataDispatcher;

    @Autowired
    private ArtifactService service;

    /**************************************************************************
     * create
     *************************************************************************/

    @Test
    @SneakyThrows
    public void create_dataNull_persistArtifact() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        ArtifactImpl artifact = new ArtifactImpl();
        when(artifactFactory.create(desc)).thenReturn(artifact);
        when(artifactRepository.saveAndFlush(artifact)).thenReturn(artifact);
        when(dataRetriever.retrieveData(eq(artifact), any()))
                .thenReturn(new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)));

        /* ACT */
        service.create(desc);

        /* ASSERT */
        verify(artifactRepository, times(1)).saveAndFlush(artifact);
        verify(dataRepository, never()).saveAndFlush(any());
    }

    @SneakyThrows
    @Test
    public void create_dataIdNull_persistDataAndArtifact() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        ArtifactImpl artifact = new ArtifactImpl();
        LocalData data = new LocalData();

        final var dataField = artifact.getClass().getDeclaredField("data");
        dataField.setAccessible(true);
        dataField.set(artifact, data);

        when(artifactFactory.create(desc)).thenReturn(artifact);
        when(artifactRepository.saveAndFlush(artifact)).thenReturn(artifact);
        when(dataRepository.saveAndFlush(data)).thenReturn(data);

        /* ACT */
        service.create(desc);

        /* ASSERT */
        verify(artifactRepository, times(1)).saveAndFlush(artifact);
        verify(dataRepository, times(1)).saveAndFlush(data);
    }

    @SneakyThrows
    @Test
    public void create_dataPresentAndChanged_persistDataAndArtifact() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();

        // create new data instance
        LocalData data = new LocalData();
        Long dataId = 1L;
        final var idField = data.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(data, dataId);

        // create new artifact instance with previously created data
        ArtifactImpl artifact = new ArtifactImpl();
        final var dataField = artifact.getClass().getDeclaredField("data");
        dataField.setAccessible(true);
        dataField.set(artifact, data);

        // create different data instance that will be the previously persisted data
        LocalData dataOld = new LocalData();
        final var valueField = dataOld.getClass().getDeclaredField("value");
        valueField.setAccessible(true);
        valueField.set(dataOld, "some value".getBytes());

        when(artifactFactory.create(desc)).thenReturn(artifact);
        when(artifactRepository.saveAndFlush(artifact)).thenReturn(artifact);
        when(dataRepository.saveAndFlush(data)).thenReturn(data);
        when(dataRepository.getById(dataId)).thenReturn(dataOld);

        /* ACT */
        service.create(desc);

        /* ASSERT */
        verify(artifactRepository, times(1)).saveAndFlush(artifact);
        verify(dataRepository, times(1)).saveAndFlush(data);
    }

    /**************************************************************************
     * update
     *************************************************************************/

    @Test
    @SneakyThrows
    void update_newLocalData_updateArtifactAndData() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();

        // create new data instance
        LocalData data = new LocalData();
        Long dataId = 1L;
        final var idField = data.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(data, dataId);

        // create new artifact instance with previously created data
        ArtifactImpl artifact = new ArtifactImpl();
        final var dataField = artifact.getClass().getDeclaredField("data");
        dataField.setAccessible(true);
        dataField.set(artifact, data);

        // create different data instance that will be the previously persisted data
        LocalData dataOld = new LocalData();
        final var valueField = dataOld.getClass().getDeclaredField("value");
        valueField.setAccessible(true);
        valueField.set(dataOld, "some value".getBytes());

        when(artifactRepository.findById(any())).thenReturn(Optional.of(artifact));
        when(artifactFactory.update(artifact, desc)).thenReturn(true);
        when(artifactRepository.saveAndFlush(artifact)).thenReturn(artifact);
        when(dataRepository.saveAndFlush(data)).thenReturn(data);
        when(dataRepository.getById(dataId)).thenReturn(dataOld);

        /* ACT */
        service.update(UUID.randomUUID(), desc);

        /* ASSERT */
        verify(artifactRepository, times(1)).saveAndFlush(artifact);
        verify(dataRepository, times(1)).saveAndFlush(data);
    }

    @Test
    @SneakyThrows
    void update_newRouteLinkAndRouteCannotBeCreated_revertChanges() {
        final var desc = new ArtifactDesc();
        final var artifactId = UUID.randomUUID();

        // create new data instance
        RemoteData data = new RemoteData();
        Long dataId = 1L;
        final var idField = data.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(data, dataId);

        // create new artifact instance with previously created data
        ArtifactImpl artifact = new ArtifactImpl();
        final var dataField = artifact.getClass().getDeclaredField("data");
        dataField.setAccessible(true);
        dataField.set(artifact, data);

        // create different data instance that will be the previously persisted data
        RemoteData dataOld = new RemoteData();

        when(artifactRepository.findById(any())).thenReturn(Optional.of(artifact));
        when(artifactFactory.update(artifact, desc)).thenReturn(true);
        when(artifactRepository.saveAndFlush(artifact)).thenReturn(artifact);
        when(dataRepository.saveAndFlush(data)).thenReturn(data);
        when(dataRepository.getById(dataId)).thenReturn(dataOld);
        doThrow(InvalidEntityException.class)
                .when(artifactRouteService).createRouteLink(any(), any());

        /* ACT && ASSERT */
        assertThrows(InvalidEntityException.class, () -> service.update(artifactId, desc));

        verify(artifactRepository, times(1)).saveAndFlush(artifact);
        verify(dataRepository, times(1)).saveAndFlush(data);
        verify(dataRepository, times(1)).saveAndFlush(dataOld);
    }

    /**************************************************************************
     * getData.
     *************************************************************************/

    @Test
    public void getData_nullArtifactId_throwsIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        when(artifactRepository.findById(null)).thenThrow(new IllegalArgumentException());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.getData(null,
                null, null, queryInput, null));
    }

    @Test
    public void getData_unknownArtifactIdNullQuery_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
        when(artifactRepository.findById(unknownUuid))
                .thenThrow(new ResourceNotFoundException("not found"));

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> service.getData(null,
                null, unknownUuid, (QueryInput) null, null));
    }

    @Test
    public void getData_knownArtifactIdNullQuery_returnLocalData() throws IOException, UnexpectedResponseException {
        /* ARRANGE */
        ArtifactImpl localArtifact = getLocalArtifact();

        when(artifactRepository.findById(any())).thenReturn(Optional.of(localArtifact));
        when(artifactFactory.create(any())).thenReturn(localArtifact);
        when(dataRepository.getById(any())).thenReturn(getLocalData());
        when(dataRetriever.retrieveData(eq(localArtifact), any()))
                .thenReturn(new ByteArrayInputStream(getLocalData().getValue()));

        /* ACT */
        final var data = service.getData(null,
                                                    null,
                                                    localArtifact.getId(),
                                                    (QueryInput) null,
                                                    null);

        /* ASSERT */
        assertTrue(Arrays.compare(getLocalData().getValue(), IOUtils.toByteArray(data)) == 0);
    }

    @SneakyThrows
    @Test
    public void getData_knownArtifactIdNullQuery_increaseAccessCounter() {
        /* ARRANGE */
        ArtifactImpl localArtifact = getLocalArtifact();

        when(artifactRepository.findById(any())).thenReturn(Optional.of(localArtifact));
        when(artifactFactory.create(any())).thenReturn(localArtifact);
        when(dataRepository.getById(any())).thenReturn(getLocalData());
        when(dataRetriever.retrieveData(eq(localArtifact), any()))
                .thenReturn(new ByteArrayInputStream(getLocalData().getValue()));

        final var before = localArtifact.getNumAccessed();

        /* ACT */
        service.getData(null, null, localArtifact.getId(), (QueryInput) null, null);

        /* ASSERT */
        Field numAccessedField = Artifact.class.getDeclaredField("numAccessed");
        numAccessedField.setAccessible(true);
        numAccessedField.set(localArtifact, before + 1);

        verify(artifactRepository, times(1)).saveAndFlush(localArtifact);
    }

    @SneakyThrows
    @Test
    public void getData_knownArtifactIdBasicAuthNullQuery_returnRemoteData() {
        /* ARRANGE */
        final var remoteData = "I am data from a remote source.".getBytes(StandardCharsets.UTF_8);
        ArtifactImpl remoteArtifact = getRemoteArtifact(getRemoteDataWithBasicAuth());
        URL url = ((RemoteData) remoteArtifact.getData()).getAccessUrl();
        final var auth = ((RemoteData) remoteArtifact.getData()).getAuthentication();

        final var response = new HttpResponse(200, new ByteArrayInputStream(remoteData));

        when(artifactRepository.findById(remoteArtifact.getId()))
                .thenReturn(Optional.of(remoteArtifact));
        when(httpService.get(url, null, auth)).thenReturn(response);
        when(artifactRepository.saveAndFlush(any())).thenAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            return args[0];
        });
        when(dataRetriever.retrieveData(any(), any()))
                .thenReturn(response.getData());

        /* ACT */
        final var data = service.getData(null,
                                                    null,
                                                    remoteArtifact.getId(),
                                                    (QueryInput)  null,
                                                    null);

        /* ASSERT */
        assertEquals(response.getData(), data);
    }

    @SneakyThrows
    @Test
    public void getData_knownArtifactIdNoBasicAuthNullQuery_returnRemoteData() {
        /* ARRANGE */
        final var remoteData = "I am data from a remote source.".getBytes(StandardCharsets.UTF_8);
        ArtifactImpl remoteArtifact = getRemoteArtifact(getRemoteData());
        URL url = ((RemoteData) remoteArtifact.getData()).getAccessUrl();

        final var response = new HttpResponse(200, new ByteArrayInputStream(remoteData));

        when(artifactRepository.findById(remoteArtifact.getId()))
                .thenReturn(Optional.of(remoteArtifact));
        when(httpService.get(url, (QueryInput) null)).thenReturn(response);
        when(artifactRepository.saveAndFlush(any())).thenAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            return args[0];
        });
        when(dataRetriever.retrieveData(any(), any()))
                .thenReturn(response.getData());

        /* ACT */
        final var data = service.getData(null, null,
                remoteArtifact.getId(),(QueryInput) null, null);

        /* ASSERT */
        assertEquals(response.getData(), data);
    }

    @SneakyThrows
    @Test
    public void getData_knownArtifactIdNoBasicAuthWithQuery_returnRemoteData() {
        /* ARRANGE */
        final var remoteData = "I am data from a remote source.".getBytes(StandardCharsets.UTF_8);
        ArtifactImpl remoteArtifact = getRemoteArtifact(getRemoteData());
        URL url = ((RemoteData) remoteArtifact.getData()).getAccessUrl();
        QueryInput queryInput = getQueryInput();

        final var response = new HttpResponse(200, new ByteArrayInputStream(remoteData));

        when(artifactRepository.findById(remoteArtifact.getId()))
                .thenReturn(Optional.of(remoteArtifact));
        when(httpService.get(url, queryInput)).thenReturn(response);
        when(artifactRepository.saveAndFlush(any())).thenAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            return args[0];
        });
        when(dataRetriever.retrieveData(eq(remoteArtifact), any()))
                .thenReturn(response.getData());

        /* ACT */
        final var data = service.getData(null, null,
                                         remoteArtifact.getId(), queryInput, null);

        /* ASSERT */
        assertEquals(response.getData(), data);
    }

    @SneakyThrows
    @Test
    public void getData_knownArtifactIdNoBasicAuthWithQuery_throwIOException() {
        /* ARRANGE */
        String expectedExceptionMessage = "Could not connect to data source.";
        ArtifactImpl remoteArtifact = getRemoteArtifact(getRemoteData());
        URL url = ((RemoteData) remoteArtifact.getData()).getAccessUrl();
        QueryInput queryInput = getQueryInput();

        when(artifactRepository.findById(remoteArtifact.getId()))
                .thenReturn(Optional.of(remoteArtifact));
        when(httpService.get(url, queryInput))
                .thenThrow(new RuntimeException(expectedExceptionMessage));

        /* ACT && ASSERT */
        assertThrows(RuntimeException.class, () -> service.getData(null, null,
                                                                   remoteArtifact.getId(),
                                                                   queryInput, null),
                expectedExceptionMessage);
    }

    @SneakyThrows
    @Test
    public void getData_unknownDataType_throwUnreachableLineException() {
        /* ARRANGE */
        ArtifactImpl unknownArtifact = getUnknownArtifact();
        final var dataField = unknownArtifact.getClass().getDeclaredField("data");
        dataField.setAccessible(true);
        dataField.set(unknownArtifact, new UnknownData());

        when(artifactRepository.findById(unknownArtifact.getId()))
                .thenReturn(Optional.of(unknownArtifact));
        when(dataRetriever.retrieveData(any(), any())).thenThrow(UnreachableLineException.class);

        /* ACT && ASSERT */
        assertThrows(UnreachableLineException.class,
                     () -> service.getData(null, null, unknownArtifact.getId(), (QueryInput) null,
                             null));
    }

    @Test
    public void getAllByAgreement_validUuid_returnList() {
        /* ARRANGE */
        final var uuid = UUID.randomUUID();
        Mockito.doReturn(List.of(getLocalArtifact())).when(artifactRepository).findAllByAgreement(eq(uuid));

        /* ACT */
        final var result = service.getAllByAgreement(uuid);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(getLocalArtifact(), result.get(0));
    }

    @Test
    public void identifyByRemoteId_validUuid_returnOptional() {
        /* ARRANGE */
        final var uuid = UUID.randomUUID();
        final var remoteId = URI.create("https://artifact");
        Mockito.doReturn(Optional.of(uuid)).when(artifactRepository).identifyByRemoteId(eq(remoteId));

        /* ACT */
        final var result = service.identifyByRemoteId(remoteId);

        /* ASSERT */
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(uuid, result.get());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private ArtifactDesc getLocalArtifactDesc() {
        final var desc = new ArtifactDesc();
        desc.setTitle("LocalArtifact");
        desc.setValue("Random Value");

        return desc;
    }

    @SneakyThrows
    private ArtifactImpl getLocalArtifact() {
        final var artifactConstructor = ArtifactImpl.class.getConstructor();
        artifactConstructor.setAccessible(true);

        final var artifact = artifactConstructor.newInstance();

        final var titleField = artifact.getClass().getSuperclass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(artifact, "LocalArtifact");

        final var idField =
                artifact.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(artifact, UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        final var dataField = artifact.getClass().getDeclaredField("data");
        dataField.setAccessible(true);
        dataField.set(artifact, getLocalData());

        return artifact;
    }

    @SneakyThrows
    private ArtifactImpl getUnknownArtifact() {
        final var artifactConstructor = ArtifactImpl.class.getConstructor();
        artifactConstructor.setAccessible(true);

        final var artifact = artifactConstructor.newInstance();

        final var titleField = artifact.getClass().getSuperclass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(artifact, "LocalArtifact");

        final var idField =
                artifact.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(artifact, UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        return artifact;
    }

    @SneakyThrows
    private LocalData getLocalData() {
        final var dataConstructor = LocalData.class.getConstructor();
        dataConstructor.setAccessible(true);

        final var localData = dataConstructor.newInstance();

        final var idField = localData.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(localData, Long.valueOf(1));

        final var valueField = localData.getClass().getDeclaredField("value");
        valueField.setAccessible(true);
        valueField.set(localData, getLocalArtifactDesc().getValue().getBytes());

        return localData;
    }

    @SneakyThrows
    private RemoteData getRemoteData() {
        final var remoteData = new RemoteData();
        ReflectionTestUtils.setField(remoteData, "authentication", new ArrayList<Authentication>());
        ReflectionTestUtils.setField(remoteData, "accessUrl",  new URL("http://some-url.com"));
        return remoteData;
    }

    @SneakyThrows
    private RemoteData getRemoteDataWithBasicAuth() {
        final var remoteData = new RemoteData();
        ReflectionTestUtils.setField(remoteData, "authentication", List
                .of(new BasicAuth("username", "password")));
        ReflectionTestUtils.setField(remoteData, "accessUrl",  new URL("http://some-url.com"));

        return remoteData;
    }

    @SneakyThrows
    private ArtifactImpl getRemoteArtifact(RemoteData remoteData) {
        final var artifactConstructor = ArtifactImpl.class.getConstructor();
        artifactConstructor.setAccessible(true);

        final var artifact = artifactConstructor.newInstance();

        final var titleField = artifact.getClass().getSuperclass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(artifact, "RemoteArtifact");

        final var idField =
                artifact.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(artifact, UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        final var dataField = artifact.getClass().getDeclaredField("data");
        dataField.setAccessible(true);
        dataField.set(artifact, remoteData);

        return artifact;
    }

     private QueryInput getQueryInput() {
         QueryInput queryInput = new QueryInput();
         queryInput.getParams().put("paramName", "paramValue");
         return queryInput;
     }

     private class UnknownData extends Data {
         /**
          * Default serial version uid.
          */
         private static final long serialVersionUID = 1L;
     }
}
