package de.fraunhofer.isst.dataspaceconnector.model;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    public void default_title_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", ContractRuleFactory.DEFAULT_TITLE);
    }

    @Test
    public void default_remoteId_is_genesis() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create("genesis"), ContractRuleFactory.DEFAULT_REMOTE_ID);
    }

    @Test
    public void default_rule_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", ContractRuleFactory.DEFAULT_RULE);
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
        final var result = factory.create(new ContractRuleDesc());

        /* ASSERT */
        assertNull(result.getCreationDate());
    }

    @Test
    public void create_validDesc_modificationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new ContractRuleDesc());

        /* ASSERT */
        assertNull(result.getModificationDate());
    }

    @Test
    public void create_validDesc_idNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new ContractRuleDesc());

        /* ASSERT */
        assertNull(result.getId());
    }

    @Test
    public void create_validDesc_contractsEmpty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = factory.create(new ContractRuleDesc());

        /* ASSERT */
        assertEquals(0, result.getContracts().size());
    }

    /**
     * remoteId.
     */

    @Test
    public void create_nullRemoteId_defaultRemoteId() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setRemoteId(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ContractRuleFactory.DEFAULT_REMOTE_ID, result.getRemoteId());
    }

    @Test
    public void update_differentRemoteId_setRemoteId() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setRemoteId(URI.create("uri"));

        final var contractRule = factory.create(new ContractRuleDesc());

        /* ACT */
        factory.update(contractRule, desc);

        /* ASSERT */
        assertEquals(desc.getRemoteId(), contractRule.getRemoteId());
    }

    @Test
    public void update_differentRemoteId_returnTrue() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setRemoteId(URI.create("uri"));

        final var contractRule = factory.create(new ContractRuleDesc());

        /* ACT */
        final var result = factory.update(contractRule, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameRemoteId_returnFalse() {
        /* ARRANGE */
        final var contractRule = factory.create(new ContractRuleDesc());

        /* ACT */
        final var result = factory.update(contractRule, new ContractRuleDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * title.
     */

    @Test
    public void create_nullTitle_defaultTitle() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setTitle(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ContractRuleFactory.DEFAULT_TITLE, result.getTitle());
    }

    @Test
    public void update_differentTitle_setTitle() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setTitle("Random Title");

        final var contractRule = factory.create(new ContractRuleDesc());

        /* ACT */
        factory.update(contractRule, desc);

        /* ASSERT */
        assertEquals(desc.getTitle(), contractRule.getTitle());
    }

    @Test
    public void update_differentTitle_returnTrue() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setTitle("Random Title");

        final var contractRule = factory.create(new ContractRuleDesc());

        /* ACT */
        final var result = factory.update(contractRule, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameTitle_returnFalse() {
        /* ARRANGE */
        final var contractRule = factory.create(new ContractRuleDesc());

        /* ACT */
        final var result = factory.update(contractRule, new ContractRuleDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * rule.
     */

    @Test
    public void create_nullRule_defaultRule() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setValue(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(ContractRuleFactory.DEFAULT_RULE, result.getValue());
    }

    @Test
    public void update_differentRule_setRule() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setValue("Random Rule");

        final var contractRule = factory.create(new ContractRuleDesc());

        /* ACT */
        factory.update(contractRule, desc);

        /* ASSERT */
        assertEquals(desc.getValue(), contractRule.getValue());
    }

    @Test
    public void update_differentRule_returnTrue() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setValue("Random Rule");

        final var contractRule = factory.create(new ContractRuleDesc());

        /* ACT */
        final var result = factory.update(contractRule, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameRule_returnFalse() {
        /* ARRANGE */
        final var contractRule = factory.create(new ContractRuleDesc());

        /* ACT */
        final var result = factory.update(contractRule, new ContractRuleDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * additional.
     */

    @Test
    public void create_nullAdditional_defaultAdditional() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setAdditional(null);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(new HashMap<>(), result.getAdditional());
    }

    @Test
    public void update_differentAdditional_setAdditional() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var contractRule = factory.create(new ContractRuleDesc());

        /* ACT */
        factory.update(contractRule, desc);

        /* ASSERT */
        assertEquals(desc.getAdditional(), contractRule.getAdditional());
    }

    @Test
    public void update_differentAdditional_returnTrue() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setAdditional(Map.of("Y", "X"));

        final var contractRule = factory.create(new ContractRuleDesc());

        /* ACT */
        final var result = factory.update(contractRule, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void update_sameAdditional_returnFalse() {
        /* ARRANGE */
        final var contractRule = factory.create(new ContractRuleDesc());

        /* ACT */
        final var result = factory.update(contractRule, new ContractRuleDesc());

        /* ASSERT */
        assertFalse(result);
    }

    /**
     * update inputs.
     */

    @Test
    public void update_nullContractRule_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(null, new ContractRuleDesc()));
    }

    @Test
    public void update_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        final var contractRule = factory.create(new ContractRuleDesc());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(contractRule, null));
    }
}
