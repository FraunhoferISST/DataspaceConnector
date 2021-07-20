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
package io.configmanager.extensions.routes.api;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * API for handling Camel route-error logs.
 */
public interface RoutesApi {

    /**
     * Get new route related errors.
     * @return The response message or an error.
     */
    @Hidden
    @GetMapping(value = "/route/error", produces = "application/ld+json")
    @Operation(summary = "Get new route related errors")
    @ApiResponse(responseCode = "200", description = "Loaded and returned cached Route-Errors.")
    ResponseEntity<String> getRouteErrors();
}
