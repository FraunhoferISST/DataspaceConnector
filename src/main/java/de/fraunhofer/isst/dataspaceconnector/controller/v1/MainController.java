package de.fraunhofer.isst.dataspaceconnector.controller.v1;

import de.fraunhofer.isst.dataspaceconnector.controller.v2.ArtifactController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.CatalogController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.ContractController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.OfferedResourceController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.RepresentationController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.RuleController;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.ids.IdsConnectorService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
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
     * Class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);

    /**
     * Service for ids connector management.
     */
    private final @NonNull IdsConnectorService connectorService;

    /**
     * Gets connector self-description without catalogs and resources.
     *
     * @return Self-description or error response.
     */
    @GetMapping({"/", ""})
    @Operation(summary = "Public IDS self-description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    public ResponseEntity<Object> getPublicSelfDescription() {
        try {
            final var connector = connectorService.getConnectorWithoutResources();
            return new ResponseEntity<>(connector, HttpStatus.OK);
        } catch (Exception exception) {
            // Connector could not be loaded or deserialized.
            LOGGER.warn("Connector could not be loaded. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Connector could not be loaded.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets connector self-description with all resources.
     *
     * @return Self-description or error response.
     */
    @GetMapping("/api/connector")
    @Operation(summary = "Private IDS self-description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @ResponseBody
    public ResponseEntity<Object> getSelfService() {
        try {
            final var connector = connectorService.getConnectorWithAllResources();
            return new ResponseEntity<>(connector, HttpStatus.OK);
        } catch (Exception exception) {
            // Connector could not be loaded or deserialized.
            LOGGER.warn("Connector could not be loaded. [exception=({})]", exception.getMessage());
            return new ResponseEntity<>("Connector could not be loaded.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
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
        model.add(linkTo(methodOn(ArtifactController.class)
                .getAll(null, null, null)).withRel("artifacts"));
        model.add(linkTo(methodOn(CatalogController.class)
                .getAll(null, null, null)).withRel("catalogs"));
        model.add(linkTo(methodOn(ContractController.class)
                .getAll(null, null, null)).withRel("contracts"));
        model.add(linkTo(methodOn(RepresentationController.class)
                .getAll(null, null, null)).withRel("representations"));
        model.add(linkTo(methodOn(OfferedResourceController.class)
                .getAll(null, null, null)).withRel("resources"));
        model.add(linkTo(methodOn(RuleController.class)
                .getAll(null, null, null)).withRel("rules"));

        return ResponseEntity.ok(model);
    }
}
