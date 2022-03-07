/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.controller.policy.util;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.ConstraintBuilder;
import de.fraunhofer.iais.eis.DutyBuilder;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.ProhibitionBuilder;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.common.ids.mapping.ToIdsObjectMapper;
import io.dataspaceconnector.common.util.ValidationUtils;
import io.dataspaceconnector.model.pattern.ConnectorRestrictionDesc;
import io.dataspaceconnector.model.pattern.DeletionDesc;
import io.dataspaceconnector.model.pattern.DurationDesc;
import io.dataspaceconnector.model.pattern.IntervalDesc;
import io.dataspaceconnector.model.pattern.LoggingDesc;
import io.dataspaceconnector.model.pattern.NotificationDesc;
import io.dataspaceconnector.model.pattern.PermissionDesc;
import io.dataspaceconnector.model.pattern.ProhibitionDesc;
import io.dataspaceconnector.model.pattern.SecurityRestrictionDesc;
import io.dataspaceconnector.model.pattern.UsageNumberDesc;

import java.net.URI;

/**
 * Contains utility methods for creating example ids rules.
 */
public final class PatternUtils {

    /**
     * Default constructor.
     */
    private PatternUtils() {
        // not used
    }

    /**
     * Build ids rule.
     *
     * @param input Rule input.
     * @return The ids rule.
     */
    public static Rule buildProvideAccessRule(final PermissionDesc input) {
        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral(
                        input.getTitle() == null ? "" : input.getTitle())))
                ._description_(Util.asList(new TypedLiteral(
                        input.getDescription() == null ? "" : input.getDescription())))
                ._action_(Util.asList(Action.USE))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @param input Rule input.
     * @return The ids rule.
     */
    public static Rule buildProhibitAccessRule(final ProhibitionDesc input) {
        return new ProhibitionBuilder()
                ._title_(Util.asList(new TypedLiteral(
                        input.getTitle() == null ? "" : input.getTitle())))
                ._description_(Util.asList(new TypedLiteral(
                        input.getDescription() == null ? "" : input.getDescription())))
                ._action_(Util.asList(Action.USE))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @param input Rule input.
     * @return The ids rule.
     * @throws Exception if input value is not a valid integer.
     */
    public static Rule buildNTimesUsageRule(final UsageNumberDesc input) throws Exception {
        final var value = input.getValue();

        if (!ValidationUtils.isValidInteger(value)) {
            throw new Exception("This is not a valid number.");
        }

        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral(
                        input.getTitle() == null ? "" : input.getTitle())))
                ._description_(Util.asList(new TypedLiteral(
                        input.getDescription() == null ? "" : input.getDescription())))
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.COUNT)
                        ._operator_(BinaryOperator.LTEQ)
                        ._rightOperand_(new RdfResource(value, URI.create("xsd:double")))
                        .build()))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @param input Rule input.
     * @return The ids rule.
     * @throws Exception if input value is not a valid integer.
     */
    public static Rule buildDurationUsageRule(final DurationDesc input) throws Exception {
        final var value = input.getValue();

        if (!ValidationUtils.isValidDuration(value)) {
            throw new Exception("This is not a valid duration.");
        }

        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral(
                        input.getTitle() == null ? "" : input.getTitle())))
                ._description_(Util.asList(new TypedLiteral(
                        input.getDescription() == null ? "" : input.getDescription())))
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.ELAPSED_TIME)
                        ._operator_(BinaryOperator.SHORTER_EQ)
                        ._rightOperand_(new RdfResource(value, URI.create("xsd:duration")))
                        .build()))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @param input Rule input.
     * @return The ids rule.
     * @throws Exception if input value is not a valid integer.
     */
    public static Rule buildIntervalUsageRule(final IntervalDesc input) throws Exception {
        final var start = input.getStart();
        final var end = input.getEnd();

        if (ValidationUtils.isInvalidDate(start) || ValidationUtils.isInvalidDate(end)) {
            throw new Exception("This is not a valid datetime.");
        }

        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral(
                        input.getTitle() == null ? "" : input.getTitle())))
                ._description_(Util.asList(new TypedLiteral(
                        input.getDescription() == null ? "" : input.getDescription())))
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                        ._operator_(BinaryOperator.AFTER)
                        ._rightOperand_(new RdfResource(start, URI.create("xsd:dateTimeStamp")))
                        .build(), new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                        ._operator_(BinaryOperator.BEFORE)
                        ._rightOperand_(new RdfResource(end, URI.create("xsd:dateTimeStamp")))
                        .build()))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @param input Rule input.
     * @return The ids rule.
     * @throws Exception if input value is not a valid integer.
     */
    public static Rule buildUsageUntilDeletionRule(final DeletionDesc input) throws Exception {
        final var start = input.getStart();
        final var end = input.getEnd();
        final var date = input.getDate();

        if (ValidationUtils.isInvalidDate(start) || ValidationUtils.isInvalidDate(end)
                || ValidationUtils.isInvalidDate(date)) {
            throw new Exception("This is not a valid datetime.");
        }

        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral(
                        input.getTitle() == null ? "" : input.getTitle())))
                ._description_(Util.asList(new TypedLiteral(
                        input.getDescription() == null ? "" : input.getDescription())))
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                        ._operator_(BinaryOperator.AFTER)
                        ._rightOperand_(new RdfResource(start, URI.create("xsd:dateTimeStamp")))
                        .build(), new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                        ._operator_(BinaryOperator.BEFORE)
                        ._rightOperand_(new RdfResource(end, URI.create("xsd:dateTimeStamp")))
                        .build()))
                ._postDuty_(Util.asList(new DutyBuilder()
                        ._action_(Util.asList(Action.DELETE))
                        ._constraint_(Util.asList(new ConstraintBuilder()
                                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                                ._operator_(BinaryOperator.TEMPORAL_EQUALS)
                                ._rightOperand_(new RdfResource(date,
                                        URI.create("xsd:dateTimeStamp")))
                                .build()))
                        .build()))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @param input Rule input.
     * @return The ids rule.
     */
    public static Rule buildUsageLoggingRule(final LoggingDesc input) {
        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral(
                        input.getTitle() == null ? "" : input.getTitle())))
                ._description_(Util.asList(new TypedLiteral(
                        input.getDescription() == null ? "" : input.getDescription())))
                ._action_(Util.asList(Action.USE))
                ._postDuty_(Util.asList(new DutyBuilder()
                        ._action_(Util.asList(Action.LOG))
                        .build()))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @param input Rule input.
     * @return The ids rule.
     * @throws Exception if input value is not a valid integer.
     */
    public static Rule buildUsageNotificationRule(final NotificationDesc input) throws Exception {
        final var recipient = input.getUrl();

        if (ValidationUtils.isInvalidUri(recipient)) {
            throw new Exception("This is not a valid url.");
        }

        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral(
                        input.getTitle() == null ? "" : input.getTitle())))
                ._description_(Util.asList(new TypedLiteral(
                        input.getDescription() == null ? "" : input.getDescription())))
                ._action_(Util.asList(Action.USE))
                ._postDuty_(Util.asList(new DutyBuilder()
                        ._action_(Util.asList(Action.NOTIFY))
                        ._constraint_(Util.asList(new ConstraintBuilder()
                                ._leftOperand_(LeftOperand.ENDPOINT)
                                ._operator_(BinaryOperator.DEFINES_AS)
                                ._rightOperand_(new RdfResource(recipient,
                                        URI.create("xsd:anyURI")))
                                .build()))
                        .build()))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @param input Rule input.
     * @return The ids rule.
     * @throws Exception if input value is not a valid integer.
     */
    public static Rule buildConnectorRestrictedUsageRule(final ConnectorRestrictionDesc input)
            throws Exception {
        final var id = input.getUrl();

        if (ValidationUtils.isInvalidUri(id)) {
            throw new Exception("This is not a valid url.");
        }

        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral(
                        input.getTitle() == null ? "" : input.getTitle())))
                ._description_(Util.asList(new TypedLiteral(
                        input.getDescription() == null ? "" : input.getDescription())))
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.SYSTEM)
                        ._operator_(BinaryOperator.SAME_AS)
                        ._rightOperand_(new RdfResource(id, URI.create("xsd:anyURI")))
                        .build()))
                .build();
    }

    /**
     * Build ids rule.
     *
     * @param desc Rule input.
     * @return The ids rule.
     * @throws Exception if input value is not a valid security profile value.
     */
    public static Rule buildSecurityProfileRestrictedUsageRule(final SecurityRestrictionDesc desc)
            throws Exception {
        final var input = desc.getProfile();
        final var profile = ToIdsObjectMapper.getSecurityProfile(input);

        if (profile.isEmpty()) {
            throw new Exception("This is not a valid profile.");
        }

        return new PermissionBuilder()
                ._title_(Util.asList(new TypedLiteral(
                        desc.getTitle() == null ? "" : desc.getTitle())))
                ._description_(Util.asList(new TypedLiteral(
                        desc.getDescription() == null ? "" : desc.getDescription())))
                ._action_(Util.asList(Action.USE))
                ._constraint_(Util.asList(new ConstraintBuilder()
                        ._leftOperand_(LeftOperand.SECURITY_LEVEL)
                        ._operator_(BinaryOperator.EQUALS)
                        ._rightOperand_(new RdfResource(profile.get().toString(),
                                URI.create("xsd:string")))
                        .build()))
                .build();
    }
}
