//package de.fraunhofer.isst.dataspaceconnector.services.resources;
//
//import java.lang.reflect.Field;
//import java.net.URISyntaxException;
//import java.net.URL;
//import java.util.Optional;
//import java.util.UUID;
//
//import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
//import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;
//import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
//import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
//import de.fraunhofer.isst.dataspaceconnector.model.ArtifactFactory;
//import de.fraunhofer.isst.dataspaceconnector.model.ArtifactImpl;
//import de.fraunhofer.isst.dataspaceconnector.model.Data;
//import de.fraunhofer.isst.dataspaceconnector.model.LocalData;
//import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
//import de.fraunhofer.isst.dataspaceconnector.model.RemoteData;
//import de.fraunhofer.isst.dataspaceconnector.repositories.ArtifactRepository;
//import de.fraunhofer.isst.dataspaceconnector.repositories.DataRepository;
//import de.fraunhofer.isst.dataspaceconnector.services.HttpService;
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest(classes = {ArtifactService.class, ArtifactFactory.class, ArtifactRepository.class,
//        DataRepository.class, HttpService.class})
//class ArtifactServiceTest {
//
//    @MockBean
//    private ArtifactRepository artifactRepository;
//
//    @MockBean
//    private DataRepository dataRepository;
//
//    @MockBean
//    private HttpService httpService;
//
//    @MockBean
//    private ArtifactFactory artifactFactory;
//
//    @Autowired
//    @InjectMocks
//    private ArtifactService service;
//
//    Artifact localArtifact = getLocalArtifact();
//
//    @BeforeEach
//    public void init() {
//
//    }
//
//    /**************************************************************************
//     * persist
//     *************************************************************************/
//
//    @Test
//    public void persist_dataNull_persistArtifact() {
//        /* ARRANGE */
//        ArtifactImpl artifact = new ArtifactImpl();
//        when(artifactRepository.saveAndFlush(artifact)).thenReturn(artifact);
//
//        /* ACT */
//        service.persist(artifact);
//
//        /* ASSERT */
//        verify(artifactRepository, times(1)).saveAndFlush(artifact);
//        verify(dataRepository, never()).saveAndFlush(any());
//    }
//
//    @SneakyThrows
//    @Test
//    public void persist_dataIdNull_persistDataAndArtifact() {
//        /* ARRANGE */
//        ArtifactImpl artifact = new ArtifactImpl();
//        LocalData data = new LocalData();
//
//        final var dataField = artifact.getClass().getDeclaredField("data");
//        dataField.setAccessible(true);
//        dataField.set(artifact, data);
//
//        when(artifactRepository.saveAndFlush(artifact)).thenReturn(artifact);
//        when(dataRepository.saveAndFlush(data)).thenReturn(data);
//
//        /* ACT */
//        service.persist(artifact);
//
//        /* ASSERT */
//        verify(artifactRepository, times(1)).saveAndFlush(artifact);
//        verify(dataRepository, times(1)).saveAndFlush(data);
//    }
//
//    @SneakyThrows
//    @Test
//    public void persist_dataPresentNotChanged_persistArtifact() {
//        /* ARRANGE */
//        ArtifactImpl artifact = new ArtifactImpl();
//        LocalData data = new LocalData();
//
//        Long dataId = 1L;
//
//        final var idField = data.getClass().getSuperclass().getDeclaredField("id");
//        idField.setAccessible(true);
//        idField.set(data, dataId);
//
//        final var dataField = artifact.getClass().getDeclaredField("data");
//        dataField.setAccessible(true);
//        dataField.set(artifact, data);
//
//        when(artifactRepository.saveAndFlush(artifact)).thenReturn(artifact);
//        when(dataRepository.saveAndFlush(data)).thenReturn(data);
//        when(dataRepository.getOne(dataId)).thenReturn(data);
//
//        /* ACT */
//        service.persist(artifact);
//
//        /* ASSERT */
//        verify(artifactRepository, times(1)).saveAndFlush(artifact);
//        verify(dataRepository, never()).saveAndFlush(any());
//    }
//
//    @SneakyThrows
//    @Test
//    public void persist_dataPresentAndChanged_persistDataAndArtifact() {
//        /* ARRANGE */
//        // create new data instance
//        LocalData data = new LocalData();
//        Long dataId = 1L;
//        final var idField = data.getClass().getSuperclass().getDeclaredField("id");
//        idField.setAccessible(true);
//        idField.set(data, dataId);
//
//        // create new artifact instance with previously created data
//        ArtifactImpl artifact = new ArtifactImpl();
//        final var dataField = artifact.getClass().getDeclaredField("data");
//        dataField.setAccessible(true);
//        dataField.set(artifact, data);
//
//        // create different data instance that will be the previously persisted data
//        LocalData dataOld = new LocalData();
//        final var valueField = dataOld.getClass().getDeclaredField("value");
//        valueField.setAccessible(true);
//        valueField.set(dataOld, "some value");
//
//        when(artifactRepository.saveAndFlush(artifact)).thenReturn(artifact);
//        when(dataRepository.saveAndFlush(data)).thenReturn(data);
//        when(dataRepository.getOne(dataId)).thenReturn(dataOld);
//
//        /* ACT */
//        service.persist(artifact);
//
//        /* ASSERT */
//        verify(artifactRepository, times(1)).saveAndFlush(artifact);
//        verify(dataRepository, times(1)).saveAndFlush(data);
//    }
//
//    /**************************************************************************
//     * getData.
//     *************************************************************************/
//
//    @Test
//    public void getData_nullArtifactId_throwsIllegalArgumentException() {
//        /* ARRANGE */
//        final var queryInput = new QueryInput();
//        when(artifactRepository.findById(null)).thenThrow(new IllegalArgumentException());
//
//        /* ACT && ASSERT */
//        assertThrows(IllegalArgumentException.class, () -> service.getData(null, queryInput));
//    }
//
//    @Test
//    public void getData_unknownArtifactIdNullQuery_throwsResourceNotFoundException() {
//        /* ARRANGE */
//        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
//        when(artifactRepository.findById(unknownUuid))
//                .thenThrow(new ResourceNotFoundException("not found"));
//
//        /* ACT && ASSERT */
//        assertThrows(ResourceNotFoundException.class, () -> service.getData(unknownUuid, (QueryInput) null));
//    }
//
//    @Test
//    public void getData_knownArtifactIdNullQuery_returnLocalData() {
//        /* ARRANGE */
//        ArtifactImpl localArtifact = getLocalArtifact();
//
//        when(artifactRepository.findById(any())).thenReturn(Optional.of(localArtifact));
//        when(artifactFactory.create(any())).thenReturn(localArtifact);
//        when(dataRepository.getOne(any())).thenReturn(getLocalData());
//
//        /* ACT */
//        final var data = service.getData(localArtifact.getId(), (QueryInput) null);
//
//        /* ASSERT */
//        assertEquals(getLocalData().getValue(), (String) data);
//    }
//
//    @SneakyThrows
//    @Test
//    public void getData_knownArtifactIdNullQuery_increaseAccessCounter() {
//        /* ARRANGE */
//        ArtifactImpl localArtifact = getLocalArtifact();
//
//        when(artifactRepository.findById(any())).thenReturn(Optional.of(localArtifact));
//        when(artifactFactory.create(any())).thenReturn(localArtifact);
//        when(dataRepository.getOne(any())).thenReturn(getLocalData());
//
//        final var before = localArtifact.getNumAccessed();
//
//        /* ACT */
//        service.getData(localArtifact.getId(), (QueryInput) null);
//
//        /* ASSERT */
//        Field numAccessedField = Artifact.class.getDeclaredField("numAccessed");
//        numAccessedField.setAccessible(true);
//        numAccessedField.set(localArtifact, before + 1);
//
//        verify(artifactRepository, times(1)).saveAndFlush(localArtifact);
//    }
//
//    @SneakyThrows
//    @Test
//    public void getData_knownArtifactIdBasicAuthNullQuery_returnRemoteData() {
//        /* ARRANGE */
//        String remoteData = "I am data from a remote source.";
//        ArtifactImpl remoteArtifact = getRemoteArtifact(getRemoteDataWithBasicAuth());
//        URL url = ((RemoteData) remoteArtifact.getData()).getAccessUrl();
//        String username = ((RemoteData) remoteArtifact.getData()).getUsername();
//        String password = ((RemoteData) remoteArtifact.getData()).getPassword();
//
//        when(artifactRepository.findById(remoteArtifact.getId()))
//                .thenReturn(Optional.of(remoteArtifact));
//        when(httpService.sendHttpsGetRequestWithBasicAuth(url.toString(), username, password, null))
//                .thenReturn(remoteData);
//
//        /* ACT */
//        final var data = service.getData(remoteArtifact.getId(), (QueryInput)  null);
//
//        /* ASSERT */
//        assertEquals(remoteData, data);
//    }
//
//    @SneakyThrows
//    @Test
//    public void getData_knownArtifactIdNoBasicAuthNullQuery_returnRemoteData() {
//        /* ARRANGE */
//        String remoteData = "I am data from a remote source.";
//        ArtifactImpl remoteArtifact = getRemoteArtifact(getRemoteData());
//        URL url = ((RemoteData) remoteArtifact.getData()).getAccessUrl();
//
//        when(artifactRepository.findById(remoteArtifact.getId()))
//                .thenReturn(Optional.of(remoteArtifact));
//        when(httpService.sendHttpsGetRequest(url.toString(), null)).thenReturn(remoteData);
//
//        /* ACT */
//        final var data = service.getData(remoteArtifact.getId(),(QueryInput)  null);
//
//        /* ASSERT */
//        assertEquals(remoteData, data);
//    }
//
//    @SneakyThrows
//    @Test
//    public void getData_knownArtifactIdNoBasicAuthWithQuery_returnRemoteData() {
//        /* ARRANGE */
//        String remoteData = "I am data from a remote source.";
//        ArtifactImpl remoteArtifact = getRemoteArtifact(getRemoteData());
//        URL url = ((RemoteData) remoteArtifact.getData()).getAccessUrl();
//        QueryInput queryInput = getQueryInput();
//
//        when(artifactRepository.findById(remoteArtifact.getId()))
//                .thenReturn(Optional.of(remoteArtifact));
//        when(httpService.sendHttpsGetRequest(url.toString(), queryInput)).thenReturn(remoteData);
//
//        /* ACT */
//        final var data = service.getData(remoteArtifact.getId(), queryInput);
//
//        /* ASSERT */
//        assertEquals(remoteData, data);
//    }
//
//    @SneakyThrows
//    @Test
//    public void getData_knownArtifactIdNoBasicAuthWithQueryMalformedUrl_throwRuntimeException() {
//        /* ARRANGE */
//        String remoteData = "I am data from a remote source.";
//        String expectedExceptionMessage = "Could not connect to data source."; //from ArtifactService
//        ArtifactImpl remoteArtifact = getRemoteArtifact(getRemoteData());
//        URL url = ((RemoteData) remoteArtifact.getData()).getAccessUrl();
//        QueryInput queryInput = getQueryInput();
//
//        when(artifactRepository.findById(remoteArtifact.getId()))
//                .thenReturn(Optional.of(remoteArtifact));
//        when(httpService.sendHttpsGetRequest(url.toString(), queryInput))
//                .thenThrow(new URISyntaxException("input", "reason"));
//
//        /* ACT && ASSERT */
//        assertThrows(RuntimeException.class, () -> service.getData(remoteArtifact.getId(), queryInput),
//                expectedExceptionMessage);
//    }
//
//    @SneakyThrows
//    @Test
//    public void getData_knownArtifactIdNoBasicAuthWithQuery_throwRuntimeException() {
//        /* ARRANGE */
//        String remoteData = "I am data from a remote source.";
//        String expectedExceptionMessage = "Exception message";
//        ArtifactImpl remoteArtifact = getRemoteArtifact(getRemoteData());
//        URL url = ((RemoteData) remoteArtifact.getData()).getAccessUrl();
//        QueryInput queryInput = getQueryInput();
//
//        when(artifactRepository.findById(remoteArtifact.getId()))
//                .thenReturn(Optional.of(remoteArtifact));
//        when(httpService.sendHttpsGetRequest(url.toString(), queryInput))
//                .thenThrow(new RuntimeException(expectedExceptionMessage));
//
//        /* ACT && ASSERT */
//        assertThrows(RuntimeException.class, () -> service.getData(remoteArtifact.getId(), queryInput),
//                expectedExceptionMessage);
//    }
//
//    @SneakyThrows
//    @Test
//    public void getData_unknownDataType_throwNotImplementedException() {
//        /* ARRANGE */
//        ArtifactImpl unknownArtifact = getUnknownArtifact();
//        final var dataField = unknownArtifact.getClass().getDeclaredField("data");
//        dataField.setAccessible(true);
//        dataField.set(unknownArtifact, new UnknownData());
//
//        when(artifactRepository.findById(unknownArtifact.getId()))
//                .thenReturn(Optional.of(unknownArtifact));
//
//        /* ACT && ASSERT */
//        assertThrows(UnreachableLineException.class,
//                () -> service.getData(unknownArtifact.getId(), (QueryInput) null));
//    }
//
//    /**************************************************************************
//     * Utilities.
//     *************************************************************************/
//
//    private ArtifactDesc getLocalArtifactDesc() {
//        final var desc = new ArtifactDesc();
//        desc.setTitle("LocalArtifact");
//        desc.setValue("Random Value");
//
//        return desc;
//    }
//
//    @SneakyThrows
//    private ArtifactImpl getLocalArtifact() {
//        final var artifactConstructor = ArtifactImpl.class.getConstructor();
//        artifactConstructor.setAccessible(true);
//
//        final var artifact = artifactConstructor.newInstance();
//
//        final var titleField = artifact.getClass().getSuperclass().getDeclaredField("title");
//        titleField.setAccessible(true);
//        titleField.set(artifact, "LocalArtifact");
//
//        final var idField =
//                artifact.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
//        idField.setAccessible(true);
//        idField.set(artifact, UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));
//
//        final var dataField = artifact.getClass().getDeclaredField("data");
//        dataField.setAccessible(true);
//        dataField.set(artifact, getLocalData());
//
//        return artifact;
//    }
//
//    @SneakyThrows
//    private ArtifactImpl getUnknownArtifact() {
//        final var artifactConstructor = ArtifactImpl.class.getConstructor();
//        artifactConstructor.setAccessible(true);
//
//        final var artifact = artifactConstructor.newInstance();
//
//        final var titleField = artifact.getClass().getSuperclass().getDeclaredField("title");
//        titleField.setAccessible(true);
//        titleField.set(artifact, "LocalArtifact");
//
//        final var idField =
//                artifact.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
//        idField.setAccessible(true);
//        idField.set(artifact, UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));
//
//        return artifact;
//    }
//
//    @SneakyThrows
//    private LocalData getLocalData() {
//        final var dataConstructor = LocalData.class.getConstructor();
//        dataConstructor.setAccessible(true);
//
//        final var localData = dataConstructor.newInstance();
//
//        final var idField = localData.getClass().getSuperclass().getDeclaredField("id");
//        idField.setAccessible(true);
//        idField.set(localData, Long.valueOf(1));
//
//        final var valueField = localData.getClass().getDeclaredField("value");
//        valueField.setAccessible(true);
//        valueField.set(localData, getLocalArtifactDesc().getValue());
//
//        return localData;
//    }
//
//    @SneakyThrows
//    private RemoteData getRemoteData() {
//        final var remoteData = new RemoteData();
//
//        Field accessUrlField = remoteData.getClass().getDeclaredField("accessUrl");
//        accessUrlField.setAccessible(true);
//        accessUrlField.set(remoteData, new URL("http://some-url.com"));
//
//        return remoteData;
//    }
//
//    @SneakyThrows
//    private RemoteData getRemoteDataWithBasicAuth() {
//        final var remoteData = new RemoteData();
//
//        Field accessUrlField = remoteData.getClass().getDeclaredField("accessUrl");
//        accessUrlField.setAccessible(true);
//        accessUrlField.set(remoteData, new URL("http://some-url.com"));
//
//        Field usernameField = remoteData.getClass().getDeclaredField("username");
//        usernameField.setAccessible(true);
//        usernameField.set(remoteData, "username");
//
//        Field passwordField = remoteData.getClass().getDeclaredField("password");
//        passwordField.setAccessible(true);
//        passwordField.set(remoteData, "password");
//
//        return remoteData;
//    }
//
//    @SneakyThrows
//    private ArtifactImpl getRemoteArtifact(RemoteData remoteData) {
//        final var artifactConstructor = ArtifactImpl.class.getConstructor();
//        artifactConstructor.setAccessible(true);
//
//        final var artifact = artifactConstructor.newInstance();
//
//        final var titleField = artifact.getClass().getSuperclass().getDeclaredField("title");
//        titleField.setAccessible(true);
//        titleField.set(artifact, "RemoteArtifact");
//
//        final var idField =
//                artifact.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
//        idField.setAccessible(true);
//        idField.set(artifact, UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));
//
//        final var dataField = artifact.getClass().getDeclaredField("data");
//        dataField.setAccessible(true);
//        dataField.set(artifact, remoteData);
//
//        return artifact;
//    }
//
//    private QueryInput getQueryInput() {
//        QueryInput queryInput = new QueryInput();
//        queryInput.getParams().put("paramName", "paramValue");
//        return queryInput;
//    }
//
//    private class UnknownData extends Data{
//
//    }
//}
