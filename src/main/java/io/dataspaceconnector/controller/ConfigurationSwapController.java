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

import de.fraunhofer.ids.messaging.core.config.ConfigContainer;
import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import io.dataspaceconnector.config.interceptor.ConfigurationMapper;
import io.dataspaceconnector.service.configuration.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.UUID;

/**
 * This class provides endpoints for changing the current configuration.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConfigurationSwapController {

    private final @NonNull ConfigContainer configContainer;

    private final @NonNull ConfigurationService configurationService;

    /**
     * Swap the current configuration.
     *
     * @param toSelect newly selected configuration
     * @return OK, when configuration was swapped
     */
    @GetMapping(value = "/swapconfig/{id}",
            produces = {"text/plain"})
    @Operation(summary = "Swap current selected configuration.")
    @Tag(name = "Connector", description = "Endpoints for connector information and configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Failed to deserialize."),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "415", description = "Wrong media type."),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    @Transactional
    public ResponseEntity<String> swapSelected(
            @Valid @PathVariable(name = "id") final UUID toSelect
    ) {
        try {
            configurationService.swapSelected(toSelect);
            var selectedIDs = configurationService.findSelected();
            if(selectedIDs.isEmpty()){
                return ResponseEntity.internalServerError().body(
                        "Could not set selected Configuration!"
                );
            }
            var selected = configurationService.get(selectedIDs.get(0));
            var configuration = ConfigurationMapper.buildInfomodelConfig(selected);
            configContainer.updateConfiguration(configuration);
        } catch (ConfigUpdateException exception) {
            return ResponseEntity.internalServerError().body(
                    "Could not update configmanager!"
            );
        }
        return ResponseEntity.ok("Updated selected configuration!");
    }

}
