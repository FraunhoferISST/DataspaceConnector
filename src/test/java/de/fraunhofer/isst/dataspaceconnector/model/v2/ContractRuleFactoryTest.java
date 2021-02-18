package de.fraunhofer.isst.dataspaceconnector.model.v2;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleFactory;
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
public class ContractRuleFactoryTest {

    private ContractRuleFactory factory;

    @Before
    public void init() {
        this.factory = new ContractRuleFactory();
    }

    @Test(expected = NullPointerException.class)
    public void create_nullDesc_throwNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        factory.create(null);
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
        assertEquals(desc.getRule(), rule.getValue());

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

        assertNotNull(rule);

        var idBefore = rule.getId();
        var creationDateBefore = rule.getCreationDate();
        var lastModificationDateBefore = rule.getModificationDate();

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
                rule.getModificationDate());
    }

    @Test
    public void update_allDescMembersNull_returnDefaultContractRule() {
        /* ARRANGE */
        var initialDesc = getValidDesc();
        var rule = factory.create(initialDesc);

        assertNotNull(rule);

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
        var rule = factory.create(initialDesc);

        assertNotNull(rule);

        /* ACT && ASSERT */
        factory.update(rule, null);
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
