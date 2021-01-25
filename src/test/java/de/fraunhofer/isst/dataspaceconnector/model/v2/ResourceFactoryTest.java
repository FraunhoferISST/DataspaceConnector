package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ResourceFactoryTest {
    @Autowired
    private ResourceFactory factory;

    @Test
    void when_passed_desc_is_null_on_creation_should_throw_exception() {
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.create(null);
        });

        assertNotNull(exception);
    }

    @Test
    void when_all_desc_members_are_set_resource_should_be_created() {
        final var desc = getValidDesc();
        final var resource = factory.create(desc);

        assertNotNull(resource);
        assertEquals(desc.getTitle(), resource.getTitle());
        assertEquals(desc.getDescription(), resource.getDescription());
        assertEquals(desc.getKeywords(), resource.getKeywords());
        assertEquals(desc.getPublisher(), resource.getPublisher());
        assertEquals(desc.getLanguage(), resource.getLanguage());
        assertEquals(desc.getLicence(), resource.getLicence());
        assertNotNull(resource.getRepresentations());
        assertEquals(resource.getRepresentations().size(), 0);
        assertNotNull(resource.getContracts());
        assertEquals(resource.getContracts().size(), 0);
        assertEquals(resource.getVersion(), 0);

        assertNull(resource.getId());
        assertNull(resource.getCreationDate());
        assertNull(resource.getLastModificationDate());
    }

    @Test
    void when_all_desc_members_are_null_the_resource_should_be_created() {
        final var desc = getDescWithNullMembers();
        final var resource = factory.create(desc);

        assertNotNull(resource);
        assertNotNull(resource.getTitle());
        assertNotNull(resource.getDescription());
        assertNotNull(resource.getKeywords());
        assertNotNull(resource.getPublisher());
        assertNotNull(resource.getLanguage());
        assertNotNull(resource.getLicence());
        assertNotNull(resource.getRepresentations());
        assertEquals(resource.getRepresentations().size(), 0);
        assertNotNull(resource.getContracts());
        assertEquals(resource.getContracts().size(), 0);
        assertEquals(resource.getVersion(), 0);

        assertNull(resource.getId());
        assertNull(resource.getCreationDate());
        assertNull(resource.getLastModificationDate());
    }

    @Test
    void when_all_desc_members_are_set_the_resource_should_be_updated() {
        var resource = factory.create(getValidDesc());

        assertNotNull(resource);

        var idBefore = resource.getId();
        var creationDateBefore = resource.getCreationDate();
        var lastModificationDateBefore = resource.getLastModificationDate();

        var versionBefore = resource.getVersion();
        var representationsBefore =
                ((HashMap<UUID, Representation>) resource.getRepresentations()).clone();
        var contractsBefore =
                ((HashMap<UUID, Contract>) resource.getContracts()).clone();
        var desc = getUpdatedDesc();
        factory.update(resource, desc);

        assertNotNull(resource);
        assertEquals(desc.getTitle(), resource.getTitle());
        assertEquals(desc.getDescription(), resource.getDescription());
        assertEquals(desc.getKeywords(), resource.getKeywords());
        assertEquals(desc.getPublisher(), resource.getPublisher());
        assertEquals(desc.getLanguage(), resource.getLanguage());
        assertEquals(desc.getLicence(), resource.getLicence());
        assertNotNull(resource.getRepresentations());
        assertEquals(representationsBefore, resource.getRepresentations());
        assertNotNull(resource.getContracts());
        assertEquals(contractsBefore, resource.getContracts());
        assertEquals(versionBefore + 1, resource.getVersion());

        assertEquals(idBefore, resource.getId());
        assertEquals(creationDateBefore, resource.getCreationDate());
        assertEquals(lastModificationDateBefore,
                resource.getLastModificationDate());
    }

    @Test
    void when_all_desc_members_are_null_the_resource_should_be_updated() {
        var initialDesc = getValidDesc();
        var resource = factory.create(initialDesc);

        assertNotNull(resource);

        var idBefore = resource.getId();
        var creationDateBefore = resource.getCreationDate();
        var lastModificationDateBefore = resource.getLastModificationDate();

        var versionBefore = resource.getVersion();
        var representationsBefore =
                ((HashMap<UUID, Representation>) resource.getRepresentations()).clone();
        var contractsBefore =
                ((HashMap<UUID, Contract>) resource.getContracts()).clone();

        var desc = getDescWithNullMembers();
        factory.update(resource, desc);

        assertNotNull(resource);
        assertNotNull(resource.getTitle());
        assertNotNull(resource.getDescription());
        assertNotEquals(initialDesc.getTitle(), resource.getTitle());
        assertNotEquals(initialDesc.getDescription(), resource.getDescription());
        assertNotEquals(desc.getKeywords(), resource.getKeywords());
        assertNotEquals(desc.getPublisher(), resource.getPublisher());
        assertNotEquals(desc.getLanguage(), resource.getLanguage());
        assertNotEquals(desc.getLicence(), resource.getLicence());
        assertNotNull(resource.getRepresentations());
        assertEquals(representationsBefore, resource.getRepresentations());
        assertNotNull(resource.getContracts());
        assertEquals(contractsBefore, resource.getContracts());
        assertEquals(versionBefore + 1, resource.getVersion());

        assertEquals(idBefore, resource.getId());
        assertEquals(creationDateBefore, resource.getCreationDate());
        assertEquals(lastModificationDateBefore,
                resource.getLastModificationDate());
    }

    @Test
    void when_resource_update_does_result_in_change_return_true() {
        var resource = factory.create(getValidDesc());
        assertTrue(factory.update(resource, getUpdatedDesc()));
    }

    @Test
    void when_resource_update_does_result_in_no_change_return_false() {
        var resource = factory.create(getValidDesc());
        assertFalse(factory.update(resource, getValidDesc()));
    }

    @Test
    void when_the_resource_is_null_the_desc_is_set_on_update_should_throw_exception() {
        var desc = getValidDesc();
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, desc);
        });
    }

    @Test
    void when_the_resource_is_null_the_desc_is_null_on_update_should_throw_exception() {
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, null);
        });
    }

    @Test
    void when_the_resource_is_set_the_desc_is_null_on_update_should_throw_exception() {
        var initialDesc = getValidDesc();
        var resource = factory.create(initialDesc);

        assertNotNull(resource);

        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(resource, null);
        });
    }

    ResourceDesc getValidDesc() {
        var desc = new ResourceDesc();
        desc.setTitle("Default");
        desc.setDescription("This is the default catalog containing all "
                + "available resources.");
        desc.setKeywords("Generic");
        desc.setPublisher("Someone");
        desc.setLanguage("English");
        desc.setLicence("MIT");

        return desc;
    }

    ResourceDesc getUpdatedDesc() {
        var desc = new ResourceDesc();
        desc.setTitle("The new default");
        desc.setDescription("The new description");
        desc.setKeywords("NewGeneric");
        desc.setPublisher("Someone else");
        desc.setLanguage("German");
        desc.setLicence("Apache");

        return desc;
    }

    ResourceDesc getDescWithNullMembers() {
        var desc = new ResourceDesc();
        desc.setTitle(null);
        desc.setDescription(null);
        desc.setKeywords(null);
        desc.setPublisher(null);
        desc.setLanguage(null);
        desc.setLicence(null);

        return desc;
    }
}
