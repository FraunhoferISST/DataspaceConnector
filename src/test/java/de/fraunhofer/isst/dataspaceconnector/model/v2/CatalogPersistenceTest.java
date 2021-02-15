package de.fraunhofer.isst.dataspaceconnector.model.v2;

import de.fraunhofer.isst.dataspaceconnector.configuration.DatabaseTestsConfig;
import de.fraunhofer.isst.dataspaceconnector.repositories.v2.implementations.CatalogRepository;
import de.fraunhofer.isst.dataspaceconnector.repositories.v2.implementations.ResourceRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DatabaseTestsConfig.class})
public class CatalogPersistenceTest {

    @Autowired
    private CatalogRepository catalogRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Before
    public void init() {
        catalogRepository.findAll().forEach(c -> catalogRepository.delete(c));
        resourceRepository.findAll().forEach(r -> resourceRepository.delete(r));
    }

    @Transactional
    @Test
    public void createCatalog_noResources_returnSameCatalog() {
        /*ARRANGE*/
        Assert.assertTrue(catalogRepository.findAll().isEmpty());

        Catalog original = getCatalog();

        /*ACT*/
        original = catalogRepository.save(original);
        Catalog persisted = catalogRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(1, catalogRepository.findAll().size());
        Assert.assertEquals(original, persisted);
    }

    @Transactional
    @Test
    public void createCatalog_withResource_returnSameCatalog() {
        /*ARRANGE*/
        Assert.assertTrue(catalogRepository.findAll().isEmpty());

        Resource resource = resourceRepository.save(getResource());
        Catalog original = getCatalogWithResources(resource);

        /*ACT*/
        original = catalogRepository.save(original);
        Catalog persisted = catalogRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(1, catalogRepository.findAll().size());
        Assert.assertEquals(original, persisted);
        Assert.assertEquals(original.getResources(), persisted.getResources());
    }

    @Transactional
    @Test
    public void updateCatalog_newTitle_returnUpdatedCatalog() {
        /*ARRANGE*/
        Assert.assertTrue(catalogRepository.findAll().isEmpty());

        Catalog original = catalogRepository.save(getCatalog());
        String newTitle = "newTitle";

        Assert.assertEquals(1, catalogRepository.findAll().size());

        /*ACT*/
        original.setTitle(newTitle);
        catalogRepository.save(original);
        Catalog updated = catalogRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(1, catalogRepository.findAll().size());
        Assert.assertEquals(newTitle, updated.getTitle());
        Assert.assertEquals(original.getDescription(), updated.getDescription());
        Assert.assertEquals(original.getResources(), updated.getResources());
    }

    @Transactional
    @Test
    public void updateCatalog_addResource_returnUpdatedCatalog() {
        /*ARRANGE*/
        Assert.assertTrue(catalogRepository.findAll().isEmpty());

        Resource resource1 = resourceRepository.save(getResource());
        Resource resource2 = resourceRepository.save(getResource());
        Catalog original = catalogRepository.save(getCatalogWithResources(resource1));

        Assert.assertEquals(1, catalogRepository.findAll().size());
        Assert.assertEquals(1, original.getResources().size());

        /*ACT*/
        original.getResources().put(resource2.getId(), resource2);
        catalogRepository.save(original);
        Catalog updated = catalogRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(1, catalogRepository.findAll().size());
        Assert.assertEquals(2, updated.getResources().size());
        Assert.assertTrue(updated.getResources().keySet()
                .containsAll(Arrays.asList(resource1.getId(), resource2.getId())));
        Assert.assertEquals(original.getTitle(), updated.getTitle());
        Assert.assertEquals(original.getDescription(), updated.getDescription());
    }

    @Transactional
    @Test
    public void updateCatalog_removeResource_returnUpdatedCatalog() {
        /*ARRANGE*/
        Assert.assertTrue(catalogRepository.findAll().isEmpty());

        Resource resource1 = resourceRepository.save(getResource());
        Resource resource2 = resourceRepository.save(getResource());
        Catalog original = catalogRepository.save(getCatalogWithResources(resource1, resource2));

        Assert.assertEquals(1, catalogRepository.findAll().size());
        Assert.assertEquals(2, original.getResources().size());

        /*ACT*/
        original.getResources().remove(resource2.getId());
        catalogRepository.save(original);
        Catalog updated = catalogRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(1, catalogRepository.findAll().size());
        Assert.assertEquals(1, updated.getResources().size());
        Assert.assertFalse(updated.getResources().containsKey(resource2.getId()));
        Assert.assertTrue(updated.getResources().containsKey(resource1.getId()));
        Assert.assertEquals(original.getTitle(), updated.getTitle());
        Assert.assertEquals(original.getDescription(), updated.getDescription());
    }

    @Test
    public void deleteCatalog_noResources_catalogDeleted() {
        /*ARRANGE*/
        Assert.assertTrue(catalogRepository.findAll().isEmpty());

        Catalog catalog = catalogRepository.save(getCatalog());

        Assert.assertEquals(1, catalogRepository.findAll().size());

        /*ACT*/
        catalogRepository.delete(catalog);

        /*ASSERT*/
        Assert.assertTrue(catalogRepository.findAll().isEmpty());
    }

    @Test
    public void deleteCatalog_withResources_catalogDeleted() {
        /*ARRANGE*/
        Assert.assertTrue(catalogRepository.findAll().isEmpty());
        Assert.assertTrue(resourceRepository.findAll().isEmpty());

        Resource resource = resourceRepository.save(getResource());
        Catalog catalog = catalogRepository.save(getCatalogWithResources(resource));

        Assert.assertEquals(1, catalogRepository.findAll().size());
        Assert.assertEquals(1, resourceRepository.findAll().size());

        /*ACT*/
        catalogRepository.delete(catalog);

        /*ASSERT*/
        Assert.assertTrue(catalogRepository.findAll().isEmpty());
        Assert.assertEquals(1, resourceRepository.findAll().size());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void deleteResource_resourceReferencedByCatalog_throwDataIntegrityViolationException() {
        /*ARRANGE*/
        Assert.assertTrue(catalogRepository.findAll().isEmpty());
        Assert.assertTrue(resourceRepository.findAll().isEmpty());

        Resource resource = resourceRepository.save(getResource());
        Catalog catalog = catalogRepository.save(getCatalogWithResources(resource));

        Assert.assertEquals(1, catalogRepository.findAll().size());
        Assert.assertEquals(1, resourceRepository.findAll().size());

        /*ACT*/
        resourceRepository.delete(resource);
    }

    private Resource getResource() {
        Resource resource = new Resource();
        resource.setTitle("resource title");
        resource.setVersion(1);
        resource.setPublisher(URI.create("http://publisher.com"));
        resource.setLicence(URI.create("http://license.com"));
        resource.setDescription("resource description");
        resource.setLanguage("EN");
        return resource;
    }

    private Catalog getCatalog() {
        Catalog catalog = new Catalog();
        catalog.setTitle("title");
        catalog.setDescription("description");
        return catalog;
    }

    private Catalog getCatalogWithResources(Resource... resources) {
        Map<UUID, Resource> resourceMap = new HashMap<>();
        Arrays.stream(resources).forEach(r -> resourceMap.put(r.getId(), r));

        Catalog catalog = getCatalog();
        catalog.setResources(resourceMap);

        return catalog;
    }

}
