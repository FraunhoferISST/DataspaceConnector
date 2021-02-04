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
class ContractContractRuleFactoryTest {
    @Autowired
    private ContractRuleFactory factory;

    @Test
    void when_passed_desc_is_null_on_creation_should_throw_exception() {
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.create(null);
        });

        assertNotNull(exception);
    }

    @Test
    void when_all_desc_members_are_set_rule_should_be_created() {
        final var desc = getValidDesc();
        final var rule = factory.create(desc);

        assertNotNull(rule);
        assertEquals(desc.getTitle(), rule.getTitle());
        assertEquals(desc.getRule(), rule.getValue());

        assertNull(rule.getId());
        assertNull(rule.getCreationDate());
        assertNull(rule.getLastModificationDate());
    }

    @Test
    void when_all_desc_members_are_null_the_rule_should_be_created() {
        final var desc = getDescWithNullMembers();
        final var rule = factory.create(desc);

        assertNotNull(rule);
        assertNotNull(rule.getTitle());
        assertNotNull(rule.getValue());

        assertNull(rule.getId());
        assertNull(rule.getCreationDate());
        assertNull(rule.getLastModificationDate());
    }

    @Test
    void when_all_desc_members_are_set_the_rule_should_be_updated() {
        var rule = factory.create(getValidDesc());

        assertNotNull(rule);

        var idBefore = rule.getId();
        var creationDateBefore = rule.getCreationDate();
        var lastModificationDateBefore = rule.getLastModificationDate();

        var desc = getUpdatedDesc();
        factory.update(rule, desc);

        assertNotNull(rule);
        assertEquals(desc.getTitle(), rule.getTitle());
        assertEquals(desc.getRule(), rule.getValue());

        assertEquals(idBefore, rule.getId());
        assertEquals(creationDateBefore, rule.getCreationDate());
        assertEquals(lastModificationDateBefore,
                rule.getLastModificationDate());
    }

    @Test
    void when_all_desc_members_are_null_the_rule_should_be_updated() {
        var initialDesc = getValidDesc();
        var rule = factory.create(initialDesc);

        assertNotNull(rule);

        var idBefore = rule.getId();
        var creationDateBefore = rule.getCreationDate();
        var lastModificationDateBefore = rule.getLastModificationDate();

        var desc = getDescWithNullMembers();
        factory.update(rule, desc);

        assertNotNull(rule);
        assertNotNull(rule.getTitle());
        assertNotNull(rule.getValue());

        assertEquals(idBefore, rule.getId());
        assertEquals(creationDateBefore, rule.getCreationDate());
        assertEquals(lastModificationDateBefore,
                rule.getLastModificationDate());
    }

    @Test
    void when_rule_update_does_result_in_change_return_true() {
        var rule = factory.create(getValidDesc());
        assertTrue(factory.update(rule, getUpdatedDesc()));
    }

    @Test
    void when_rule_update_does_result_in_no_change_return_false() {
        var rule = factory.create(getValidDesc());
        assertFalse(factory.update(rule, getValidDesc()));
    }

    @Test
    void when_the_rule_is_null_the_desc_is_set_on_update_should_throw_exception() {
        var desc = getValidDesc();
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, desc);
        });
    }

    @Test
    void when_the_rule_is_null_the_desc_is_null_on_update_should_throw_exception() {
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, null);
        });
    }

    @Test
    void when_the_rule_is_set_the_desc_is_null_on_update_should_throw_exception() {
        var initialDesc = getValidDesc();
        var rule = factory.create(initialDesc);

        assertNotNull(rule);

        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(rule, null);
        });
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
