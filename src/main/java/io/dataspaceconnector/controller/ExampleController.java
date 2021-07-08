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
package io.dataspaceconnector.controller;

import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.BasicAuthenticationBuilder;
import de.fraunhofer.iais.eis.ConfigurationModelBuilder;
import de.fraunhofer.iais.eis.ConnectorDeployMode;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.ConnectorStatus;
import de.fraunhofer.iais.eis.KeyType;
import de.fraunhofer.iais.eis.LogLevel;
import de.fraunhofer.iais.eis.ProxyBuilder;
import de.fraunhofer.iais.eis.PublicKeyBuilder;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.exception.ContractException;
import io.dataspaceconnector.model.pattern.DeletionDesc;
import io.dataspaceconnector.model.pattern.DurationDesc;
import io.dataspaceconnector.model.pattern.IntervalDesc;
import io.dataspaceconnector.model.pattern.LoggingDesc;
import io.dataspaceconnector.model.pattern.NotificationDesc;
import io.dataspaceconnector.model.pattern.PatternDesc;
import io.dataspaceconnector.model.pattern.PermissionDesc;
import io.dataspaceconnector.model.pattern.ProhibitionDesc;
import io.dataspaceconnector.model.pattern.RestrictionDesc;
import io.dataspaceconnector.model.pattern.UsageNumberDesc;
import io.dataspaceconnector.service.ids.DeserializationService;
import io.dataspaceconnector.util.ControllerUtils;
import io.dataspaceconnector.util.PatternUtils;
import io.dataspaceconnector.util.RuleUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class provides endpoints exposing example resources and configurations.
 */
@RestController
@RequestMapping("/api/examples")
@RequiredArgsConstructor
public class ExampleController {
    /**
     * Policy management point.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Get an example configuration for the config.json.
     *
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @Hidden
    @Operation(summary = "Get sample connector configuration",
            description = "Get a sample connector configuration for the config.json.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
    @GetMapping("/configuration")
    @ResponseBody
    public ResponseEntity<Object> getConnectorConfiguration() {
        return ResponseEntity.ok(
                new ConfigurationModelBuilder(URI.create("configId"))
                        ._configurationModelLogLevel_(LogLevel.NO_LOGGING)
                        ._connectorDeployMode_(ConnectorDeployMode.TEST_DEPLOYMENT)
                        ._connectorProxy_(Util.asList(
                                new ProxyBuilder(URI.create("proxiId"))
                                        ._noProxy_(new ArrayList<>(Collections.singletonList(
                                                URI.create("https://localhost:8080/"))))
                                        ._proxyAuthentication_(
                                                new BasicAuthenticationBuilder(
                                                        URI.create("basicAuthId")).build())
                                        ._proxyURI_(URI.create(
                                                "proxy.dortmund.isst.fraunhofer.de:3128"))
                                        .build()))
                        ._connectorStatus_(ConnectorStatus.CONNECTOR_ONLINE)
                        ._connectorDescription_(
                                new BaseConnectorBuilder(URI.create("connectorId"))
                                        ._maintainer_(URI.create("https://example.com"))
                                        ._curator_(URI.create("https://example.com"))
                                        ._securityProfile_(SecurityProfile.BASE_SECURITY_PROFILE)
                                        ._outboundModelVersion_("4.0.0")
                                        ._inboundModelVersion_(Util.asList("4.0.0"))
                                        ._title_(Util.asList(
                                                new TypedLiteral("Dataspace Connector")))
                                        ._description_(Util.asList(new TypedLiteral(
                                                "IDS Connector with static example resources "
                                                        + "hosted by the Fraunhofer ISST.")))
                                        ._version_("v3.0.0")
                                        ._publicKey_(
                                                new PublicKeyBuilder(URI.create("keyId"))
                                                        ._keyType_(KeyType.RSA)
                                                        ._keyValue_(
                                                                "Your daps token here.".getBytes(
                                                                        StandardCharsets.UTF_8))
                                                        .build())
                                        ._hasDefaultEndpoint_(
                                                new ConnectorEndpointBuilder(
                                                        URI.create("endpointId"))
                                                        ._accessURL_(URI.create("/api/ids/data"))
                                                        .build())
                                        .build())
                        ._keyStore_(URI.create("file:///conf/keystore.p12"))
                        ._trustStore_(URI.create("file:///conf/truststore.p12"))
                        .build().toRdf());
    }

    /**
     * Validate a rule and get the policy pattern.
     *
     * @param ruleAsString Policy as string.
     * @return A pattern enum or error.
     */
    @Operation(summary = "Get pattern of policy",
            description = "Get the policy pattern represented by a given JSON string.")
    @Tag(name = "Usage Control", description = "Endpoints for contract/policy handling")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @PostMapping("/validation")
    @ResponseBody
    public ResponseEntity<Object> getPolicyPattern(
            @Parameter(description = "The JSON string representing a policy", required = true)
            @RequestBody final String ruleAsString) {
        try {
            final var rule = deserializationService.getRule(ruleAsString);
            return ResponseEntity.ok(RuleUtils.getPatternByRule(rule));
        } catch (IllegalStateException | ContractException exception) {
            return ControllerUtils.respondPatternNotIdentified(exception);
        }
    }

    /**
     * Get an example policy pattern.
     *
     * @param input Policy pattern type and values.
     * @return An example policy object that can be filled out.
     */
    @Operation(summary = "Get example policy",
            description = "Get an example policy for a given policy pattern.")
    @Tag(name = "Usage Control", description = "Endpoints for contract/policy handling")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Bad Request")})
    @PostMapping("/policy")
    @ResponseBody
    public ResponseEntity<Object> getExampleUsagePolicy(@RequestBody final PatternDesc input) {
        try {
            if (input instanceof PermissionDesc) {
                return ResponseEntity.ok(PatternUtils.buildProvideAccessRule().toRdf());
            } else if (input instanceof ProhibitionDesc) {
                return ResponseEntity.ok(PatternUtils.buildProhibitAccessRule().toRdf());
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
                return ResponseEntity.ok(PatternUtils.buildUsageLoggingRule().toRdf());
            } else if (input instanceof NotificationDesc) {
                final var policy =
                        PatternUtils.buildUsageNotificationRule((NotificationDesc) input);
                return ResponseEntity.ok(policy.toRdf());
            } else if (input instanceof RestrictionDesc) {
                final var policy =
                        PatternUtils.buildConnectorRestrictedUsageRule((RestrictionDesc) input);
                return ResponseEntity.ok(policy.toRdf());
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
