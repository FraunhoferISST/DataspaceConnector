/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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

import io.dataspaceconnector.common.net.ContentType;
import io.dataspaceconnector.controller.gui.util.GuiUtils;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The api class offers the possibilities to provide other api's which could be needed.
 * As an example, enum values are supplied here via an api.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/utils")
@Tag(name = "_Utils")
public class GuiController {

    /**
     * Auxiliary API endpoint to provide IDS Infomodel or connector specific data.
     *
     * @return The response message or an error.
     */
    @GetMapping(value = "/enums", produces = ContentType.JSON)
    @Operation(summary = "Get a list of enums by value name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
            @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                    description = ResponseDescription.UNAUTHORIZED)})
    public ResponseEntity<JSONObject> getEnums() {
        return ResponseEntity.ok(GuiUtils.getListOfEnums());
    }
}
