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
package io.dataspaceconnector.controller.policy;

import io.dataspaceconnector.common.exception.ContractException;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.common.ids.policy.RuleUtils;
import io.dataspaceconnector.common.net.JsonResponse;
import io.dataspaceconnector.common.net.ContentType;
import io.dataspaceconnector.controller.policy.util.PatternUtils;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.dataspaceconnector.controller.util.ResponseUtils;
import io.dataspaceconnector.model.pattern.ConnectorRestrictionDesc;
import io.dataspaceconnector.model.pattern.DeletionDesc;
import io.dataspaceconnector.model.pattern.DurationDesc;
import io.dataspaceconnector.model.pattern.IntervalDesc;
import io.dataspaceconnector.model.pattern.LoggingDesc;
import io.dataspaceconnector.model.pattern.NotificationDesc;
import io.dataspaceconnector.model.pattern.PatternDesc;
import io.dataspaceconnector.model.pattern.PermissionDesc;
import io.dataspaceconnector.model.pattern.ProhibitionDesc;
import io.dataspaceconnector.model.pattern.SecurityRestrictionDesc;
import io.dataspaceconnector.model.pattern.UsageNumberDesc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class provides endpoints exposing example resources and configurations.
 */
@Log4j2
@RestController
@ApiResponses(value = {
        @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
        @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                description = ResponseDescription.UNAUTHORIZED)})
@RequestMapping("/api/examples")
@Tag(name = "_Utils")
@RequiredArgsConstructor
public class ExampleController {
    /**
     * Policy management point.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Validate a rule and get the policy pattern.
     *
     * @param ruleAsString Policy as string.
     * @return A pattern enum or error.
     */
    @Operation(summary = "Get the policy pattern represented by a given JSON string.")
    @ApiResponse(responseCode = ResponseCode.INTERNAL_SERVER_ERROR,
            description = ResponseDescription.INTERNAL_SERVER_ERROR)
    @PostMapping(value = "/validation", produces = ContentType.JSON)
    @ResponseBody
    public ResponseEntity<Object> getPolicyPattern(
            @Parameter(description = "The JSON string representing a policy.", required = true)
            @RequestBody final String ruleAsString) {
        try {
            final var rule = deserializationService.getRule(ruleAsString);
            final var pattern = RuleUtils.getPatternByRule(rule);

            return new JsonResponse(null, null, pattern.name()).create(HttpStatus.OK);
        } catch (IllegalStateException | ContractException exception) {
            return ResponseUtils.respondPatternNotIdentified(exception);
        }
    }

    /**
     * Get an example policy pattern.
     *
     * @param input Policy pattern type and values.
     * @return An example policy object that can be filled out.
     */
    @Operation(summary = "Get an example policy for a given policy pattern.")
    @ApiResponse(responseCode = ResponseCode.BAD_REQUEST,
            description = ResponseDescription.BAD_REQUEST)
    @PostMapping(value = "/policy", produces = ContentType.JSON_LD)
    @ResponseBody
    public ResponseEntity<Object> getExampleUsagePolicy(@RequestBody final PatternDesc input) {
        try {
            if (input instanceof PermissionDesc) {
                final var policy = PatternUtils.buildProvideAccessRule((PermissionDesc) input);
                return ResponseEntity.ok(policy.toRdf());
            } else if (input instanceof ProhibitionDesc) {
                final var policy = PatternUtils.buildProhibitAccessRule((ProhibitionDesc) input);
                return ResponseEntity.ok(policy.toRdf());
            } else if (input instanceof UsageNumberDesc) {
                final var policy = PatternUtils.buildNTimesUsageRule((UsageNumberDesc) input);
                return ResponseEntity.ok(policy.toRdf());
            } else if (input instanceof DurationDesc) {
                final var policy = PatternUtils.buildDurationUsageRule((DurationDesc) input);
                return ResponseEntity.ok(policy.toRdf());
            } else if (input instanceof IntervalDesc) {
                final var policy = PatternUtils.buildIntervalUsageRule((IntervalDesc) input);
                return ResponseEntity.ok(policy.toRdf());
            } else if (input instanceof DeletionDesc) {
                final var policy = PatternUtils.buildUsageUntilDeletionRule((DeletionDesc) input);
                return ResponseEntity.ok(policy.toRdf());
            } else if (input instanceof LoggingDesc) {
                final var policy = PatternUtils.buildUsageLoggingRule((LoggingDesc) input);
                return ResponseEntity.ok(policy.toRdf());
            } else if (input instanceof NotificationDesc) {
                final var policy = PatternUtils.buildUsageNotificationRule(
                        (NotificationDesc) input);
                return ResponseEntity.ok(policy.toRdf());
            } else if (input instanceof ConnectorRestrictionDesc) {
                final var policy = PatternUtils.buildConnectorRestrictedUsageRule(
                        (ConnectorRestrictionDesc) input);
                return ResponseEntity.ok(policy.toRdf());
            } else if (input instanceof SecurityRestrictionDesc) {
                final var policy = PatternUtils.buildSecurityProfileRestrictedUsageRule(
                        (SecurityRestrictionDesc) input);
                return ResponseEntity.ok(policy.toRdf());
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not resolve pattern. [exception=({})]", e.getMessage());
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
