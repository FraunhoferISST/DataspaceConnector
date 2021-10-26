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
package io.dataspaceconnector.controller.policy;

import io.dataspaceconnector.common.net.ResponseType;
import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.controller.policy.tag.PolicyDescription;
import io.dataspaceconnector.controller.policy.tag.PolicyName;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class provides endpoints for connector configurations via a connected config manager.
 */
@RestController
@ApiResponses(value = {
        @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
        @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                description = ResponseDescription.UNAUTHORIZED)})

@RequestMapping("/api/configuration")
@Tag(name = PolicyName.POLICIES, description = PolicyDescription.POLICY_SETTINGS)
@RequiredArgsConstructor
public class SettingsController {

    /**
     * The current policy configuration.
     */
    private final @NonNull ConnectorConfig connectorConfig;

    /**
     * Turns contract negotiation on or off (at runtime).
     *
     * @param status The desired state.
     * @return Http ok or error response.
     */
    @PutMapping(value = "/negotiation", produces = ResponseType.JSON)
    @Operation(summary = "Set contract negotiation status.")
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
    @GetMapping(value = "/negotiation", produces = ResponseType.JSON)
    @Operation(summary = "Get contract negotiation status.")
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
    @PutMapping(value = "/pattern", produces = ResponseType.JSON)
    @Operation(summary = "Allow unsupported patterns.", description = "Allow requesting data "
            + "without policy enforcement if an unsupported pattern is recognized.")
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
    @GetMapping(value = "/pattern", produces = ResponseType.JSON)
    @Operation(summary = "Get pattern validation status.",
            description = "Return whether unsupported patterns are ignored when requesting data.")
    @ResponseBody
    public ResponseEntity<JSONObject> getPatternStatus() {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final var body = new JSONObject();
        body.put("status", connectorConfig.isAllowUnsupported());

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}
