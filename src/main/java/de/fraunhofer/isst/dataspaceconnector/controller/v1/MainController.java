package de.fraunhofer.isst.dataspaceconnector.controller.v1;

import de.fraunhofer.iais.eis.BaseConnectorImpl;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.config.PolicyConfiguration;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.ArtifactController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.CatalogController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.ContractController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.OfferedResourceController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.RepresentationController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.RuleController;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ConnectorConfigurationException;
import de.fraunhofer.isst.dataspaceconnector.services.IdsResourceService;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class provides endpoints for basic connector services.
 */
@RestController
@Tag(name = "Connector", description = "Endpoints for connector information and configuration")
@RequiredArgsConstructor
public class MainController {

    /**
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    /**
     * The provider for ids serialization.
     */
    private final @NonNull SerializerProvider serializerProvider;

    /**
     * Provides ids utility functions.
     */
    private final @NonNull IdsUtils idsUtils;

    /**
     * The current policy configuration.
     */
    private final @NonNull PolicyConfiguration policyConfiguration;

    /**
     * The service for getting resources in ids format.
     */
    private final @NonNull IdsResourceService idsResourceService;

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
    @RequestMapping(value = {"/public", "public"}, method = RequestMethod.GET)
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
        policyConfiguration.setAllowUnsupported(status);

        if (policyConfiguration.isAllowUnsupported()) {
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
        if (policyConfiguration.isAllowUnsupported()) {
            return new ResponseEntity<>("Data can be accessed despite unsupported pattern.",
                HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Data cannot be accessed with an unsupported pattern.",
                HttpStatus.OK);
        }
    }

    @GetMapping("/api")
    public ResponseEntity<RepresentationModel>  root() {
        final var model = new RepresentationModel();

        model.add(linkTo(methodOn(MainController.class).root()).withSelfRel());
        model.add(linkTo(methodOn(ArtifactController.class).getAll(null, null, null)).withRel("artifacts"));
        model.add(linkTo(methodOn(CatalogController.class).getAll(null, null, null)).withRel("catalogs"));
        model.add(linkTo(methodOn(ContractController.class).getAll(null, null, null)).withRel("contracts"));
        model.add(linkTo(methodOn(RepresentationController.class).getAll(null, null, null)).withRel("representations"));
        model.add(linkTo(methodOn(OfferedResourceController.class).getAll(null, null, null)).withRel("resources"));
        model.add(linkTo(methodOn(RuleController.class).getAll(null, null, null)).withRel("rules"));

        return ResponseEntity.ok(model);
    }

    private ResourceCatalog buildResourceCatalog() throws ConstraintViolationException {
        return new ResourceCatalogBuilder()
            ._offeredResource_((ArrayList<? extends Resource>) idsResourceService.getAllOfferedResources())
            ._requestedResource_((ArrayList<? extends Resource>) idsResourceService.getAllRequestedResources())
            .build();
    }
}
