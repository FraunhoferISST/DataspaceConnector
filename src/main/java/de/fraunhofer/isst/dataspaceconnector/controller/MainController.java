package de.fraunhofer.isst.dataspaceconnector.controller;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceControllers;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import de.fraunhofer.isst.dataspaceconnector.utils.ControllerUtils;
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
@Tag(name = "Connector", description = "Endpoints for connector information and configuration")
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
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    public ResponseEntity<Object> getPublicSelfDescription() {
        try {
            final var connector = connectorService.getConnectorWithoutResources();
            return ResponseEntity.ok(connector.toRdf());
        } catch (Exception exception) {
            // Connector could not be loaded or deserialized.
            return ControllerUtils.respondConnectorNotLoaded(exception);
        }
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
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    public ResponseEntity<Object> getPrivateSelfDescription() {
        try {
            final var connector = connectorService.getConnectorWithOfferedResources();
            return ResponseEntity.ok(connector.toRdf());
        } catch (Exception exception) {
            // Connector could not be loaded or deserialized.
            return ControllerUtils.respondConnectorNotLoaded(exception);
        }
    }

    /**
     * Provides links at root page.
     *
     * @return Http ok.
     */
    @Hidden
    @GetMapping("/api")
    public ResponseEntity<RepresentationModel> root() {
        final var model = new RepresentationModel();

        model.add(linkTo(methodOn(MainController.class).root()).withSelfRel());
        model.add(linkTo(methodOn(ResourceControllers.AgreementController.class)
                .getAll(null, null, null)).withRel("agreements"));
        model.add(linkTo(methodOn(ResourceControllers.ArtifactController.class)
                .getAll(null, null, null)).withRel("artifacts"));
        model.add(linkTo(methodOn(ResourceControllers.CatalogController.class)
                .getAll(null, null, null)).withRel("catalogs"));
        model.add(linkTo(methodOn(ResourceControllers.ContractController.class)
                .getAll(null, null, null)).withRel("contracts"));
        model.add(linkTo(methodOn(ResourceControllers.OfferedResourceController.class)
                .getAll(null, null, null)).withRel("offers"));
        model.add(linkTo(methodOn(ResourceControllers.RepresentationController.class)
                .getAll(null, null, null)).withRel("representations"));
        model.add(linkTo(methodOn(ResourceControllers.RequestedResourceController.class)
                .getAll(null, null, null)).withRel("requests"));
        model.add(linkTo(methodOn(ResourceControllers.RuleController.class)
                .getAll(null, null, null)).withRel("rules"));

        return ResponseEntity.ok(model);
    }
}
