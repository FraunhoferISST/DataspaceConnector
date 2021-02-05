package de.fraunhofer.isst.dataspaceconnector.integration.database;

import de.fraunhofer.isst.dataspaceconnector.integration.database.configuration.DatabaseTestsConfig;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.EndpointService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DatabaseTestsConfig.class})
public class EndpointTest {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private EndpointService endpointService;

    @Before
    public void init() {
        endpointService.getAll().forEach(e -> endpointService.delete(e));
        resourceService.getAll().forEach(r -> resourceService.delete(r));
    }

    @Test
    public void createEndpoint_returnSameEndpoint() {
        /*ARRANGE*/
        Assert.assertTrue(endpointService.getAll().isEmpty());

        ResourceDesc resourceDesc = getResourceDesc();
        Resource resource = resourceService.create(resourceDesc);

        EndpointId endpointId = new EndpointId("/some/path", resource.getId());
        Endpoint original = endpointService.create(endpointId, resource.getId());

        Assert.assertEquals(1, endpointService.getAll().size());

        /*ACT*/
        Endpoint persisted = endpointService.get(endpointId);

        /*ASSERT*/
        Assert.assertEquals(original, persisted);
    }

    @Test
    public void updateEndpoint_resourcePointerToResourcePointer_changeResource() {
        /*ARRANGE*/
        Assert.assertTrue(endpointService.getAll().isEmpty());

        ResourceDesc resourceDesc = getResourceDesc();
        Resource resource1 = resourceService.create(resourceDesc);
        UUID resourceId1 = resource1.getId();

        Resource resource2 = resourceService.create(resourceDesc);
        UUID resourceId2 = resource2.getId();

        EndpointId endpointId = new EndpointId("/some/path", resourceId1);
        endpointService.create(endpointId, resourceId1);

        Endpoint persisted = endpointService.get(endpointId);
        Assert.assertNotNull(persisted.getInternalId());
        Assert.assertNull(persisted.getNewLocation());
        Assert.assertEquals(resourceId1, persisted.getInternalId());

        /*ACT*/
        endpointService.update(endpointId, resourceId2);

        /*ASSERT*/
        persisted = endpointService.get(endpointId);
        Assert.assertNotNull(persisted.getInternalId());
        Assert.assertNull(persisted.getNewLocation());
        Assert.assertEquals(resourceId2, persisted.getInternalId());
    }

    @Test
    public void updateEndpoint_redirectToRedirect_changeLocation() {
        /*ARRANGE*/
        Assert.assertTrue(endpointService.getAll().isEmpty());

        ResourceDesc resourceDesc = getResourceDesc();
        Resource resource = resourceService.create(resourceDesc);

        UUID resourceId = resource.getId();

        EndpointId endpointId = new EndpointId("/some/path", resourceId);
        Endpoint original = endpointService.create(endpointId, resourceId);

        EndpointId redirectTargetId1 = new EndpointId("/some/other/path", resourceId);
        Endpoint redirectTarget1 = endpointService.create(redirectTargetId1, resourceId);

        EndpointId redirectTargetId2 = new EndpointId("/some/other/path2", resourceId);
        Endpoint redirectTarget2 = endpointService.create(redirectTargetId2, resourceId);

        endpointService.update(endpointId, redirectTargetId1);

        Endpoint persisted = endpointService.get(endpointId);
        Assert.assertNull(persisted.getInternalId());
        Assert.assertNotNull(persisted.getNewLocation());
        Assert.assertEquals(redirectTarget1, persisted.getNewLocation());

        /*ACT*/
        endpointService.update(endpointId, redirectTargetId2);

        /*ASSERT*/
        persisted = endpointService.get(endpointId);
        Assert.assertNull(persisted.getInternalId());
        Assert.assertNotNull(persisted.getNewLocation());
        Assert.assertEquals(redirectTarget2, persisted.getNewLocation());
    }

    @Test
    public void updateEndpoint_resourcePointerToRedirect_updatedEndpointReturned() {
        /*ARRANGE*/
        Assert.assertTrue(endpointService.getAll().isEmpty());

        ResourceDesc resourceDesc = getResourceDesc();
        Resource resource = resourceService.create(resourceDesc);

        UUID resourceId = resource.getId();

        EndpointId endpointId = new EndpointId("/some/path", resourceId);
        endpointService.create(endpointId, resourceId);

        EndpointId redirectTargetId = new EndpointId("/some/other/path", resourceId);
        Endpoint redirectTarget = endpointService.create(redirectTargetId, resourceId);

        Endpoint persisted = endpointService.get(endpointId);
        Assert.assertNotNull(persisted.getInternalId());
        Assert.assertNull(persisted.getNewLocation());

        /*ACT*/
        endpointService.update(endpointId, redirectTargetId);

        /*ASSERT*/
        persisted = endpointService.get(endpointId);
        Assert.assertNull(persisted.getInternalId());
        Assert.assertNotNull(persisted.getNewLocation());
        Assert.assertEquals(redirectTarget, persisted.getNewLocation());
    }

    @Test
    public void updateEndpoint_redirectToResourcePointer_updatedEndpointReturned() {
        /*ARRANGE*/
        Assert.assertTrue(endpointService.getAll().isEmpty());

        ResourceDesc resourceDesc = getResourceDesc();
        Resource resource = resourceService.create(resourceDesc);

        UUID resourceId = resource.getId();

        EndpointId endpointId = new EndpointId("/some/path", resourceId);
        Endpoint original = endpointService.create(endpointId, resourceId);

        EndpointId redirectTargetId = new EndpointId("/some/other/path", resourceId);
        Endpoint redirectTarget = endpointService.create(redirectTargetId, resourceId);

        endpointService.update(endpointId, redirectTargetId);

        Endpoint persisted = endpointService.get(endpointId);
        Assert.assertNull(persisted.getInternalId());
        Assert.assertNotNull(persisted.getNewLocation());

        /*ACT*/
        endpointService.update(endpointId, resourceId);

        /*ASSERT*/
        persisted = endpointService.get(endpointId);
        Assert.assertNotNull(persisted.getInternalId());
        Assert.assertNull(persisted.getNewLocation());
        Assert.assertEquals(resourceId, persisted.getInternalId());
    }

    @Test
    public void deleteResource_oneEndpoint_deleteEndpoint() {
        /*ARRANGE*/
        Assert.assertTrue(resourceService.getAll().isEmpty());
        Assert.assertTrue(endpointService.getAll().isEmpty());

        ResourceDesc resourceDesc = getResourceDesc();
        Resource resource = resourceService.create(resourceDesc);

        EndpointId endpointId = new EndpointId("/some/path", resource.getId());
        Endpoint endpoint = endpointService.create(endpointId, resource.getId());

        Assert.assertEquals(1, resourceService.getAll().size());
        Assert.assertEquals(1, endpointService.getAll().size());

        /*ACT*/
        resourceService.delete(resource.getId());

        /*ASSERT*/
        Assert.assertTrue(resourceService.getAll().isEmpty());
        Assert.assertTrue(endpointService.getAll().isEmpty());
    }

    @Test
    public void deleteResource_twoEndpoints_deleteEndpoints() {
        /*ARRANGE*/
        Assert.assertTrue(resourceService.getAll().isEmpty());
        Assert.assertTrue(endpointService.getAll().isEmpty());

        ResourceDesc resourceDesc = getResourceDesc();
        Resource resource = resourceService.create(resourceDesc);

        UUID resourceId = resource.getId();

        EndpointId firstEndpointId = new EndpointId("/some/path", resourceId);
        Endpoint firstEndpoint = endpointService.create(firstEndpointId, resourceId);

        EndpointId secondEndpointId = new EndpointId("/some/other/path", resourceId);
        endpointService.create(secondEndpointId, resourceId);
        endpointService.update(secondEndpointId, firstEndpointId);

        Assert.assertEquals(1, resourceService.getAll().size());
        Assert.assertEquals(2, endpointService.getAll().size());

        /*ACT*/
        resourceService.delete(resource.getId());

        /*ASSERT*/
        Assert.assertTrue(resourceService.getAll().isEmpty());
        Assert.assertTrue(endpointService.getAll().isEmpty());
    }

    private ResourceDesc getResourceDesc() {
        ResourceDesc resourceDesc = new ResourceDesc();
        resourceDesc.setTitle("title");
        resourceDesc.setDescription("description");
        resourceDesc.setKeywords(Collections.singletonList("keyword"));
        resourceDesc.setPublisher(URI.create("http://publisher.com"));
        resourceDesc.setLanguage("EN");
        resourceDesc.setLicence(URI.create("http://license.com"));
        return resourceDesc;
    }

}
