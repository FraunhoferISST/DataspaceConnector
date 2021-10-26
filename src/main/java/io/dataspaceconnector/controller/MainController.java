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
import io.dataspaceconnector.common.net.ResponseType;
import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.type.AgreementController;
import io.dataspaceconnector.controller.resource.type.AppController;
import io.dataspaceconnector.controller.resource.type.AppStoreController;
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
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
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
@ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
        description = ResponseDescription.UNAUTHORIZED)
@Tag(name = "Connector", description = "Endpoints for connector information.")
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
    @SecurityRequirements
    @GetMapping(value = {"/", ""}, produces = ResponseType.JSON_LD)
    @Operation(summary = "Get the public IDS self-description.")
    @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK)
    @ResponseBody
    public ResponseEntity<Object> getPublicSelfDescription() {
        return ResponseEntity.ok(connectorService.getConnectorWithoutResources().toRdf());
    }

    /**
     * Gets connector self-description with all resources.
     *
     * @return Self-description or error response.
     */
    @GetMapping(value = "/api/connector", produces = ResponseType.JSON_LD)
    @Operation(summary = "Get the private IDS self-description.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
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
    @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK)
    public ResponseEntity<RepresentationModel<?>> root() {
        final var model = new RepresentationModel<>();

        model.add(linkTo(methodOn(MainController.class).root()).withSelfRel());
        model.add(linkTo(methodOn(AgreementController.class)
                .getAll(null, null)).withRel(BaseType.AGREEMENTS));
        model.add(linkTo(methodOn(AppController.class)
                .getAll(null, null)).withRel(BaseType.APPS));
        model.add(linkTo(methodOn(AppStoreController.class)
                .getAll(null, null)).withRel(BaseType.APPSTORES));
        model.add(linkTo(methodOn(ArtifactController.class)
                .getAll(null, null)).withRel(BaseType.ARTIFACTS));
        model.add(linkTo(methodOn(BrokerController.class)
                .getAll(null, null)).withRel(BaseType.BROKERS));
        model.add(linkTo(methodOn(CatalogController.class)
                .getAll(null, null)).withRel(BaseType.CATALOGS));
        model.add(linkTo(methodOn(ContractController.class)
                .getAll(null, null)).withRel(BaseType.CONTRACTS));
        model.add(linkTo(methodOn(DataSourceController.class)
                .getAll(null, null)).withRel(BaseType.DATA_SOURCES));
        model.add(linkTo(methodOn(EndpointController.class)
                .getAll(null, null)).withRel(BaseType.ENDPOINTS));
        model.add(linkTo(methodOn(OfferedResourceController.class)
                .getAll(null, null)).withRel(BaseType.OFFERS));
        model.add(linkTo(methodOn(RepresentationController.class)
                .getAll(null, null)).withRel(BaseType.REPRESENTATIONS));
        model.add(linkTo(methodOn(RouteController.class)
                .getAll(null, null)).withRel(BaseType.ROUTES));
        model.add(linkTo(methodOn(RequestedResourceController.class)
                .getAll(null, null)).withRel(BaseType.REQUESTS));
        model.add(linkTo(methodOn(RuleController.class)
                .getAll(null, null)).withRel(BaseType.RULES));
        model.add(linkTo(methodOn(SubscriptionController.class)
                .getAll(null, null)).withRel(BaseType.SUBSCRIPTIONS));

        return ResponseEntity.ok(model);
    }
}
