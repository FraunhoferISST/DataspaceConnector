package de.fraunhofer.isst.dataspaceconnector.model;

import de.fraunhofer.isst.dataspaceconnector.configuration.DatabaseTestsConfig;
import de.fraunhofer.isst.dataspaceconnector.repositories.RuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {DatabaseTestsConfig.class})
public class ContractRulePersistenceTest {

    @Autowired
    private RuleRepository ruleRepository;

    @BeforeEach
    public void init() {
        ruleRepository.findAll().forEach(r -> ruleRepository.delete(r));
        ruleRepository.flush();
    }

    @Transactional
    @Test
    public void createContractRule_returnSameContractRule() {
        /*ARRANGE*/
        assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule original = getContractRule();

        /*ACT*/
        original = ruleRepository.saveAndFlush(original);
        ContractRule persisted = ruleRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(1, ruleRepository.findAll().size());
        assertEquals(original, persisted);
    }

    @Transactional
    @Test
    public void updateContractRule_newTitle_returnUpdatedContractRule() {
        /*ARRANGE*/
        assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule original = ruleRepository.saveAndFlush(getContractRule());
        String newTitle = "new title";

        assertEquals(1, ruleRepository.findAll().size());

        /*ACT*/
        original.setTitle(newTitle);
        ruleRepository.saveAndFlush(original);
        ContractRule updated = ruleRepository.getOne(original.getId());

        /*ASSERT*/
        assertEquals(1, ruleRepository.findAll().size());
        assertEquals(newTitle, updated.getTitle());
        assertEquals(original.getValue(), updated.getValue());
    }

    @Test
    public void deleteContractRule_contractRuleDeleted() {
        /*ARRANGE*/
        assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule contractRule = ruleRepository.saveAndFlush(getContractRule());

        assertEquals(1, ruleRepository.findAll().size());

        /*ACT*/
        ruleRepository.delete(contractRule);

        /*ASSERT*/
        assertTrue(ruleRepository.findAll().isEmpty());
    }

    private ContractRule getContractRule() {
        ContractRule contractRule = new ContractRule();
        contractRule.setTitle("title");
        contractRule.setValue("rule value");
        return contractRule;
    }

}
