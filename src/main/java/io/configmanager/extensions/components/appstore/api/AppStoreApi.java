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
package io.configmanager.extensions.components.appstore.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

public interface AppStoreApi {
    /**
     * Creates a new AppStore.
     * @param accessUrl The location of the AppStore.
     * @param title The title of the AppStore.
     * @return The response message or an error.
     */
    @PostMapping(value = "/appstore", produces = "application/ld+json")
    @Operation(summary = "Creates a new app store")
    @ApiResponse(responseCode = "200", description = "Created a new app store")
    ResponseEntity<String> createAppStore(@RequestParam(value = "accessUrl") URI accessUrl,
                                          @RequestParam(value = "title",
                                                  required = false) String title);

    /**
     * Updates metadata of an existing AppStore.
     * @param accessUrl The location of the AppStore.
     * @param title The title of the AppStore.
     * @return The response message or an error.
     */
    @PutMapping(value = "/appstore", produces = "application/ld+json")
    @Operation(summary = "Updates an app store")
    @ApiResponse(responseCode = "200", description = "Updated the app store")
    @ApiResponse(responseCode = "400", description = "Can not update the app store")
    ResponseEntity<String> updateAppStore(@RequestParam(value = "accessUrl") URI accessUrl,
                                        @RequestParam(value = "title",
                                                required = false) String title);

    /**
     * Deletes metadata of an existing AppStore.
     * @param accessUrl The location of the AppStore used to identify the AppStore.
     * @return The response message or an error.
     */
    @DeleteMapping(value = "/appstore", produces = "application/ld+json")
    @Operation(summary = "Deletes an app store")
    @ApiResponse(responseCode = "200", description = "Deleted the app store")
    @ApiResponse(responseCode = "400", description = "Can not delete the app store")
    ResponseEntity<String> deleteAppStore(@RequestParam(value = "accessUrl") URI accessUrl);

    /**
     * Returns the list of all metadata of all known AppStores.
     * @return The response message or an error.
     */
    @GetMapping(value = "/appstores", produces = "application/ld+json")
    @Operation(summary = "Returns the list of all app stores")
    @ApiResponse(responseCode = "200",
            description = "Successfully returned the list of all app stores")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<String> getAllAppStores();
}
