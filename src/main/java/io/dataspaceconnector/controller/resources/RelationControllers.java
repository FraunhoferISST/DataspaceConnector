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
package io.dataspaceconnector.controller.resources;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;

import io.dataspaceconnector.model.Agreement;
import io.dataspaceconnector.model.Artifact;
import io.dataspaceconnector.model.Catalog;
import io.dataspaceconnector.model.Contract;
import io.dataspaceconnector.model.ContractRule;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.Representation;
import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.services.resources.AbstractCatalogResourceLinker;
import io.dataspaceconnector.services.resources.AbstractResourceContractLinker;
import io.dataspaceconnector.services.resources.AbstractResourceRepresentationLinker;
import io.dataspaceconnector.services.resources.RelationServices;
import io.dataspaceconnector.view.AgreementView;
import io.dataspaceconnector.view.ArtifactView;
import io.dataspaceconnector.view.CatalogView;
import io.dataspaceconnector.view.ContractRuleView;
import io.dataspaceconnector.view.ContractView;
import io.dataspaceconnector.view.OfferedResourceView;
import io.dataspaceconnector.view.RepresentationView;
import io.dataspaceconnector.view.RequestedResourceView;
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

/**
 * This class contains all implementations of the {@link BaseResourceChildController}.
 */
public final class RelationControllers {

    /**
     * Offers the endpoints for managing the relations between rules and contracts.
     */
    @RestController
    @RequestMapping("/api/rules/{id}/contracts")
    @Tag(name = "Rules", description = "Endpoints for linking rules to contracts")
    public static class RulesToContracts extends BaseResourceChildController<
            RelationServices.RuleContractLinker, Contract, ContractView> {
    }

    /**
     * Offers the endpoints for managing the relations between artifacts and representations.
     */
    @RestController
    @RequestMapping("/api/artifacts/{id}/representations")
    @Tag(name = "Artifacts", description = "Endpoints for linking artifacts to representations")
    public static class ArtifactsToRepresentations extends BaseResourceChildController<
            RelationServices.ArtifactRepresentationLinker, Representation, RepresentationView> {
    }

    /**
     * Offers the endpoints for managing the relations between representations and offered
     * resources.
     */
    @RestController
    @RequestMapping("/api/representations/{id}/offers")
    @Tag(name = "Representations", description = "Endpoints for linking representations to "
            + "offered resources")
    public static class RepresentationsToOfferedResources extends BaseResourceChildController<
            RelationServices.RepresentationOfferedResourceLinker, OfferedResource,
            OfferedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between representations and requested
     * resources.
     */
    @RestController
    @RequestMapping("/api/representations/{id}/requests")
    @Tag(name = "Representations", description = "Endpoints for linking representations to "
            + "requested resources")
    public static class RepresentationsToRequestedResources extends BaseResourceChildController<
            RelationServices.RepresentationOfferedResourceLinker, RequestedResource,
            RequestedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and catalogs.
     */
    @RestController
    @RequestMapping("/api/offers/{id}/catalogs")
    @Tag(name = "Resources", description = "Endpoints for linking offered resources to catalogs")
    public static class OfferedResourcesToCatalogs extends BaseResourceChildController<
            RelationServices.OfferedResourceCatalogLinker, Catalog, CatalogView> {
    }

    /**
     * Offers the endpoints for managing the relations between requested resources and catalogs.
     */
    @RestController
    @RequestMapping("/api/requests/{id}/catalogs")
    @Tag(name = "Resources", description = "Endpoints for linking requested resources to catalogs")
    public static class RequestedResourcesToCatalogs extends BaseResourceChildController<
            RelationServices.RequestedResourceCatalogLinker, Catalog, CatalogView> {
    }

    /**
     * Offers the endpoints for managing the relations between contracts and offered resources.
     */
    @RestController
    @RequestMapping("/api/contracts/{id}/offers")
    @Tag(name = "Contracts", description = "Endpoints for linking contracts to offers")
    public static class ContractsToOfferedResources extends BaseResourceChildController<
            RelationServices.ContractOfferedResourceLinker, OfferedResource,
            OfferedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between contracts and requested resources.
     */
    @RestController
    @RequestMapping("/api/contracts/{id}/requests")
    @Tag(name = "Contracts", description = "Endpoints for linking contracts to requests")
    public static class ContractsToRequestedResources extends BaseResourceChildController<
            RelationServices.ContractRequestedResourceLinker, RequestedResource,
            RequestedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between artifacts and agreements.
     */
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

    /**
     * Offers the endpoints for managing the relations between agreements and artifacts.
     */
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

    /**
     * Offers the endpoints for managing the relations between representations and artifacts.
     */
    @RestController
    @RequestMapping("/api/representations/{id}/artifacts")
    @Tag(name = "Representations", description = "Endpoints for linking artifacts to "
            + "representations")
    public static class RepresentationsToArtifacts
            extends BaseResourceChildController<RelationServices.RepresentationArtifactLinker,
            Artifact, ArtifactView> {
    }

    /**
     * Offers the endpoints for managing the relations between contracts and rules.
     */
    @RestController
    @RequestMapping("/api/contracts/{id}/rules")
    @Tag(name = "Contracts", description = "Endpoints for linking rules to contracts")
    public static class ContractsToRules extends BaseResourceChildController<
            RelationServices.ContractRuleLinker, ContractRule, ContractRuleView> {
    }

    /**
     * Offers the endpoints for managing the relations between catalogs and offered resources.
     */
    @RestController
    @RequestMapping("/api/catalogs/{id}/offers")
    @Tag(name = "Catalogs", description = "Endpoints for linking offered resources to catalogs")
    public static class CatalogsToOfferedResources extends BaseResourceChildController<
            AbstractCatalogResourceLinker<OfferedResource>, OfferedResource, OfferedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and contracts.
     */
    @RestController
    @RequestMapping("/api/offers/{id}/contracts")
    @Tag(name = "Resources", description = "Endpoints for linking contracts to resources")
    public static class OfferedResourcesToContracts
            extends BaseResourceChildController<AbstractResourceContractLinker<OfferedResource>,
            Contract, ContractView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and
     * representations.
     */
    @RestController
    @RequestMapping("/api/offers/{id}/representations")
    @Tag(name = "Resources", description = "Endpoints for linking representations to resources")
    public static class OfferedResourcesToRepresentations
            extends BaseResourceChildController<AbstractResourceRepresentationLinker<
            OfferedResource>, Representation, RepresentationView> {
    }

    /**
     * Offers the endpoints for managing the relations between requested resources and contracts.
     */
    @RestController
    @RequestMapping("/api/requests/{id}/contracts")
    @Tag(name = "Resources", description = "Endpoints for linking contracts to resources")
    public static class RequestedResourcesToContracts
            extends BaseResourceChildController<AbstractResourceContractLinker<RequestedResource>,
            Contract, ContractView> {
    }

    /**
     * Offers the endpoints for managing the relations between requested resources and
     * representations.
     */
    @RestController
    @RequestMapping("/api/requests/{id}/representations")
    @Tag(name = "Resources", description = "Endpoints for linking representations to resources")
    public static class RequestedResourcesToRepresentations
            extends BaseResourceChildController<AbstractResourceRepresentationLinker<
            RequestedResource>, Representation, RepresentationView> {
    }
}
