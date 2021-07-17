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

import javax.validation.Valid;
import java.util.UUID;

import de.fraunhofer.ids.messaging.core.config.ConfigContainer;
import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import io.dataspaceconnector.config.ConnectorConfiguration;
import io.dataspaceconnector.controller.configuration.ConfigManagerControllers;
import io.dataspaceconnector.service.configuration.ConfigurationService;
import io.dataspaceconnector.util.ControllerUtils;
import io.dataspaceconnector.view.configuration.ConfigurationView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class provides endpoints for connector configurations via a connected config manager.
 */
@RestController
@RequestMapping("/api/configuration")
@RequiredArgsConstructor
public class ConfigurationController {

    /**
     * The current connector configuration.
     */
    private final @NonNull ConfigContainer configContainer;

    /**
     * The current policy configuration.
     */
    private final @NonNull ConnectorConfiguration connectorConfig;

    /**
     * Configuration Service, to read and set current config in DB.
     */
    private final @NonNull ConfigurationService configurationService;

    /**
     * The controller for all configurations.
     */
    private final @NonNull ConfigManagerControllers.ConfigurationController configurationController;

    /**
     * Update the connector's current configuration.
     *
     * @param toSelect The new configuration.
     * @return Ok or error response.
     */
    @PutMapping(value = "/{id}", consumes = {"*/*"})
    @Operation(summary = "Update current configuration.")
    @Tag(name = "Connector", description = "Endpoints for connector information and configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Failed to deserialize."),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "415", description = "Wrong media type."),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    public ResponseEntity<Object> setConfiguration(@Valid @PathVariable(name = "id")
                                                       final UUID toSelect) {
        try {
            configurationService.swapActiveConfig(toSelect);
        } catch (ConfigUpdateException exception) {
            return ControllerUtils.respondConfigurationUpdateError(exception);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Return the connector's current configuration.
     *
     * @return The configuration object or an error.
     */
    @GetMapping(value = "/", produces = "application/hal+json")
    @Operation(summary = "Get current configuration.")
    @Tag(name = "Connector", description = "Endpoints for connector information and configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @ResponseBody
    public ConfigurationView getConfiguration() {
            return configurationController.get(configurationService.getActiveConfig().getId());
    }

    /**
     * Return the connector's current configuration.
     *
     * @return The configuration object or an error.
     */
    @GetMapping(value = "/", produces = "application/ld+json")
    @Operation(summary = "Get current configuration.")
    @Tag(name = "Connector", description = "Endpoints for connector information and configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @ResponseBody
    public ResponseEntity<Object> getIdsConfiguration() {
        return ResponseEntity.ok(configContainer.getConfigurationModel().toRdf());
    }

    /**
     * Turns contract negotiation on or off (at runtime).
     *
     * @param status The desired state.
     * @return Http ok or error response.
     */
    @PutMapping(value = "/negotiation", produces = "application/json")
    @Operation(summary = "Set contract negotiation status")
    @Tag(name = "Usage Control", description = "Endpoints for contract/policy handling")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @ResponseBody
    public ResponseEntity<JSONObject> setNegotiationStatus(
            @RequestParam("status") final boolean status) {
        connectorConfig.setPolicyNegotiation(status);
        return getNegotiationStatus();
    }

    /**
     * Returns the contract negotiation status.
     *
     * @return Http ok or error response.
     */
    @GetMapping(value = "/negotiation", produces = "application/json")
    @Operation(summary = "Get contract negotiation status")
    @Tag(name = "Usage Control", description = "Endpoints for contract/policy handling")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @ResponseBody
    public ResponseEntity<JSONObject> getNegotiationStatus() {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final var body = new JSONObject();
        body.put("status", connectorConfig.isPolicyNegotiation());

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    /**
     * Allows requesting data without policy enforcement.
     *
     * @param status The desired state.
     * @return Http ok or error response.
     */
    @PutMapping(value = "/pattern", produces = "application/json")
    @Operation(summary = "Allow unsupported patterns", description = "Allow "
            + "requesting data without policy enforcement if an unsupported pattern is recognized.")
    @Tag(name = "Usage Control", description = "Endpoints for contract/policy handling")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @ResponseBody
    public ResponseEntity<JSONObject> setPatternStatus(
            @RequestParam("status") final boolean status) {
        connectorConfig.setAllowUnsupported(status);
        return getPatternStatus();
    }

    /**
     * Returns the unsupported pattern status.
     *
     * @return Http ok or error response.
     */
    @GetMapping(value = "/pattern", produces = "application/json")
    @Operation(summary = "Get pattern validation status",
            description = "Return if unsupported patterns are ignored when requesting data.")
    @Tag(name = "Usage Control", description = "Endpoints for contract/policy handling")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Ok"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @ResponseBody
    public ResponseEntity<JSONObject> getPatternStatus() {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final var body = new JSONObject();
        body.put("status", connectorConfig.isAllowUnsupported());

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}
