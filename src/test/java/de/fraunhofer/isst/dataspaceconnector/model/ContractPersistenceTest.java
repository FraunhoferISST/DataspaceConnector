//package de.fraunhofer.isst.dataspaceconnector.model;
//
//import javax.transaction.Transactional;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//import de.fraunhofer.isst.dataspaceconnector.configuration.DatabaseTestsConfig;
//import de.fraunhofer.isst.dataspaceconnector.repositories.ContractRepository;
//import de.fraunhofer.isst.dataspaceconnector.repositories.RuleRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.dao.DataIntegrityViolationException;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest(classes = {DatabaseTestsConfig.class})
//public class ContractPersistenceTest {
//
//    @Autowired
//    private RuleRepository ruleRepository;
//
//    @Autowired
//    private ContractRepository contractRepository;
//
//    @BeforeEach
//    public void init() {
//        contractRepository.findAll().forEach(e -> contractRepository.delete(e));
//        ruleRepository.findAll().forEach(r -> ruleRepository.delete(r));
//    }
//
//    @Transactional
//    @Test
//    public void createContract_noRules_returnSameContract() {
//        /*ARRANGE*/
//        assertTrue(contractRepository.findAll().isEmpty());
//
//        Contract original = contractRepository.save(getContract());
//
//        assertEquals(1, contractRepository.findAll().size());
//
//        /*ACT*/
//        Contract persisted = contractRepository.getOne(original.getId());
//
//        /*ASSERT*/
//        assertEquals(original, persisted);
//    }
//
//    @Transactional
//    @Test
//    public void createContract_withRules_returnSameContract() {
//        /*ARRANGE*/
//        assertTrue(contractRepository.findAll().isEmpty());
//
//        ContractRule contractRule1 = ruleRepository.save(getContractRule());
//        ContractRule contractRule2 = ruleRepository.save(getContractRule());
//        Contract original = contractRepository
//                .save(getContractWithRules(contractRule1, contractRule2));
//
//        assertEquals(1, contractRepository.findAll().size());
//
//        /*ACT*/
//        Contract persisted = contractRepository.getOne(original.getId());
//
//        /*ASSERT*/
//        assertEquals(original, persisted);
//        assertEquals(original.getRules(), persisted.getRules());
//    }
//
//    @Transactional
//    @Test
//    public void updateContract_newTitle_returnUpdatedContract() {
//        /*ARRANGE*/
//        assertTrue(contractRepository.findAll().isEmpty());
//
//        Contract original = contractRepository.save(getContract());
//
//        String newTitle = "new title";
//
//        /*ACT*/
//        original.setTitle(newTitle);
//        contractRepository.save(original);
//
//        /*ASSERT*/
//        Contract updated = contractRepository.getOne(original.getId());
//        assertEquals(newTitle, updated.getTitle());
//        assertEquals(original.getRules(), updated.getRules());
//    }
//
//    @Transactional
//    @Test
//    public void updateContract_addRule_returnUpdatedContract() {
//        /*ARRANGE*/
//        assertTrue(contractRepository.findAll().isEmpty());
//
//        ContractRule contractRule1 = ruleRepository.save(getContractRule());
//        ContractRule contractRule2 = ruleRepository.save(getContractRule());
//        Contract original = contractRepository.save(getContractWithRules(contractRule1));
//
//        assertEquals(1, original.getRules().size());
//
//        /*ACT*/
//        original.getRules().put(contractRule2.getId(), contractRule2);
//        contractRepository.save(original);
//
//        /*ASSERT*/
//        Contract updated = contractRepository.getOne(original.getId());
//        assertEquals(original.getTitle(), updated.getTitle());
//        assertEquals(2, updated.getRules().size());
//        assertTrue(updated.getRules().values()
//                .containsAll(Arrays.asList(contractRule1, contractRule2)));
//    }
//
//    @Transactional
//    @Test
//    public void updateContract_removeRule_returnUpdatedContract() {
//        /*ARRANGE*/
//        assertTrue(contractRepository.findAll().isEmpty());
//
//        ContractRule contractRule1 = ruleRepository.save(getContractRule());
//        ContractRule contractRule2 = ruleRepository.save(getContractRule());
//        Contract original = contractRepository
//                .save(getContractWithRules(contractRule1, contractRule2));
//
//        assertEquals(2, original.getRules().size());
//
//        /*ACT*/
//        original.getRules().remove(contractRule2.getId());
//        contractRepository.save(original);
//
//        /*ASSERT*/
//        Contract updated = contractRepository.getOne(original.getId());
//        assertEquals(original.getTitle(), updated.getTitle());
//        assertEquals(1, updated.getRules().size());
//        assertTrue(updated.getRules().containsValue(contractRule1));
//    }
//
//    @Test
//    public void deleteContract_noRules_contractDeleted() {
//        /*ARRANGE*/
//        assertTrue(contractRepository.findAll().isEmpty());
//
//        Contract original = contractRepository.save(getContract());
//
//        assertEquals(1, contractRepository.findAll().size());
//
//        /*ACT*/
//        contractRepository.deleteById(original.getId());
//
//        /*ASSERT*/
//        assertTrue(contractRepository.findAll().isEmpty());
//    }
//
//    @Test
//    public void deleteContract_withRules_contractDeletedAndRulesNotAffected() {
//        /*ARRANGE*/
//        assertTrue(contractRepository.findAll().isEmpty());
//        assertTrue(ruleRepository.findAll().isEmpty());
//
//        ContractRule contractRule1 = ruleRepository.save(getContractRule());
//        ContractRule contractRule2 = ruleRepository.save(getContractRule());
//        Contract contract = contractRepository
//                .save(getContractWithRules(contractRule1, contractRule2));
//
//        assertEquals(1, contractRepository.findAll().size());
//        assertEquals(2, ruleRepository.findAll().size());
//
//        /*ACT*/
//        contractRepository.deleteById(contract.getId());
//
//        /*ASSERT*/
//        assertTrue(contractRepository.findAll().isEmpty());
//        assertEquals(2, ruleRepository.findAll().size());
//    }
//
//    @Test
//    public void deleteRule_ruleReferencedByContract_throwDataIntegrityViolationException() {
//        /*ARRANGE*/
//        assertTrue(contractRepository.findAll().isEmpty());
//        assertTrue(ruleRepository.findAll().isEmpty());
//
//        ContractRule contractRule = ruleRepository.save(getContractRule());
//        Contract contract = contractRepository.save(getContractWithRules(contractRule));
//
//        assertEquals(1, contractRepository.findAll().size());
//        assertEquals(1, ruleRepository.findAll().size());
//
//        /*ACT*/
//        assertThrows(DataIntegrityViolationException.class, () -> ruleRepository.deleteById(contractRule.getId()));
//    }
//
//    private Contract getContract() {
//        Contract contract = new Contract();
//        contract.setTitle("title");
//        return contract;
//    }
//
//    private Contract getContractWithRules(ContractRule... rules) {
//        Map<UUID, ContractRule> ruleMap = new HashMap<>();
//        Arrays.stream(rules).forEach(r -> ruleMap.put(r.getId(), r));
//
//        Contract contract = getContract();
//        contract.setTitle("title");
//        contract.setRules(ruleMap);
//        return contract;
//    }
//
//    private ContractRule getContractRule() {
//        ContractRule contractRule = new ContractRule();
//        contractRule.setTitle("rule title");
//        contractRule.setValue("some JSON rule");
//        return contractRule;
//    }
//
//}
