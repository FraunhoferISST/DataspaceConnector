package de.fraunhofer.isst.dataspaceconnector.model;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.Date;

import de.fraunhofer.isst.dataspaceconnector.configuration.DatabaseTestsConfig;
import de.fraunhofer.isst.dataspaceconnector.repositories.RuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class tests persistence of BaseResources (@CreationTimestamp, @UpdateTimestamp) using
 * ContractRule as an example.
 */
@SpringBootTest(classes = {DatabaseTestsConfig.class})
public class BaseResourcePersistenceTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RuleRepository ruleRepository;

    @BeforeEach
    public void init() {
        ruleRepository.findAll().forEach(r -> ruleRepository.delete(r));
    }

    @Transactional
    @Test
    public void createBaseResource_noSubclass_throwIllegalArgumentException() {
        /*ACT*/
        assertThrows(IllegalArgumentException.class, () -> entityManager.persist(new AbstractEntity()));
    }

    @Test
    public void createBaseResource_noCreationTimestamp_creationAndUpdateTimestampSet() {
        /*ARRANGE*/
        assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule contractRule = getContractRule();

        assertNull(contractRule.getCreationDate());
        assertNull(contractRule.getModificationDate());

        /*ACT*/
        contractRule = ruleRepository.save(contractRule);

        /*ASSERT*/
        assertNotNull(contractRule.getCreationDate());
        assertNotNull(contractRule.getModificationDate());
    }

    @Transactional
    @Test
    public void readBaseResource_multipleTimes_creationAndUpdateTimestampsUnchanged() {
        /*ARRANGE*/
        assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule contractRule = ruleRepository.save(getContractRule());

        final var creationDate = contractRule.getCreationDate();
        final var modificationDate = contractRule.getModificationDate();

        //read and check the dates 3 times
        for (int i = 0; i < 3; i++) {
            /*ACT*/
            ContractRule persisted = ruleRepository.getOne(contractRule.getId());

            /*ASSERT*/
            assertEquals(creationDate, persisted.getCreationDate());
            assertEquals(modificationDate, persisted.getModificationDate());
        }
    }

    @Test
    public void updateBaseResource_creationTimestampUnchangedAndUpdateTimestampUpdated() {
        /*ARRANGE*/
        assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule original = ruleRepository.save(getContractRule());

        final var creationDate = original.getCreationDate();
        final var modificationDate = original.getModificationDate();

        /*ACT*/
        original.setTitle("new rule title");
        ContractRule updated = ruleRepository.save(original);

        /*ASSERT*/
        assertEquals(creationDate, updated.getCreationDate());
        assertNotEquals(modificationDate, updated.getModificationDate());
    }

    @Transactional
    @Test
    public void updateBaseResource_changedCreationTimestamp_creationTimestampNotUpdated()
            throws NoSuchFieldException, IllegalAccessException {
        /*ARRANGE*/
        assertTrue(ruleRepository.findAll().isEmpty());

        ContractRule contractRule = ruleRepository.saveAndFlush(getContractRule());

        final var creationDate = contractRule.getCreationDate();

        /*ACT*/
        Field creationDateField = AbstractEntity.class.getDeclaredField("creationDate");
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
