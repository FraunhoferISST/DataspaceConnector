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
package io.dataspaceconnector.service.resource;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.UUID;

import io.dataspaceconnector.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.ArtifactDesc;
import io.dataspaceconnector.model.ArtifactFactory;
import io.dataspaceconnector.model.ArtifactImpl;
import io.dataspaceconnector.model.Data;
import io.dataspaceconnector.model.LocalData;
import io.dataspaceconnector.model.QueryInput;
import io.dataspaceconnector.model.RemoteData;
import io.dataspaceconnector.repository.ArtifactRepository;
import io.dataspaceconnector.repository.AuthenticationRepository;
import io.dataspaceconnector.repository.DataRepository;
import io.dataspaceconnector.service.HttpService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @Autowired
    private ArtifactService service;

    /**************************************************************************
     * persist
     *************************************************************************/

    @Test
    public void persist_dataNull_persistArtifact() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        ArtifactImpl artifact = new ArtifactImpl();
        when(artifactFactory.create(desc)).thenReturn(artifact);
        when(artifactRepository.saveAndFlush(artifact)).thenReturn(artifact);

        /* ACT */
        service.create(desc);

        /* ASSERT */
        verify(artifactRepository, times(1)).saveAndFlush(artifact);
        verify(dataRepository, never()).saveAndFlush(any());
    }

    @SneakyThrows
    @Test
    public void persist_dataIdNull_persistDataAndArtifact() {
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
    public void persist_dataPresentNotChanged_persistArtifact() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        ArtifactImpl artifact = new ArtifactImpl();
        LocalData data = new LocalData();

        Long dataId = 1L;

        final var idField = data.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(data, dataId);

        final var dataField = artifact.getClass().getDeclaredField("data");
        dataField.setAccessible(true);
        dataField.set(artifact, data);

        when(artifactFactory.create(desc)).thenReturn(artifact);
        when(artifactRepository.saveAndFlush(artifact)).thenReturn(artifact);
        when(dataRepository.saveAndFlush(data)).thenReturn(data);
        when(dataRepository.getById(dataId)).thenReturn(data);

        /* ACT */
        service.create(desc);

        /* ASSERT */
        verify(artifactRepository, times(1)).saveAndFlush(artifact);
        verify(dataRepository, never()).saveAndFlush(any());
    }

    @SneakyThrows
    @Test
    public void persist_dataPresentAndChanged_persistDataAndArtifact() {
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
     * getData.
     *************************************************************************/

    @Test
    public void getData_nullArtifactId_throwsIllegalArgumentException() {
        /* ARRANGE */
        final var queryInput = new QueryInput();
        when(artifactRepository.findById(null)).thenThrow(new IllegalArgumentException());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.getData( null,
                null,
                null,
                null,
                queryInput));
    }

    @Test
    public void getData_unknownArtifactIdNullQuery_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
        when(artifactRepository.findById(unknownUuid))
                .thenThrow(new ResourceNotFoundException("not found"));

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> service.getData(null,
                null,
                unknownUuid,
                null,
                (QueryInput) null));
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

        Field accessUrlField = remoteData.getClass().getDeclaredField("accessUrl");
        accessUrlField.setAccessible(true);
        accessUrlField.set(remoteData, new URL("http://some-url.com"));

        return remoteData;
    }

    @SneakyThrows
    private RemoteData getRemoteDataWithBasicAuth() {
        final var remoteData = new RemoteData();

        Field accessUrlField = remoteData.getClass().getDeclaredField("accessUrl");
        accessUrlField.setAccessible(true);
        accessUrlField.set(remoteData, new URL("http://some-url.com"));

        Field usernameField = remoteData.getClass().getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(remoteData, "username");

        Field passwordField = remoteData.getClass().getDeclaredField("password");
        passwordField.setAccessible(true);
        passwordField.set(remoteData, "password");

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
