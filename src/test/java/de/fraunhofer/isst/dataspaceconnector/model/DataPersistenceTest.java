package de.fraunhofer.isst.dataspaceconnector.model;

import de.fraunhofer.isst.dataspaceconnector.configuration.DatabaseTestsConfig;
import de.fraunhofer.isst.dataspaceconnector.repositories.DataRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DatabaseTestsConfig.class})
public class DataPersistenceTest {

    @Autowired
    private DataRepository dataRepository;

    @Before
    public void init() {
        dataRepository.findAll().forEach(d -> dataRepository.delete(d));
    }

    @Transactional
    @Test
    public void createLocalData_returnSameLocalData() {
        /*ARRANGE*/
        Assert.assertTrue(dataRepository.findAll().isEmpty());

        LocalData original = dataRepository.save(getLocalData());

        Assert.assertEquals(1, dataRepository.findAll().size());

        /*ACT*/
        LocalData persisted = (LocalData) dataRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(original, persisted);
    }

    @Transactional
    @Test
    public void createRemoteData_returnSameRemoteData() throws MalformedURLException {
        /*ARRANGE*/
        Assert.assertTrue(dataRepository.findAll().isEmpty());

        RemoteData original = dataRepository.save(getRemoteData());

        Assert.assertEquals(1, dataRepository.findAll().size());

        /*ACT*/
        RemoteData persisted = (RemoteData) dataRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(original, persisted);
    }

    @Test
    public void queryData_returnAllData() throws MalformedURLException {
        /*ARRANGE*/
        Assert.assertTrue(dataRepository.findAll().isEmpty());

        LocalData localData = dataRepository.save(getLocalData());
        RemoteData remoteData = dataRepository.save(getRemoteData());

        Assert.assertEquals(2, dataRepository.findAll().size());

        /*ACT*/
        List<Data> dataList = dataRepository.findAll();

        /*ASSERT*/
        Assert.assertTrue(dataList.contains(localData));
        Assert.assertTrue(dataList.contains(remoteData));
    }

    @Test
    public void queryLocalData_returnOnlyLocalData() throws MalformedURLException {
        /*ARRANGE*/
        Assert.assertTrue(dataRepository.findAll().isEmpty());

        LocalData localData = dataRepository.save(getLocalData());
        RemoteData remoteData = dataRepository.save(getRemoteData());

        Assert.assertEquals(2, dataRepository.findAll().size());

        /*ACT*/
        List<LocalData> dataList = dataRepository.findAllLocalData();

        /*ASSERT*/
        Assert.assertTrue(dataList.contains(localData));
        Assert.assertFalse(dataList.contains(remoteData));
    }

    @Test
    public void queryRemoteData_returnOnlyRemoteData() throws MalformedURLException {
        /*ARRANGE*/
        Assert.assertTrue(dataRepository.findAll().isEmpty());

        LocalData localData = dataRepository.save(getLocalData());
        RemoteData remoteData = dataRepository.save(getRemoteData());

        Assert.assertEquals(2, dataRepository.findAll().size());

        /*ACT*/
        List<RemoteData> dataList = dataRepository.findAllRemoteData();

        /*ASSERT*/
        Assert.assertFalse(dataList.contains(localData));
        Assert.assertTrue(dataList.contains(remoteData));
    }

    @Transactional
    @Test
    public void updateLocalData_returnUpdatedLocalData() throws MalformedURLException {
        /*ARRANGE*/
        Assert.assertTrue(dataRepository.findAll().isEmpty());

        RemoteData remoteData = dataRepository.save(getRemoteData());

        Assert.assertEquals(1, dataRepository.findAll().size());

        /*ACT*/
        remoteData.setUsername("new name");
        dataRepository.save(remoteData);

        /*ASSERT*/
        RemoteData updated = (RemoteData) dataRepository.getOne(remoteData.getId());
        Assert.assertEquals(remoteData.getUsername(), updated.getUsername());
        Assert.assertEquals(getRemoteData().getPassword(), updated.getPassword());
        Assert.assertEquals(getRemoteData().getAccessUrl(), updated.getAccessUrl());
    }

    @Transactional
    @Test
    public void updateRemoteData_returnUpdatedRemoteData() {
        /*ARRANGE*/
        Assert.assertTrue(dataRepository.findAll().isEmpty());

        LocalData localData = dataRepository.save(getLocalData());

        Assert.assertEquals(1, dataRepository.findAll().size());
    }

    @Test
    public void deleteLocalData_localDataDeleted() {
        /*ARRANGE*/
        Assert.assertTrue(dataRepository.findAll().isEmpty());

        LocalData localData = dataRepository.save(getLocalData());

        Assert.assertEquals(1, dataRepository.findAll().size());

        /*ACT*/
        dataRepository.deleteById(localData.getId());

        /*ASSERT*/
        Assert.assertTrue(dataRepository.findAll().isEmpty());
    }

    @Test
    public void deleteRemoteData_remoteDataDeleted() throws MalformedURLException {
        /*ARRANGE*/
        Assert.assertTrue(dataRepository.findAll().isEmpty());

        RemoteData remoteData = dataRepository.save(getRemoteData());

        Assert.assertEquals(1, dataRepository.findAll().size());

        /*ACT*/
        dataRepository.deleteById(remoteData.getId());

        /*ASSERT*/
        Assert.assertTrue(dataRepository.findAll().isEmpty());
    }

    @Test
    public void deleteAllData_localAndRemoteDataDeleted() throws MalformedURLException {
        /*ARRANGE*/
        Assert.assertTrue(dataRepository.findAll().isEmpty());

        LocalData localData = dataRepository.save(getLocalData());
        RemoteData remoteData = dataRepository.save(getRemoteData());

        Assert.assertEquals(2, dataRepository.findAll().size());

        /*ACT*/
        dataRepository.deleteAll();

        /*ASSERT*/
        Assert.assertTrue(dataRepository.findAll().isEmpty());
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
