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

import io.dataspaceconnector.common.ids.ConnectorService;
import io.dataspaceconnector.controller.resource.type.AgreementController;
import io.dataspaceconnector.controller.resource.type.ArtifactController;
import io.dataspaceconnector.controller.resource.type.BrokerController;
import io.dataspaceconnector.controller.resource.type.CatalogController;
import io.dataspaceconnector.controller.resource.type.ContractController;
import io.dataspaceconnector.controller.resource.type.DataSourceController;
import io.dataspaceconnector.controller.resource.type.EndpointController;
import io.dataspaceconnector.controller.resource.type.OfferedResourceController;
import io.dataspaceconnector.controller.resource.type.RepresentationController;
import io.dataspaceconnector.controller.resource.type.RequestedResourceController;
import io.dataspaceconnector.controller.resource.type.RouteController;
import io.dataspaceconnector.controller.resource.type.RuleController;
import io.dataspaceconnector.controller.resource.type.SubscriptionController;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.dataspaceconnector.controller.configuration.AppStoreControllers;
import io.dataspaceconnector.controller.configuration.BrokerControllers;
import io.dataspaceconnector.controller.configuration.DataSourceController;
import io.dataspaceconnector.controller.configuration.EndpointController;
import io.dataspaceconnector.controller.configuration.RouteControllers;
import io.dataspaceconnector.controller.configuration.AppControllers;
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
            @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
            @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                    description = ResponseDescription.UNAUTHORIZED)})
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
            @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
            @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                    description = ResponseDescription.UNAUTHORIZED),
            @ApiResponse(responseCode = ResponseCode.INTERNAL_SERVER_ERROR,
                    description = ResponseDescription.INTERNAL_SERVER_ERROR)})
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
            @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
            @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                    description = ResponseDescription.UNAUTHORIZED)})
    public ResponseEntity<RepresentationModel<?>> root() {
        final var model = new RepresentationModel<>();

        model.add(linkTo(methodOn(MainController.class).root()).withSelfRel());
        model.add(linkTo(methodOn(AgreementController.class)
                .getAll(null, null)).withRel("agreements"));
        model.add(linkTo(methodOn(ArtifactController.class)
                .getAll(null, null)).withRel("artifacts"));
        model.add(linkTo(methodOn(BrokerController.class)
                .getAll(null, null)).withRel("brokers"));
        model.add(linkTo(methodOn(CatalogController.class)
                .getAll(null, null)).withRel("catalogs"));
        model.add(linkTo(methodOn(ContractController.class)
                .getAll(null, null)).withRel("contracts"));
        model.add(linkTo(methodOn(DataSourceController.class)
                .getAll(null, null)).withRel("datasources"));
        model.add(linkTo(methodOn(EndpointController.class)
                .getAll(null, null)).withRel("endpoints"));
        model.add(linkTo(methodOn(OfferedResourceController.class)
                .getAll(null, null)).withRel("offers"));
        model.add(linkTo(methodOn(RepresentationController.class)
                .getAll(null, null)).withRel("representations"));
        model.add(linkTo(methodOn(RouteController.class)
                .getAll(null, null)).withRel("routes"));
        model.add(linkTo(methodOn(RequestedResourceController.class)
                .getAll(null, null)).withRel("requests"));
        model.add(linkTo(methodOn(RuleController.class)
                .getAll(null, null)).withRel("rules"));
        model.add(linkTo(methodOn(SubscriptionController.class)
                .getAll(null, null)).withRel("subscriptions"));
        model.add(linkTo(methodOn(AppControllers.AppController.class)
                .getAll(null, null)).withRel("apps"));
        model.add(linkTo(methodOn(AppStoreControllers.AppStoreController.class)
                .getAll(null, null)).withRel("appstores"));

        return ResponseEntity.ok(model);
    }
}
