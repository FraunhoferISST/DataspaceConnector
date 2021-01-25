package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ContractFactoryTest {
    @Autowired
    private ContractFactory factory;

    @Test
    void when_passed_desc_is_null_on_creation_should_throw_exception() {
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.create(null);
        });

        assertNotNull(exception);
    }

    @Test
    void when_all_desc_members_are_set_contract_should_be_created() {
        final var desc = getValidDesc();
        final var contract = factory.create(desc);

        assertNotNull(contract);
        assertEquals(desc.getTitle(), contract.getTitle());
        assertNotNull(contract.getRules());
        assertEquals(contract.getRules().size(), 0);

        assertNull(contract.getId());
        assertNull(contract.getCreationDate());
        assertNull(contract.getLastModificationDate());
    }

    @Test
    void when_all_desc_members_are_null_the_contract_should_be_created() {
        final var desc = getDescWithNullMembers();
        final var contract = factory.create(desc);

        assertNotNull(contract);
        assertNotNull(contract.getTitle());
        assertNotNull(contract.getRules());
        assertEquals(contract.getRules().size(), 0);

        assertNull(contract.getId());
        assertNull(contract.getCreationDate());
        assertNull(contract.getLastModificationDate());
    }

    @Test
    void when_all_desc_members_are_set_the_contract_should_be_updated() {
        var contract = factory.create(getValidDesc());

        assertNotNull(contract);

        var idBefore = contract.getId();
        var creationDateBefore = contract.getCreationDate();
        var lastModificationDateBefore = contract.getLastModificationDate();

        var rulesBefore = ((HashMap<UUID, Rule>) contract.getRules()).clone();
        var desc = getUpdatedDesc();
        factory.update(contract, desc);

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
    void when_all_desc_members_are_null_the_contract_should_be_updated() {
        var initialDesc = getValidDesc();
        var contract = factory.create(initialDesc);

        assertNotNull(contract);

        var idBefore = contract.getId();
        var creationDateBefore = contract.getCreationDate();
        var lastModificationDateBefore = contract.getLastModificationDate();

        var rulesBefore = ((HashMap<UUID, Rule>) contract.getRules()).clone();
        var desc = getDescWithNullMembers();
        factory.update(contract, desc);

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
    void when_the_contract_is_null_the_desc_is_set_on_update_should_throw_exception() {
        var desc = getValidDesc();
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, desc);
        });
    }

    @Test
    void when_the_contract_is_null_the_desc_is_null_on_update_should_throw_exception() {
        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(null, null);
        });
    }

    @Test
    void when_the_contract_is_set_the_desc_is_null_on_update_should_throw_exception() {
        var initialDesc = getValidDesc();
        var catalog = factory.create(initialDesc);

        assertNotNull(catalog);

        final var exception = assertThrows(NullPointerException.class, () -> {
            factory.update(catalog, null);
        });
    }

    @Test
    void when_contract_update_does_result_in_change_return_true() {
        var contract = factory.create(getValidDesc());
        assertTrue(factory.update(contract, getUpdatedDesc()));
    }

    @Test
    void when_contract_update_does_result_in_no_change_return_false() {
        var contract = factory.create(getValidDesc());
        assertFalse(factory.update(contract, getValidDesc()));
    }

    ContractDesc getValidDesc() {
        var desc = new ContractDesc();
        desc.setTitle("Default");

        return desc;
    }

    ContractDesc getUpdatedDesc() {
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
