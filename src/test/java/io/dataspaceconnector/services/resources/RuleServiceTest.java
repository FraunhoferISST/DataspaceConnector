package io.dataspaceconnector.services.resources;

import java.util.List;
import java.util.UUID;

import io.dataspaceconnector.model.ContractRule;
import io.dataspaceconnector.model.ContractRuleFactory;
import io.dataspaceconnector.repositories.RuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {RuleService.class})
public class RuleServiceTest {

    @MockBean
    private RuleRepository repository;

    @MockBean
    private ContractRuleFactory factory;

    @Autowired
    private RuleService service;

    @Test
    public void getAllByContract_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.getAllByContract(null));
    }

    @Test
    public void getAllByContract_validInput_returnRules() {
        /* ARRANGE */
        final var contractId = UUID.randomUUID();
        final var rules = List.of(new ContractRule());

        when(repository.findAllByContract(contractId)).thenReturn(rules);

        /* ACT */
        final var result = service.getAllByContract(contractId);

        /* ASSERT */
        assertEquals(rules, result);
    }

}
