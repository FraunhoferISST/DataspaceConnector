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
package io.dataspaceconnector.controller.routing.error;

import io.dataspaceconnector.common.net.ContentType;
import io.dataspaceconnector.controller.routing.tag.CamelDescription;
import io.dataspaceconnector.controller.routing.tag.CamelName;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * The api class implements the AppRouteApi and offers the possibilities to manage the app routes.
 */
@RestController("configManagerRoutesController")
@NoArgsConstructor
@RequestMapping("/api/camel/routes")
@Tag(name = CamelName.CAMEL, description = CamelDescription.CAMEL)
public class ErrorController {
    /**
     * Max. amount of route errors to be logged.
     */
    public static final int MAX_ERROR_LOG = 100;

    /**
     * The temp. storage of route errors.
     */
    private final LinkedList<String> routeErrors = new LinkedList<>();

    /**
     * Adds a route error message to the temp. storage.
     *
     * @param routeError the error message
     */
    public void addRouteErrors(final String routeError) {
        if (routeErrors.size() >= MAX_ERROR_LOG) {
            routeErrors.remove(0);
        }
        routeErrors.add(routeError);
    }

    /**
     * This method returns all saved Route-Errors for the GET-API.
     *
     * @return Response-Code and all logged Route-Errors.
     */
    @GetMapping(value = "/error", produces = ContentType.JSON)
    @Operation(summary = "Get new route related errors.")
    @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK)
    @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
            description = ResponseDescription.UNAUTHORIZED)
    public ResponseEntity<Object> getRouteErrors() {
        final var value = routeErrors.stream().collect(Collectors.joining(",", "[", "]"));
        return ResponseEntity.ok("{" + value + "}");
    }
}
