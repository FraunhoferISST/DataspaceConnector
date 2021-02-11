package de.fraunhofer.isst.dataspaceconnector.model.v2;

import de.fraunhofer.isst.dataspaceconnector.configuration.DatabaseTestsConfig;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.repositories.v2.implementations.ContractRepository;
import de.fraunhofer.isst.dataspaceconnector.repositories.v2.implementations.RuleRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DatabaseTestsConfig.class})
public class ContractPersistenceTest {

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Before
    public void init() {
        contractRepository.findAll().forEach(e -> contractRepository.delete(e));
        ruleRepository.findAll().forEach(r -> ruleRepository.delete(r));
    }

    @Transactional
    @Test
    public void createContract_noRules_returnSameContract() {
        /*ARRANGE*/
        Assert.assertTrue(contractRepository.findAll().isEmpty());

        Contract original = contractRepository.save(getContract());

        Assert.assertEquals(1, contractRepository.findAll().size());

        /*ACT*/
        Contract persisted = contractRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(original, persisted);
    }

    @Transactional
    @Test
    public void createContract_withRules_returnSameContract() {
        /*ARRANGE*/
        Assert.assertTrue(contractRepository.findAll().isEmpty());

        ContractRule contractRule1 = ruleRepository.save(getContractRule());
        ContractRule contractRule2 = ruleRepository.save(getContractRule());
        Contract original = contractRepository
                .save(getContractWithRules(contractRule1, contractRule2));

        Assert.assertEquals(1, contractRepository.findAll().size());

        /*ACT*/
        Contract persisted = contractRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(original, persisted);
        Assert.assertEquals(original.getRules(), persisted.getRules());
    }

    @Transactional
    @Test
    public void updateContract_newTitle_returnUpdatedContract() {
        /*ARRANGE*/
        Assert.assertTrue(contractRepository.findAll().isEmpty());

        Contract original = contractRepository.save(getContract());

        String newTitle = "new title";

        /*ACT*/
        original.setTitle(newTitle);
        contractRepository.save(original);

        /*ASSERT*/
        Contract updated = contractRepository.getOne(original.getId());
        Assert.assertEquals(newTitle, updated.getTitle());
        Assert.assertEquals(original.getRules(), updated.getRules());
    }

    @Transactional
    @Test
    public void updateContract_addRule_returnUpdatedContract() {
        /*ARRANGE*/
        Assert.assertTrue(contractRepository.findAll().isEmpty());

        ContractRule contractRule1 = ruleRepository.save(getContractRule());
        ContractRule contractRule2 = ruleRepository.save(getContractRule());
        Contract original = contractRepository.save(getContractWithRules(contractRule1));

        Assert.assertEquals(1, original.getRules().size());

        /*ACT*/
        original.getRules().put(contractRule2.getId(), contractRule2);
        contractRepository.save(original);

        /*ASSERT*/
        Contract updated = contractRepository.getOne(original.getId());
        Assert.assertEquals(original.getTitle(), updated.getTitle());
        Assert.assertEquals(2, updated.getRules().size());
        Assert.assertTrue(updated.getRules().values()
                .containsAll(Arrays.asList(contractRule1, contractRule2)));
    }

    @Transactional
    @Test
    public void updateContract_removeRule_returnUpdatedContract() {
        /*ARRANGE*/
        Assert.assertTrue(contractRepository.findAll().isEmpty());

        ContractRule contractRule1 = ruleRepository.save(getContractRule());
        ContractRule contractRule2 = ruleRepository.save(getContractRule());
        Contract original = contractRepository
                .save(getContractWithRules(contractRule1, contractRule2));

        Assert.assertEquals(2, original.getRules().size());

        /*ACT*/
        original.getRules().remove(contractRule2.getId());
        contractRepository.save(original);

        /*ASSERT*/
        Contract updated = contractRepository.getOne(original.getId());
        Assert.assertEquals(original.getTitle(), updated.getTitle());
        Assert.assertEquals(1, updated.getRules().size());
        Assert.assertTrue(updated.getRules().containsValue(contractRule1));
    }

    @Test
    public void deleteContract_noRules_contractDeleted() {
        /*ARRANGE*/
        Assert.assertTrue(contractRepository.findAll().isEmpty());

        Contract original = contractRepository.save(getContract());

        Assert.assertEquals(1, contractRepository.findAll().size());

        /*ACT*/
        contractRepository.deleteById(original.getId());

        /*ASSERT*/
        Assert.assertTrue(contractRepository.findAll().isEmpty());
    }

    @Test
    public void deleteContract_withRules_contractDeletedAndRulesNotAffected() {
        /*ARRANGE*/
        Assert.assertTrue(contractRepository.findAll().isEmpty());
        Assert.assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule contractRule1 = ruleRepository.save(getContractRule());
        ContractRule contractRule2 = ruleRepository.save(getContractRule());
        Contract contract = contractRepository
                .save(getContractWithRules(contractRule1, contractRule2));

        Assert.assertEquals(1, contractRepository.findAll().size());
        Assert.assertEquals(2, ruleRepository.findAll().size());

        /*ACT*/
        contractRepository.deleteById(contract.getId());

        /*ASSERT*/
        Assert.assertTrue(contractRepository.findAll().isEmpty());
        Assert.assertEquals(2, ruleRepository.findAll().size());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void deleteRule_ruleReferencedByContract_throwDataIntegrityViolationException() {
        /*ARRANGE*/
        Assert.assertTrue(contractRepository.findAll().isEmpty());
        Assert.assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule contractRule = ruleRepository.save(getContractRule());
        Contract contract = contractRepository.save(getContractWithRules(contractRule));

        Assert.assertEquals(1, contractRepository.findAll().size());
        Assert.assertEquals(1, ruleRepository.findAll().size());

        /*ACT*/
        ruleRepository.deleteById(contractRule.getId());
    }

    private Contract getContract() {
        Contract contract = new Contract();
        contract.setTitle("title");
        return contract;
    }

    private Contract getContractWithRules(ContractRule... rules) {
        Map<UUID, ContractRule> ruleMap = new HashMap<>();
        Arrays.stream(rules).forEach(r -> ruleMap.put(r.getId(), r));

        Contract contract = getContract();
        contract.setTitle("title");
        contract.setRules(ruleMap);
        return contract;
    }

    private ContractRule getContractRule() {
        ContractRule contractRule = new ContractRule();
        contractRule.setTitle("rule title");
        contractRule.setValue("some JSON rule");
        return contractRule;
    }

}
