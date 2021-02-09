package de.fraunhofer.isst.dataspaceconnector.controller.v1;

import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.v1.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.v1.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.RepresentationView;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.ResourceView;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.RuleView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v1.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v1.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.ArtifactBFFService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.BFFContractRuleLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.BFFRepresentationArtifactLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.BFFRepresentationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.BFFResourceContractLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.BFFResourceRepresentationLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.BFFResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.Basepaths;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.RuleBFFService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.TemplateBuilder42;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.dataspaceconnector.services.utils.ResourceApiBridge;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ResourceControllerV1 {

    @Autowired
    private BFFResourceService resourceService;

    @Autowired
    private BFFRepresentationService representationService;

    @Autowired
    private BFFResourceRepresentationLinker resourceRepresentationLinker;

    @Autowired
    private BFFResourceContractLinker resourceContractLinker;

    @Autowired
    private BFFContractRuleLinker contractRuleLinker;

    @Autowired
    private RuleBFFService ruleService;

    @Autowired
    private BFFRepresentationArtifactLinker representationArtifactLinker;

    @Autowired
    private ArtifactBFFService artifactService;

    @Autowired
    private TemplateBuilder42 templateBuilder;

    /**
     * Constructor for ResourceController.
     *
     * @param offeredResourceService The service for the offered resources
     * @param policyHandler The service for handling policies
     * @param requestedResourceService The service for the requested resources
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Autowired
    public ResourceControllerV1(OfferedResourceServiceImpl offeredResourceService,
                                PolicyHandler policyHandler, RequestedResourceServiceImpl requestedResourceService)
        throws IllegalArgumentException {
        if (offeredResourceService == null) {
            throw new IllegalArgumentException("The OfferedResourceService cannot be null.");
        }

        if (policyHandler == null) {
            throw new IllegalArgumentException("The PolicyHandler cannot be null.");
        }

        if (requestedResourceService == null) {
            throw new IllegalArgumentException("The RequestedResourceService cannot be null.");
        }
    }

    /**
     * Registers a resource with its metadata and, if wanted, with an already existing id.
     *
     * @param resourceMetadata The resource metadata.
     * @param uuid             The resource uuid.
     * @return The added uuid.
     */
    @Operation(summary = "Create resource",
            description = "Register a resource by its metadata.", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Resource created"),
            @ApiResponse(responseCode = "400", description = "Invalid resource"),
            @ApiResponse(responseCode = "409", description = "Resource already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/resource", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ResourceView> createResource(@RequestBody ResourceMetadata resourceMetadata,
                                                       @RequestParam(value = "id", required = false) UUID uuid) {
        final var template = ResourceApiBridge.toResourceTemplate(uuid, resourceMetadata);
        final var endpointId = templateBuilder.build(template);

        final var headers = new HttpHeaders();
        headers.setLocation(endpointId.toUri());

        return new ResponseEntity<>(resourceService.get(endpointId), headers, HttpStatus.CREATED);
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Invalid resource"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateResource(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId,
        @RequestBody ResourceMetadata resourceMetadata) {
        // Try to access the resource. This will throw 404, when the resource does not exists,
        // preventing the builder to create a new resource.
        resourceService.get(new EndpointId(Basepaths.Resources.toString(), resourceId));

        final var template = ResourceApiBridge.toResourceTemplate(resourceId, resourceMetadata);
        templateBuilder.build(template);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Gets resource metadata by id.
     *
     * @param resourceId The resource id.
     * @return Metadata or an error response.
     */
    @Operation(summary = "Get resource",
            description = "Get the resource's metadata by its uuid.", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getResource(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId) {
        final var resource = resourceService.get(new EndpointId(Basepaths.Resources.toString(), resourceId));
        return ResponseEntity.ok(resource);
    }

    /**
     * Deletes resource by id.
     *
     * @param resourceId The resource id.
     * @return OK or error response.
     */
    @Operation(summary = "Delete resource",
            description = "Delete a resource by its uuid.", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found")})
    @RequestMapping(value = "/{resource-id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> deleteResource(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId) {
        resourceService.delete(new EndpointId(Basepaths.Resources.toString(), resourceId));
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Invalid resource"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}/contract", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<RuleView> updateContract(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId,
        @Parameter(description = "A new resource contract.", required = true)
        @RequestBody String policy) {

        final var representations = resourceRepresentationLinker.get(new EndpointId(Basepaths.Resources.toString(), resourceId));

        if(representations.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var contracts = resourceContractLinker.get((EndpointId)representations.toArray()[0]);

        if(contracts.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var rules = contractRuleLinker.get((EndpointId)contracts.toArray()[0]);

        if(rules.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var template = ResourceApiBridge.toRuleTemplate(policy);
        template.getDesc().setStaticId(((EndpointId)rules.toArray()[0]).getResourceId());

        final var endpointId = templateBuilder.build(template);

        return new ResponseEntity<>(ruleService.get(endpointId), HttpStatus.OK);
    }

    /**
     * Gets usage policy.
     *
     * @param resourceId The resource id.
     * @return Contract or an error response.
     */
    @Operation(summary = "Get resource contract",
            description = "Get the resource's usage policy.", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}/contract", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getContract(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId) {
        final var contracts = resourceContractLinker.get(new EndpointId(Basepaths.Resources.toString(), resourceId));

        if(contracts.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var rules = contractRuleLinker.get((EndpointId)contracts.toArray()[0]);

        if(rules.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        return new ResponseEntity<>(ruleService.get((EndpointId)rules.toArray()[0]).getValue(), HttpStatus.OK);
    }

    /**
     * Get how often resource data has been accessed.
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @return a {@link org.springframework.http.ResponseEntity} object.
     */
    @Operation(summary = "Get data access",
            description = "Get the number of the resource's data access.", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}/access", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getAccess(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId) {

        final var representations = resourceRepresentationLinker.get(new EndpointId(Basepaths.Resources.toString(), resourceId));

        if(representations.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        final var artifacts = representationArtifactLinker.get((EndpointId)representations.toArray()[0]);

        if(artifacts.isEmpty()) {
            throw new ResourceNotFoundException("");
        }

        return new ResponseEntity<>(artifactService.get((EndpointId)artifacts.toArray()[0]).getNumAccessed(), HttpStatus.OK);
    }

    /**
     * Adds resource representation.
     *
     * @param resourceId     The resource id.
     * @param representation A new representation.
     * @return OK or an error response.
     */
    @Operation(summary = "Add representation",
            description = "Add a representation to a resource.", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Representation created"),
            @ApiResponse(responseCode = "400", description = "Invalid representation"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "409", description = "Representation already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}/representation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<RepresentationView> addRepresentation(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId,
        @Parameter(description = "A new resource representation.", required = true)
        @RequestBody ResourceRepresentation representation,
        @RequestParam(value = "id", required = false) UUID uuid) {
        representation.setUuid(uuid);
        final var template = ResourceApiBridge.toRepresentationTemplate(representation);
        final var endpointId = templateBuilder.build(template);

        resourceRepresentationLinker.add(new EndpointId(Basepaths.Representations.toString(), resourceId), Collections.singleton(endpointId));

        final var headers = new HttpHeaders();
        headers.setLocation(endpointId.toUri());

        return new ResponseEntity<>(representationService.get(endpointId), headers, HttpStatus.CREATED);
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "400", description = "Invalid representation"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}/{representation-id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> updateRepresentation(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId,
        @Parameter(description = "The representation uuid.", required = true)
        @PathVariable("representation-id") UUID representationId,
        @Parameter(description = "A new resource representation.", required = true)
        @RequestBody ResourceRepresentation representation) {

        // Try to access the resource. This will throw 404, when the resource does not exists,
        // preventing the builder to create a new resource.
        resourceService.get(new EndpointId(Basepaths.Resources.toString(), resourceId));

        // Try to access the representation. This will throw 404, when the representation does not exists,
        // preventing the builder to create a new representation.
        representationService.get(new EndpointId(Basepaths.Representations.toString(), representationId));

        // Try to access the relation. This will throw 404, when the relation does not exists,
        // preventing the builder to create a new resource and representation.
        if(!resourceRepresentationLinker.get(new EndpointId(Basepaths.Resources.toString(), resourceId)).contains(new EndpointId(Basepaths.Representations.toString(), representationId))) {
            throw new ResourceNotFoundException("");
        }

        final var template = ResourceApiBridge.toRepresentationTemplate(representation);
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}/{representation-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> getRepresentation(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId,
        @Parameter(description = "The representation uuid.", required = true)
        @PathVariable("representation-id") UUID representationId) {
        final var representation = representationService.get(new EndpointId(Basepaths.Representations.toString(), representationId));
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @RequestMapping(value = "/{resource-id}/{representation-id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteRepresentation(
        @Parameter(description = "The resource uuid.", required = true)
        @PathVariable("resource-id") UUID resourceId,
        @Parameter(description = "The representation uuid.", required = true)
        @PathVariable("representation-id") UUID representationId) {
        representationService.delete(new EndpointId(Basepaths.Representations.toString(), representationId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
