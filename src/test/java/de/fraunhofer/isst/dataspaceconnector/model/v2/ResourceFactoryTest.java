package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ResourceFactoryTest {

    private ResourceFactory factory;

    @Before
    public void init() {
        this.factory = new ResourceFactory();
    }

    @Test(expected = NullPointerException.class)
    public void create_nullDesc_throwNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        factory.create(null);
    }

    @Test
    public void create_allDescMembersNotNull_returnResource() {
        /* ARRANGE */
        final var desc = getValidDesc();

        /* ACT */
        final var resource = factory.create(desc);

        /* ASSERT */
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
    public void create_KeywordsContainsNullOrEmpty_returnFilteredKeywordsResource() {
        /* ARRANGE */
        final var desc = getValidDescKeywordsContainsNullOrEmpty();

        /* ACT */
        final var resource = factory.create(desc);

        /* ASSERT */
        assertEquals(getValidDesc().getKeywords(), resource.getKeywords());
    }

    @Test
    public void create_allDescMembersNull_returnDefaultResource() {
        /* ARRANGE */
        final var desc = getDescWithNullMembers();

        /* ACT */
        final var resource = factory.create(desc);

        /* ASSERT */
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
    public void update_allDescMembersNotNull_returnUpdatedResource() {
        /* ARRANGE */
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

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
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
    public void update_KeywordsContainsNullOrEmpty_returnFilteredKeywordsResource() {
        /* ARRANGE */
        var resource = factory.create(getUpdatedDesc());
        final var desc = getValidDescKeywordsContainsNullOrEmpty();

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
        assertEquals(getValidDesc().getKeywords(), resource.getKeywords());
    }

    @Test
    public void update_allDescMembersNull_returnDefaultResource() {
        /* ARRANGE */
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

        /* ACT */
        factory.update(resource, desc);

        /* ASSERT */
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
    public void update_changeValidDesc_true() {
        /* ARRANGE */
        var resource = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertTrue(factory.update(resource, getUpdatedDesc()));
    }

    @Test
    public void update_sameValidDesc_false() {
        /* ARRANGE */
        var resource = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertFalse(factory.update(resource, getValidDesc()));
    }

    @Test(expected = NullPointerException.class)
    public void update_nullResourceValidDesc_throwsNullPointerException() {
        /* ARRANGE */
        var desc = getValidDesc();

        /* ACT && ASSERT */
        factory.update(null, desc);
    }

    @Test(expected = NullPointerException.class)
    public void update_nullResourceNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        factory.update(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void update_validResourceNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var resource = factory.create(initialDesc);

        assertNotNull(resource);

        /* ACT && ASSERT */
        factory.update(resource, null);
    }

    ResourceDesc getValidDesc() {
        var desc = new ResourceDesc();
        desc.setTitle("Default");
        desc.setDescription("This is the default catalog containing all "
                + "available resources.");
        desc.setKeywords(new ArrayList<>(Arrays.asList("Hello", "World")));
        desc.setPublisher(URI.create("Someone"));
        desc.setLanguage("English");
        desc.setLicence(URI.create("MIT"));

        return desc;
    }

    ResourceDesc getValidDescKeywordsContainsNullOrEmpty() {
        var desc = getValidDesc();

        final var rnd = new Random();
        // Add N times null
        final var numNull = rnd.nextInt(32);
        for(int i = 0; i < numNull; i++)
            desc.getKeywords().add(rnd.nextInt(desc.getKeywords().size() - 1), null);

        // Add M times empty
        final var numEmpty = rnd.nextInt(32);
        for(int i = 0; i < numEmpty; i++)
            desc.getKeywords().add(rnd.nextInt(desc.getKeywords().size() - 1), "");

        return desc;
    }

    ResourceDesc getUpdatedDesc() {
        var desc = new ResourceDesc();
        desc.setTitle("The new default");
        desc.setDescription("The new description");
        desc.setKeywords(new ArrayList<>(Collections.singletonList("Greetings")));
        desc.setPublisher(URI.create("SomeoneElse"));
        desc.setLanguage("German");
        desc.setLicence(URI.create("Apache"));

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
