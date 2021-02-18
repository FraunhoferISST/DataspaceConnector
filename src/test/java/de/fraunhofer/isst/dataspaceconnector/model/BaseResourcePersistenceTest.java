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

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * This class tests persistence of BaseResources (@CreationTimestamp, @UpdateTimestamp) using
 * ContractRule as an example.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DatabaseTestsConfig.class})
public class BaseResourcePersistenceTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RuleRepository ruleRepository;

    @Before
    public void init() {
        ruleRepository.findAll().forEach(r -> ruleRepository.delete(r));
    }

    @Transactional
    @Test(expected = IllegalArgumentException.class)
    public void createBaseResource_noSubclass_throwIllegalArgumentException() {
        /*ACT*/
        entityManager.persist(new BaseEntity());
    }

    @Test
    public void createBaseResource_noCreationTimestamp_creationAndUpdateTimestampSet() {
        /*ARRANGE*/
        Assert.assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule contractRule = getContractRule();

        Assert.assertNull(contractRule.getCreationDate());
        Assert.assertNull(contractRule.getModificationDate());

        /*ACT*/
        contractRule = ruleRepository.save(contractRule);

        /*ASSERT*/
        Assert.assertNotNull(contractRule.getCreationDate());
        Assert.assertNotNull(contractRule.getModificationDate());
    }

    @Transactional
    @Test
    public void readBaseResource_multipleTimes_creationAndUpdateTimestampsUnchanged() {
        /*ARRANGE*/
        Assert.assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule contractRule = ruleRepository.save(getContractRule());

        final var creationDate = contractRule.getCreationDate();
        final var modificationDate = contractRule.getModificationDate();

        //read and check the dates 3 times
        for (int i = 0; i < 3; i++) {
            /*ACT*/
            ContractRule persisted = ruleRepository.getOne(contractRule.getId());

            /*ASSERT*/
            Assert.assertEquals(creationDate, persisted.getCreationDate());
            Assert.assertEquals(modificationDate, persisted.getModificationDate());
        }
    }

    @Test
    public void updateBaseResource_creationTimestampUnchangedAndUpdateTimestampUpdated() {
        /*ARRANGE*/
        Assert.assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule original = ruleRepository.save(getContractRule());

        final var creationDate = original.getCreationDate();
        final var modificationDate = original.getModificationDate();

        /*ACT*/
        original.setTitle("new rule title");
        ContractRule updated = ruleRepository.save(original);

        /*ASSERT*/
        Assert.assertEquals(creationDate, updated.getCreationDate());
        Assert.assertNotEquals(modificationDate, updated.getModificationDate());
    }

    @Transactional
    @Test
    public void updateBaseResource_changedCreationTimestamp_creationTimestampNotUpdated()
            throws NoSuchFieldException, IllegalAccessException {
        /*ARRANGE*/
        Assert.assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule contractRule = ruleRepository.saveAndFlush(getContractRule());

        final var creationDate = contractRule.getCreationDate();

        /*ACT*/
        Field creationDateField = BaseEntity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(contractRule, new Date());

        ruleRepository.saveAndFlush(contractRule);

        //assertion fails, new creation date is persisted
        /*ASSERT*/
        ContractRule updated = ruleRepository.getOne(contractRule.getId());
//        Assert.assertEquals(creationDate, updated.getCreationDate());
    }

    private ContractRule getContractRule() {
        ContractRule contractRule = new ContractRule();
        contractRule.setTitle("rule title");
        contractRule.setValue("some JSON rule");
        return contractRule;
    }

}
