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
package io.configmanager.extensions.routes.api.controller;

import io.configmanager.extensions.routes.api.RoutesApi;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * The api class implements the AppRouteApi and offers the possibilities to manage
 * the app routes in the configuration manager.
 */
@Log4j2
@RestController("configManagerRoutesController")
@NoArgsConstructor
@RequestMapping("/api/configmanager")
@Tag(name = "ConfigManager: Routes")
public class RoutesController implements RoutesApi {
    /**
     * Max. amount of route errors to be logged.
     */
    public static final int MAX_ERROR_LOG = 100;

    /**
     * The temp. storage of route errors.
     */
    private final LinkedList<String> routeErrors = new LinkedList<>();

    /**
     * This method saves occuring route errors.
     *
     * @param routeError The route error to be saved
     * @return The response code for the API.
     */
    @Override
    public ResponseEntity<String> setRouteError(final String routeError) {
        if (routeErrors.size() >= MAX_ERROR_LOG) {
            routeErrors.remove(0);
        }

        routeErrors.add(routeError);

        return ResponseEntity.ok("Saved Route-Error in ConfigManager-backend.");
    }

    /**
     * This method returns all saved Route-Errors for the GET-API.
     *
     * @return Response-Code and all logged Route-Errors.
     */
    @Override
    public ResponseEntity<String> getRouteErrors() {
        return ResponseEntity.ok(routeErrors.stream().collect(Collectors.joining(",", "[", "]")));
    }
}
