package de.fraunhofer.isst.dataspaceconnector.controller;

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
import de.fraunhofer.isst.dataspaceconnector.exceptions.ContractException;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyPattern;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.PatternUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * This class provides endpoints exposing example resources and configurations.
 */
@RestController
@RequestMapping("/api/examples")
@RequiredArgsConstructor
public class ExampleController {

    /**
     * The DAT provider.
     */
    private final @NonNull DapsTokenProvider tokenProvider;

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
        // NOTE: This needs some cleanup. Skip exception handling.
        var exceptions = new ArrayList<URI>();
        exceptions.add(URI.create("https://localhost:8080/"));
        exceptions.add(URI.create("http://localhost:8080/"));

        return ResponseEntity.ok(new ConfigurationModelBuilder()
                ._configurationModelLogLevel_(LogLevel.NO_LOGGING)
                ._connectorDeployMode_(ConnectorDeployMode.TEST_DEPLOYMENT)
                ._connectorProxy_(Util.asList(new ProxyBuilder()
                        ._noProxy_(exceptions)
                        ._proxyAuthentication_(new BasicAuthenticationBuilder().build())
                        ._proxyURI_(URI.create("proxy.dortmund.isst.fraunhofer.de:3128"))
                        .build()))
                ._connectorStatus_(ConnectorStatus.CONNECTOR_ONLINE)
                ._connectorDescription_(new BaseConnectorBuilder()
                        ._maintainer_(URI.create("https://example.com"))
                        ._curator_(URI.create("https://example.com"))
                        ._securityProfile_(SecurityProfile.BASE_SECURITY_PROFILE)
                        ._outboundModelVersion_("4.0.0")
                        ._inboundModelVersion_(Util.asList("4.0.0"))
                        ._title_(Util.asList(new TypedLiteral("Dataspace Connector")))
                        ._description_(Util.asList(new TypedLiteral(
                                "IDS Connector with static "
                                        + "example resources hosted by the Fraunhofer ISST.")))
                        ._version_("v3.0.0")
                        ._publicKey_(new PublicKeyBuilder()
                                ._keyType_(KeyType.RSA) //tokenProvider.providePublicKey()
                                // .getAlgorithm() ?
                                ._keyValue_(tokenProvider.provideDapsToken()
                                        .getBytes(StandardCharsets.UTF_16))
                                .build()
                        )
                        ._hasDefaultEndpoint_(new ConnectorEndpointBuilder()
                                ._accessURL_(URI.create("/api/ids/data"))
                                .build())
                        .build())
                ._keyStore_(URI.create("file:///conf/keystore.p12"))
                ._trustStore_(URI.create("file:///conf/truststore.p12"))
                .build());
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
            return ResponseEntity.ok(PolicyUtils.getPatternByRule(rule));
        } catch (ContractException exception) {
            return ControllerUtils.respondPatternNotIdentified(exception);
        } catch (Exception exception) {
            return ControllerUtils.respondInvalidInput(exception);
        }
    }

    /**
     * Get an example policy pattern.
     *
     * @param pattern Policy pattern type.
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
    public ResponseEntity<Object> getExampleUsagePolicy(
            @Parameter(description = "Selection of supported policy patterns.", required = true)
            @RequestParam("type") final PolicyPattern pattern) {
        switch (pattern) {
            case PROVIDE_ACCESS:
                return ResponseEntity.ok(PatternUtils.buildProvideAccessRule());
            case PROHIBIT_ACCESS:
                return ResponseEntity.ok(PatternUtils.buildProhibitAccessRule());
            case N_TIMES_USAGE:
                return ResponseEntity.ok(PatternUtils.buildNTimesUsageRule());
            case DURATION_USAGE:
                return ResponseEntity.ok(PatternUtils.buildDurationUsageRule());
            case USAGE_DURING_INTERVAL:
                return ResponseEntity.ok(PatternUtils.buildIntervalUsageRule());
            case USAGE_UNTIL_DELETION:
                return ResponseEntity.ok(PatternUtils.buildUsageUntilDeletionRule());
            case USAGE_LOGGING:
                return ResponseEntity.ok(PatternUtils.buildUsageLoggingRule());
            case USAGE_NOTIFICATION:
                return ResponseEntity.ok(PatternUtils.buildUsageNotificationRule());
            case CONNECTOR_RESTRICTED_USAGE:
                return ResponseEntity.ok(PatternUtils.buildConnectorRestrictedUsageRule());
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
