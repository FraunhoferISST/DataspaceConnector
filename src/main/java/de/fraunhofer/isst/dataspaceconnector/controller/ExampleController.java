package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.ContractException;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;

/**
 * This class provides endpoints exposing example resources and configurations.
 */
@RestController
@RequestMapping("/admin/api/example")
@Tag(name = "Examples", description = "Endpoints for testing purpose")
public class ExampleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleController.class);

    private final DapsTokenProvider tokenProvider;
    private final PolicyHandler policyHandler;

    /**
     * Constructor for ExampleController.
     *
     * @param tokenProvider The token provider
     * @param policyHandler The service for handling policies
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Autowired
    public ExampleController(DapsTokenProvider tokenProvider,
        PolicyHandler policyHandler) throws IllegalArgumentException {
        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (policyHandler == null)
            throw new IllegalArgumentException("The PolicyHandler cannot be null.");

        this.tokenProvider = tokenProvider;
        this.policyHandler = policyHandler;
    }

    /**
     * Get an example configuration.
     *
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @Operation(summary = "Get Sample Connector configuration",
        description = "Get a sample connector configuration for the config.json.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getConnectorConfiguration() {
        //NOTE: This needs some cleanup. Skip exception handling.
        var exceptions = new ArrayList<URI>();
        exceptions.add(URI.create("https://localhost:8080/"));
        exceptions.add(URI.create("http://localhost:8080/"));

        return new ResponseEntity<>(new ConfigurationModelBuilder()
            ._configurationModelLogLevel_(LogLevel.NO_LOGGING)
            ._connectorDeployMode_(ConnectorDeployMode.TEST_DEPLOYMENT)
            ._connectorProxy_(Util.asList(new ProxyBuilder()
                ._noProxy_(exceptions)
                ._proxyAuthentication_(new BasicAuthenticationBuilder().build())
                ._proxyURI_(URI.create("http://proxy.dortmund.isst.fraunhofer.de:3128"))
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
                    "IDS Connector with static example resources hosted by the Fraunhofer ISST")))
                ._version_("v3.0.0")
                ._publicKey_(new PublicKeyBuilder()
                    ._keyType_(KeyType.RSA) //tokenProvider.providePublicKey().getAlgorithm() ?
                    ._keyValue_(tokenProvider.provideDapsToken().getBytes())
                    .build()
                )
                ._hasDefaultEndpoint_(new ConnectorEndpointBuilder()
                    ._accessURL_(URI.create("https://localhost:8080/api/ids/data"))
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
    @RequestMapping(value = "/policy-validation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> getPolicyPattern(
        @Parameter(description = "The JSON string representing a policy", required = true)
        @RequestBody String policy) {
        try {
            // Return the policy pattern
            return new ResponseEntity<>(policyHandler.getPattern(policy), HttpStatus.OK);
        } catch (ContractException exception) {
            // Failed to receive the pattern. Inform the requester.
            LOGGER.error("Failed to read policy.", exception);
            return new ResponseEntity<>("The policy is invalid.",
                HttpStatus.INTERNAL_SERVER_ERROR);
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
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @RequestMapping(value = "/usage-policy", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> getExampleUsagePolicy(
        @Parameter(description = "The policy pattern.", required = true)
        @RequestParam("pattern") PolicyHandler.Pattern pattern) {
        ContractOffer contractOffer = null;

        switch (pattern) {
            case PROVIDE_ACCESS:
                contractOffer = new ContractOfferBuilder()
                    ._permission_(Util.asList(new PermissionBuilder()
                        ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                        ._description_(Util.asList(new TypedLiteral("provide-access")))
                        ._action_(Util.asList(Action.USE))
                        .build()))
                    .build();
                break;
            case PROHIBIT_ACCESS:
                contractOffer = new ContractOfferBuilder()
                    ._prohibition_(Util.asList(new ProhibitionBuilder()
                        ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                        ._description_(Util.asList(new TypedLiteral("prohibit-access")))
                        ._action_(Util.asList(Action.USE))
                        .build()))
                    .build();
                break;
            case N_TIMES_USAGE:
                contractOffer = new NotMoreThanNOfferBuilder()
                    ._permission_(Util.asList(new PermissionBuilder()
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
                        .build()))
                    .build();
                break;
            case DURATION_USAGE:
                contractOffer = new ContractOfferBuilder()
                    ._permission_(Util.asList(new PermissionBuilder()
                        ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                        ._description_(Util.asList(new TypedLiteral("duration-usage")))
                        ._action_(Util.asList(Action.USE))
                        ._constraint_(Util.asList(new ConstraintBuilder()
                            ._leftOperand_(LeftOperand.ELAPSED_TIME)
                            ._operator_(BinaryOperator.SHORTER_EQ)
                            ._rightOperand_(new RdfResource("PT4H", URI.create("xsd:duration")))
                            .build()))
                        .build()))
                    .build();
                break;
            case USAGE_DURING_INTERVAL:
                contractOffer = new ContractOfferBuilder()
                    ._permission_(Util.asList(new PermissionBuilder()
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
                        .build()))
                    .build();
                break;
            case USAGE_UNTIL_DELETION:
                contractOffer = new ContractOfferBuilder()
                    ._permission_(Util.asList(new PermissionBuilder()
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
                        .build()))
                    .build();
                break;
            case USAGE_LOGGING:
                contractOffer = new ContractOfferBuilder()
                    ._permission_(Util.asList(new PermissionBuilder()
                        ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                        ._description_(Util.asList(new TypedLiteral("usage-logging")))
                        ._action_(Util.asList(Action.USE))
                        ._postDuty_(Util.asList(new DutyBuilder()
                            ._action_(Util.asList(Action.LOG))
                            .build()))
                        .build()))
                    .build();
                break;
            case USAGE_NOTIFICATION:
                contractOffer = new ContractOfferBuilder()
                    ._permission_(Util.asList(new PermissionBuilder()
                        ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                        ._description_(Util.asList(new TypedLiteral("usage-notification")))
                        ._action_(Util.asList(Action.USE))
                        ._postDuty_(Util.asList(new DutyBuilder()
                            ._action_(Util.asList(Action.NOTIFY))
                            ._constraint_(Util.asList(new ConstraintBuilder()
                                ._leftOperand_(LeftOperand.ENDPOINT)
                                ._operator_(BinaryOperator.DEFINES_AS)
                                ._rightOperand_(
                                    new RdfResource("https://localhost:8000/api/ids/data",
                                        URI.create("xsd:anyURI")))
                                .build()))
                            .build()))
                        .build()))
                    .build();
                break;
            case CONNECTOR_RESTRICTED_USAGE:
                contractOffer = new ContractOfferBuilder()
                        ._permission_(Util.asList(new PermissionBuilder()
                                ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                                ._description_(Util.asList(new TypedLiteral("duration-usage")))
                                ._action_(Util.asList(Action.USE))
                                ._constraint_(Util.asList(new ConstraintBuilder()
                                        ._leftOperand_(LeftOperand.SYSTEM)
                                        ._operator_(BinaryOperator.SAME_AS)
                                        ._rightOperand_(
                                                new RdfResource("https://w3id.org/idsa/autogen/baseConnector/7b934432-a85e-41c5-9f65-669219dde4ae",
                                                        URI.create("xsd:anyURI")))
                                        .build()))
                                .build()))
                        .build();
                break;
        }

        return new ResponseEntity<>(contractOffer.toRdf(), HttpStatus.OK);
    }
}
