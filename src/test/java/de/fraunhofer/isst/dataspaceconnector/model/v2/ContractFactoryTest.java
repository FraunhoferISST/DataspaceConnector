package de.fraunhofer.isst.dataspaceconnector.model.v2;

import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractFactory;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class ContractFactoryTest {

    private ContractFactory factory;

    @Before
    public void init() {
        this.factory = new ContractFactory();
    }

    @Test(expected = NullPointerException.class)
    public void create_nullDesc_throwNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        factory.create(null);
    }

    @Test
    public void create_allDescMembersNotNull_returnContract() {
        /* ARRANGE */
        final var desc = getValidDesc();

        /* ACT */
        final var contract = factory.create(desc);

        /* ASSERT */
        assertNotNull(contract);
        assertEquals(desc.getTitle(), contract.getTitle());
        assertNotNull(contract.getRules());
        assertEquals(contract.getRules().size(), 0);

        assertNull(contract.getId());
        assertNull(contract.getCreationDate());
        assertNull(contract.getLastModificationDate());
    }

    @Test
    public void create_allDescMembersNull_returnDefaultContract() {
        /* ARRANGE */
        final var desc = getDescWithNullMembers();

        /* ACT */
        final var contract = factory.create(desc);

        /* ASSERT */
        assertNotNull(contract);
        assertNotNull(contract.getTitle());
        assertNotNull(contract.getRules());
        assertEquals(contract.getRules().size(), 0);

        assertNull(contract.getId());
        assertNull(contract.getCreationDate());
        assertNull(contract.getLastModificationDate());
    }

    @Test
    public void update_allDescMembersNotNull_returnUpdatedContract() {
        /* ARRANGE */
        var contract = factory.create(getValidDesc());

        assertNotNull(contract);

        var idBefore = contract.getId();
        var creationDateBefore = contract.getCreationDate();
        var lastModificationDateBefore = contract.getLastModificationDate();

        var rulesBefore = ((HashMap<UUID, ContractRule>) contract.getRules()).clone();
        var desc = getUpdatedValidDesc();

        /* ACT */
        factory.update(contract, desc);

        /* ASSERT */
        assertNotNull(contract);
        assertEquals(desc.getTitle(), contract.getTitle());
        assertNotNull(contract.getRules());
        assertEquals(rulesBefore, contract.getRules());

        assertEquals(idBefore, contract.getId());
        assertEquals(creationDateBefore, contract.getCreationDate());
        assertEquals(lastModificationDateBefore,
                contract.getLastModificationDate());
    }

    @Test
    public void update_allDescMembersNull_returnDefaultContract() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var contract = factory.create(initialDesc);

        assertNotNull(contract);

        var idBefore = contract.getId();
        var creationDateBefore = contract.getCreationDate();
        var lastModificationDateBefore = contract.getLastModificationDate();

        var rulesBefore = ((HashMap<UUID, ContractRule>) contract.getRules()).clone();
        var desc = getDescWithNullMembers();

        /* ACT */
        factory.update(contract, desc);

        /* ASSERT */
        assertNotNull(contract);
        assertNotNull(contract.getTitle());
        assertNotNull(contract.getRules());
        assertEquals(rulesBefore, contract.getRules());

        assertEquals(idBefore, contract.getId());
        assertEquals(creationDateBefore, contract.getCreationDate());
        assertEquals(lastModificationDateBefore,
                contract.getLastModificationDate());
    }

    @Test
    public void update_changeValidDesc_true() {
        /* ARRANGE */
        var contract = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertTrue(factory.update(contract, getUpdatedValidDesc()));
    }

    @Test
    public void update_sameValidDesc_false() {
        /* ARRANGE */
        var contract = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertFalse(factory.update(contract, getValidDesc()));
    }
    @Test(expected = NullPointerException.class)
    public void update_nullContractValidDesc_throwsNullPointerException() {
        /* ARRANGE */
        var desc = getValidDesc();

        /* ACT && ASSERT */
        factory.update(null, desc);
    }

    @Test(expected = NullPointerException.class)
    public void update_nullContractNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        factory.update(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void update_validContractNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var catalog = factory.create(initialDesc);

        assertNotNull(catalog);

        /* ACT && ASSERT */
        factory.update(catalog, null);
    }

    ContractDesc getValidDesc() {
        var desc = new ContractDesc();
        desc.setTitle("Default");

        return desc;
    }

    ContractDesc getUpdatedValidDesc() {
        var desc = new ContractDesc();
        desc.setTitle("The new default.");

        return desc;
    }

    ContractDesc getDescWithNullMembers() {
        var desc = new ContractDesc();
        desc.setTitle(null);

        return desc;
    }
}
