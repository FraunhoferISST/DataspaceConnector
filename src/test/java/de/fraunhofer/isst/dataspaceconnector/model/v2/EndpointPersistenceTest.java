package de.fraunhofer.isst.dataspaceconnector.model.v2;

import de.fraunhofer.isst.dataspaceconnector.configuration.DatabaseTestsConfig;
import de.fraunhofer.isst.dataspaceconnector.repositories.v2.EndpointRepository;
import de.fraunhofer.isst.dataspaceconnector.repositories.v2.implementations.ResourceRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DatabaseTestsConfig.class})
public class EndpointPersistenceTest {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private EndpointRepository endpointRepository;

    @Before
    public void init() {
        endpointRepository.findAll().forEach(e -> endpointRepository.delete(e));
        resourceRepository.findAll().forEach(r -> resourceRepository.delete(r));
    }

    @Transactional
    @Test
    public void createEndpoint_returnSameEndpoint() {
        /*ARRANGE*/
        Assert.assertTrue(endpointRepository.findAll().isEmpty());

        Resource resource = resourceRepository.save(getResource());

        EndpointId endpointId = new EndpointId("/some/path", resource.getId());
        Endpoint original = new Endpoint();
        original.setId(endpointId);
        original.setInternalId(resource.getId());
        endpointRepository.save(original);

        Assert.assertEquals(1, endpointRepository.findAll().size());

        /*ACT*/
        Endpoint persisted = endpointRepository.getOne(endpointId);

        /*ASSERT*/
        Assert.assertEquals(original, persisted);
    }

    @Transactional
    @Test
    public void updateEndpoint_returnUpdatedEndpoint() {
        /*ARRANGE*/
        Assert.assertTrue(endpointRepository.findAll().isEmpty());

        Resource resource1 = resourceRepository.save(getResource());
        Resource resource2 = resourceRepository.save(getResource());

        EndpointId endpointId = new EndpointId("/some/path", resource1.getId());
        Endpoint original = new Endpoint();
        original.setId(endpointId);
        original.setInternalId(resource1.getId());
        endpointRepository.save(original);

        //due to transactional(?) 'persisted' is updated when save is called again => assert fails
//        Endpoint persisted = endpointRepository.getOne(endpointId);
        Endpoint persisted = new Endpoint();
        persisted.setId(original.getId());
        persisted.setInternalId(original.getInternalId());
        persisted.setNewLocation(original.getNewLocation());

        /*ACT*/
        original.setInternalId(resource2.getId());
        endpointRepository.save(original);

        Endpoint updated = endpointRepository.getOne(endpointId);

        /*ASSERT*/
        Assert.assertNotEquals(updated, persisted);
        Assert.assertEquals(resource2.getId(), updated.getInternalId());
    }

    @Test
    public void deleteEndpoint_endpointDeleted() {
        /*ARRANGE*/
        Assert.assertTrue(endpointRepository.findAll().isEmpty());

        Resource resource1 = resourceRepository.save(getResource());
        Resource resource2 = resourceRepository.save(getResource());

        EndpointId endpointId = new EndpointId("/some/path", resource1.getId());
        Endpoint original = new Endpoint();
        original.setId(endpointId);
        original.setInternalId(resource1.getId());
        original = endpointRepository.save(original);

        Assert.assertEquals(1, endpointRepository.findAll().size());

        /*ACT*/
        endpointRepository.delete(original);

        /*ASSERT*/
        Assert.assertTrue(endpointRepository.findAll().isEmpty());
    }

    @Test
    public void deleteEndpointById_endpointDeleted() {
        /*ARRANGE*/
        Assert.assertTrue(endpointRepository.findAll().isEmpty());

        Resource resource1 = resourceRepository.save(getResource());
        Resource resource2 = resourceRepository.save(getResource());

        EndpointId endpointId = new EndpointId("/some/path", resource1.getId());
        Endpoint original = new Endpoint();
        original.setId(endpointId);
        original.setInternalId(resource1.getId());
        original = endpointRepository.save(original);

        Assert.assertEquals(1, endpointRepository.findAll().size());

        /*ACT*/
        endpointRepository.deleteById(original.getId());

        /*ASSERT*/
        Assert.assertTrue(endpointRepository.findAll().isEmpty());
    }

    private Resource getResource() {
        Resource resource = new Resource();
        resource.setTitle("title");
        resource.setLanguage("EN");
        resource.setDescription("description");
        resource.setKeywords(Arrays.asList("keyword1", "keyword2"));
        resource.setLicence(URI.create("http://license.com"));
        resource.setPublisher(URI.create("http://publisher.com"));
        resource.setVersion(1);
        return resource;
    }

}
