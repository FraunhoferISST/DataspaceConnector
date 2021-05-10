package io.dataspaceconnector.controller.resources;

import io.dataspaceconnector.model.*;
import io.dataspaceconnector.services.resources.AbstractCatalogResourceLinker;
import io.dataspaceconnector.services.resources.AbstractResourceContractLinker;
import io.dataspaceconnector.services.resources.AbstractResourceRepresentationLinker;
import io.dataspaceconnector.services.resources.RelationServices;
import io.dataspaceconnector.view.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

public final class RelationControllers {

    @RestController
    @RequestMapping("/api/rules/{id}/contracts")
    @Tag(name = "Rules", description = "Endpoints for linking rules to contracts")
    public static class RulesToContracts extends BaseResourceChildController<
            RelationServices.RuleContractLinker, Contract, ContractView> {
    }

    @RestController
    @RequestMapping("/api/artifacts/{id}/representations")
    @Tag(name = "Artifacts", description = "Endpoints for linking artifacts to representations")
    public static class ArtifactsToRepresentations extends BaseResourceChildController<
            RelationServices.ArtifactRepresentationLinker, Representation, RepresentationView> {
    }

    @RestController
    @RequestMapping("/api/representations/{id}/offers")
    @Tag(name = "Representations", description = "Endpoints for linking representations to "
            + "offered resources")
    public static class RepresentationsToOfferedResources extends BaseResourceChildController<
            RelationServices.RepresentationOfferedResourceLinker, OfferedResource,
            OfferedResourceView> {
    }

    @RestController
    @RequestMapping("/api/representations/{id}/requests")
    @Tag(name = "Representations", description = "Endpoints for linking representations to "
            + "requested resources")
    public static class RepresentationsToRequestedResources extends BaseResourceChildController<
            RelationServices.RepresentationOfferedResourceLinker, RequestedResource,
            RequestedResourceView> {
    }

    @RestController
    @RequestMapping("/api/offers/{id}/catalogs")
    @Tag(name = "Resources", description = "Endpoints for linking offered resources to catalogs")
    public static class OfferedResourcesToCatalogs extends BaseResourceChildController<
            RelationServices.OfferedResourceCatalogLinker, Catalog, CatalogView> {
    }

    @RestController
    @RequestMapping("/api/requests/{id}/catalogs")
    @Tag(name = "Resources", description = "Endpoints for linking requested resources to catalogs")
    public static class RequestedResourcesToCatalogs extends BaseResourceChildController<
            RelationServices.RequestedResourceCatalogLinker, Catalog, CatalogView> {
    }

    @RestController
    @RequestMapping("/api/contracts/{id}/offers")
    @Tag(name = "Contracts", description = "Endpoints for linking contracts to offers")
    public static class ContractsToOfferedResources extends BaseResourceChildController<
            RelationServices.ContractOfferedResourceLinker, OfferedResource,
            OfferedResourceView> {
    }

    @RestController
    @RequestMapping("/api/contracts/{id}/requests")
    @Tag(name = "Contracts", description = "Endpoints for linking contracts to requests")
    public static class ContractsToRequestedResources extends BaseResourceChildController<
            RelationServices.ContractRequestedResourceLinker, RequestedResource,
            RequestedResourceView> {
    }

    @RestController
    @RequestMapping("/api/artifacts/{id}/agreements")
    @Tag(name = "Artifacts", description = "Endpoints for linking artifacts to agreements")
    public static class ArtifactsToAgreements extends BaseResourceChildController<
            RelationServices.ArtifactAgreementLinker, Agreement, AgreementView> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final HttpEntity<PagedModel<AgreementView>> addResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content")})
        public final HttpEntity<Void> replaceResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content")})
        public final HttpEntity<Void> removeResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @RestController
    @RequestMapping("/api/agreements/{id}/artifacts")
    @Tag(name = "Agreements", description = "Endpoints for linking agreements to artifacts")
    public static class AgreementsToArtifacts extends BaseResourceChildController<
            RelationServices.AgreementArtifactLinker, Artifact, ArtifactView> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final HttpEntity<PagedModel<ArtifactView>> addResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content")})
        public final HttpEntity<Void> replaceResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content")})
        public final HttpEntity<Void> removeResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @RestController
    @RequestMapping("/api/representations/{id}/artifacts")
    @Tag(name = "Representations", description = "Endpoints for linking artifacts to "
            + "representations")
    public static class RepresentationsToArtifacts
            extends BaseResourceChildController<RelationServices.RepresentationArtifactLinker,
            Artifact, ArtifactView> {
    }

    @RestController
    @RequestMapping("/api/contracts/{id}/rules")
    @Tag(name = "Contracts", description = "Endpoints for linking rules to contracts")
    public static class ContractsToRules extends BaseResourceChildController<
            RelationServices.ContractRuleLinker, ContractRule, ContractRuleView> {
    }

    @RestController
    @RequestMapping("/api/catalogs/{id}/offers")
    @Tag(name = "Catalogs", description = "Endpoints for linking offered resources to catalogs")
    public static class CatalogsToOfferedResources extends BaseResourceChildController<
            AbstractCatalogResourceLinker<OfferedResource>, OfferedResource, OfferedResourceView> {
    }

    @RestController
    @RequestMapping("/api/offers/{id}/contracts")
    @Tag(name = "Resources", description = "Endpoints for linking contracts to resources")
    public static class OfferedResourcesToContracts
            extends BaseResourceChildController<AbstractResourceContractLinker<OfferedResource>,
            Contract, ContractView> {
    }

    @RestController
    @RequestMapping("/api/offers/{id}/representations")
    @Tag(name = "Resources", description = "Endpoints for linking representations to resources")
    public static class OfferedResourcesToRepresentations
            extends BaseResourceChildController<AbstractResourceRepresentationLinker<
            OfferedResource>, Representation, RepresentationView> {
    }

    @RestController
    @RequestMapping("/api/requests/{id}/contracts")
    @Tag(name = "Resources", description = "Endpoints for linking contracts to resources")
    public static class RequestedResourcesToContracts
            extends BaseResourceChildController<AbstractResourceContractLinker<RequestedResource>,
            Contract, ContractView> {
    }

    @RestController
    @RequestMapping("/api/requests/{id}/representations")
    @Tag(name = "Resources", description = "Endpoints for linking representations to resources")
    public static class RequestedResourcesToRepresentations
            extends BaseResourceChildController<AbstractResourceRepresentationLinker<
            RequestedResource>, Representation, RepresentationView> {
    }
}
