package de.fraunhofer.isst.dataspaceconnector.model;

import de.fraunhofer.isst.dataspaceconnector.configuration.DatabaseTestsConfig;
import de.fraunhofer.isst.dataspaceconnector.repositories.RuleRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DatabaseTestsConfig.class})
public class ContractRulePersistenceTest {

    @Autowired
    private RuleRepository ruleRepository;

    @Before
    public void init() {
        ruleRepository.findAll().forEach(r -> ruleRepository.delete(r));
    }

    @Transactional
    @Test
    public void createContractRule_returnSameContractRule() {
        /*ARRANGE*/
        Assert.assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule original = getContractRule();

        /*ACT*/
        original = ruleRepository.save(original);
        ContractRule persisted = ruleRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(1, ruleRepository.findAll().size());
        Assert.assertEquals(original, persisted);
    }

    @Transactional
    @Test
    public void updateContractRule_newTitle_returnUpdatedContractRule() {
        /*ARRANGE*/
        Assert.assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule original = ruleRepository.save(getContractRule());
        String newTitle = "new title";

        Assert.assertEquals(1, ruleRepository.findAll().size());

        /*ACT*/
        original.setTitle(newTitle);
        ruleRepository.save(original);
        ContractRule updated = ruleRepository.getOne(original.getId());

        /*ASSERT*/
        Assert.assertEquals(1, ruleRepository.findAll().size());
        Assert.assertEquals(newTitle, updated.getTitle());
        Assert.assertEquals(original.getValue(), updated.getValue());
    }

    @Test
    public void deleteContractRule_contractRuleDeleted() {
        /*ARRANGE*/
        Assert.assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule contractRule = ruleRepository.save(getContractRule());

        Assert.assertEquals(1, ruleRepository.findAll().size());

        /*ACT*/
        ruleRepository.delete(contractRule);

        /*ASSERT*/
        Assert.assertTrue(ruleRepository.findAll().isEmpty());
    }

    private ContractRule getContractRule() {
        ContractRule contractRule = new ContractRule();
        contractRule.setTitle("title");
        contractRule.setValue("rule value");
        return contractRule;
    }

}
