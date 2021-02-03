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
    void create_nullDesc_throwNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.create(null);
        });

        /* ASSERT */
        assertNotNull(exception);
    }

    @Test
    void create_allDescMembersNotNull_returnCatalog() {
        /* ARRANGE */
        final var desc = getValidDesc();

        /* ACT */
        final var catalog = factory.create(desc);

        /* ASSERT */
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
    void create_allDescMembersNull_returnDefaultCatalog() {
        /* ARRANGE */
        final var desc = getDescWithNullMembers();

        /* ACT */
        final var catalog = factory.create(desc);

        /* ASSERT */
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
    void update_allDescMembersNotNull_returnUpdatedCatalog() {
        /* ARRANGE */
        var catalog = factory.create(getValidDesc());

        assertNotNull(catalog);

        var idBefore = catalog.getId();
        var creationDateBefore = catalog.getCreationDate();
        var lastModificationDateBefore = catalog.getLastModificationDate();

        var resourcesBefore =
                ((HashMap<UUID, Resource>) catalog.getResources()).clone();
        var desc = getUpdatedValidDesc();

        /* ACT */
        factory.update(catalog, desc);

        /* ASSERT */
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
    void update_allDescMembersNull_returnDefaultCatalog() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var catalog = factory.create(initialDesc);

        assertNotNull(catalog);

        var idBefore = catalog.getId();
        var creationDateBefore = catalog.getCreationDate();
        var lastModificationDateBefore = catalog.getLastModificationDate();

        var resourcesBefore =
                ((HashMap<UUID, Resource>) catalog.getResources()).clone();
        var desc = getDescWithNullMembers();

        /* ACT */
        factory.update(catalog, desc);

        /* ASSERT */
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
    void update_changeValidDesc_true() {
        /* ARRANGE */
        var catalog = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertTrue(factory.update(catalog, getUpdatedValidDesc()));
    }

    @Test
    void update_SameValidDesc_true() {
        /* ARRANGE */
        var catalog = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertFalse(factory.update(catalog, getValidDesc()));
    }

    @Test
    void update_nullCatalogValidDesc_throwsNullPointerException() {
        /* ARRANGE */
        var desc = getValidDesc();

        /* ACT */
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, desc);
        });

        /* ASSERT */
        assertNotNull(exception);
    }

    @Test
    void update_nullCatalogNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, null);
        });

        /* ASSERT */
        assertNotNull(exception);
    }

    @Test
    void update_ValidCatalogNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var catalog = factory.create(initialDesc);

        assertNotNull(catalog);

        /* ACT */
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(catalog, null);
        });

        /* ASSERT */
        assertNotNull(exception);
    }

    CatalogDesc getValidDesc() {
        var desc = new CatalogDesc();
        desc.setTitle("Default");
        desc.setDescription("This is the default catalog containing all "
                + "available resources.");

        return desc;
    }

    CatalogDesc getUpdatedValidDesc() {
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
