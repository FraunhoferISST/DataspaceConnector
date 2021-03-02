package de.fraunhofer.isst.dataspaceconnector.controller.v1;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.BasicAuthenticationBuilder;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.ConfigurationModelBuilder;
import de.fraunhofer.iais.eis.ConnectorDeployMode;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.ConnectorStatus;
import de.fraunhofer.iais.eis.ConstraintBuilder;
import de.fraunhofer.iais.eis.DutyBuilder;
import de.fraunhofer.iais.eis.KeyType;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.LogLevel;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.ProhibitionBuilder;
import de.fraunhofer.iais.eis.ProxyBuilder;
import de.fraunhofer.iais.eis.PublicKeyBuilder;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.ContractException;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;

/**
 * This class provides endpoints exposing example resources and configurations.
 */
@RestController
@RequestMapping("/api/examples")
@Tag(name = "Connector", description = "Endpoints for testing purpose")
@RequiredArgsConstructor
public class ExampleController {

    /**
     * The DAT provider.
     */
    private final @NonNull DapsTokenProvider tokenProvider;

    /**
     * The service for handling policies.
     */
    private final @NonNull PolicyHandler policyHandler;

    /**
     * Get an example configuration.
     *
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @Hidden
    @Operation(summary = "Get sample connector configuration",
            description = "Get a sample connector configuration for the config.json.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getConnectorConfiguration() {
        // NOTE: This needs some cleanup. Skip exception handling.
        var exceptions = new ArrayList<URI>();
        exceptions.add(URI.create("https://localhost:8080/"));
        exceptions.add(URI.create("http://localhost:8080/"));

        return new ResponseEntity<>(new ConfigurationModelBuilder()
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
                        ._description_(Util.asList(new TypedLiteral("IDS Connector with static " +
                                "example resources hosted by the Fraunhofer ISST")))
                        ._version_("v3.0.0")
                        ._publicKey_(new PublicKeyBuilder()
                                ._keyType_(KeyType.RSA) //tokenProvider.providePublicKey()
                                // .getAlgorithm() ?
                                ._keyValue_(tokenProvider.provideDapsToken().getBytes())
                                .build()
                        )
                        ._hasDefaultEndpoint_(new ConnectorEndpointBuilder()
                                ._accessURL_(URI.create("/api/ids/data"))
                                .build())
                        .build())
                ._keyStore_(URI.create("file:///conf/keystore.p12"))
                ._trustStore_(URI.create("file:///conf/truststore.p12"))
                .build().toRdf(), HttpStatus.OK);
    }

    /**
     * Get the policy pattern.
     *
     * @param policy a {@link java.lang.String} object.
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @Operation(summary = "Get pattern of policy",
            description = "Get the policy pattern represented by a given JSON string.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/policy/validation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> getPolicyPattern(
            @Parameter(description = "The JSON string representing a policy", required = true)
            @RequestBody final String policy) {
        try {
            // Return the policy pattern.
            return new ResponseEntity<>(policyHandler.getPattern(policy), HttpStatus.OK);
        } catch (ContractException exception) {
            return ControllerUtils.responsePatternNotIdentified(exception);
        } catch (Exception exception) {
            return ControllerUtils.responseInvalidInput(exception);
        }
    }

    /**
     * Get an example policy pattern.
     *
     * @param pattern a {@link PolicyHandler.Pattern} object.
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @Operation(summary = "Get example policy",
            description = "Get an example policy for a given policy pattern.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
    @RequestMapping(value = "/policy", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> getExampleUsagePolicy(
            @Parameter(description = "The policy pattern.", required = true)
            @RequestParam("pattern") final PolicyHandler.Pattern pattern) {
        Rule rule = null;

        switch (pattern) {
            case PROVIDE_ACCESS:
                rule = new PermissionBuilder()
                        ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                        ._description_(Util.asList(new TypedLiteral("provide-access")))
                        ._action_(Util.asList(Action.USE))
                        .build();
                break;
            case PROHIBIT_ACCESS:
                rule = new ProhibitionBuilder()
                        ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                        ._description_(Util.asList(new TypedLiteral("prohibit-access")))
                        ._action_(Util.asList(Action.USE))
                        .build();
                break;
            case N_TIMES_USAGE:
                rule = new PermissionBuilder()
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
                break;
            case DURATION_USAGE:
                rule = new PermissionBuilder()
                        ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                        ._description_(Util.asList(new TypedLiteral("duration-usage")))
                        ._action_(Util.asList(Action.USE))
                        ._constraint_(Util.asList(new ConstraintBuilder()
                                ._leftOperand_(LeftOperand.ELAPSED_TIME)
                                ._operator_(BinaryOperator.SHORTER_EQ)
                                ._rightOperand_(new RdfResource("PT4H", URI.create("xsd:duration")))
                                .build()))
                        .build();
                break;
            case USAGE_DURING_INTERVAL:
                rule = new PermissionBuilder()
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
                break;
            case USAGE_UNTIL_DELETION:
                rule = new PermissionBuilder()
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
                break;
            case USAGE_LOGGING:
                rule = new PermissionBuilder()
                        ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                        ._description_(Util.asList(new TypedLiteral("usage-logging")))
                        ._action_(Util.asList(Action.USE))
                        ._postDuty_(Util.asList(new DutyBuilder()
                                ._action_(Util.asList(Action.LOG))
                                .build()))
                        .build();
                break;
            case USAGE_NOTIFICATION:
                rule = new PermissionBuilder()
                        ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                        ._description_(Util.asList(new TypedLiteral("usage-notification")))
                        ._action_(Util.asList(Action.USE))
                        ._postDuty_(Util.asList(new DutyBuilder()
                                ._action_(Util.asList(Action.NOTIFY))
                                ._constraint_(Util.asList(new ConstraintBuilder()
                                        ._leftOperand_(LeftOperand.ENDPOINT)
                                        ._operator_(BinaryOperator.DEFINES_AS)
                                        ._rightOperand_(
                                                new RdfResource("https://localhost:8000/api/ids" +
                                                        "/data",
                                                        URI.create("xsd:anyURI")))
                                        .build()))
                                .build()))
                        .build();
                break;
        }

        return new ResponseEntity<>(rule.toRdf(), HttpStatus.OK);
    }
}
