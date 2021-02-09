package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(JUnit4.class)
class ContractRuleFactoryTest {

    private ContractRuleFactory factory;

    @BeforeEach
    public void init() {
        this.factory = new ContractRuleFactory();
    }

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
    void create_allDescMembersNotNull_returnContractRule() {
        /* ARRANGE */
        final var desc = getValidDesc();

        /* ACT */
        final var rule = factory.create(desc);

        /* ASSERT */
        assertNotNull(rule);
        assertEquals(desc.getTitle(), rule.getTitle());
        assertEquals(desc.getRule(), rule.getValue());

        assertNull(rule.getId());
        assertNull(rule.getCreationDate());
        assertNull(rule.getLastModificationDate());
    }

    @Test
    void create_allDescMembersNull_returnDefaultContractRule() {
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
        assertNull(rule.getLastModificationDate());
    }

    @Test
    void update_allDescMembersNotNull_returnUpdatedContractRule() {
        /* ARRANGE */
        var rule = factory.create(getValidDesc());

        assertNotNull(rule);

        var idBefore = rule.getId();
        var creationDateBefore = rule.getCreationDate();
        var lastModificationDateBefore = rule.getLastModificationDate();

        var desc = getUpdatedDesc();

        /* ACT */
        factory.update(rule, desc);

        /* ASSERT */
        assertNotNull(rule);
        assertEquals(desc.getTitle(), rule.getTitle());
        assertEquals(desc.getRule(), rule.getValue());

        assertEquals(idBefore, rule.getId());
        assertEquals(creationDateBefore, rule.getCreationDate());
        assertEquals(lastModificationDateBefore,
                rule.getLastModificationDate());
    }

    @Test
    void update_allDescMembersNull_returnDefaultContractRule() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var rule = factory.create(initialDesc);

        assertNotNull(rule);

        var idBefore = rule.getId();
        var creationDateBefore = rule.getCreationDate();
        var lastModificationDateBefore = rule.getLastModificationDate();

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
                rule.getLastModificationDate());
    }

    @Test
    void update_changeValidDesc_true() {
        /* ARRANGE */
        var rule = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertTrue(factory.update(rule, getUpdatedDesc()));
    }

    @Test
    void update_sameValidDesc_false() {
        /* ARRANGE */
        var rule = factory.create(getValidDesc());

        /* ACT && ASSERT */
        assertFalse(factory.update(rule, getValidDesc()));
    }

    @Test
    void update_nullResourceValidDesc_throwsNullPointerException() {
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
    void update_nullResourceNullDesc_throwsNullPointerException() {
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
    void update_validResourceNullDesc_throwsNullPointerException() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var rule = factory.create(initialDesc);

        assertNotNull(rule);

        /* ACT */
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(rule, null);
        });

        /* ASSERT */
        assertNotNull(exception);
    }

    ContractRuleDesc getValidDesc() {
        var desc = new ContractRuleDesc();
        desc.setTitle("Default");
        desc.setRule("");

        return desc;
    }

    ContractRuleDesc getUpdatedDesc() {
        var desc = new ContractRuleDesc();
        desc.setTitle("The new default.");
        desc.setRule("none");

        return desc;
    }

    ContractRuleDesc getDescWithNullMembers() {
        var desc = new ContractRuleDesc();
        desc.setTitle(null);
        desc.setRule(null);

        return desc;
    }
}
