package de.fraunhofer.isst.dataspaceconnector.model;

import java.util.ArrayList;

import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractFactory;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContractFactoryTest {

    private ContractFactory factory;

    @BeforeEach
    public void init() {
        this.factory = new ContractFactory();
    }

    @Test
    public void create_nullDesc_throwNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> factory.create(null));
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
        assertNull(contract.getModificationDate());
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
        assertNull(contract.getModificationDate());
    }

    @Test
    public void update_allDescMembersNotNull_returnUpdatedContract() {
        /* ARRANGE */
        var contract = factory.create(getValidDesc());
        var idBefore = contract.getId();
        var creationDateBefore = contract.getCreationDate();
        var lastModificationDateBefore = contract.getModificationDate();
        var rulesBefore = ((ArrayList<ContractRule>) contract.getRules()).clone();
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
                contract.getModificationDate());
    }

    @Test
    public void update_allDescMembersNull_returnDefaultContract() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var contract = factory.create(initialDesc);
        var idBefore = contract.getId();
        var creationDateBefore = contract.getCreationDate();
        var lastModificationDateBefore = contract.getModificationDate();
        var rulesBefore = ((ArrayList<ContractRule>) contract.getRules()).clone();
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
                contract.getModificationDate());
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
    @Test
    public void update_nullContractValidDesc_throwsNullPointerException() {
        /* ARRANGE */
        var desc = getValidDesc();

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> factory.update(null, desc));
    }

    @Test
    public void update_nullContractNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> factory.update(null, null));
    }

    @Test
    public void update_validContractNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var catalog = factory.create(initialDesc);

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> factory.update(catalog, null));
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
