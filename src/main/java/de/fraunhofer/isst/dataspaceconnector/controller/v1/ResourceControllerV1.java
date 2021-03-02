package de.fraunhofer.isst.dataspaceconnector.controller.v1;

import de.fraunhofer.isst.dataspaceconnector.exceptions.handler.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.v1.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.v1.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ContractRuleLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.RepresentationArtifactLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.RepresentationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceContractLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceRepresentationLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.RuleService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.TemplateBuilder42;
import de.fraunhofer.isst.dataspaceconnector.utils.EndpointUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.EntityApiBridge;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.UUID;

/**
 * This class provides endpoints for the internal resource handling. Resources can be created and
 * modified with it's {@link ResourceMetadata} including {@link de.fraunhofer.iais.eis.Contract} and
 * {@link ResourceRepresentation}.
 */
@RestController
@RequestMapping("/api/v1/resources")
@Tag(name = "Resource Handling", description = "Endpoints for resource handling")
@RequiredArgsConstructor
public class ResourceControllerV1 {

    private final @NonNull ResourceService<OfferedResource, ?> resourceService;
    private final @NonNull RepresentationService representationService;
    private final @NonNull ResourceRepresentationLinker<OfferedResource> resourceRepresentationLinker;
    private final @NonNull ResourceContractLinker<OfferedResource> resourceContractLinker;
    private final @NonNull ContractRuleLinker contractRuleLinker;
    private final @NonNull RuleService ruleService;
    private final @NonNull RepresentationArtifactLinker representationArtifactLinker;
    private final @NonNull ArtifactService artifactService;
    private final @NonNull TemplateBuilder42<OfferedResource, OfferedResourceDesc> templateBuilder;

    /**
     * Registers a resource with its metadata and, if wanted, with an already existing id.
     *
     * @param resourceMetadata The resource metadata.
     * @param resourceId       The resource uuid.
     * @return The added uuid.
     */
    @Operation(summary = "Create resource", description = "Register a resource by its metadata.",
            deprecated = true)
    @ApiResponses(value =
            {
                @ApiResponse(responseCode = "201", description = "Resource created"),
                @ApiResponse(responseCode = "400", description = "Invalid resource"),
                @ApiResponse(responseCode = "409", description = "Resource already exists"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @RequestMapping(value = "/resource", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<OfferedResource>
    createResource(@RequestBody final ResourceMetadata resourceMetadata,
            @RequestParam(value = "id", required = false) final UUID resourceId) {
        final var template =
                EntityApiBridge.toOfferedResourceTemplate(resourceId, resourceMetadata);
        final var resource = templateBuilder.build(template);

        final var headers = new HttpHeaders();
        headers.setLocation(EndpointUtils.getCurrentBasePath());

        return new ResponseEntity<>(resource, headers, HttpStatus.CREATED);
    }

    /**
     * Updates resource metadata by id.
     *
     * @param resourceId       The resource id.
     * @param resourceMetadata The updated metadata.
     * @return OK or error response.
     */
    @Operation(summary = "Update resource",
            description = "Update the resource's metadata by its uuid.", deprecated = true)
    @ApiResponses(value =
            {
                @ApiResponse(responseCode = "200", description = "Ok"),
                @ApiResponse(responseCode = "400", description = "Invalid resource"),
                @ApiResponse(responseCode = "404", description = "Not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateResource(
                   @Parameter(description = "The resource uuid.", required = true)
                   @PathVariable("resource-id") final UUID resourceId,
                   @RequestBody final ResourceMetadata resourceMetadata) {
        // Try to access the resource. This will throw 404, when the resource does not exists,
        // preventing the builder to create a new resource.
        resourceService.get(resourceId);

        final var template =
                EntityApiBridge.toOfferedResourceTemplate(resourceId, resourceMetadata);
        templateBuilder.build(template);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Gets resource metadata by id.
     *
     * @param resourceId The resource id.
     * @return Metadata or an error response.
     */
    @Operation(summary = "Get resource", description = "Get the resource's metadata by its uuid.",
            deprecated = true)
    @ApiResponses(value =
            {
                @ApiResponse(responseCode = "200", description = "Ok"),
                @ApiResponse(responseCode = "404", description = "Not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getResource(
            @Parameter(description = "The resource uuid.", required = true)
            @PathVariable("resource-id") final UUID resourceId) {
        final var resource = resourceService.get(resourceId);
        return ResponseEntity.ok(resource);
    }

    /**
     * Deletes resource by id.
     *
     * @param resourceId The resource id.
     * @return OK or error response.
     */
    @Operation(summary = "Delete resource", description = "Delete a resource by its uuid.",
            deprecated = true)
    @ApiResponses(value =
            {
                @ApiResponse(responseCode = "200", description = "Ok"),
                @ApiResponse(responseCode = "404", description = "Not found")
            })
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteResource(
            @Parameter(description = "The resource uuid.", required = true)
            @PathVariable("resource-id") final UUID resourceId) {
        resourceService.delete(resourceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Updates usage policy.
     *
     * @param resourceId The resource id.
     * @param policy     The resource's usage policy as string.
     * @return OK or an error response.
     */
    @Operation(summary = "Update resource contract",
            description = "Update the resource's usage policy.", deprecated = true)
    @ApiResponses(value =
            {
                @ApiResponse(responseCode = "200", description = "Ok"),
                @ApiResponse(responseCode = "400", description = "Invalid resource"),
                @ApiResponse(responseCode = "404", description = "Not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @RequestMapping(value = "/{resource-id}/contract", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<ContractRule> updateContract(
                   @Parameter(description = "The resource uuid.", required = true)
                   @PathVariable("resource-id") final UUID resourceId,
                   @Parameter(description = "A new resource contract.", required = true)
                   @RequestBody final String policy) {
        final var representations = resourceRepresentationLinker.get(resourceId);

        if (representations.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var contracts = resourceContractLinker.get((UUID) representations.toArray()[0]);

        if (contracts.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var rules = contractRuleLinker.get((UUID) contracts.toArray()[0]);

        if (rules.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var template = EntityApiBridge.toRuleTemplate(policy);
        template.getDesc().setStaticId(((EndpointId) rules.toArray()[0]).getResourceId());

        final var rule = templateBuilder.build(template);

        return new ResponseEntity<>(rule, HttpStatus.OK);
    }

    /**
     * Gets usage policy.
     *
     * @param resourceId The resource id.
     * @return Contract or an error response.
     */
    @Operation(summary = "Get resource contract", description = "Get the resource's usage policy.",
            deprecated = true)
    @ApiResponses(value =
            {
                @ApiResponse(responseCode = "200", description = "Ok"),
                @ApiResponse(responseCode = "404", description = "Not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @RequestMapping(value = "/{resource-id}/contract", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getContract(
            @Parameter(description = "The resource uuid.", required = true)
            @PathVariable("resource-id") final UUID resourceId) {
        final var contracts = resourceContractLinker.get(resourceId);

        if (contracts.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var rules = contractRuleLinker.get((UUID) contracts.toArray()[0]);

        if (rules.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        return new ResponseEntity<>(
                ruleService.get((UUID) rules.toArray()[0]).getValue(), HttpStatus.OK);
    }

    /**
     * Get how often resource data has been accessed.
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @Operation(summary = "Get data access",
            description = "Get the number of the resource's data access.", deprecated = true)
    @ApiResponses(value =
            {
                @ApiResponse(responseCode = "200", description = "Ok"),
                @ApiResponse(responseCode = "404", description = "Not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @RequestMapping(value = "/{resource-id}/access", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getAccess(
            @Parameter(description = "The resource uuid.", required = true)
            @PathVariable("resource-id") final UUID resourceId) {
        final var representations = resourceRepresentationLinker.get(resourceId);

        if (representations.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var artifacts =
                representationArtifactLinker.get((UUID) representations.toArray()[0]);

        if (artifacts.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        return new ResponseEntity<>(
                artifactService.get((UUID) artifacts.toArray()[0]).getNumAccessed(),
                HttpStatus.OK);
    }

    /**
     * Adds resource representation.
     *
     * @param resourceId     The resource id.
     * @param representation A new representation.
     * @param representationId The representation id.
     * @return OK or an error response.
     */
    @Operation(summary = "Add representation", description = "Add a representation to a resource.",
            deprecated = true)
    @ApiResponses(value =
            {
                @ApiResponse(responseCode = "201", description = "Representation created"),
                @ApiResponse(responseCode = "400", description = "Invalid representation"),
                @ApiResponse(responseCode = "404", description = "Not found"),
                @ApiResponse(responseCode = "409", description = "Representation already exists"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @RequestMapping(value = "/{resource-id}/representation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Representation> addRepresentation(
            @Parameter(description = "The resource uuid.", required = true)
            @PathVariable("resource-id") final UUID resourceId,
            @Parameter(description = "A new resource representation.", required = true)
            @RequestBody final ResourceRepresentation representation,
            @RequestParam(value = "id", required = false) final UUID representationId) {
        representation.setUuid(representationId);
        final var template = EntityApiBridge.toRepresentationTemplate(representation);
        final var rep = templateBuilder.build(template);

        resourceRepresentationLinker.add(resourceId, Collections.singleton(rep.getId()));

        final var headers = new HttpHeaders();
        headers.setLocation(EndpointUtils.getCurrentBasePath());

        return new ResponseEntity<>(rep, headers, HttpStatus.CREATED);
    }

    /**
     * Updates resource representation.
     *
     * @param resourceId       The resource id.
     * @param representationId The representation id.
     * @param representation   A new representation.
     * @return OK or an error response.
     */
    @Operation(summary = "Update representation",
            description = "Update a resource's representation by its uuid.", deprecated = true)
    @ApiResponses(value =
            {
                @ApiResponse(responseCode = "200", description = "Ok"),
                @ApiResponse(responseCode = "400", description = "Invalid representation"),
                @ApiResponse(responseCode = "404", description = "Not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @RequestMapping(value = "/{resource-id}/{representation-id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateRepresentation(
            @Parameter(description = "The resource uuid.", required = true)
            @PathVariable("resource-id") final UUID resourceId,
            @Parameter(description = "The representation uuid.", required = true)
            @PathVariable("representation-id") final UUID representationId,
            @Parameter(description = "A new resource representation.", required = true)
            @RequestBody final ResourceRepresentation representation) {
        // Try to access the resource. This will throw 404, when the resource does not exists,
        // preventing the builder to create a new resource.
        resourceService.get(resourceId);

        // Try to access the representation. This will throw 404, when the representation does
        // not exists,
        // preventing the builder to create a new representation.
        representationService.get(representationId);

        // Try to access the relation. This will throw 404, when the relation does not exists,
        // preventing the builder to create a new resource and representation.
        if (!resourceRepresentationLinker.get(resourceId).contains(representationId)) {
            throw new ResourceNotFoundException("");
        }

        final var template = EntityApiBridge.toRepresentationTemplate(representation);
        templateBuilder.build(template);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Adds resource representation.
     *
     * @param resourceId       The resource id.
     * @param representationId The representation id.
     * @return OK or an error response.
     */
    @Operation(summary = "Get representation",
            description = "Get the resource's representation by its uuid.", deprecated = true)
    @ApiResponses(value =
            {
                @ApiResponse(responseCode = "200", description = "Ok"),
                @ApiResponse(responseCode = "404", description = "Not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @RequestMapping(value = "/{resource-id}/{representation-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Representation> getRepresentation(
            @Parameter(description = "The resource uuid.", required = true)
            @PathVariable("resource-id") final UUID resourceId,
            @Parameter(description = "The representation uuid.", required = true)
            @PathVariable("representation-id") final UUID representationId) {
        final var representation = representationService.get(representationId);
        return ResponseEntity.ok(representation);
    }

    /**
     * Removes resource representation.
     *
     * @param resourceId       The resource id.
     * @param representationId The representation id.
     * @return OK or an error response.
     */
    @Operation(summary = "Remove resource representation",
            description = "Remove a resource's representation by its uuid.", deprecated = true)
    @ApiResponses(value =
            {
                @ApiResponse(responseCode = "200", description = "Ok"),
                @ApiResponse(responseCode = "404", description = "Not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            })
    @RequestMapping(value = "/{resource-id}/{representation-id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteRepresentation(
            @Parameter(description = "The resource uuid.", required = true)
            @PathVariable("resource-id") final UUID resourceId,
            @Parameter(description = "The representation uuid.", required = true)
            @PathVariable("representation-id") final UUID representationId) {
        representationService.delete(representationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
