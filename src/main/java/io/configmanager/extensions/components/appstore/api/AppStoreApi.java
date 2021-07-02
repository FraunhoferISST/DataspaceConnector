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
    @PostMapping(value = "/appstore", produces = "application/ld+json")
    @Operation(summary = "Creates a new app store")
    @ApiResponse(responseCode = "200", description = "Created a new app store")
    ResponseEntity<String> createAppStore(@RequestParam(value = "accessUrl") URI accessUrl,
                                        @RequestParam(value = "title", required = false) String title);

    @PutMapping(value = "/appstore", produces = "application/ld+json")
    @Operation(summary = "Updates an app store")
    @ApiResponse(responseCode = "200", description = "Updated the app store")
    @ApiResponse(responseCode = "400", description = "Can not update the app store")
    ResponseEntity<String> updateAppStore(@RequestParam(value = "accessUrl") URI accessUrl,
                                        @RequestParam(value = "title", required = false) String title);

    @DeleteMapping(value = "/appstore", produces = "application/ld+json")
    @Operation(summary = "Deletes an app store")
    @ApiResponse(responseCode = "200", description = "Deleted the app store")
    @ApiResponse(responseCode = "400", description = "Can not delete the app store")
    ResponseEntity<String> deleteAppStore(@RequestParam(value = "accessUrl") URI accessUrl);

    @GetMapping(value = "/appstores", produces = "application/ld+json")
    @Operation(summary = "Returns the list of all app stores")
    @ApiResponse(responseCode = "200", description = "Successfully returned the list of all app stores")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    ResponseEntity<String> getAllAppStores();
}
