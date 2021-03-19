package de.fraunhofer.isst.dataspaceconnector.model;

import de.fraunhofer.isst.dataspaceconnector.configuration.DatabaseTestsConfig;
import de.fraunhofer.isst.dataspaceconnector.repositories.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {DatabaseTestsConfig.class})
public class DataPersistenceTest {

    @Autowired
    private DataRepository dataRepository;

    @BeforeEach
    public void init() {
        dataRepository.findAll().forEach(d -> dataRepository.delete(d));
        dataRepository.flush();
    }

    @Transactional
    @Test
    public void createLocalData_returnSameLocalData() {
        /*ARRANGE*/
        assertTrue(dataRepository.findAll().isEmpty());

        LocalData original = dataRepository.saveAndFlush(getLocalData());

        assertEquals(1, dataRepository.findAll().size());

        /*ACT*/
        LocalData persisted = (LocalData) dataRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(original, persisted);
    }

    @Transactional
    @Test
    public void createRemoteData_returnSameRemoteData() throws MalformedURLException {
        /*ARRANGE*/
        assertTrue(dataRepository.findAll().isEmpty());

        RemoteData original = dataRepository.saveAndFlush(getRemoteData());

        assertEquals(1, dataRepository.findAll().size());

        /*ACT*/
        RemoteData persisted = (RemoteData) dataRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(original, persisted);
    }

    @Test
    public void queryData_returnAllData() throws MalformedURLException {
        /*ARRANGE*/
        assertTrue(dataRepository.findAll().isEmpty());

        LocalData localData = dataRepository.saveAndFlush(getLocalData());
        RemoteData remoteData = dataRepository.saveAndFlush(getRemoteData());

        assertEquals(2, dataRepository.findAll().size());

        /*ACT*/
        List<Data> dataList = dataRepository.findAll();

        /*ASSERT*/
        assertTrue(dataList.contains(localData));
        assertTrue(dataList.contains(remoteData));
    }

    @Test
    public void queryLocalData_returnOnlyLocalData() throws MalformedURLException {
        /*ARRANGE*/
        assertTrue(dataRepository.findAll().isEmpty());

        LocalData localData = dataRepository.saveAndFlush(getLocalData());
        RemoteData remoteData = dataRepository.saveAndFlush(getRemoteData());

        assertEquals(2, dataRepository.findAll().size());

        /*ACT*/
        List<LocalData> dataList = dataRepository.findAllLocalData();

        /*ASSERT*/
        assertTrue(dataList.contains(localData));
        assertFalse(dataList.contains(remoteData));
    }

    @Test
    public void queryRemoteData_returnOnlyRemoteData() throws MalformedURLException {
        /*ARRANGE*/
        assertTrue(dataRepository.findAll().isEmpty());

        LocalData localData = dataRepository.saveAndFlush(getLocalData());
        RemoteData remoteData = dataRepository.saveAndFlush(getRemoteData());

        assertEquals(2, dataRepository.findAll().size());

        /*ACT*/
        List<RemoteData> dataList = dataRepository.findAllRemoteData();

        /*ASSERT*/
        assertFalse(dataList.contains(localData));
        assertTrue(dataList.contains(remoteData));
    }

    @Transactional
    @Test
    public void updateLocalData_returnUpdatedLocalData() throws MalformedURLException {
        /*ARRANGE*/
        assertTrue(dataRepository.findAll().isEmpty());

        RemoteData remoteData = dataRepository.saveAndFlush(getRemoteData());

        assertEquals(1, dataRepository.findAll().size());

        /*ACT*/
        remoteData.setUsername("new name");
        dataRepository.saveAndFlush(remoteData);

        /*ASSERT*/
        RemoteData updated = (RemoteData) dataRepository.getOne(remoteData.getId());
        assertEquals(remoteData.getUsername(), updated.getUsername());
        assertEquals(getRemoteData().getPassword(), updated.getPassword());
        assertEquals(getRemoteData().getAccessUrl(), updated.getAccessUrl());
    }

    @Transactional
    @Test
    public void updateRemoteData_returnUpdatedRemoteData() {
        /*ARRANGE*/
        assertTrue(dataRepository.findAll().isEmpty());

        LocalData localData = dataRepository.saveAndFlush(getLocalData());

        assertEquals(1, dataRepository.findAll().size());
    }

    @Test
    public void deleteLocalData_localDataDeleted() {
        /*ARRANGE*/
        assertTrue(dataRepository.findAll().isEmpty());

        LocalData localData = dataRepository.saveAndFlush(getLocalData());

        assertEquals(1, dataRepository.findAll().size());

        /*ACT*/
        dataRepository.deleteById(localData.getId());

        /*ASSERT*/
        assertTrue(dataRepository.findAll().isEmpty());
    }

    @Test
    public void deleteRemoteData_remoteDataDeleted() throws MalformedURLException {
        /*ARRANGE*/
        assertTrue(dataRepository.findAll().isEmpty());

        RemoteData remoteData = dataRepository.saveAndFlush(getRemoteData());

        assertEquals(1, dataRepository.findAll().size());

        /*ACT*/
        dataRepository.deleteById(remoteData.getId());

        /*ASSERT*/
        assertTrue(dataRepository.findAll().isEmpty());
    }

    @Test
    public void deleteAllData_localAndRemoteDataDeleted() throws MalformedURLException {
        /*ARRANGE*/
        assertTrue(dataRepository.findAll().isEmpty());

        LocalData localData = dataRepository.saveAndFlush(getLocalData());
        RemoteData remoteData = dataRepository.saveAndFlush(getRemoteData());

        assertEquals(2, dataRepository.findAll().size());

        /*ACT*/
        dataRepository.deleteAll();

        /*ASSERT*/
        assertTrue(dataRepository.findAll().isEmpty());
    }

    private LocalData getLocalData() {
        LocalData localData = new LocalData();
        localData.setValue("data");
        return localData;
    }

    private RemoteData getRemoteData() throws MalformedURLException {
        RemoteData remoteData = new RemoteData();
        remoteData.setAccessUrl(new URL("http://data.com"));
        remoteData.setUsername("username");
        remoteData.setPassword("password");
        return remoteData;
    }

}
