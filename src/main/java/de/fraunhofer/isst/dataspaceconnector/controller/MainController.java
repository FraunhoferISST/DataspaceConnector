package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.RdfResource;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

/**
 * This class provides endpoints for basic connector services.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@RestController
@RequestMapping("/admin/api")
@Tag(name = "Connector: Selfservice", description = "Endpoints for connector information")
public class MainController {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private TokenProvider tokenProvider;
    private ConfigurationContainer configurationContainer;
    private SerializerProvider serializerProvider;

    private OfferedResourceService offeredResourceService;
    private RequestedResourceService requestedResourceService;

    private PolicyHandler policyHandler;

    @Autowired
    /**
     * <p>Constructor for MainController.</p>
     *
     * @param policyHandler a {@link de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler} object.
     * @param tokenProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider} object.
     * @param configProducer a {@link de.fraunhofer.isst.ids.framework.spring.starter.ConfigProducer} object.
     * @param serializerProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider} object.
     * @param offeredResourceService a {@link de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService} object.
     * @param requestedResourceService a {@link de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceService} object.
     */
    public MainController(TokenProvider tokenProvider, ConfigurationContainer configurationContainer,
                          SerializerProvider serializerProvider, OfferedResourceService offeredResourceService,
                          RequestedResourceService requestedResourceService, PolicyHandler policyHandler) {
        this.tokenProvider = tokenProvider;
        this.configurationContainer = configurationContainer;
        this.serializerProvider = serializerProvider;
        this.offeredResourceService = offeredResourceService;
        this.requestedResourceService = requestedResourceService;
        this.policyHandler = policyHandler;
    }

    /**
     * Gets connector self-description.
     *
     * @return Self-description or error response.
     */
    @Operation(summary = "Connector Self-description", description = "Get the connector's self-description.")
    @RequestMapping(value = {"/selfservice"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getSelfService() {
        try {
            BaseConnectorImpl connector = (BaseConnectorImpl) configurationContainer.getConnector();
            connector.setResourceCatalog(Util.asList(new ResourceCatalogBuilder()
                    ._offeredResource_(offeredResourceService.getResourceList())
                    ._requestedResource_(requestedResourceService.getRequestedResources())
                    .build()));
            return new ResponseEntity<>(serializerProvider.getSerializer().serialize(connector), HttpStatus.OK);
        } catch (IOException e) {
            LOGGER.error("Error during creation of the self description: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * <p>getConnector.</p>
     *
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @RequestMapping(value = "/example/configuration", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getConnector() {
        ArrayList<URI> exceptions = new ArrayList<>();
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
                        ._description_(Util.asList(new TypedLiteral("IDS Connector with static example resources hosted by the Fraunhofer ISST")))
                        ._version_("v3.0.0")
                        ._publicKey_(new PublicKeyBuilder()
                                ._keyType_(KeyType.RSA) //tokenProvider.providePublicKey().getAlgorithm() ?
                                ._keyValue_(tokenProvider.providePublicKey().getEncoded())
                                .build()
                        )
                        ._hasDefaultEndpoint_(new ConnectorEndpointBuilder()
                                ._accessURL_(URI.create("/api/ids/data"))
                                .build())
                        .build())
                .build().toRdf(), HttpStatus.OK);
    }

    /**
     * <p>getPolicyPattern.</p>
     *
     * @param policy a {@link java.lang.String} object.
     * @return a {@link org.springframework.http.ResponseEntity} object.
     * @throws java.io.IOException if any.
     */
    @RequestMapping(value = "/example/policy-pattern", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> getPolicyPattern(@RequestParam("policy") String policy) throws IOException {
        return new ResponseEntity<>(policyHandler.getPattern(policy), HttpStatus.OK);
    }

    /**
     * <p>getExampleUsagePolicy.</p>
     *
     * @param pattern a {@link de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler.Pattern} object.
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @RequestMapping(value = "/example/usage-policy", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> getExampleUsagePolicy(@RequestParam("pattern") PolicyHandler.Pattern pattern) {
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
                                        ._pipEndpoint_(URI.create("https://localhost:8080/admin/api/resources/"))
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
                                        ._rightOperand_(new RdfResource("2020-07-11T00:00:00Z", URI.create("xsd:dateTimeStamp")))
                                        .build(), new ConstraintBuilder()
                                        ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                                        ._operator_(BinaryOperator.BEFORE)
                                        ._rightOperand_(new RdfResource("2020-07-11T00:00:00Z", URI.create("xsd:dateTimeStamp")))
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
                                        ._rightOperand_(new RdfResource("2020-07-11T00:00:00Z", URI.create("xsd:dateTimeStamp")))
                                        .build(), new ConstraintBuilder()
                                        ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                                        ._operator_(BinaryOperator.BEFORE)
                                        ._rightOperand_(new RdfResource("2020-07-11T00:00:00Z", URI.create("xsd:dateTimeStamp")))
                                        .build()))
                                ._postDuty_(Util.asList(new DutyBuilder()
                                        ._action_(Util.asList(Action.DELETE))
                                        ._constraint_(Util.asList(new ConstraintBuilder()
                                                ._leftOperand_(LeftOperand.POLICY_EVALUATION_TIME)
                                                ._operator_(BinaryOperator.TEMPORAL_EQUALS)
                                                ._rightOperand_(new RdfResource("2020-07-11T00:00:00Z", URI.create("xsd:dateTimeStamp")))
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
                                                ._rightOperand_(new RdfResource("https://localhost:8000/api/ids/data", URI.create("xsd:anyURI")))
                                                .build()))
                                        .build()))
                                .build()))
                        .build();
                break;
        }
        return new ResponseEntity<>(contractOffer.toRdf(), HttpStatus.OK);
    }
}
