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
package io.dataspaceconnector.util;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Prohibition;
import io.dataspaceconnector.model.pattern.DeletionDesc;
import io.dataspaceconnector.model.pattern.DurationDesc;
import io.dataspaceconnector.model.pattern.IntervalDesc;
import io.dataspaceconnector.model.pattern.NotificationDesc;
import io.dataspaceconnector.model.pattern.ConnectorRestrictionDesc;
import io.dataspaceconnector.model.pattern.UsageNumberDesc;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternUtilsTest {

   private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

   @Test
   public void buildProvideAccessRule_returnPermissionWithoutConstraintsAndPostDuties() {
       /* ARRANGE */
       // Nothing to arrange here.

       /* ACT */
       final var rule = PatternUtils.buildProvideAccessRule();

       /* ASSERT */
       assertTrue(Permission.class.isAssignableFrom(rule.getClass()));
       assertEquals(Action.USE, rule.getAction().get(0));
       assertNull(rule.getConstraint());
       assertNull(((Permission) rule).getPostDuty());
   }

   @Test
   public void buildProhibitAccessRule_returnProhibition() {
       /* ARRANGE */
       // Nothing to arrange here.

       /* ACT */
       final var rule = PatternUtils.buildProhibitAccessRule();

       /* ASSERT */
       assertTrue(Prohibition.class.isAssignableFrom(rule.getClass()));
       assertEquals(Action.USE, rule.getAction().get(0));
   }

   @Test
   @SneakyThrows
   public void buildNTimesUsageRule_returnPermissionWithConstraint() {
       /* ARRANGE */
       final var value = "5";
       final var input = new UsageNumberDesc();
       input.setValue(value);

       /* ACT */
       final var rule = PatternUtils.buildNTimesUsageRule(input);

       /* ASSERT */
       assertTrue(Permission.class.isAssignableFrom(rule.getClass()));
       assertEquals(Action.USE, rule.getAction().get(0));
       assertEquals(1, rule.getConstraint().size());

       final var constraint = (Constraint) rule.getConstraint().get(0);
       assertEquals(LeftOperand.COUNT, constraint.getLeftOperand());

       final var operator = constraint.getOperator();
       assertTrue((BinaryOperator.LT.equals(operator))
               || (BinaryOperator.LTEQ.equals(operator)));

       final var valueResult = constraint.getRightOperand().getValue();
       assertEquals(value, valueResult);
       assertTrue(isValidInteger(valueResult));
   }

   @Test
   @SneakyThrows
   public void buildDurationUsageRule_returnPermissionWithConstraint() {
       /* ARRANGE */
       final var value = "PT1M30.5S";
       final var input = new DurationDesc();
       input.setValue(value);

       /* ACT */
       final var rule = PatternUtils.buildDurationUsageRule(input);

       /* ASSERT */
       assertTrue(Permission.class.isAssignableFrom(rule.getClass()));
       assertEquals(Action.USE, rule.getAction().get(0));
       assertEquals(1, rule.getConstraint().size());

       final var constraint = (Constraint) rule.getConstraint().get(0);
       assertEquals(LeftOperand.ELAPSED_TIME, constraint.getLeftOperand());

       final var operator = constraint.getOperator();
       assertTrue((BinaryOperator.SHORTER_EQ.equals(operator))
               || (BinaryOperator.SHORTER.equals(operator)));

       final var valueResult = constraint.getRightOperand().getValue();
       assertEquals(value, valueResult);
       assertTrue(isValidDuration(valueResult));
   }

   @Test
   @SneakyThrows
   public void buildIntervalUsageRule_returnPermissionWithTwoConstraints() {
       /* ARRANGE */
       final var start = "2020-07-11T00:00:00Z";
       final var end = "2020-07-11T00:00:00Z";
       final var input = new IntervalDesc();
       input.setStart(start);
       input.setEnd(end);

       /* ACT */
       final var rule = PatternUtils.buildIntervalUsageRule(input);

       /* ASSERT */
       assertTrue(Permission.class.isAssignableFrom(rule.getClass()));
       assertEquals(Action.USE, rule.getAction().get(0));
       assertEquals(2, rule.getConstraint().size());

       final var constraint1 = (Constraint) rule.getConstraint().get(0);
       assertEquals(LeftOperand.POLICY_EVALUATION_TIME, constraint1.getLeftOperand());
       final var operator1 = constraint1.getOperator();
       assertTrue((BinaryOperator.BEFORE.equals(operator1))
               || (BinaryOperator.AFTER.equals(operator1)));
       final var startResult = constraint1.getRightOperand().getValue();
       assertEquals(start, startResult);
       assertTrue(isValidDate(startResult));

       final var constraint2 = (Constraint) rule.getConstraint().get(1);
       assertEquals(LeftOperand.POLICY_EVALUATION_TIME, constraint2.getLeftOperand());
       final var operator2 = constraint2.getOperator();
       assertTrue((BinaryOperator.BEFORE.equals(operator2))
               || (BinaryOperator.AFTER.equals(operator2)));
       final var endResult = constraint2.getRightOperand().getValue();
       assertEquals(end, endResult);
       assertTrue(isValidDate(endResult));
   }

   @Test
   @SneakyThrows
   public void buildUsageUntilDeletionRule_returnPermissionWithTwoConstraintsAndPostDuty() {
       /* ARRANGE */
       final var start = "2020-07-11T00:00:00Z";
       final var end = "2020-07-11T00:00:00Z";
       final var date = "2020-07-11T00:00:00Z";
       final var input = new DeletionDesc();
       input.setStart(start);
       input.setEnd(end);
       input.setDate(date);

       /* ACT */
       final var rule = PatternUtils.buildUsageUntilDeletionRule(input);

       /* ASSERT */
       assertTrue(Permission.class.isAssignableFrom(rule.getClass()));
       assertEquals(Action.USE, rule.getAction().get(0));
       assertEquals(2, rule.getConstraint().size());
       assertEquals(1, ((Permission) rule).getPostDuty().size());

       final var constraint1 = (Constraint) rule.getConstraint().get(0);
       assertEquals(LeftOperand.POLICY_EVALUATION_TIME, constraint1.getLeftOperand());
       final var operator1 = constraint1.getOperator();
       assertTrue((BinaryOperator.BEFORE.equals(operator1))
               || (BinaryOperator.AFTER.equals(operator1)));
       final var startResult = constraint1.getRightOperand().getValue();
       assertEquals(start, startResult);
       assertTrue(isValidDate(startResult));

       final var constraint2 = (Constraint) rule.getConstraint().get(1);
       assertEquals(LeftOperand.POLICY_EVALUATION_TIME, constraint2.getLeftOperand());
       final var operator2 = constraint2.getOperator();
       assertTrue((BinaryOperator.BEFORE.equals(operator2))
               || (BinaryOperator.AFTER.equals(operator2)));
       final var endResult = constraint2.getRightOperand().getValue();
       assertEquals(end, endResult);
       assertTrue(isValidDate(endResult));

       final var duty = ((Permission) rule).getPostDuty().get(0);
       assertEquals(1, duty.getConstraint().size());

       final var dutyConstraint = (Constraint) duty.getConstraint().get(0);
       assertEquals(LeftOperand.POLICY_EVALUATION_TIME, dutyConstraint.getLeftOperand());
       final var dutyConstraintOperator = dutyConstraint.getOperator();
       assertEquals(BinaryOperator.TEMPORAL_EQUALS, dutyConstraintOperator);
       final var dateResult = dutyConstraint.getRightOperand().getValue();
       assertEquals(date, dateResult);
       assertTrue(isValidDate(dateResult));
   }

   @Test
   public void buildUsageLoggingRule_returnPermissionWithPostDuty() {
       /* ARRANGE */
       // Nothing to arrange here.

       /* ACT */
       final var rule = PatternUtils.buildUsageLoggingRule();

       /* ASSERT */
       assertTrue(Permission.class.isAssignableFrom(rule.getClass()));
       assertEquals(Action.USE, rule.getAction().get(0));
       assertEquals(1, ((Permission) rule).getPostDuty().size());

       final var dutyAction = ((Permission) rule).getPostDuty().get(0).getAction().get(0);
       assertEquals(Action.LOG, dutyAction);
   }

   @Test
   @SneakyThrows
   public void buildUsageNotificationRule_returnPermissionWithPostDuty() {
       /* ARRANGE */
       final var value = "https://someRecipient";
       final var input = new NotificationDesc();
       input.setUrl(value);

       /* ACT */
       final var rule = PatternUtils.buildUsageNotificationRule(input);

       /* ASSERT */
       assertTrue(Permission.class.isAssignableFrom(rule.getClass()));
       assertEquals(Action.USE, rule.getAction().get(0));
       assertEquals(1, ((Permission) rule).getPostDuty().size());

       final var duty = ((Permission) rule).getPostDuty().get(0);
       final var dutyAction = duty.getAction().get(0);
       assertEquals(Action.NOTIFY, dutyAction);
       assertEquals(1, duty.getConstraint().size());

       final var dutyConstraint = (Constraint) duty.getConstraint().get(0);
       assertEquals(LeftOperand.ENDPOINT, dutyConstraint.getLeftOperand());

       final var dutyConstraintOperator = dutyConstraint.getOperator();
       assertEquals(BinaryOperator.DEFINES_AS, dutyConstraintOperator);

       final var valueResult = dutyConstraint.getRightOperand().getValue();
       assertEquals(value, valueResult);
       assertTrue(isValidUri(valueResult));
   }

    @Test
    @SneakyThrows
    public void buildConnectorRestrictedUsageRule_returnPermissionWithPostDuty() {
        /* ARRANGE */
        final var value = "https://someRecipient";
        final var input = new ConnectorRestrictionDesc();
        input.setUrl(value);

        /* ACT */
        final var rule = PatternUtils.buildConnectorRestrictedUsageRule(input);

        /* ASSERT */
        assertTrue(Permission.class.isAssignableFrom(rule.getClass()));
        assertEquals(Action.USE, rule.getAction().get(0));
        assertEquals(1, rule.getConstraint().size());

        final var constraint = (Constraint) rule.getConstraint().get(0);
        assertEquals(LeftOperand.SYSTEM, constraint.getLeftOperand());
        assertEquals(BinaryOperator.SAME_AS, constraint.getOperator());
        final var valueResult = constraint.getRightOperand().getValue();
        assertEquals(value, valueResult);
        assertTrue(isValidUri(valueResult));
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

   private boolean isValidInteger(String string) {
       try {
           Integer.parseInt(string);
           return true;
       } catch (NumberFormatException e) {
           return false;
       }
   }

   private boolean isValidDuration(String string) {
       try {
           Duration.parse(string);
           return true;
       } catch (DateTimeParseException e) {
           return false;
       }
   }

   private boolean isValidDate(String string) {
       try {
           sdf.parse(string);
           return true;
       } catch (ParseException e) {
           return false;
       }
   }

   private boolean isValidUri(String string) {
       try {
           URI.create(string);
           return true;
       } catch (IllegalArgumentException e) {
           return false;
       }
   }
}
