package de.fraunhofer.isst.dataspaceconnector.model.v2;

import de.fraunhofer.isst.dataspaceconnector.model.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogFactory;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class CatalogFactoryTest {

    private CatalogFactory factory;

    @Before
    public void init() {
        this.factory = new CatalogFactory();
    }

    @Test(expected = NullPointerException.class)
    public void create_nullDesc_throwNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        factory.create(null);
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
        assertNull(catalog.getLastModificationDate());
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
        assertNull(catalog.getLastModificationDate());
    }

    @Test
    public void update_allDescMembersNotNull_returnUpdatedCatalog() {
        /* ARRANGE */
        var catalog = factory.create(getValidDesc());

        assertNotNull(catalog);

        var idBefore = catalog.getId();
        var creationDateBefore = catalog.getCreationDate();
        var lastModificationDateBefore = catalog.getLastModificationDate();

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
                catalog.getLastModificationDate());
    }

    @Test
    public void update_allDescMembersNull_returnDefaultCatalog() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var catalog = factory.create(initialDesc);

        assertNotNull(catalog);

        var idBefore = catalog.getId();
        var creationDateBefore = catalog.getCreationDate();
        var lastModificationDateBefore = catalog.getLastModificationDate();

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
                catalog.getLastModificationDate());
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

    @Test(expected = NullPointerException.class)
    public void update_nullCatalogValidDesc_throwsNullPointerException() {
        /* ARRANGE */
        var desc = getValidDesc();

        /* ACT && ASSERT */
        factory.update(null, desc);
    }

    @Test(expected = NullPointerException.class)
    public void update_nullCatalogNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        factory.update(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void update_validCatalogNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var catalog = factory.create(initialDesc);

        assertNotNull(catalog);

        /* ACT && ASSERT */
        factory.update(catalog, null);
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
