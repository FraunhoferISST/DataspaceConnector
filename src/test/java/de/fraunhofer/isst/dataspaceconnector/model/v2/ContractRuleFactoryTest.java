package de.fraunhofer.isst.dataspaceconnector.model.v2;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContractRuleFactoryTest {

    private ContractRuleFactory factory;

    @BeforeEach
    public void init() {
        this.factory = new ContractRuleFactory();
    }

    @Test
    public void create_nullDesc_throwNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> factory.create(null));
    }

    @Test
    public void create_allDescMembersNotNull_returnContractRule() {
        /* ARRANGE */
        final var desc = getValidDesc();

        /* ACT */
        final var rule = factory.create(desc);

        /* ASSERT */
        assertNotNull(rule);
        assertEquals(desc.getTitle(), rule.getTitle());
        assertEquals(desc.getValue(), rule.getValue());

        assertNull(rule.getId());
        assertNull(rule.getCreationDate());
        assertNull(rule.getModificationDate());
    }

    @Test
    public void create_allDescMembersNull_returnDefaultContractRule() {
        /* ARRANGE */
        final var desc = getDescWithNullMembers();

        /* ACT */
        final var rule = factory.create(desc);

        /* ASSERT */
        assertNotNull(rule);
        assertNotNull(rule.getTitle());
        assertNotNull(rule.getValue());

        assertNull(rule.getId());
        assertNull(rule.getCreationDate());
        assertNull(rule.getModificationDate());
    }

    @Test
    public void update_allDescMembersNotNull_returnUpdatedContractRule() {
        /* ARRANGE */
        var rule = factory.create(getValidDesc());
        var idBefore = rule.getId();
        var creationDateBefore = rule.getCreationDate();
        var lastModificationDateBefore = rule.getModificationDate();
        var desc = getUpdatedDesc();

        /* ACT */
        factory.update(rule, desc);

        /* ASSERT */
        assertNotNull(rule);
        assertEquals(desc.getTitle(), rule.getTitle());
        assertEquals(desc.getValue(), rule.getValue());

        assertEquals(idBefore, rule.getId());
        assertEquals(creationDateBefore, rule.getCreationDate());
        assertEquals(lastModificationDateBefore,
                rule.getModificationDate());
    }

    @Test
    public void update_allDescMembersNull_returnDefaultContractRule() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var rule = factory.create(initialDesc);
        var idBefore = rule.getId();
        var creationDateBefore = rule.getCreationDate();
        var lastModificationDateBefore = rule.getModificationDate();
        var desc = getDescWithNullMembers();

        /* ACT */
        factory.update(rule, desc);

        /* ASSERT */
        assertNotNull(rule);
        assertNotNull(rule.getTitle());
        assertNotNull(rule.getValue());

        assertEquals(idBefore, rule.getId());
        assertEquals(creationDateBefore, rule.getCreationDate());
        assertEquals(lastModificationDateBefore,
                rule.getModificationDate());
    }

    @Test
    public void update_changeValidDesc_true() {
        /* ARRANGE */
        var rule = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertTrue(factory.update(rule, getUpdatedDesc()));
    }

    @Test
    public void update_sameValidDesc_false() {
        /* ARRANGE */
        var rule = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertFalse(factory.update(rule, getValidDesc()));
    }

    @Test
    public void update_nullResourceValidDesc_throwsNullPointerException() {
        /* ARRANGE */
        var desc = getValidDesc();

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> factory.update(null, desc));
    }

    @Test
    public void update_nullResourceNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> factory.update(null, null));
    }

    @Test
    public void update_validResourceNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var rule = factory.create(initialDesc);

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> factory.update(rule, null));
    }

    ContractRuleDesc getValidDesc() {
        var desc = new ContractRuleDesc();
        desc.setTitle("Default");
        desc.setValue("");

        return desc;
    }

    ContractRuleDesc getUpdatedDesc() {
        var desc = new ContractRuleDesc();
        desc.setTitle("The new default.");
        desc.setValue("none");

        return desc;
    }

    ContractRuleDesc getDescWithNullMembers() {
        var desc = new ContractRuleDesc();
        desc.setTitle(null);
        desc.setValue(null);

        return desc;
    }
}
