package de.fraunhofer.isst.dataspaceconnector.model;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    public void default_title_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", CatalogFactory.DEFAULT_TITLE);
    }

    @Test
    public void default_description_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", CatalogFactory.DEFAULT_DESCRIPTION);
    }

    @Test
    public void create_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.create(null));
    }

    @Test
    public void create_validDesc_creationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new CatalogDesc());

        /* ASSERT */
        assertNull(result.getCreationDate());
    }

    @Test
    public void create_validDesc_modificationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new CatalogDesc());

        /* ASSERT */
        assertNull(result.getModificationDate());
    }

    @Test
    public void create_validDesc_idNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new CatalogDesc());

        /* ASSERT */
        assertNull(result.getId());
    }

    @Test
    public void create_validDesc_offeredResourcesEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new CatalogDesc());

        /* ASSERT */
        assertEquals(0, result.getOfferedResources().size());
    }

    @Test
    public void create_validDesc_requestedResourcesEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new CatalogDesc());

        /* ASSERT */
        assertEquals(0, result.getRequestedResources().size());
    }

    /**
     * title.
     */

    @Test
    public void create_nullTitle_defaultTitle() {
        /* ARRANGE */
        final var desc = new CatalogDesc();
        desc.setTitle(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(CatalogFactory.DEFAULT_TITLE, result.getTitle());
    }

    @Test
    public void update_differentTitle_setTitle() {
        /* ARRANGE */
        final var desc = new CatalogDesc();
        desc.setTitle("Random Title");

        final var catalog = factory.create(new CatalogDesc());

        /* ACT */
        factory.update(catalog, desc);

        /* ASSERT */
        assertEquals(desc.getTitle(), catalog.getTitle());
    }

    @Test
    public void update_differentTitle_returnTrue() {
        /* ARRANGE */
        final var desc = new CatalogDesc();
        desc.setTitle("Random Title");

        final var catalog = factory.create(new CatalogDesc());

        /* ACT */
        final var result = factory.update(catalog, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameTitle_returnFalse() {
        /* ARRANGE */
        final var catalog = factory.create(new CatalogDesc());

        /* ACT */
        final var result = factory.update(catalog, new CatalogDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * desc.
     */

    @Test
    public void create_nullDescription_defaultDescription() {
        /* ARRANGE */
        final var desc = new CatalogDesc();
        desc.setDescription(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(CatalogFactory.DEFAULT_DESCRIPTION, result.getDescription());
    }

    @Test
    public void update_differentDescription_setDescription() {
        /* ARRANGE */
        final var desc = new CatalogDesc();
        desc.setDescription("Random Desc");

        final var catalog = factory.create(new CatalogDesc());

        /* ACT */
        factory.update(catalog, desc);

        /* ASSERT */
        assertEquals(desc.getDescription(), catalog.getDescription());
    }

    @Test
    public void update_differentDescription_returnTrue() {
        /* ARRANGE */
        final var desc = new CatalogDesc();
        desc.setDescription("Random Desc");

        final var catalog = factory.create(new CatalogDesc());

        /* ACT */
        final var result = factory.update(catalog, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameDescription_returnFalse() {
        /* ARRANGE */
        final var catalog = factory.create(new CatalogDesc());

        /* ACT */
        final var result = factory.update(catalog, new CatalogDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * additional.
     */

    @Test
    public void create_nullAdditional_defaultAdditional() {
        /* ARRANGE */
        final var desc = new CatalogDesc();
        desc.setAdditional(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(new HashMap<>(), result.getAdditional());
    }

    @Test
    public void update_differentAdditional_setAdditional() {
        /* ARRANGE */
        final var desc = new CatalogDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var catalog = factory.create(new CatalogDesc());

        /* ACT */
        factory.update(catalog, desc);

        /* ASSERT */
        assertEquals(desc.getAdditional(), catalog.getAdditional());
    }

    @Test
    public void update_differentAdditional_returnTrue() {
        /* ARRANGE */
        final var desc = new CatalogDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var catalog = factory.create(new CatalogDesc());

        /* ACT */
        final var result = factory.update(catalog, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameAdditional_returnFalse() {
        /* ARRANGE */
        final var catalog = factory.create(new CatalogDesc());

        /* ACT */
        final var result = factory.update(catalog, new CatalogDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * update inputs.
     */

    @Test
    public void update_nullCatalog_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(null, new CatalogDesc()));
    }

    @Test
    public void update_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        final var catalog = factory.create(new CatalogDesc());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(catalog, null));
    }
}
