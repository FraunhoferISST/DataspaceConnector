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
class CatalogFactoryTest {
    @Autowired
    private CatalogFactory factory;

    @Test
    void when_passed_desc_is_null_on_creation_should_throw_exception() {
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.create(null);
        });

        assertNotNull(exception);
    }

    @Test
    void when_all_desc_members_are_set_catalog_should_be_created() {
        final var desc = getValidDesc();
        final var catalog = factory.create(desc);

        assertNotNull(catalog);
        assertEquals(desc.getTitle(), catalog.getTitle());
        assertEquals(desc.getDescription(), catalog.getDescription());
        assertNotNull(catalog.getResources());
        assertEquals(catalog.getResources().size(), 0);

        assertNull(catalog.getId());
        assertNull(catalog.getCreationDate());
        assertNull(catalog.getLastModificationDate());
    }

    @Test
    void when_all_desc_members_are_null_the_catalog_should_be_created() {
        final var desc = getDescWithNullMembers();
        final var catalog = factory.create(desc);

        assertNotNull(catalog);
        assertNotNull(catalog.getTitle());
        assertNotNull(catalog.getDescription());
        assertNotNull(catalog.getResources());
        assertEquals(catalog.getResources().size(), 0);

        assertNull(catalog.getId());
        assertNull(catalog.getCreationDate());
        assertNull(catalog.getLastModificationDate());
    }

    @Test
    void when_all_desc_members_are_set_the_catalog_should_be_updated() {
        var catalog = factory.create(getValidDesc());

        assertNotNull(catalog);

        var idBefore = catalog.getId();
        var creationDateBefore = catalog.getCreationDate();
        var lastModificationDateBefore = catalog.getLastModificationDate();

        var resourcesBefore =
                ((HashMap<UUID, Resource>) catalog.getResources()).clone();
        var desc = getUpdatedDesc();
        factory.update(catalog, desc);

        assertNotNull(catalog);
        assertEquals(desc.getTitle(), catalog.getTitle());
        assertEquals(desc.getDescription(), catalog.getDescription());
        assertNotNull(catalog.getResources());
        assertEquals(resourcesBefore, catalog.getResources());

        assertEquals(idBefore, catalog.getId());
        assertEquals(creationDateBefore, catalog.getCreationDate());
        assertEquals(lastModificationDateBefore,
                catalog.getLastModificationDate());
    }

    @Test
    void when_all_desc_members_are_null_the_catalog_should_be_updated() {
        var initialDesc = getValidDesc();
        var catalog = factory.create(initialDesc);

        assertNotNull(catalog);

        var idBefore = catalog.getId();
        var creationDateBefore = catalog.getCreationDate();
        var lastModificationDateBefore = catalog.getLastModificationDate();

        var resourcesBefore =
                ((HashMap<UUID, Resource>) catalog.getResources()).clone();
        var desc = getDescWithNullMembers();
        factory.update(catalog, desc);

        assertNotNull(catalog);
        assertNotNull(catalog.getTitle());
        assertNotNull(catalog.getDescription());
        assertNotEquals(initialDesc.getTitle(), catalog.getTitle());
        assertNotEquals(initialDesc.getDescription(), catalog.getDescription());
        assertNotNull(catalog.getResources());
        assertEquals(resourcesBefore, catalog.getResources());

        assertEquals(idBefore, catalog.getId());
        assertEquals(creationDateBefore, catalog.getCreationDate());
        assertEquals(lastModificationDateBefore,
                catalog.getLastModificationDate());
    }

    @Test
    void when_catalog_update_does_result_in_change_return_true() {
        var catalog = factory.create(getValidDesc());
        assertTrue(factory.update(catalog, getUpdatedDesc()));
    }

    @Test
    void when_catalog_update_does_result_in_no_change_return_false() {
        var catalog = factory.create(getValidDesc());
        assertFalse(factory.update(catalog, getValidDesc()));
    }

    @Test
    void when_the_catalog_is_null_the_desc_is_set_on_update_should_throw_exception() {
        var desc = getValidDesc();
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, desc);
        });
    }

    @Test
    void when_the_catalog_is_null_the_desc_is_null_on_update_should_throw_exception() {
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, null);
        });
    }

    @Test
    void when_the_catalog_is_set_the_desc_is_null_on_update_should_throw_exception() {
        var initialDesc = getValidDesc();
        var catalog = factory.create(initialDesc);

        assertNotNull(catalog);

        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(catalog, null);
        });
    }

    CatalogDesc getValidDesc() {
        var desc = new CatalogDesc();
        desc.setTitle("Default");
        desc.setDescription("This is the default catalog containing all "
                + "available resources.");

        return desc;
    }

    CatalogDesc getUpdatedDesc() {
        var desc = new CatalogDesc();
        desc.setDescription("The new description.");
        desc.setTitle("The new title.");

        return desc;
    }

    CatalogDesc getDescWithNullMembers() {
        var desc = new CatalogDesc();
        desc.setTitle(null);
        desc.setDescription(null);

        return desc;
    }
}
