package de.fraunhofer.isst.dataspaceconnector.model;

import de.fraunhofer.isst.dataspaceconnector.configuration.DatabaseTestsConfig;
import de.fraunhofer.isst.dataspaceconnector.repositories.CatalogRepository;
import de.fraunhofer.isst.dataspaceconnector.repositories.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import javax.transaction.Transactional;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {DatabaseTestsConfig.class})
public class CatalogPersistenceTest {

    @Autowired
    private CatalogRepository catalogRepository;

    @Qualifier("offeredResourcesRepository")
    @Autowired
    private ResourceRepository resourceRepository;

    @BeforeEach
    public void init() {
        catalogRepository.findAll().forEach(c -> catalogRepository.delete(c));
        resourceRepository.findAll().forEach(r -> resourceRepository.delete(r));
    }

    @Transactional
    @Test
    public void createCatalog_noResources_returnSameCatalog() {
        /*ARRANGE*/
        assertTrue(catalogRepository.findAll().isEmpty());

        Catalog original = getCatalog();

        /*ACT*/
        original = catalogRepository.save(original);
        Catalog persisted = catalogRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(1, catalogRepository.findAll().size());
        assertEquals(original, persisted);
    }

    @Transactional
    @Test
    public void createCatalog_withResource_returnSameCatalog() {
        /*ARRANGE*/
        assertTrue(catalogRepository.findAll().isEmpty());

        OfferedResource resource = (OfferedResource) resourceRepository.save(getResource());
        Catalog original = getCatalogWithResources(resource);

        /*ACT*/
        original = catalogRepository.save(original);
        Catalog persisted = catalogRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(1, catalogRepository.findAll().size());
        assertEquals(original, persisted);
        assertEquals(original.getOfferedResources(), persisted.getOfferedResources());
    }

    @Transactional
    @Test
    public void updateCatalog_newTitle_returnUpdatedCatalog() {
        /*ARRANGE*/
        assertTrue(catalogRepository.findAll().isEmpty());

        Catalog original = catalogRepository.save(getCatalog());
        String newTitle = "newTitle";

        assertEquals(1, catalogRepository.findAll().size());

        /*ACT*/
        original.setTitle(newTitle);
        catalogRepository.save(original);
        Catalog updated = catalogRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(1, catalogRepository.findAll().size());
        assertEquals(newTitle, updated.getTitle());
        assertEquals(original.getDescription(), updated.getDescription());
        assertEquals(original.getOfferedResources(), updated.getOfferedResources());
    }

    @Transactional
    @Test
    public void updateCatalog_addResource_returnUpdatedCatalog() {
        /*ARRANGE*/
        assertTrue(catalogRepository.findAll().isEmpty());

        //IDs not used in equals(), thus resource1 is equal to resource2 if no field changed
        OfferedResource resource1 = (OfferedResource) resourceRepository.save(getResource());
        OfferedResource resource2 = getResource();
        resource2.setTitle("another resource title");
        resource2 = (OfferedResource) resourceRepository.save(resource2);

        Catalog original = catalogRepository.save(getCatalogWithResources(resource1));

        assertEquals(1, catalogRepository.findAll().size());
        assertEquals(1, original.getOfferedResources().size());

        /*ACT*/
        original.getOfferedResources().add(resource2);
        catalogRepository.save(original);
        Catalog updated = catalogRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(1, catalogRepository.findAll().size());
        assertEquals(2, updated.getOfferedResources().size());
        assertTrue(updated.getOfferedResources()
                .containsAll(Arrays.asList(resource1, resource2)));
        assertEquals(original.getTitle(), updated.getTitle());
        assertEquals(original.getDescription(), updated.getDescription());
    }

    @Transactional
    @Test
    public void updateCatalog_removeResource_returnUpdatedCatalog() {
        /*ARRANGE*/
        assertTrue(catalogRepository.findAll().isEmpty());

        //IDs not used in equals(), thus resource1 is equal to resource2 if no field changed
        OfferedResource resource1 = (OfferedResource) resourceRepository.save(getResource());
        OfferedResource resource2 = getResource();
        resource2.setTitle("another resource title");
        resource2 = (OfferedResource) resourceRepository.save(resource2);

        Catalog original = catalogRepository.save(getCatalogWithResources(resource1, resource2));

        assertEquals(1, catalogRepository.findAll().size());
        assertEquals(2, original.getOfferedResources().size());

        /*ACT*/
        original.getOfferedResources().remove(resource2);
        catalogRepository.save(original);
        Catalog updated = catalogRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(1, catalogRepository.findAll().size());
        assertEquals(1, updated.getOfferedResources().size());
        assertFalse(updated.getOfferedResources().contains(resource2));
        assertTrue(updated.getOfferedResources().contains(resource1));
        assertEquals(original.getTitle(), updated.getTitle());
        assertEquals(original.getDescription(), updated.getDescription());
    }

    @Test
    public void deleteCatalog_noResources_catalogDeleted() {
        /*ARRANGE*/
        assertTrue(catalogRepository.findAll().isEmpty());

        Catalog catalog = catalogRepository.save(getCatalog());

        assertEquals(1, catalogRepository.findAll().size());

        /*ACT*/
        catalogRepository.delete(catalog);

        /*ASSERT*/
        assertTrue(catalogRepository.findAll().isEmpty());
    }

    @Test
    public void deleteCatalog_withResources_catalogDeletedAndResourcesNotAffected() {
        /*ARRANGE*/
        assertTrue(catalogRepository.findAll().isEmpty());
        assertTrue(resourceRepository.findAll().isEmpty());

        OfferedResource resource = (OfferedResource) resourceRepository.save(getResource());
        Catalog catalog = catalogRepository.save(getCatalogWithResources(resource));

        assertEquals(1, catalogRepository.findAll().size());
        assertEquals(1, resourceRepository.findAll().size());

        /*ACT*/
        catalogRepository.delete(catalog);

        /*ASSERT*/
        assertTrue(catalogRepository.findAll().isEmpty());
        assertEquals(1, resourceRepository.findAll().size());
    }

    @Test
    public void deleteResource_resourceReferencedByCatalog_throwDataIntegrityViolationException() {
        /*ARRANGE*/
        assertTrue(catalogRepository.findAll().isEmpty());
        assertTrue(resourceRepository.findAll().isEmpty());

        OfferedResource resource = (OfferedResource) resourceRepository.save(getResource());
        Catalog catalog = catalogRepository.save(getCatalogWithResources(resource));

        assertEquals(1, catalogRepository.findAll().size());
        assertEquals(1, resourceRepository.findAll().size());

        /*ACT*/
        assertThrows(DataIntegrityViolationException.class, () -> resourceRepository.delete(resource));
    }

    private OfferedResource getResource() {
        OfferedResource resource = new OfferedResource();
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

    private Catalog getCatalogWithResources(OfferedResource... resources) {
        List<OfferedResource> resourceList = new ArrayList<>(Arrays.asList(resources));

        Catalog catalog = getCatalog();
        catalog.setOfferedResources(resourceList);

        return catalog;
    }

}
