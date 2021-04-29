package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.DutyBuilder;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.ProhibitionBuilder;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PolicyUtilsTest {

    @Test
    public void extractRulesFromContract_contractWithoutRules_returnEmptyList() {
        /* ARRANGE */
        final var contract = new ContractRequestBuilder()
                ._contractStart_(getGregorianNow())
                .build();

        /* ACT */
        final var result = PolicyUtils.extractRulesFromContract(contract);

        /* ASSERT */
        assertEquals(0, result.size());
    }

    @Test
    public void extractRulesFromContract_contractWithThreeRules_returnRuleList() {
        /* ARRANGE */
        final var permission = (Permission) getRuleThree();
        final var prohibition = (Prohibition) getRuleTwo();
        final var obligation = (Duty) getRuleOne();
        final var contract = new ContractRequestBuilder()
                ._contractStart_(getGregorianNow())
                ._permission_(Util.asList(permission))
                ._prohibition_(Util.asList(prohibition))
                ._obligation_(Util.asList(obligation))
                .build();

        /* ACT */
        final var result = PolicyUtils.extractRulesFromContract(contract);

        /* ASSERT */
        assertEquals(3, result.size());
        assertTrue(result.contains(permission));
        assertTrue(result.contains(prohibition));
        assertTrue(result.contains(obligation));
    }

    @Test
    public void extractRulesFromContract_contractWithTwoProhibitions_returnRuleList() {
        /* ARRANGE */
        final var permission = (Permission) getRuleThree();
        final var prohibition = (Prohibition) getRuleTwo();
        final var obligation = (Duty) getRuleOne();
        final var contract = new ContractRequestBuilder()
                ._contractStart_(getGregorianNow())
                ._permission_(Util.asList(permission))
                ._prohibition_(Util.asList(prohibition, prohibition))
                ._obligation_(Util.asList(obligation))
                .build();

        /* ACT */
        final var result = PolicyUtils.extractRulesFromContract(contract);

        /* ASSERT */
        assertEquals(4, result.size());
        assertTrue(result.contains(permission));
        assertTrue(result.contains(prohibition));
        assertTrue(result.contains(obligation));
    }

    @Test
    public void extractRulesFromContract_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> PolicyUtils.extractRulesFromContract(null));
    }

    @Test
    public void getRulesForTargetId_matchingTargetIdForOneRule_returnRuleList() {
        /* ARRANGE */
        final var target = URI.create("https://target");
        final var permission = getPermissionWithTarget(target);
        final var prohibition = getProhibitionWithTarget(null);
        final var obligation = getDutyWithTarget(null);
        final var contract = new ContractRequestBuilder()
                ._contractStart_(getGregorianNow())
                ._permission_(Util.asList(permission))
                ._prohibition_(Util.asList(prohibition))
                ._obligation_(Util.asList(obligation))
                .build();

        /* ACT */
        final var result = PolicyUtils.getRulesForTargetId(contract, target);

        /* ASSERT */
        assertEquals(1, result.size());
        assertTrue(result.contains(permission));
    }

    @Test
    public void getRulesForTargetId_matchingTargetIdForMultipleRules_returnRuleList() {
        /* ARRANGE */
        final var target = URI.create("https://target");
        final var permission = getPermissionWithTarget(target);
        final var prohibition = getProhibitionWithTarget(null);
        final var prohibition2 = getProhibitionWithTarget(target);
        final var obligation = getDutyWithTarget(target);
        final var contract = new ContractRequestBuilder()
                ._contractStart_(getGregorianNow())
                ._permission_(Util.asList(permission))
                ._prohibition_(Util.asList(prohibition, prohibition2))
                ._obligation_(Util.asList(obligation))
                .build();

        /* ACT */
        final var result = PolicyUtils.getRulesForTargetId(contract, target);

        /* ASSERT */
        assertEquals(3, result.size());
        assertTrue(result.contains(permission));
        assertTrue(result.contains(prohibition2));
        assertTrue(result.contains(obligation));
    }

    @Test
    public void getRulesForTargetId_noMatchingTargetId_returnRuleList() {
        /* ARRANGE */
        final var target = URI.create("https://target");
        final var permission = getPermissionWithTarget(null);
        final var prohibition = getProhibitionWithTarget(null);
        final var obligation = getDutyWithTarget(null);
        final var contract = new ContractRequestBuilder()
                ._contractStart_(getGregorianNow())
                ._permission_(Util.asList(permission))
                ._prohibition_(Util.asList(prohibition))
                ._obligation_(Util.asList(obligation))
                .build();

        /* ACT */
        final var result = PolicyUtils.getRulesForTargetId(contract, target);

        /* ASSERT */
        assertEquals(0, result.size());
    }

    @Test
    public void getRulesForTargetId_emptyContract_throwIllegalArgumentException() {
        /* ARRANGE */
        final var target = URI.create("https://target");

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class, () -> PolicyUtils.getRulesForTargetId(null,
                target));
    }

    @Test
    public void getTargetRuleMap_listWithValidRulesAndTargets_returnMap() {
        /* ARRANGE */
        final var target_1 = URI.create("https://target1");
        final var target_2 = URI.create("https://target2");
        final var target_3 = URI.create("https://target3");
        final var permission = getPermissionWithTarget(target_1);
        final var prohibition = getProhibitionWithTarget(target_2);
        final var obligation = getDutyWithTarget(target_3);
        final var list = List.of(permission, prohibition, obligation);

        /* ACT */
        final var result = PolicyUtils.getTargetRuleMap(list);

        /* ASSERT */
        assertEquals(3, result.keySet().size());
        assertEquals(3, result.entrySet().size());
        assertTrue(result.containsKey(target_1));
        assertTrue(result.containsKey(target_2));
        assertTrue(result.containsKey(target_3));
        assertTrue(result.get(target_1).contains(permission));
        assertTrue(result.get(target_2).contains(prohibition));
        assertTrue(result.get(target_3).contains(obligation));
    }

    @Test
    public void getTargetRuleMap_listWithMultipleRulesForOneTarget_returnMap() {
        /* ARRANGE */
        final var target_1 = URI.create("https://target1");
        final var target_2 = URI.create("https://target2");
        final var permission = getPermissionWithTarget(target_1);
        final var prohibition = getProhibitionWithTarget(target_2);
        final var obligation = getDutyWithTarget(target_2);
        final var list = List.of(permission, prohibition, obligation);

        /* ACT */
        final var result = PolicyUtils.getTargetRuleMap(list);

        /* ASSERT */
        assertEquals(2, result.keySet().size());
        assertEquals(2, result.entrySet().size());
        assertTrue(result.containsKey(target_1));
        assertTrue(result.containsKey(target_2));
        assertTrue(result.get(target_1).contains(permission));
        assertTrue(result.get(target_2).contains(prohibition));
        assertTrue(result.get(target_2).contains(obligation));
        assertEquals(2, result.get(target_2).size());
    }

    @Test
    public void getTargetRuleMap_listWithRulesWithoutTargets_returnMap() {
        /* ARRANGE */
        final var permission = (Permission) getRuleThree();
        final var prohibition = (Prohibition) getRuleTwo();
        final var obligation = (Duty) getRuleOne();
        final var contract = new ContractRequestBuilder()
                ._contractStart_(getGregorianNow())
                ._permission_(Util.asList(permission))
                ._prohibition_(Util.asList(prohibition, prohibition))
                ._obligation_(Util.asList(obligation))
                .build();
    }

    @Test
    public void compareRules_null_returnTrue() {
        /* ACT && ASSERT */
        assertTrue(PolicyUtils.compareRules(null, null));
    }

    @Test
    public void compareRules_leftNull_returnFalse() {
        /* ACT && ASSERT */
        assertFalse(PolicyUtils.compareRules(null, new ArrayList<>()));
    }

    @Test
    public void compareRules_rightNull_returnFalse() {
        /* ACT && ASSERT */
        assertFalse(PolicyUtils.compareRules(new ArrayList<>(), null));
    }

    @Test
    public void compareRules_sameList_returnTrue() {
        /* ACT && ASSERT */
        assertTrue(PolicyUtils.compareRules(Util.asList(getRuleOne(), getRuleTwo()),
                Util.asList(getRuleOne(), getRuleTwo())));
    }

    @Test
    public void compareRules_sameSets_returnTrue() {
        /* ACT && ASSERT */
        assertTrue(PolicyUtils.compareRules(Util.asList(getRuleOne(), getRuleTwo(), getRuleOne())
                , Util.asList(getRuleOne(), getRuleTwo())));
    }

    @Test
    public void compareRules_differentSets_returnFalse() {
        /* ACT && ASSERT */
        assertFalse(PolicyUtils.compareRules(Util.asList(getRuleOne(), getRuleTwo(),
                getRuleOne()), Util.asList(getRuleOne(), getRuleThree())));
    }

    /**
     * removeContractsWithInvalidConsumer
     */
    @Test
    public void removeContractsWithInvalidConsumer_sameConsumer_removeNothing() {
        /* ARRANGE */
        final var issuer = URI.create("https://someConsumer");
        final var list = List.of(getContractWithConsumer(), getContractWithoutConsumer());

        /* ACT */
        final var result = PolicyUtils.removeContractsWithInvalidConsumer(list, issuer);

        /* ASSERT */
        assertEquals(list, result);
    }

    @Test
    public void removeContractsWithInvalidConsumer_differentConsumer_removeRestrictedOffer() {
        /* ARRANGE */
        final var issuer = URI.create("https://someOtherConsumer");
        final var list = List.of(getContractWithConsumer(), getContractWithoutConsumer());

        /* ACT */
        final var result = PolicyUtils.removeContractsWithInvalidConsumer(list, issuer);

        /* ASSERT */
        assertEquals(List.of(getContractWithoutConsumer()), result);
    }

    @Test
    public void removeContractsWithInvalidConsumer_nullList_throwIllegalArgumentException() {
        /* ARRANGE */
        final var issuer = URI.create("https://someOtherConsumer");

        /* ACT && ASSERT*/
        assertThrows(IllegalArgumentException.class,
                () -> PolicyUtils.removeContractsWithInvalidConsumer(null, issuer));
    }

    @Test
    public void removeContractsWithInvalidConsumer_nullIssuer_throwIllegalArgumentException() {
        /* ARRANGE */
        final var list = List.of(getContractWithConsumer(), getContractWithoutConsumer());

        /* ACT && ASSERT*/
        assertThrows(IllegalArgumentException.class,
                () -> PolicyUtils.removeContractsWithInvalidConsumer(list, null));
    }

    @Test
    public void removeContractsWithInvalidConsumer_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT*/
        assertThrows(IllegalArgumentException.class,
                () -> PolicyUtils.removeContractsWithInvalidConsumer(null, null));
    }

    /**
     * Utilities
     */

    public Rule getRuleOne() {
        return new DutyBuilder()
                ._action_(Util.asList(getActionOne()))
                .build();
    }

    public Rule getRuleTwo() {
        return new ProhibitionBuilder()
                ._action_(Util.asList(getActionsTwo()))
                .build();
    }

    public Rule getRuleThree() {
        return new PermissionBuilder()
                ._action_(Util.asList(getActionThree()))
                .build();
    }

    public Action getActionOne() {
        return Action.USE;
    }

    public Action getActionsTwo() {
        return Action.NOTIFY;
    }

    public Action getActionThree() {
        return Action.LOG;
    }

    private Prohibition getProhibitionWithTarget(final URI target) {
        return new ProhibitionBuilder()
                ._action_(Util.asList(getActionsTwo()))
                ._target_(target)
                .build();
    }

    private Permission getPermissionWithTarget(final URI target) {
        return new PermissionBuilder()
                ._action_(Util.asList(getActionThree()))
                ._target_(target)
                .build();
    }

    private Duty getDutyWithTarget(final URI target) {
        return new DutyBuilder()
                ._action_(Util.asList(getActionOne()))
                ._target_(target)
                .build();
    }

    @SneakyThrows
    private Contract getContractWithoutConsumer() {
        final var constructor = Contract.class.getConstructor();
        constructor.setAccessible(true);

        final var contract = constructor.newInstance();

        final var titleField = contract.getClass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(contract, "Catalog without consumer");

        final var issuerField = contract.getClass().getDeclaredField("consumer");
        issuerField.setAccessible(true);
        issuerField.set(contract, URI.create(""));

        return contract;
    }

    @SneakyThrows
    private Contract getContractWithConsumer() {
        final var constructor = Contract.class.getConstructor();
        constructor.setAccessible(true);

        final var contract = constructor.newInstance();

        final var titleField = contract.getClass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(contract, "Catalog with consumer");

        final var issuerField = contract.getClass().getDeclaredField("consumer");
        issuerField.setAccessible(true);
        issuerField.set(contract, URI.create("https://someConsumer"));

        return contract;
    }
}
