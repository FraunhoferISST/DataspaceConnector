package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.config.ConnectorConfiguration;
import de.fraunhofer.isst.dataspaceconnector.services.IdsSerializationService;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationUpdateException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@Tag(name = "Connector")
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
    private final @NonNull IdsSerializationService idsService;

    /**
     * Update the connector's current configuration.
     *
     * @param configuration The new configuration.
     * @return Ok or error response.
     */
    @PostMapping("/configuration")
    @Operation(summary = "Update current configuration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    public ResponseEntity<Object> updateConfiguration(@RequestBody final String configuration) {
        try {
            // Deserialize input.
            final var newConfig = idsService.deserializeConfigurationModel(configuration);

            // Update configuration of connector.
            configContainer.updateConfiguration(newConfig);
            return new ResponseEntity<>("Configuration successfully updated.", HttpStatus.OK);
        } catch (ConfigurationUpdateException exception) {
            return ControllerUtils.respondConfigurationUpdateError(exception);
        } catch (IllegalArgumentException exception) {
            return ControllerUtils.responseDeserializationError(exception);
        }
    }

    /**
     * Return the connector's current configuration.
     *
     * @return The configuration object or an error.
     */
    @GetMapping("/configuration")
    @Operation(summary = "Get current configuration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    @ResponseBody
    public ResponseEntity<Object> getConfiguration() {
        final var config = configContainer.getConfigModel();
        if (config == null) {
            return respondConfigurationNotFound();
        } else {
            return new ResponseEntity<>(config.toRdf(), HttpStatus.OK);
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
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @ResponseBody
    public ResponseEntity<Object> setNegotiationStatus(@RequestParam("status") final boolean status) {
        connectorConfig.setPolicyNegotiation(status);
        if (connectorConfig.isPolicyNegotiation()) {
            return new ResponseEntity<>("Contract Negotiation is activated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Contract Negotiation is deactivated.", HttpStatus.OK);
        }
    }

    /**
     * Returns the contract negotiation status.
     *
     * @return Http ok or error response.
     */
    @GetMapping("/api/configuration/negotiation")
    @Operation(summary = "Get contract negotiation status")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @ResponseBody
    public ResponseEntity<Object> getNegotiationStatus() {
        if (connectorConfig.isPolicyNegotiation()) {
            return new ResponseEntity<>("Contract Negotiation is activated.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Contract Negotiation is deactivated.", HttpStatus.OK);
        }
    }

    /**
     * Allows requesting data without policy enforcement.
     *
     * @param status The desired state.
     * @return Http ok or error response.
     */
    @PutMapping("/api/configuration/pattern")
    @Operation(summary = "Allow unsupported patterns", description = "Allow "
            + "requesting data without policy enforcement if an unsupported pattern is recognized.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @ResponseBody
    public ResponseEntity<Object> getPatternStatus(@RequestParam("status") final boolean status) {
        connectorConfig.setAllowUnsupported(status);
        if (connectorConfig.isAllowUnsupported()) {
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
    @GetMapping("/api/configuration/pattern")
    @Operation(summary = "Get pattern validation status",
            description = "Return if unsupported patterns are ignored when requesting data.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Ok") })
    @ResponseBody
    public ResponseEntity<Object> getPatternStatus() {
        if (connectorConfig.isAllowUnsupported()) {
            return new ResponseEntity<>("Data can be accessed despite unsupported pattern.",
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Data cannot be accessed with an unsupported pattern.",
                    HttpStatus.OK);
        }
    }
}
