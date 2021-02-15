package de.fraunhofer.isst.dataspaceconnector.controller.v1;

import de.fraunhofer.iais.eis.BaseConnectorImpl;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.config.PolicyConfiguration;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ConnectorConfigurationException;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * This class provides endpoints for basic connector services.
 */
@RestController
@Tag(name = "Connector", description = "Endpoints for connector information and configuration")
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private final SerializerProvider serializerProvider;
    //private final ResourceService offeredResourceService, requestedResourceService;
    private final IdsUtils idsUtils;
    private final PolicyConfiguration policyConfiguration;

    /**
     * Constructor for MainController.
     *
     * @param serializerProvider The provider for serialization
     * @param idsUtils The utilities for ids messages
     * @throws IllegalArgumentException if one of the parameters is null.
     */
    @Autowired
    public MainController(SerializerProvider serializerProvider, IdsUtils idsUtils,
                          PolicyConfiguration policyConfiguration) throws IllegalArgumentException {
        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        if (idsUtils == null)
            throw new IllegalArgumentException("The IdsUtils cannot be null.");

        if (policyConfiguration == null)
            throw new IllegalArgumentException("The PolicyConfiguration cannot be null.");

        this.serializerProvider = serializerProvider;
        this.policyConfiguration = policyConfiguration;
        this.idsUtils = idsUtils;
    }

    /**
     * Gets connector self-description without catalog.
     *
     * @return Self-description or error response.
     */
    @Operation(summary = "Public IDS self-description",
        description = "Get the connector's reduced self-description.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getPublicSelfDescription() {
        try {
            // Modify a connector for exposing the reduced self-description
            var connector = (BaseConnectorImpl) idsUtils.getConnector();
            connector.setResourceCatalog(null);
            connector.setPublicKey(null);

            return new ResponseEntity<>(serializerProvider.getSerializer().serialize(connector),
                HttpStatus.OK);
        } catch (ConnectorConfigurationException exception) {
            // No connector found
            LOGGER.warn("No connector has been configurated.");
            return new ResponseEntity<>("No connector is currently available.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException exception) {
            // Could not serialize the connector.
            LOGGER.warn("Could not serialize the connector. [exception=({})]",
                exception.getMessage());
            return new ResponseEntity<>("No connector is currently available.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets connector self-description.
     *
     * @return Self-description or error response.
     */
    @Operation(summary = "Private IDS self-description",
        description = "Get the connector's self-description with all catalogs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = {"/api/self-description"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getSelfService() {
        try {
            // Modify a connector for exposing a resource catalog
            var connector = (BaseConnectorImpl) idsUtils.getConnector();
            connector.setResourceCatalog(Util.asList(buildResourceCatalog()));

            return new ResponseEntity<>(serializerProvider.getSerializer().serialize(connector),
                HttpStatus.OK);
        } catch (ConnectorConfigurationException exception) {
            // No connector found
            LOGGER.warn("No connector has been configurated.");
            return new ResponseEntity<>("No connector is currently available.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException exception) {
            // Could not serialize the connector.
            LOGGER.warn("Could not serialize the connector. [exception=({})]",
                exception.getMessage());
            return new ResponseEntity<>("No connector is currently available.",
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Turns policy negotiation on or off.
     *
     * @param status The desired state.
     * @return Http ok or error response.
     */
    @Operation(summary = "Set policy negotiation status",
        description = "Turn the policy negotiation on or off.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @RequestMapping(value = {"/api/configuration/contract-negotiation"}, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> setNegotiationStatus(@RequestParam("status") boolean status) {
        policyConfiguration.setPolicyNegotiation(status);

        if (policyConfiguration.isPolicyNegotiation()) {
            return new ResponseEntity<>("Policy Negotiation was turned on.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Policy Negotiation was turned off.", HttpStatus.OK);
        }
    }

    /**
     * Returns the policy negotiation status.
     *
     * @return Http ok or error response.
     */
    @Operation(summary = "Policy negotiation status check",
        description = "Return the policy negotiation status.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @RequestMapping(value = {"/api/configuration/contract-negotiation"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getNegotiationStatus() {
        if (policyConfiguration.isPolicyNegotiation()) {
            return new ResponseEntity<>("Policy Negotiation is turned on.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Policy Negotiation is turned off.", HttpStatus.OK);
        }
    }

    /**
     * Allows requesting data without policy enforcement.
     *
     * @param status The desired state.
     * @return Http ok or error response.
     */
    @Operation(summary = "Allow unsupported patterns", description = "Allow "
        + "requesting data without policy enforcement if an unsupported pattern is recognized.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @RequestMapping(value = {"/api/configuration/ignore-unsupported-patterns"}, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> getPatternStatus(@RequestParam("status") boolean status) {
        policyConfiguration.setUnsupportedPatterns(status);

        if (policyConfiguration.isUnsupportedPatterns()) {
            return new ResponseEntity<>("Data can be accessed despite unsupported pattern.",
                HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Data cannot be accessed with an unsupported pattern.",
                HttpStatus.OK);
        }
    }

    /**
     * Returns the unsupported pattern status.
     *
     * @return Http ok or error response.
     */
    @Operation(summary = "Return pattern validation status",
        description = "Return if unsupported patterns are ignored when requesting data.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @RequestMapping(value = {"/api/configuration/ignore-unsupported-patterns"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getPatternStatus() {
        if (policyConfiguration.isUnsupportedPatterns()) {
            return new ResponseEntity<>("Data can be accessed despite unsupported pattern.",
                HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Data cannot be accessed with an unsupported pattern.",
                HttpStatus.OK);
        }
    }

    private ResourceCatalog buildResourceCatalog() throws ConstraintViolationException {
//        return new ResourceCatalogBuilder()
//            ._offeredResource_(new ArrayList<>(offeredResourceService.getResources()))
//            ._requestedResource_(new ArrayList<>(requestedResourceService.getResources()))
//            .build();
        throw new RuntimeException();
    }
}
