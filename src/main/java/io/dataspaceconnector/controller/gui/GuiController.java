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
package io.dataspaceconnector.controller.gui;

import io.dataspaceconnector.controller.gui.util.GuiUtils;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The api class offers the possibilities to provide other api's which could be needed.
 * As an example, enum values are supplied here via an api.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/configmanager")
@Tag(name = "ConfigManager: GUI Utilities")
@Log4j2
public class GuiController {

    /**
     * Auxiliary API to provide data to a GUI, which either comes from the infomodel or is connector
     * specific.
     *
     * @param enumName Selection of the domain of the requested data, e.g. language.
     * @return The response message or an error.
     */
    @Hidden
    @GetMapping(value = "/enum/{enumName}")
    @Operation(summary = "Get a list of enums by value name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
            @ApiResponse(responseCode = ResponseCode.BAD_REQUEST,
                    description = ResponseDescription.BAD_REQUEST),
            @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                    description = ResponseDescription.UNAUTHORIZED)})
    ResponseEntity<String> getSpecificEnum(final @PathVariable String enumName) {
        final var enums = GuiUtils.getSpecificEnum(enumName);
        return enums == null ? ResponseEntity.badRequest().body("Could not get the enums.")
                : ResponseEntity.ok(enums);
    }

}
