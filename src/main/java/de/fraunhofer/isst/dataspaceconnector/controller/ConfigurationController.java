package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.config.ConnectorConfiguration;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils.respondConfigurationNotFound;

/**
 * This class provides endpoints for connector configurations via a connected config manager.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConfigurationController {

    /**
     * The current connector configuration.
     */
    private final @NonNull ConfigurationContainer configContainer;

    /**
     * The current policy configuration.
     */
    private final @NonNull ConnectorConfiguration connectorConfig;

    /**
     * Service for deserializing ids objects.
     */
    private final @NonNull DeserializationService idsService;

    /**
     * Update the connector's current configuration.
     *
     * @param configuration The new configuration.
     * @return Ok or error response.
     */
    @PostMapping("/configuration")
    @Operation(summary = "Update current configuration.")
    @Tag(name = "Connector", description = "Endpoints for connector information and configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    public ResponseEntity<Object> updateConfiguration(@RequestBody final String configuration) {
        try {
            // Deserialize input.
            final var config = idsService.getConfigurationModel(configuration);

            // Update configuration of connector.
            configContainer.updateConfiguration(config);
            return ResponseEntity.ok("Configuration successfully updated.");
        } catch (ConfigurationUpdateException exception) {
            return ControllerUtils.respondConfigurationUpdateError(exception);
        } catch (IllegalArgumentException exception) {
            return ControllerUtils.respondDeserializationError(exception);
        }
    }

    /**
     * Return the connector's current configuration.
     *
     * @return The configuration object or an error.
     */
    @GetMapping(value = "/configuration", produces = "application/ld+json")
    @Operation(summary = "Get current configuration.")
    @Tag(name = "Connector", description = "Endpoints for connector information and configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    @ResponseBody
    public ResponseEntity<Object> getConfiguration() {
        final var config = configContainer.getConfigModel();
        if (config == null) {
            return respondConfigurationNotFound();
        } else {
            return ResponseEntity.ok(config.toRdf());
        }
    }

    /**
     * Turns contract negotiation on or off (at runtime).
     *
     * @param status The desired state.
     * @return Http ok or error response.
     */
    @PutMapping("/configuration/negotiation")
    @Operation(summary = "Set contract negotiation status")
    @Tag(name = "Usage Control", description = "Endpoints for contract/policy handling")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
    @ResponseBody
    public ResponseEntity<Object> setNegotiationStatus(
            @RequestParam("status") final boolean status) {
        connectorConfig.setPolicyNegotiation(status);
        if (connectorConfig.isPolicyNegotiation()) {
            return ResponseEntity.ok("Contract Negotiation is activated.");
        } else {
            return ResponseEntity.ok("Contract Negotiation is deactivated.");
        }
    }

    /**
     * Returns the contract negotiation status.
     *
     * @return Http ok or error response.
     */
    @GetMapping("/configuration/negotiation")
    @Operation(summary = "Get contract negotiation status")
    @Tag(name = "Usage Control", description = "Endpoints for contract/policy handling")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
    @ResponseBody
    public ResponseEntity<Object> getNegotiationStatus() {
        if (connectorConfig.isPolicyNegotiation()) {
            return ResponseEntity.ok("Contract Negotiation is activated.");
        } else {
            return ResponseEntity.ok("Contract Negotiation is deactivated.");
        }
    }

    /**
     * Allows requesting data without policy enforcement.
     *
     * @param status The desired state.
     * @return Http ok or error response.
     */
    @PutMapping("/configuration/pattern")
    @Operation(summary = "Allow unsupported patterns", description = "Allow "
            + "requesting data without policy enforcement if an unsupported pattern is recognized.")
    @Tag(name = "Usage Control", description = "Endpoints for contract/policy handling")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
    @ResponseBody
    public ResponseEntity<Object> getPatternStatus(@RequestParam("status") final boolean status) {
        connectorConfig.setAllowUnsupported(status);
        if (connectorConfig.isAllowUnsupported()) {
            return ResponseEntity.ok("Data can be accessed despite unsupported pattern.");
        } else {
            return ResponseEntity.ok("Data cannot be accessed with unsupported patterns.");
        }
    }

    /**
     * Returns the unsupported pattern status.
     *
     * @return Http ok or error response.
     */
    @GetMapping("/configuration/pattern")
    @Operation(summary = "Get pattern validation status",
            description = "Return if unsupported patterns are ignored when requesting data.")
    @Tag(name = "Usage Control", description = "Endpoints for contract/policy handling")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok")})
    @ResponseBody
    public ResponseEntity<Object> getPatternStatus() {
        if (connectorConfig.isAllowUnsupported()) {
            return ResponseEntity.ok("Data can be accessed despite unsupported pattern.");
        } else {
            return ResponseEntity.ok("Data cannot be accessed with unsupported patterns.");
        }
    }
}
