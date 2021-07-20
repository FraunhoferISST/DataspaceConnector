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
package io.configmanager.extensions.gui.api;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Utility API for Configmanager / Dataspace Connector GUI.
 */
public interface GuiUtilApi {
    /**
     * Auxiliary API to provide data to a GUI,
     * which either comes from the infomodel or is connector specific.
     * @param enumName Selection of the domain of the requested data, e.g. language.
     * @return The response message or an error.
     */
    @Hidden
    @GetMapping(value = "/enum/{enumName}")
    @Operation(summary = "Get the specific enum")
    @ApiResponse(responseCode = "200", description = "Successfully get the enums")
    @ApiResponse(responseCode = "400", description = "Can not find the enums")
    ResponseEntity<String> getSpecificEnum(@PathVariable String enumName);
}
