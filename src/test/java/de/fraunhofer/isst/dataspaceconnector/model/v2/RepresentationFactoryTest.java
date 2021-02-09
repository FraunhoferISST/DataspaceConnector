package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RepresentationFactoryTest {
    @Autowired
    private RepresentationFactory factory;

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
    void create_allDescMembersNotNull_returnRepresentation() {
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
    void create_allDescMembersNull_returnDefaultRepresentation() {
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
    void update_allDescMembersNotNull_returnUpdatedRepresentation() {
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
    void update_allDescMembersNull_returnDefaultRepresentation() {
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
    void update_changeValidDesc_true() {
        /* ARRANGE */
        var representation = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertTrue(factory.update(representation, getUpdatedValidDesc()));
    }

    @Test
    void update_sameValidDesc_false() {
        /* ARRANGE */
        var representation = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertFalse(factory.update(representation, getValidDesc()));
    }

    @Test
    void update_nullRepresentationValidDesc_throwsNullPointerException() {
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
    void update_nullRepresentationNullDesc_throwsNullPointerException() {
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
    void update_validContractNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var representation = factory.create(initialDesc);

        assertNotNull(representation);

        /* ACT */
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(representation, null);
        });

        /* ASSERT */
        assertNotNull(exception);
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
