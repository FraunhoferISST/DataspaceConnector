package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.Prohibition;
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
   public void buildNTimesUsageRule_returnPermissionWithConstraint() {
       /* ARRANGE */
       // Nothing to arrange here.

       /* ACT */
       final var rule = PatternUtils.buildNTimesUsageRule();

       /* ASSERT */
       assertTrue(Permission.class.isAssignableFrom(rule.getClass()));
       assertEquals(Action.USE, rule.getAction().get(0));
       assertEquals(1, rule.getConstraint().size());

       final var constraint = (Constraint) rule.getConstraint().get(0);
       assertEquals(LeftOperand.COUNT, constraint.getLeftOperand());

       final var operator = constraint.getOperator();
       assertTrue((BinaryOperator.LT.equals(operator))
               || (BinaryOperator.LTEQ.equals(operator)));

       assertTrue(isValidInteger(constraint.getRightOperand().getValue()));
   }

   @Test
   public void buildDurationUsageRule_returnPermissionWithConstraint() {
       /* ARRANGE */
       // Nothing to arrange here.

       /* ACT */
       final var rule = PatternUtils.buildDurationUsageRule();

       /* ASSERT */
       assertTrue(Permission.class.isAssignableFrom(rule.getClass()));
       assertEquals(Action.USE, rule.getAction().get(0));
       assertEquals(1, rule.getConstraint().size());

       final var constraint = (Constraint) rule.getConstraint().get(0);
       assertEquals(LeftOperand.ELAPSED_TIME, constraint.getLeftOperand());

       final var operator = constraint.getOperator();
       assertTrue((BinaryOperator.SHORTER_EQ.equals(operator))
               || (BinaryOperator.SHORTER.equals(operator)));

       assertTrue(isValidDuration(constraint.getRightOperand().getValue()));
   }

   @Test
   public void buildIntervalUsageRule_returnPermissionWithTwoConstraints() {
       /* ARRANGE */
       // Nothing to arrange here.

       /* ACT */
       final var rule = PatternUtils.buildIntervalUsageRule();

       /* ASSERT */
       assertTrue(Permission.class.isAssignableFrom(rule.getClass()));
       assertEquals(Action.USE, rule.getAction().get(0));
       assertEquals(2, rule.getConstraint().size());

       final var constraint1 = (Constraint) rule.getConstraint().get(0);
       assertEquals(LeftOperand.POLICY_EVALUATION_TIME, constraint1.getLeftOperand());
       final var operator1 = constraint1.getOperator();
       assertTrue((BinaryOperator.BEFORE.equals(operator1))
               || (BinaryOperator.AFTER.equals(operator1)));
       assertTrue(isValidDate(constraint1.getRightOperand().getValue()));

       final var constraint2 = (Constraint) rule.getConstraint().get(1);
       assertEquals(LeftOperand.POLICY_EVALUATION_TIME, constraint2.getLeftOperand());
       final var operator2 = constraint2.getOperator();
       assertTrue((BinaryOperator.BEFORE.equals(operator2))
               || (BinaryOperator.AFTER.equals(operator2)));
       assertTrue(isValidDate(constraint2.getRightOperand().getValue()));
   }

   @Test
   public void buildUsageUntilDeletionRule_returnPermissionWithTwoConstraintsAndPostDuty() {
       /* ARRANGE */
       // Nothing to arrange here.

       /* ACT */
       final var rule = PatternUtils.buildUsageUntilDeletionRule();

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
       assertTrue(isValidDate(constraint1.getRightOperand().getValue()));

       final var constraint2 = (Constraint) rule.getConstraint().get(1);
       assertEquals(LeftOperand.POLICY_EVALUATION_TIME, constraint2.getLeftOperand());
       final var operator2 = constraint2.getOperator();
       assertTrue((BinaryOperator.BEFORE.equals(operator2))
               || (BinaryOperator.AFTER.equals(operator2)));
       assertTrue(isValidDate(constraint2.getRightOperand().getValue()));

       final var duty = ((Permission) rule).getPostDuty().get(0);
       assertEquals(1, duty.getConstraint().size());

       final var dutyConstraint = (Constraint) duty.getConstraint().get(0);
       assertEquals(LeftOperand.POLICY_EVALUATION_TIME, dutyConstraint.getLeftOperand());
       final var dutyConstraintOperator = dutyConstraint.getOperator();
       assertEquals(BinaryOperator.TEMPORAL_EQUALS, dutyConstraintOperator);
       assertTrue(isValidDate(dutyConstraint.getRightOperand().getValue()));
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
   public void buildUsageNotificationRule_returnPermissionWithPostDuty() {
       /* ARRANGE */
       // Nothing to arrange here.

       /* ACT */
       final var rule = PatternUtils.buildUsageNotificationRule();

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

       assertTrue(isValidUri(dutyConstraint.getRightOperand().getValue()));
   }

    @Test
    public void buildConnectorRestrictedUsageRule_returnPermissionWithPostDuty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var rule = PatternUtils.buildConnectorRestrictedUsageRule();

        /* ASSERT */
        assertTrue(Permission.class.isAssignableFrom(rule.getClass()));
        assertEquals(Action.USE, rule.getAction().get(0));
        assertEquals(1, rule.getConstraint().size());

        final var constraint = (Constraint) rule.getConstraint().get(0);
        assertEquals(LeftOperand.SYSTEM, constraint.getLeftOperand());
        assertEquals(BinaryOperator.SAME_AS, constraint.getOperator());
        assertTrue(isValidUri(constraint.getRightOperand().getValue()));
    }

   /**************************************************************************
    * Utilities.
    *************************************************************************/

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
