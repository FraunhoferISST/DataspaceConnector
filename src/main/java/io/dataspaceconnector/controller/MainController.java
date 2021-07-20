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

import io.dataspaceconnector.controller.configuration.BrokerControllers;
import io.dataspaceconnector.controller.configuration.DataSourceController;
import io.dataspaceconnector.controller.configuration.EndpointController;
import io.dataspaceconnector.controller.configuration.RouteControllers;
import io.dataspaceconnector.controller.resource.ResourceControllers;
import io.dataspaceconnector.service.ids.ConnectorService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * This class provides endpoints for basic connector services.
 */
@RestController
@Tag(name = "Connector", description = "Endpoints for connector information")
@RequiredArgsConstructor
public class MainController {

    /**
     * Service for ids connector management.
     */
    private final @NonNull ConnectorService connectorService;

    /**
     * Gets connector self-description without catalogs and resources.
     *
     * @return Self-description or error response.
     */
    @GetMapping(value = {"/", ""}, produces = "application/ld+json")
    @Operation(summary = "Public IDS self-description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @ResponseBody
    public ResponseEntity<Object> getPublicSelfDescription() {
        return ResponseEntity.ok(connectorService.getConnectorWithoutResources().toRdf());
    }

    /**
     * Gets connector self-description with all resources.
     *
     * @return Self-description or error response.
     */
    @GetMapping(value = "/api/connector", produces = "application/ld+json")
    @Operation(summary = "Private IDS self-description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    public ResponseEntity<Object> getPrivateSelfDescription() {
        return ResponseEntity.ok(connectorService.getConnectorWithOfferedResources().toRdf());
    }

    /**
     * Provides links at root page.
     *
     * @return Http ok.
     */
    @Hidden
    @GetMapping("/api")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public ResponseEntity<RepresentationModel<?>> root() {
        final var model = new RepresentationModel<>();

        model.add(linkTo(methodOn(MainController.class).root()).withSelfRel());
        model.add(linkTo(methodOn(ResourceControllers.AgreementController.class)
                .getAll(null, null)).withRel("agreements"));
        model.add(linkTo(methodOn(ResourceControllers.ArtifactController.class)
                .getAll(null, null)).withRel("artifacts"));
        model.add(linkTo(methodOn(BrokerControllers.BrokerController.class)
                .getAll(null, null)).withRel("brokers"));
        model.add(linkTo(methodOn(ResourceControllers.CatalogController.class)
                .getAll(null, null)).withRel("catalogs"));
        model.add(linkTo(methodOn(ResourceControllers.ContractController.class)
                .getAll(null, null)).withRel("contracts"));
        model.add(linkTo(methodOn(DataSourceController.class)
                .getAll(null, null)).withRel("datasources"));
        model.add(linkTo(methodOn(EndpointController.class)
                .getAll(null, null)).withRel("endpoints"));
        model.add(linkTo(methodOn(ResourceControllers.OfferedResourceController.class)
                .getAll(null, null)).withRel("offers"));
        model.add(linkTo(methodOn(ResourceControllers.RepresentationController.class)
                .getAll(null, null)).withRel("representations"));
        model.add(linkTo(methodOn(RouteControllers.RouteController.class)
                .getAll(null, null)).withRel("routes"));
        model.add(linkTo(methodOn(ResourceControllers.RequestedResourceController.class)
                .getAll(null, null)).withRel("requests"));
        model.add(linkTo(methodOn(ResourceControllers.RuleController.class)
                .getAll(null, null)).withRel("rules"));
        model.add(linkTo(methodOn(ResourceControllers.SubscriptionController.class)
                .getAll(null, null)).withRel("subscriptions"));

        return ResponseEntity.ok(model);
    }
}
