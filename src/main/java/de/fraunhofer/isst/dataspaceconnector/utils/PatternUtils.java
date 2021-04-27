package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.ConstraintBuilder;
import de.fraunhofer.iais.eis.DutyBuilder;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.ProhibitionBuilder;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;

import java.net.URI;

public final class PatternUtils {

    private PatternUtils() {
        // not used
    }

    /**
     * Build ids rule.
     *
     * @return The ids rule.
     * @throws ConstraintViolationException If the object creation fails.
     */
    public static Rule buildProvideAccessRule() {
        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                ._description_(Util.asList(new TypedLiteral("provide-access")))
                ._action_(Util.asList(Action.USE))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @return The ids rule.
     * @throws ConstraintViolationException If the object creation fails.
     */
    public static Rule buildProhibitAccessRule() {
        return new ProhibitionBuilder()
                ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                ._description_(Util.asList(new TypedLiteral("prohibit-access")))
                ._action_(Util.asList(Action.USE))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @return The ids rule.
     * @throws ConstraintViolationException If the object creation fails.
     */
    public static Rule buildNTimesUsageRule() {
        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                ._description_(Util.asList(new TypedLiteral("n-times-usage")))
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.COUNT)
                        ._operator_(BinaryOperator.LTEQ)
                        ._rightOperand_(new RdfResource("5", URI.create("xsd:double")))
                        ._pipEndpoint_(
                                URI.create("https://localhost:8080/admin/api/resources/"))
                        .build()))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @return The ids rule.
     * @throws ConstraintViolationException If the object creation fails.
     */
    public static Rule buildDurationUsageRule() {
        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                ._description_(Util.asList(new TypedLiteral("duration-usage")))
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.ELAPSED_TIME)
                        ._operator_(BinaryOperator.SHORTER_EQ)
                        ._rightOperand_(new RdfResource("PT4H", URI.create("xsd:duration")))
                        .build()))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @return The ids rule.
     * @throws ConstraintViolationException If the object creation fails.
     */
    public static Rule buildIntervalUsageRule() {
        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                ._description_(Util.asList(new TypedLiteral("usage-during-interval")))
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                        ._operator_(BinaryOperator.AFTER)
                        ._rightOperand_(new RdfResource("2020-07-11T00:00:00Z",
                                URI.create("xsd:dateTimeStamp")))
                        .build(), new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                        ._operator_(BinaryOperator.BEFORE)
                        ._rightOperand_(new RdfResource("2020-07-11T00:00:00Z",
                                URI.create("xsd:dateTimeStamp")))
                        .build()))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @return The ids rule.
     * @throws ConstraintViolationException If the object creation fails.
     */
    public static Rule buildUsageUntilDeletionRule() {
        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                ._description_(Util.asList(new TypedLiteral("usage-until-deletion")))
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                        ._operator_(BinaryOperator.AFTER)
                        ._rightOperand_(new RdfResource("2020-07-11T00:00:00Z",
                                URI.create("xsd:dateTimeStamp")))
                        .build(), new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                        ._operator_(BinaryOperator.BEFORE)
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
    }

    /**
     * Build ids rule.
     *
     * @return The ids rule.
     * @throws ConstraintViolationException If the object creation fails.
     */
    public static Rule buildUsageLoggingRule() {
        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                ._description_(Util.asList(new TypedLiteral("usage-logging")))
                ._action_(Util.asList(Action.USE))
                ._postDuty_(Util.asList(new DutyBuilder()
                        ._action_(Util.asList(Action.LOG))
                        .build()))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @return The ids rule.
     * @throws ConstraintViolationException If the object creation fails.
     */
    public static Rule buildUsageNotificationRule() {
        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                ._description_(Util.asList(new TypedLiteral("usage-notification")))
                ._action_(Util.asList(Action.USE))
                ._postDuty_(Util.asList(new DutyBuilder()
                        ._action_(Util.asList(Action.NOTIFY))
                        ._constraint_(Util.asList(new ConstraintBuilder()
                                ._leftOperand_(LeftOperand.ENDPOINT)
                                ._operator_(BinaryOperator.DEFINES_AS)
                                ._rightOperand_(new RdfResource(
                                        "https://localhost:8000/api/ids"
                                                + "/data", URI.create("xsd:anyURI")))
                                .build()))
                        .build()))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @return The ids rule.
     * @throws ConstraintViolationException If the object creation fails.
     */
    public static Rule buildConnectorRestrictedUsageRule() {
        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                ._description_(Util.asList(new TypedLiteral("connector-restriction")))
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.SYSTEM)
                        ._operator_(BinaryOperator.SAME_AS)
                        ._rightOperand_(
                                new RdfResource("https://example.com",
                                        URI.create("xsd:anyURI")))
                        .build()))
                .build();
    }
}
