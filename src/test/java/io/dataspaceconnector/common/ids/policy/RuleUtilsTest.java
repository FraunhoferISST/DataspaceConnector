/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.common.ids.policy;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.ConstraintBuilder;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.DutyBuilder;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.ProhibitionBuilder;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.model.contract.Contract;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.net.URI;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static de.fraunhofer.ids.messaging.util.IdsMessageUtils.getGregorianNow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RuleUtilsTest {

    /**
     * policy validation
     */

    @Test
    public void getPatternByRule_invalidInput_returnNull() {
        /* ARRANGE */
        final var rule = new PermissionBuilder()._action_(Action.DELETE).build();

        /* ACT */
        final var result = RuleUtils.getPatternByRule(rule);

        /* ASSERT */
        assertEquals(PolicyPattern.PROVIDE_ACCESS, result);
    }

    /**
     * helper methods
     */

    @Test
    public void extractRulesFromContract_contractWithoutRules_returnEmptyList() {
        /* ARRANGE */
        final var contract = new ContractRequestBuilder()
                ._contractStart_(getGregorianNow())
                .build();

        /* ACT */
        final var result = ContractUtils.extractRulesFromContract(contract);

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
        final var result = ContractUtils.extractRulesFromContract(contract);

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
        final var result = ContractUtils.extractRulesFromContract(contract);

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
                () -> ContractUtils.extractRulesFromContract(null));
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
        final var result = ContractUtils.getRulesForTargetId(contract, target);

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
        final var result = ContractUtils.getRulesForTargetId(contract, target);

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
        final var result = ContractUtils.getRulesForTargetId(contract, target);

        /* ASSERT */
        assertEquals(0, result.size());
    }

    @Test
    public void getRulesForTargetId_emptyContract_throwIllegalArgumentException() {
        /* ARRANGE */
        final var target = URI.create("https://target");

        /* ACT & ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> ContractUtils.getRulesForTargetId(null, target));
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
        final var result = ContractUtils.getTargetRuleMap(list);

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
        final var result = ContractUtils.getTargetRuleMap(list);

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
    public void compareRules_null_returnTrue() {
        /* ACT && ASSERT */
        assertTrue(RuleUtils.compareRules(null, null));
    }

    @Test
    public void compareRules_leftNull_returnTrue() {
        /* ACT && ASSERT */
        assertTrue(RuleUtils.compareRules(null, new ArrayList<>()));
    }

    @Test
    public void compareRules_rightNull_returnTrue() {
        /* ACT && ASSERT */
        assertTrue(RuleUtils.compareRules(new ArrayList<>(), null));
    }

    @Test
    public void compareRules_sameList_returnTrue() {
        /* ACT && ASSERT */
        assertTrue(RuleUtils.compareRules(Util.asList(getRuleOne(), getRuleTwo()),
                Util.asList(getRuleOne(), getRuleTwo())));
    }

    @Test
    public void compareRules_sameSets_returnTrue() {
        /* ACT && ASSERT */
        assertTrue(RuleUtils.compareRules(Util.asList(getRuleOne(), getRuleTwo(), getRuleOne()),
                Util.asList(getRuleOne(), getRuleTwo())));
    }

    @Test
    public void compareRules_differentSets_returnFalse() {
        /* ACT && ASSERT */
        assertFalse(RuleUtils.compareRules(Util.asList(getRuleOne(), getRuleTwo(), getRuleOne()),
                Util.asList(getRuleOne(), getRuleThree())));
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
        final var result = ContractUtils.removeContractsWithInvalidConsumer(list, issuer);

        /* ASSERT */
        assertEquals(list, result);
    }

    @Test
    public void removeContractsWithInvalidConsumer_differentConsumer_removeRestrictedOffer() {
        /* ARRANGE */
        final var issuer = URI.create("https://someOtherConsumer");
        final var list = List.of(getContractWithConsumer(), getContractWithoutConsumer());

        /* ACT */
        final var result = ContractUtils.removeContractsWithInvalidConsumer(list, issuer);

        /* ASSERT */
        assertEquals(List.of(getContractWithoutConsumer()), result);
    }

    @Test
    public void removeContractsWithInvalidConsumer_nullList_throwIllegalArgumentException() {
        /* ARRANGE */
        final var issuer = URI.create("https://someOtherConsumer");

        /* ACT && ASSERT*/
        assertThrows(IllegalArgumentException.class,
                () -> ContractUtils.removeContractsWithInvalidConsumer(null, issuer));
    }

    @Test
    public void removeContractsWithInvalidConsumer_nullIssuer_throwIllegalArgumentException() {
        /* ARRANGE */
        final var list = List.of(getContractWithConsumer(), getContractWithoutConsumer());

        /* ACT && ASSERT*/
        assertThrows(IllegalArgumentException.class,
                () -> ContractUtils.removeContractsWithInvalidConsumer(list, null));
    }

    @Test
    public void removeContractsWithInvalidConsumer_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT*/
        assertThrows(IllegalArgumentException.class,
                () -> ContractUtils.removeContractsWithInvalidConsumer(null, null));
    }

    @Test
    public void getMaxAccess_inputNull_throwNullPointerException() {
        /* ACT & ASSERT */
        assertThrows(NullPointerException.class, () -> RuleUtils.getMaxAccess(null));
    }

    @Test
    public void getMaxAccess_inputCorrectOperatorEquals_returnAccessInteger() {
        /* ARRANGE */
        final var maxAccess = 2;

        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource(String.valueOf(maxAccess),
                        URI.create("xsd:decimal")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getMaxAccess(permission);

        /* ASSERT */
        assertEquals(maxAccess, result);
    }

    @Test
    public void getMaxAccess_inputCorrectOperatorLessThanEquals_returnAccessInteger() {
        /* ARRANGE */
        final var maxAccess = 2;

        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.LTEQ)
                ._rightOperand_(new RdfResource(String.valueOf(maxAccess),
                        URI.create("xsd:decimal")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getMaxAccess(permission);

        /* ASSERT */
        assertEquals(maxAccess, result);
    }

    @Test
    public void getMaxAccess_inputCorrectOperatorLessThan_returnAccessInteger() {
        /* ARRANGE */
        final var maxAccess = 2;

        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.LT)
                ._rightOperand_(new RdfResource(String.valueOf(maxAccess),
                        URI.create("xsd:decimal")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getMaxAccess(permission);

        /* ASSERT */
        assertEquals(maxAccess - 1, result);
    }

    @Test
    public void getMaxAccess_inputInvalidAccessBiggerThanMaxInteger_returnSomething() {
        /* ARRANGE */
        final var maxAccess = Integer.MAX_VALUE + 1;

        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource(String.valueOf(maxAccess),
                        URI.create("xsd:decimal")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getMaxAccess(permission);

        /* ASSERT */
        assertTrue(result >= 0);
    }

    @Test
    public void getMaxAccess_inputInvalidAccessNotInteger_throwNumberFormatException() {
        /* ARRANGE */
        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource(
                        "I am not an integer.", URI.create("xsd:decimal")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT & ASSERT */
        assertThrows(NumberFormatException.class, () -> RuleUtils.getMaxAccess(permission));
    }

    @Test
    public void getMaxAccess_inputInvalidAccessNegative_returnZero() {
        /* ARRANGE */
        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource("-3", URI.create("xsd:decimal")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getMaxAccess(permission);

        /* ASSERT */
        assertEquals(0, result);
    }

    @Test
    public void
    getMaxAccess_inputInvalidMaxAccessConstraintNotFirstInList_throwNumberFormatException() {
        /* ARRANGE */
        final var maxAccess = 3;

        final var constraint1 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ELAPSED_TIME)
                ._operator_(BinaryOperator.SHORTER_EQ)
                ._rightOperand_(new RdfResource("P6M", URI.create("xsd:duration")))
                .build();
        final var constraint2 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource(String.valueOf(maxAccess),
                        URI.create("xsd:decimal")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint1, constraint2))
                .build();

        /* ACT & ASSERT */
        assertThrows(NumberFormatException.class, () -> RuleUtils.getMaxAccess(permission));
    }

    @Test
    public void getTimeInterval_inputNull_throwNullPointerException() {
        /* ACT & ASSERT */
        assertThrows(NullPointerException.class, () -> RuleUtils.getTimeInterval(null));
    }

    @Test
    public void getTimeInterval_inputCorrect_returnTimeInterval() throws ParseException {
        /* ARRANGE */
        final var startDate = "2021-01-01T00:00:00Z";
        final var endDate = "2022-01-01T00:00:00Z";

        final var startConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource(startDate, URI.create("xsd:dateTimeStamp")))
                .build();
        final var endConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.BEFORE)
                ._rightOperand_(new RdfResource(endDate, URI.create("xsd:dateTimeStamp")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(startConstraint, endConstraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getTimeInterval(permission);

        /* ASSERT */
        assertEquals(ZonedDateTime.parse(startDate), result.getStart());
        assertEquals(ZonedDateTime.parse(endDate), result.getEnd());
    }

    @Test
    public void getTimeInterval_inputInvalidWrongConstraintType_returnNull() throws ParseException {
        /* ARRANGE */
        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ELAPSED_TIME)
                ._operator_(BinaryOperator.SHORTER_EQ)
                ._rightOperand_(new RdfResource("P6M", URI.create("xsd:duration")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getTimeInterval(permission);

        /* ASSERT */
        assertNull(result.getStart());
        assertNull(result.getEnd());
    }

    @Test
    public void getTimeInterval_inputInvalidNoStartDate_returnCorrectOutput() throws ParseException {
        /* ARRANGE */
        final var endDate = "2022-01-01T00:00:00Z";

        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.BEFORE)
                ._rightOperand_(new RdfResource(endDate, URI.create("xsd:dateTimeStamp")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getTimeInterval(permission);

        /* ASSERT */
        assertNull(result.getStart());
        assertEquals(ZonedDateTime.parse(endDate), result.getEnd());
    }

    @Test
    public void getTimeInterval_inputInvalidNoEndDate_returnCorrectOutput() throws ParseException {
        /* ARRANGE */
        final var startDate = "2021-01-01T00:00:00Z";

        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource(startDate, URI.create("xsd:dateTimeStamp")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getTimeInterval(permission);

        /* ASSERT */
        assertEquals(ZonedDateTime.parse(startDate), result.getStart());
        assertNull(result.getEnd());
    }

    @Test
    public void getTimeInterval_inputInvalidStartAfterEnd_returnCorrectOutput()
            throws ParseException {
        /* ARRANGE */
        final var startDate = "2022-01-01T00:00:00Z";
        final var endDate = "2021-01-01T00:00:00Z";

        final var startConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource(startDate, URI.create("xsd:dateTimeStamp")))
                .build();
        final var endConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.BEFORE)
                ._rightOperand_(new RdfResource(endDate, URI.create("xsd:dateTimeStamp")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(startConstraint, endConstraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getTimeInterval(permission);

        /* ASSERT */
        assertEquals(ZonedDateTime.parse(startDate), result.getStart());
        assertEquals(ZonedDateTime.parse(endDate), result.getEnd());
    }

    @Test
    public void getTimeInterval_inputInvalidWrongOperator_returnNull() throws ParseException {
        /* ARRANGE */
        final var startDate = "2021-01-01T00:00:00Z";
        final var endDate = "2022-01-01T00:00:00Z";

        final var startConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.CONTAINS)
                ._rightOperand_(new RdfResource(startDate, URI.create("xsd:dateTimeStamp")))
                .build();
        final var endConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.CONTAINS)
                ._rightOperand_(new RdfResource(endDate, URI.create("xsd:dateTimeStamp")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(startConstraint, endConstraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getTimeInterval(permission);

        /* ASSERT */
        assertNull(result.getStart());
        assertNull(result.getEnd());
    }

    @Test
    public void getTimeInterval_inputInvalidWrongDateFormat_returnNull() throws ParseException {
        /* ARRANGE */
        final var startDate = "2021-01-01T00:00:00.000";
        final var endDate = "2022-01-01T00:00:00.000";

        final var startConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.CONTAINS)
                ._rightOperand_(new RdfResource(startDate, URI.create("xsd:dateTimeStamp")))
                .build();
        final var endConstraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.CONTAINS)
                ._rightOperand_(new RdfResource(endDate, URI.create("xsd:dateTimeStamp")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(startConstraint, endConstraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getTimeInterval(permission);

        /* ASSERT */
        assertNull(result.getStart());
        assertNull(result.getEnd());
    }

    @Test
    public void getEndpoint_inputNull_throwNullPointerException() {
        /* ACT & ASSERT */
        assertThrows(NullPointerException.class, () -> RuleUtils.getEndpoint(null));
    }

    @Test
    public void getEndpoint_inputCorrect_returnEndpoint() {
        /* ARRANGE */
        final var endpoint = "https://localhost:8000/notify";

        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ENDPOINT)
                ._operator_(BinaryOperator.DEFINES_AS)
                ._rightOperand_(new RdfResource(endpoint, URI.create("xsd:anyURI")))
                .build();

        final var duty = new DutyBuilder()
                ._action_(Util.asList(Action.NOTIFY))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getEndpoint(duty);

        /* ASSERT */
        assertEquals(endpoint, result);
    }

    @Test
    public void getEndpoint_inputInvalidWrongConstraintType_returnValue() {
        /* ARRANGE */
        final var value = "5";
        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource(value, URI.create("xsd:decimal")))
                .build();

        final var duty = new DutyBuilder()
                ._action_(Util.asList(Action.NOTIFY))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getEndpoint(duty);

        /* ASSERT */
        assertEquals(value, result);
    }

    @Test
    public void getEndpoint_inputInvalidNotificationConstraintNotFirstInList_returnWrongValue() {
        /* ARRANGE */
        final var endpoint = "https://localhost:8000/notify";

        final var constraint1 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource("5", URI.create("xsd:decimal")))
                .build();
        final var constraint2 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ENDPOINT)
                ._operator_(BinaryOperator.DEFINES_AS)
                ._rightOperand_(new RdfResource(endpoint, URI.create("xsd:anyURI")))
                .build();

        final var duty = new DutyBuilder()
                ._action_(Util.asList(Action.NOTIFY))
                ._constraint_(Util.asList(constraint1, constraint2))
                .build();

        /* ACT */
        final var result = RuleUtils.getEndpoint(duty);

        /* ASSERT */
        assertNotEquals(endpoint, result);
    }

    @Test
    public void getPipEndpoint_inputNull_throwNullPointerException() {
        /* ACT & ASSERT */
        assertThrows(NullPointerException.class, () -> RuleUtils.getPipEndpoint(null));
    }

    @Test
    public void getPipEndpoint_inputCorrect_returnPipEndpoint() {
        /* ARRANGE */
        final var pipEndpoint = URI.create("https://pip.com");

        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource("5", URI.create("xsd:decimal")))
                ._pipEndpoint_(pipEndpoint)
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getPipEndpoint(permission);

        /* ASSERT */
        assertEquals(pipEndpoint, result);
    }

    @Test
    public void getPipEndpoint_inputInvalidConstraintHasNoPipEndpoint_returnNull() {
        /* ARRANGE */
        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource("5", URI.create("xsd:decimal")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getPipEndpoint(permission);

        /* ASSERT */
        assertNull(result);
    }

    @Test
    public void getDate_inputNull_throwNullPointerException() {
        /* ACT & ASSERT*/
        assertThrows(NullPointerException.class, () -> RuleUtils.getDate(null));
    }

    @Test
    public void getDate_inputCorrect_returnDate() {
        /* ARRANGE */
        final var date = "2021-01-01T00:00:00Z";

        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource(date, URI.create("xsd:dateTimeStamp")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getDate(permission);

        /* ASSERT */
        assertEquals(ZonedDateTime.parse(date), result);
    }

    @Test
    public void getDate_inputInvalidWrongConstraintType_throwDateTimeParseException() {
        /* ARRANGE */
        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.EQ)
                ._rightOperand_(new RdfResource("5", URI.create("xsd:decimal")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT & ASSERT */
        assertThrows(DateTimeParseException.class, () -> RuleUtils.getDate(permission));
    }

    @Test
    public void getDate_inputInvalidWrongDateFormat_throwDateTimeParseException() {
        /* ARRANGE */
        final var date = "2021-01-01T00:00:00.000";

        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource(date, URI.create("xsd:dateTimeStamp")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT & ASSERT */
        assertThrows(DateTimeParseException.class, () -> RuleUtils.getDate(permission));
    }

    @Test
    public void getDate_inputInvalidNotADate_throwDateTimeParseException() {
        /* ARRANGE */
        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource(
                        "I am not a date.", URI.create("xsd:dateTimeStamp")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        assertThrows(DateTimeParseException.class, () -> RuleUtils.getDate(permission));
    }

    @Test
    public void getDate_inputInvalidDateConstraintNotFirstInList_throwDateTimeParseException() {
        /* ARRANGE */
        final var date = "2021-01-01T00:00:00Z";

        final var constraint1 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ELAPSED_TIME)
                ._operator_(BinaryOperator.SHORTER_EQ)
                ._rightOperand_(new RdfResource("P6M", URI.create("xsd:duration")))
                .build();
        final var constraint2 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                ._operator_(BinaryOperator.AFTER)
                ._rightOperand_(new RdfResource(date, URI.create("xsd:dateTimeStamp")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint1, constraint2))
                .build();

        /* ACT & ASSERT */
        assertThrows(DateTimeParseException.class, () -> RuleUtils.getDate(permission));
    }

    @Test
    public void getDuration_inputNull_throwNullPointerException() {
        /* ACT & ASSERT*/
        assertThrows(NullPointerException.class, () -> RuleUtils.getDuration(null));
    }

    @Test
    public void getDuration_inputCorrect_returnDuration() throws DatatypeConfigurationException {
        /* ARRANGE */
        final var duration = "PT1M30.5S";

        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ELAPSED_TIME)
                ._operator_(BinaryOperator.SHORTER_EQ)
                ._rightOperand_(new RdfResource(duration, URI.create("http://www.w3.org/2001/XMLSchema#duration")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getDuration(permission);

        /* ASSERT */
        assertEquals(DatatypeFactory.newInstance().newDuration(duration).toString(),
                result.toString());
    }

    @Test
    public void getDuration_inputInvalidWrongConstraintType_returnNull() {
        /* ARRANGE */
        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.LTEQ)
                ._rightOperand_(new RdfResource("5", URI.create("xsd:decimal")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT */
        final var result = RuleUtils.getDuration(permission);

        /* ASSERT */
        assertNull(result);
    }

    @Test
    public void getDuration_inputInvalidNotADuration_throwDateTimeParseException() {
        /* ARRANGE */
        final var constraint = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ELAPSED_TIME)
                ._operator_(BinaryOperator.SHORTER_EQ)
                ._rightOperand_(new RdfResource(
                        "I am not a duration.", URI.create("http://www.w3.org/2001/XMLSchema#duration")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint))
                .build();

        /* ACT & ASSERT */
        assertThrows(DateTimeParseException.class, () -> RuleUtils.getDuration(permission));
    }

    @Test
    public void getDuration_inputInvalidDurationConstraintNotFirstInList_returnNull() {
        /* ARRANGE */
        final var duration = "P6M";

        final var constraint1 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.COUNT)
                ._operator_(BinaryOperator.LTEQ)
                ._rightOperand_(new RdfResource("5", URI.create("xsd:decimal")))
                .build();
        final var constraint2 = new ConstraintBuilder()
                ._leftOperand_(LeftOperand.ELAPSED_TIME)
                ._operator_(BinaryOperator.SHORTER_EQ)
                ._rightOperand_(new RdfResource(duration, URI.create("xsd:duration")))
                .build();

        final var permission = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(constraint1, constraint2))
                .build();

        /*ACT*/
        final var result = RuleUtils.getDuration(permission);

        /*ASSERT*/
        assertNull(result);
    }

    @Test
    public void checkRuleForDeletion_shouldDelete_returnTrue() {
        /* ARRANGE */
        final var expiredRule = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                        ._operator_(BinaryOperator.AFTER)
                        ._rightOperand_(new RdfResource("2020-07-11T00:00:00Z",
                                URI.create("xsd:dateTimeStamp")))
                        .build()))
                ._postDuty_(Util.asList(new DutyBuilder()
                        ._action_(Util.asList(Action.DELETE))
                        ._constraint_(Util.asList(new ConstraintBuilder()
                                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                                ._operator_(BinaryOperator.TEMPORAL_EQUALS)
                                ._rightOperand_(new RdfResource("2020-07-11T00:00:00Z",
                                        URI.create("xsd:dateTimeStamp")))
                                .build()))
                        .build()))
                .build();

        /* ACT && ASSERT */
        assertTrue(RuleUtils.checkRuleForDeletion(expiredRule.getPostDuty().get(0)));
    }

    @Test
    public void checkRuleForDeletion_shouldNotDelete_returnFalse() {
        /* ARRANGE */
        final var expiredRule = new PermissionBuilder()
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                        ._operator_(BinaryOperator.AFTER)
                        ._rightOperand_(new RdfResource("2050-07-11T00:00:00Z",
                                URI.create("xsd:dateTimeStamp")))
                        .build()))
                ._postDuty_(Util.asList(new DutyBuilder()
                        ._action_(Util.asList(Action.DELETE))
                        ._constraint_(Util.asList(new ConstraintBuilder()
                                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                                ._operator_(BinaryOperator.TEMPORAL_EQUALS)
                                ._rightOperand_(new RdfResource("2050-07-11T00:00:00Z",
                                        URI.create("xsd:dateTimeStamp")))
                                .build()))
                        .build()))
                .build();

        /* ACT && ASSERT */
        assertFalse(RuleUtils.checkRuleForDeletion(expiredRule.getPostDuty().get(0)));
    }

    @Test
    public void isExpired_dateBefore_returnTrue() {
        /* ARRANGE */
        final var expiration = ZonedDateTime.parse("2021-02-14T12:13:14+01:00");

        /* ACT && ASSERT */
        assertTrue(RuleUtils.isExpired(expiration));
    }

    @Test
    public void isExpired_dateAfter_returnFalse() {
        /* ARRANGE */
        final var expiration = ZonedDateTime.parse("2051-02-14T12:13:14+01:00");

        /* ACT && ASSERT */
        assertFalse(RuleUtils.isExpired(expiration));
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    public Rule getRuleOne() {
        return new DutyBuilder()._action_(Util.asList(getActionOne())).build();
    }

    public Rule getRuleTwo() {
        return new ProhibitionBuilder()._action_(Util.asList(getActionsTwo())).build();
    }

    public Rule getRuleThree() {
        return new PermissionBuilder()._action_(Util.asList(getActionThree())).build();
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
        return new DutyBuilder()._action_(Util.asList(getActionOne()))._target_(target).build();
    }

    @SneakyThrows
    private Contract getContractWithoutConsumer() {
        final var constructor = Contract.class.getConstructor();
        constructor.setAccessible(true);

        final var contract = constructor.newInstance();
        ReflectionTestUtils.setField(contract, "title", "Catalog without consumer");
        ReflectionTestUtils.setField(contract, "consumer", URI.create(""));

        return contract;
    }

    @SneakyThrows
    private Contract getContractWithConsumer() {
        final var constructor = Contract.class.getConstructor();
        constructor.setAccessible(true);

        final var contract = constructor.newInstance();
        ReflectionTestUtils.setField(contract, "title", "Catalog with consumer");
        ReflectionTestUtils.setField(contract, "consumer", URI.create("https://someConsumer"));

        return contract;
    }
}
