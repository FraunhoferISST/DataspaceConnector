package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class RepresentationFactoryTest {

    private RepresentationFactory factory;

    @Before
    public void init() {
        this.factory = new RepresentationFactory();
    }

    @Test(expected = NullPointerException.class)
    public void create_nullDesc_throwNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        factory.create(null);
    }

    @Test
    public void create_allDescMembersNotNull_returnRepresentation() {
        /* ARRANGE */
        final var desc = getValidDesc();

        /* ACT */
        final var representation = factory.create(desc);

        /* ASSERT */
        assertNotNull(representation);
        assertEquals(desc.getTitle(), representation.getTitle());
        assertEquals(desc.getLanguage(), representation.getLanguage());
        assertEquals(desc.getType(), representation.getMediaType());
        assertNotNull(representation.getArtifacts());
        assertEquals(representation.getArtifacts().size(), 0);

        assertNull(representation.getId());
        assertNull(representation.getCreationDate());
        assertNull(representation.getLastModificationDate());
    }

    @Test
    public void create_allDescMembersNull_returnDefaultRepresentation() {
        /* ARRANGE */
        final var desc = getDescWithNullMembers();

        /* ACT */
        final var representation = factory.create(desc);

        /* ASSERT */
        assertNotNull(representation);
        assertNotNull(representation.getTitle());
        assertNotNull(representation.getLanguage());
        assertNotNull(representation.getMediaType());
        assertNotNull(representation.getArtifacts());
        assertEquals(representation.getArtifacts().size(), 0);

        assertNull(representation.getId());
        assertNull(representation.getCreationDate());
        assertNull(representation.getLastModificationDate());
    }

    @Test
    public void update_allDescMembersNotNull_returnUpdatedRepresentation() {
        /* ARRANGE */
        var representation = factory.create(getValidDesc());

        assertNotNull(representation);

        var idBefore = representation.getId();
        var creationDateBefore = representation.getCreationDate();
        var lastModificationDateBefore =
                representation.getLastModificationDate();

        var desc = getUpdatedValidDesc();

        /* ACT */
        factory.update(representation, desc);

        /* ASSERT */
        assertNotNull(representation);
        assertEquals(desc.getTitle(), representation.getTitle());
        assertEquals(desc.getLanguage(), representation.getLanguage());
        assertEquals(desc.getType(), representation.getMediaType());

        assertEquals(idBefore, representation.getId());
        assertEquals(creationDateBefore, representation.getCreationDate());
        assertEquals(lastModificationDateBefore,
                representation.getLastModificationDate());
    }

    @Test
    public void update_allDescMembersNull_returnDefaultRepresentation() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var representation = factory.create(initialDesc);

        assertNotNull(representation);

        var idBefore = representation.getId();
        var creationDateBefore = representation.getCreationDate();
        var lastModificationDateBefore =
                representation.getLastModificationDate();

        var desc = getDescWithNullMembers();

        /* ACT */
        factory.update(representation, desc);

        /* ASSERT */
        assertNotNull(representation);
        assertNotNull(representation.getTitle());
        assertNotNull(representation.getLanguage());
        assertNotNull(representation.getMediaType());
        assertNotNull(representation.getArtifacts());

        assertEquals(idBefore, representation.getId());
        assertEquals(creationDateBefore, representation.getCreationDate());
        assertEquals(lastModificationDateBefore,
                representation.getLastModificationDate());
    }

    @Test
    public void update_changeValidDesc_true() {
        /* ARRANGE */
        var representation = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertTrue(factory.update(representation, getUpdatedValidDesc()));
    }

    @Test
    public void update_sameValidDesc_false() {
        /* ARRANGE */
        var representation = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertFalse(factory.update(representation, getValidDesc()));
    }

    @Test(expected = NullPointerException.class)
    public void update_nullRepresentationValidDesc_throwsNullPointerException() {
        /* ARRANGE */
        var desc = getValidDesc();

        /* ACT && ASSERT */
        factory.update(null, desc);
    }

    @Test(expected = NullPointerException.class)
    public void update_nullRepresentationNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        factory.update(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void update_validContractNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var representation = factory.create(initialDesc);

        assertNotNull(representation);

        /* ACT && ASSERT */
        factory.update(representation, null);
    }

    RepresentationDesc getValidDesc() {
        var desc = new RepresentationDesc();
        desc.setTitle("Default");
        desc.setLanguage("English");
        desc.setType("TXT");

        return desc;
    }

    RepresentationDesc getUpdatedValidDesc() {
        var desc = new RepresentationDesc();
        desc.setTitle("The new default.");
        desc.setLanguage("German");
        desc.setType("JSON");

        return desc;
    }

    RepresentationDesc getDescWithNullMembers() {
        var desc = new RepresentationDesc();
        desc.setTitle(null);
        desc.setLanguage(null);
        desc.setType(null);

        return desc;
    }
}
