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
    void when_passed_desc_is_null_on_creation_should_throw_exception() {
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.create(null);
        });

        assertNotNull(exception);
    }

    @Test
    void when_all_desc_members_are_set_representation_should_be_created() {
        final var desc = getValidDesc();
        final var representation = factory.create(desc);

        assertNotNull(representation);
        assertEquals(desc.getTitle(), representation.getTitle());
        assertEquals(desc.getLanguage(), representation.getLanguage());
        assertEquals(desc.getType(), representation.getMediaType());

        assertNull(representation.getId());
        assertNull(representation.getCreationDate());
        assertNull(representation.getLastModificationDate());
    }

    @Test
    void when_all_desc_members_are_null_the_representation_should_be_created() {
        final var desc = getDescWithNullMembers();
        final var representation = factory.create(desc);

        assertNotNull(representation);
        assertNotNull(representation.getTitle());
        assertNotNull(representation.getLanguage());
        assertNotNull(representation.getMediaType());

        assertNull(representation.getId());
        assertNull(representation.getCreationDate());
        assertNull(representation.getLastModificationDate());
    }

    @Test
    void when_all_desc_members_are_set_the_representation_should_be_updated() {
        var representation = factory.create(getValidDesc());

        assertNotNull(representation);

        var idBefore = representation.getId();
        var creationDateBefore = representation.getCreationDate();
        var lastModificationDateBefore =
                representation.getLastModificationDate();

        var desc = getUpdatedDesc();
        factory.update(representation, desc);

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
    void when_all_desc_members_are_null_the_representation_should_be_updated() {
        var initialDesc = getValidDesc();
        var representation = factory.create(initialDesc);

        assertNotNull(representation);

        var idBefore = representation.getId();
        var creationDateBefore = representation.getCreationDate();
        var lastModificationDateBefore =
                representation.getLastModificationDate();

        var desc = getDescWithNullMembers();
        factory.update(representation, desc);

        assertNotNull(representation);
        assertNotNull(representation.getTitle());
        assertNotNull(representation.getLanguage());
        assertNotNull(representation.getMediaType());

        assertEquals(idBefore, representation.getId());
        assertEquals(creationDateBefore, representation.getCreationDate());
        assertEquals(lastModificationDateBefore,
                representation.getLastModificationDate());
    }

    @Test
    void when_the_representation_is_null_the_desc_is_set_on_update_should_throw_exception() {
        var desc = getValidDesc();
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, desc);
        });
    }

    @Test
    void when_the_representation_is_null_the_desc_is_null_on_update_should_throw_exception() {
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, null);
        });
    }

    @Test
    void when_the_representation_is_set_the_desc_is_null_on_update_should_throw_exception() {
        var initialDesc = getValidDesc();
        var representation = factory.create(initialDesc);

        assertNotNull(representation);

        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(representation, null);
        });
    }

    @Test
    void when_representation_update_does_result_in_change_return_true() {
        var representation = factory.create(getValidDesc());
        assertTrue(factory.update(representation, getUpdatedDesc()));
    }

    @Test
    void when_representation_update_does_result_in_no_change_return_false() {
        var representation = factory.create(getValidDesc());
        assertFalse(factory.update(representation, getValidDesc()));
    }

    RepresentationDesc getValidDesc() {
        var desc = new RepresentationDesc();
        desc.setTitle("Default");
        desc.setLanguage("English");
        desc.setType("TXT");

        return desc;
    }

    RepresentationDesc getUpdatedDesc() {
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
