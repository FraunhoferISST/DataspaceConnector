package de.fraunhofer.isst.dataspaceconnector.model.v2;

import java.util.HashMap;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogFactory;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CatalogFactoryTest {

    private CatalogFactory factory;

    @BeforeEach
    public void init() {
        this.factory = new CatalogFactory();
    }

    @Test
    public void create_nullDesc_throwNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> factory.create(null));
    }

    @Test
    public void create_allDescMembersNotNull_returnCatalog() {
        /* ARRANGE */
        final var desc = getValidDesc();

        /* ACT */
        final var catalog = factory.create(desc);

        /* ASSERT */
        assertNotNull(catalog);
        assertEquals(desc.getTitle(), catalog.getTitle());
        assertEquals(desc.getDescription(), catalog.getDescription());
        assertNotNull(catalog.getOfferedResources());
        assertEquals(catalog.getOfferedResources().size(), 0);
        assertNotNull(catalog.getRequestedResources());
        assertEquals(catalog.getRequestedResources().size(), 0);

        assertNull(catalog.getId());
        assertNull(catalog.getCreationDate());
        assertNull(catalog.getModificationDate());
    }

    @Test
    public void create_allDescMembersNull_returnDefaultCatalog() {
        /* ARRANGE */
        final var desc = getDescWithNullMembers();

        /* ACT */
        final var catalog = factory.create(desc);

        /* ASSERT */
        assertNotNull(catalog);
        assertNotNull(catalog.getTitle());
        assertNotNull(catalog.getDescription());
        assertNotNull(catalog.getOfferedResources());
        assertEquals(catalog.getOfferedResources().size(), 0);
        assertNotNull(catalog.getRequestedResources());
        assertEquals(catalog.getRequestedResources().size(), 0);

        assertNull(catalog.getId());
        assertNull(catalog.getCreationDate());
        assertNull(catalog.getModificationDate());
    }

    @Test
    public void update_allDescMembersNotNull_returnUpdatedCatalog() {
        /* ARRANGE */
        var catalog = factory.create(getValidDesc());
        var idBefore = catalog.getId();
        var creationDateBefore = catalog.getCreationDate();
        var lastModificationDateBefore = catalog.getModificationDate();

        var offeredResourcesBefore = ((HashMap<UUID, OfferedResource>) catalog.getOfferedResources()).clone();
        var requestedResourcesBefore = ((HashMap<UUID, RequestedResource>) catalog.getRequestedResources()).clone();
        var desc = getUpdatedValidDesc();

        /* ACT */
        factory.update(catalog, desc);

        /* ASSERT */
        assertNotNull(catalog);
        assertEquals(desc.getTitle(), catalog.getTitle());
        assertEquals(desc.getDescription(), catalog.getDescription());
        assertNotNull(catalog.getOfferedResources());
        assertEquals(offeredResourcesBefore, catalog.getOfferedResources());
        assertNotNull(catalog.getRequestedResources());
        assertEquals(requestedResourcesBefore, catalog.getRequestedResources());

        assertEquals(idBefore, catalog.getId());
        assertEquals(creationDateBefore, catalog.getCreationDate());
        assertEquals(lastModificationDateBefore,
                catalog.getModificationDate());
    }

    @Test
    public void update_allDescMembersNull_returnDefaultCatalog() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var catalog = factory.create(initialDesc);
        var idBefore = catalog.getId();
        var creationDateBefore = catalog.getCreationDate();
        var lastModificationDateBefore = catalog.getModificationDate();

        var offeredResourcesBefore = ((HashMap<UUID, OfferedResource>) catalog.getOfferedResources()).clone();
        var requestedResourcesBefore = ((HashMap<UUID, RequestedResource>) catalog.getRequestedResources()).clone();
        var desc = getDescWithNullMembers();

        /* ACT */
        factory.update(catalog, desc);

        /* ASSERT */
        assertNotNull(catalog);
        assertNotNull(catalog.getTitle());
        assertNotNull(catalog.getDescription());
        assertNotEquals(initialDesc.getTitle(), catalog.getTitle());
        assertNotEquals(initialDesc.getDescription(), catalog.getDescription());
        assertNotNull(catalog.getOfferedResources());
        assertEquals(offeredResourcesBefore, catalog.getOfferedResources());
        assertNotNull(catalog.getRequestedResources());
        assertEquals(requestedResourcesBefore, catalog.getRequestedResources());

        assertEquals(idBefore, catalog.getId());
        assertEquals(creationDateBefore, catalog.getCreationDate());
        assertEquals(lastModificationDateBefore,
                catalog.getModificationDate());
    }

    @Test
    public void update_changeValidDesc_true() {
        /* ARRANGE */
        var catalog = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertTrue(factory.update(catalog, getUpdatedValidDesc()));
    }

    @Test
    public void update_sameValidDesc_false() {
        /* ARRANGE */
        var catalog = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertFalse(factory.update(catalog, getValidDesc()));
    }

    @Test
    public void update_nullCatalogValidDesc_throwsNullPointerException() {
        /* ARRANGE */
        var desc = getValidDesc();

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> factory.update(null, desc));
    }

    @Test
    public void update_nullCatalogNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> factory.update(null, null));
    }

    @Test
    public void update_validCatalogNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var catalog = factory.create(initialDesc);

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> factory.update(catalog, null));
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
