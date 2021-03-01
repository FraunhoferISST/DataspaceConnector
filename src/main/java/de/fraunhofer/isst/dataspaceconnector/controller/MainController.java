package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.iais.eis.BaseConnectorImpl;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.config.PolicyConfiguration;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ConnectorConfigurationException;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
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
import java.util.ArrayList;

/**
 * This class provides endpoints for basic connector services.
 */
@RestController
@Tag(name = "Connector: Selfservice", description = "Endpoints for connector information")
public class MainController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    private final SerializerProvider serializerProvider;
    private final ResourceService offeredResourceService, requestedResourceService;
    private final PolicyConfiguration policyConfiguration;
    private final ConfigurationContainer configurationContainer;

    /**
     * Constructor for MainController.
     *
     * @param serializerProvider The provider for serialization
     * @param offeredResourceService The service for the offered resources
     * @param requestedResourceService The service for the requested resources
     * @throws IllegalArgumentException if one of the parameters is null.
     */
    @Autowired
    public MainController(SerializerProvider serializerProvider,
                          OfferedResourceServiceImpl offeredResourceService,
                          RequestedResourceServiceImpl requestedResourceService,
                          ConfigurationContainer configurationContainer,
                          PolicyConfiguration policyConfiguration) throws IllegalArgumentException {
        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");

        if (offeredResourceService == null)
            throw new IllegalArgumentException("The OfferedResourceService cannot be null.");

        if (requestedResourceService == null)
            throw new IllegalArgumentException("The RequestedResourceService cannot be null.");

        if (policyConfiguration == null)
            throw new IllegalArgumentException("The PolicyConfiguration cannot be null.");

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        this.serializerProvider = serializerProvider;
        this.offeredResourceService = offeredResourceService;
        this.requestedResourceService = requestedResourceService;
        this.policyConfiguration = policyConfiguration;
        this.configurationContainer = configurationContainer;
    }

    /**
     * Gets connector self-description without catalog.
     *
     * @return Self-description or error response.
     */
    @Operation(summary = "Public Endpoint for Connector Self-description",
        description = "Get the connector's reduced self-description.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getPublicSelfDescription() {
        try {
            // Get a local copy of the current connector.
            var connector = (BaseConnectorImpl) configurationContainer.getConnector();

            // Modify a connector for exposing the reduced self-description.
            connector.setResourceCatalog(null);

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
    @Operation(summary = "Connector Self-description",
        description = "Get the connector's self-description.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = {"/admin/api/connector"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getSelfService() {
        try {
            // Get a local copy of the current connector.
            var connector = (BaseConnectorImpl) configurationContainer.getConnector();

            // Modify a connector for exposing a resource catalog
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
    @Operation(summary = "Endpoint for Policy Negotiation Status",
        description = "Turn the policy negotiation on or off.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @RequestMapping(value = {"/admin/api/negotiation"}, method = RequestMethod.PUT)
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
    @Operation(summary = "Endpoint for Policy Negotiation Status Check",
        description = "Return the policy negotiation status.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @RequestMapping(value = {"/admin/api/negotiation"}, method = RequestMethod.GET)
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
    @Operation(summary = "Endpoint for Allowing Unsupported Patterns", description = "Allow "
        + "requesting data without policy enforcement if an unsupported pattern is recognized.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @RequestMapping(value = {"/admin/api/ignore-unsupported-patterns"}, method = RequestMethod.PUT)
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
    @Operation(summary = "Endpoint for Pattern Checking",
        description = "Return if unsupported patterns are ignored when requesting data.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @RequestMapping(value = {"/admin/api/ignore-unsupported-patterns"}, method = RequestMethod.GET)
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
        return new ResourceCatalogBuilder()
            ._offeredResource_(new ArrayList<>(offeredResourceService.getResources()))
            ._requestedResource_(new ArrayList<>(requestedResourceService.getResources()))
            .build();
    }
}
