//package de.fraunhofer.isst.dataspaceconnector.model;
//
//import javax.transaction.Transactional;
//import java.net.URI;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//import de.fraunhofer.isst.dataspaceconnector.configuration.DatabaseTestsConfig;
//import de.fraunhofer.isst.dataspaceconnector.repositories.CatalogRepository;
//import de.fraunhofer.isst.dataspaceconnector.repositories.ResourceRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.dao.DataIntegrityViolationException;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest(classes = {DatabaseTestsConfig.class})
//public class CatalogPersistenceTest {
//
//    @Autowired
//    private CatalogRepository catalogRepository;
//
//    @Autowired
//    private ResourceRepository resourceRepository;
//
//    @BeforeEach
//    public void init() {
//        catalogRepository.findAll().forEach(c -> catalogRepository.delete(c));
//        resourceRepository.findAll().forEach(r -> resourceRepository.delete(r));
//    }
//
//    @Transactional
//    @Test
//    public void createCatalog_noResources_returnSameCatalog() {
//        /*ARRANGE*/
//        assertTrue(catalogRepository.findAll().isEmpty());
//
//        Catalog original = getCatalog();
//
//        /*ACT*/
//        original = catalogRepository.save(original);
//        Catalog persisted = catalogRepository.getOne(original.getId());
//
//        /*ASSERT*/
//        assertEquals(1, catalogRepository.findAll().size());
//        assertEquals(original, persisted);
//    }
//
//    @Transactional
//    @Test
//    public void createCatalog_withResource_returnSameCatalog() {
//        /*ARRANGE*/
//        assertTrue(catalogRepository.findAll().isEmpty());
//
//        Resource resource = resourceRepository.save(getResource());
//        Catalog original = getCatalogWithResources(resource);
//
//        /*ACT*/
//        original = catalogRepository.save(original);
//        Catalog persisted = catalogRepository.getOne(original.getId());
//
//        /*ASSERT*/
//        assertEquals(1, catalogRepository.findAll().size());
//        assertEquals(original, persisted);
//        assertEquals(original.getResources(), persisted.getResources());
//    }
//
//    @Transactional
//    @Test
//    public void updateCatalog_newTitle_returnUpdatedCatalog() {
//        /*ARRANGE*/
//        assertTrue(catalogRepository.findAll().isEmpty());
//
//        Catalog original = catalogRepository.save(getCatalog());
//        String newTitle = "newTitle";
//
//        assertEquals(1, catalogRepository.findAll().size());
//
//        /*ACT*/
//        original.setTitle(newTitle);
//        catalogRepository.save(original);
//        Catalog updated = catalogRepository.getOne(original.getId());
//
//        /*ASSERT*/
//        assertEquals(1, catalogRepository.findAll().size());
//        assertEquals(newTitle, updated.getTitle());
//        assertEquals(original.getDescription(), updated.getDescription());
//        assertEquals(original.getResources(), updated.getResources());
//    }
//
//    @Transactional
//    @Test
//    public void updateCatalog_addResource_returnUpdatedCatalog() {
//        /*ARRANGE*/
//        assertTrue(catalogRepository.findAll().isEmpty());
//
//        Resource resource1 = resourceRepository.save(getResource());
//        Resource resource2 = resourceRepository.save(getResource());
//        Catalog original = catalogRepository.save(getCatalogWithResources(resource1));
//
//        assertEquals(1, catalogRepository.findAll().size());
//        assertEquals(1, original.getResources().size());
//
//        /*ACT*/
//        original.getResources().put(resource2.getId(), resource2);
//        catalogRepository.save(original);
//        Catalog updated = catalogRepository.getOne(original.getId());
//
//        /*ASSERT*/
//        assertEquals(1, catalogRepository.findAll().size());
//        assertEquals(2, updated.getResources().size());
//        assertTrue(updated.getResources().keySet()
//                .containsAll(Arrays.asList(resource1.getId(), resource2.getId())));
//        assertEquals(original.getTitle(), updated.getTitle());
//        assertEquals(original.getDescription(), updated.getDescription());
//    }
//
//    @Transactional
//    @Test
//    public void updateCatalog_removeResource_returnUpdatedCatalog() {
//        /*ARRANGE*/
//        assertTrue(catalogRepository.findAll().isEmpty());
//
//        Resource resource1 = resourceRepository.save(getResource());
//        Resource resource2 = resourceRepository.save(getResource());
//        Catalog original = catalogRepository.save(getCatalogWithResources(resource1, resource2));
//
//        assertEquals(1, catalogRepository.findAll().size());
//        assertEquals(2, original.getResources().size());
//
//        /*ACT*/
//        original.getResources().remove(resource2.getId());
//        catalogRepository.save(original);
//        Catalog updated = catalogRepository.getOne(original.getId());
//
//        /*ASSERT*/
//        assertEquals(1, catalogRepository.findAll().size());
//        assertEquals(1, updated.getResources().size());
//        assertFalse(updated.getResources().containsKey(resource2.getId()));
//        assertTrue(updated.getResources().containsKey(resource1.getId()));
//        assertEquals(original.getTitle(), updated.getTitle());
//        assertEquals(original.getDescription(), updated.getDescription());
//    }
//
//    @Test
//    public void deleteCatalog_noResources_catalogDeleted() {
//        /*ARRANGE*/
//        assertTrue(catalogRepository.findAll().isEmpty());
//
//        Catalog catalog = catalogRepository.save(getCatalog());
//
//        assertEquals(1, catalogRepository.findAll().size());
//
//        /*ACT*/
//        catalogRepository.delete(catalog);
//
//        /*ASSERT*/
//        assertTrue(catalogRepository.findAll().isEmpty());
//    }
//
//    @Test
//    public void deleteCatalog_withResources_catalogDeleted() {
//        /*ARRANGE*/
//        assertTrue(catalogRepository.findAll().isEmpty());
//        assertTrue(resourceRepository.findAll().isEmpty());
//
//        Resource resource = resourceRepository.save(getResource());
//        Catalog catalog = catalogRepository.save(getCatalogWithResources(resource));
//
//        assertEquals(1, catalogRepository.findAll().size());
//        assertEquals(1, resourceRepository.findAll().size());
//
//        /*ACT*/
//        catalogRepository.delete(catalog);
//
//        /*ASSERT*/
//        assertTrue(catalogRepository.findAll().isEmpty());
//        assertEquals(1, resourceRepository.findAll().size());
//    }
//
//    @Test
//    public void deleteResource_resourceReferencedByCatalog_throwDataIntegrityViolationException() {
//        /*ARRANGE*/
//        assertTrue(catalogRepository.findAll().isEmpty());
//        assertTrue(resourceRepository.findAll().isEmpty());
//
//        Resource resource = resourceRepository.save(getResource());
//        Catalog catalog = catalogRepository.save(getCatalogWithResources(resource));
//
//        assertEquals(1, catalogRepository.findAll().size());
//        assertEquals(1, resourceRepository.findAll().size());
//
//        /*ACT*/
//        assertThrows(DataIntegrityViolationException.class, () -> resourceRepository.delete(resource));
//    }
//
//    private Resource getResource() {
//        Resource resource = new Resource();
//        resource.setTitle("resource title");
//        resource.setVersion(1);
//        resource.setPublisher(URI.create("http://publisher.com"));
//        resource.setLicence(URI.create("http://license.com"));
//        resource.setDescription("resource description");
//        resource.setLanguage("EN");
//        return resource;
//    }
//
//    private Catalog getCatalog() {
//        Catalog catalog = new Catalog();
//        catalog.setTitle("title");
//        catalog.setDescription("description");
//        return catalog;
//    }
//
//    private Catalog getCatalogWithResources(Resource... resources) {
//        Map<UUID, Resource> resourceMap = new HashMap<>();
//        Arrays.stream(resources).forEach(r -> resourceMap.put(r.getId(), r));
//
//        Catalog catalog = getCatalog();
//        catalog.setResources(resourceMap);
//
//        return catalog;
//    }
//
//}
