package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {RelationServices.ContractRuleLinker.class})
class ContractRuleLinkerTest {
    @MockBean
    ContractService contractService;

    @MockBean
    RuleService ruleService;

    @Autowired
    @InjectMocks
    RelationServices.ContractRuleLinker linker;

    Contract contract = getContract();
    ContractRule rule = getRule();

    /**************************************************************************
     * getInternal
     *************************************************************************/

    @Test
    public void getInternal_null_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> linker.getInternal(null));
    }

    @Test
    public void getInternal_Valid_returnOfferedResources() {
        /* ARRANGE */
        contract.getRules().add(rule);

        /* ACT */
        final var resources = linker.getInternal(contract);

        /* ASSERT */
        final var expected = List.of(rule);
        assertEquals(expected, resources);
    }

    /**************************************************************************
     * Utilities
     *************************************************************************/

    @SneakyThrows
    private Contract getContract() {
        final var constructor = Contract.class.getConstructor();
        constructor.setAccessible(true);

        final var contract = constructor.newInstance();

        final var titleField = contract.getClass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(contract, "Catalog");

        final var rulesField = contract.getClass().getDeclaredField("rules");
        rulesField.setAccessible(true);
        rulesField.set(contract, new ArrayList<ContractRule>());

        final var idField = contract.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(contract, UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        return contract;
    }

    @SneakyThrows
    private ContractRule getRule() {
        final var constructor = ContractRule.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var rule = constructor.newInstance();

        final var titleField = rule.getClass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(rule, "Hello");

        final var idField = rule.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(rule, UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return rule;
    }
}
