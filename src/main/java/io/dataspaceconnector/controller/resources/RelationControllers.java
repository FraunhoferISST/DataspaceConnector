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

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import io.dataspaceconnector.controller.resources.exceptions.MethodNotAllowed;
import io.dataspaceconnector.controller.resources.tags.ResourceDescriptions;
import io.dataspaceconnector.controller.resources.tags.ResourceNames;
import io.dataspaceconnector.model.core.Agreement;
import io.dataspaceconnector.model.core.Artifact;
import io.dataspaceconnector.model.core.Catalog;
import io.dataspaceconnector.model.core.Contract;
import io.dataspaceconnector.model.core.ContractRule;
import io.dataspaceconnector.model.core.OfferedResource;
import io.dataspaceconnector.model.core.Representation;
import io.dataspaceconnector.model.core.RequestedResource;
import io.dataspaceconnector.resources.AbstractCatalogResourceLinker;
import io.dataspaceconnector.resources.AbstractResourceContractLinker;
import io.dataspaceconnector.resources.AbstractResourceRepresentationLinker;
import io.dataspaceconnector.resources.RelationServices;
import io.dataspaceconnector.controller.resources.view.AgreementView;
import io.dataspaceconnector.controller.resources.view.ArtifactView;
import io.dataspaceconnector.controller.resources.view.CatalogView;
import io.dataspaceconnector.controller.resources.view.ContractRuleView;
import io.dataspaceconnector.controller.resources.view.ContractView;
import io.dataspaceconnector.controller.resources.view.OfferedResourceView;
import io.dataspaceconnector.controller.resources.view.RepresentationView;
import io.dataspaceconnector.controller.resources.view.RequestedResourceView;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
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
    @Tag(name = ResourceNames.RULES, description = ResourceDescriptions.RULES)
    public static class RulesToContracts extends BaseResourceChildController<
            RelationServices.RuleContractLinker, Contract, ContractView> {
    }

    /**
     * Offers the endpoints for managing the relations between artifacts and representations.
     */
    @RestController
    @RequestMapping("/api/artifacts/{id}/representations")
    @Tag(name = ResourceNames.ARTIFACTS, description = ResourceDescriptions.ARTIFACTS)
    public static class ArtifactsToRepresentations extends BaseResourceChildController<
            RelationServices.ArtifactRepresentationLinker, Representation, RepresentationView> {
    }

    /**
     * Offers the endpoints for managing the relations between representations and offered
     * resources.
     */
    @RestController
    @RequestMapping("/api/representations/{id}/offers")
    @Tag(name = ResourceNames.REPRESENTATIONS, description = ResourceDescriptions.REPRESENTATIONS)
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
    @Tag(name = ResourceNames.REPRESENTATIONS, description = ResourceDescriptions.REPRESENTATIONS)
    public static class RepresentationsToRequestedResources extends BaseResourceChildController<
            RelationServices.RepresentationOfferedResourceLinker, RequestedResource,
            RequestedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and catalogs.
     */
    @RestController
    @RequestMapping("/api/offers/{id}/catalogs")
    @Tag(name = ResourceNames.OFFERS, description = ResourceDescriptions.OFFERS)
    public static class OfferedResourcesToCatalogs extends BaseResourceChildController<
            RelationServices.OfferedResourceCatalogLinker, Catalog, CatalogView> {
    }

    /**
     * Offers the endpoints for managing the relations between requested resources and catalogs.
     */
    @RestController
    @RequestMapping("/api/requests/{id}/catalogs")
    @Tag(name = ResourceNames.REQUESTS, description = ResourceDescriptions.REQUESTS)
    public static class RequestedResourcesToCatalogs extends BaseResourceChildController<
            RelationServices.RequestedResourceCatalogLinker, Catalog, CatalogView> {
    }

    /**
     * Offers the endpoints for managing the relations between contracts and offered resources.
     */
    @RestController
    @RequestMapping("/api/contracts/{id}/offers")
    @Tag(name = ResourceNames.CONTRACTS, description = ResourceDescriptions.CONTRACTS)
    public static class ContractsToOfferedResources extends BaseResourceChildController<
            RelationServices.ContractOfferedResourceLinker, OfferedResource,
            OfferedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between contracts and requested resources.
     */
    @RestController
    @RequestMapping("/api/contracts/{id}/requests")
    @Tag(name = ResourceNames.CONTRACTS, description = ResourceDescriptions.CONTRACTS)
    public static class ContractsToRequestedResources extends BaseResourceChildController<
            RelationServices.ContractRequestedResourceLinker, RequestedResource,
            RequestedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between artifacts and agreements.
     */
    @RestController
    @RequestMapping("/api/artifacts/{id}/agreements")
    @Tag(name = ResourceNames.ARTIFACTS, description = ResourceDescriptions.ARTIFACTS)
    public static class ArtifactsToAgreements extends BaseResourceChildController<
            RelationServices.ArtifactAgreementLinker, Agreement, AgreementView> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final PagedModel<AgreementView> addResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content")})
        public final HttpEntity<Void> replaceResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content")})
        public final HttpEntity<Void> removeResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            throw new MethodNotAllowed();
        }
    }

    /**
     * Offers the endpoints for managing the relations between agreements and artifacts.
     */
    @RestController
    @RequestMapping("/api/agreements/{id}/artifacts")
    @Tag(name = ResourceNames.AGREEMENTS, description = ResourceDescriptions.AGREEMENTS)
    public static class AgreementsToArtifacts extends BaseResourceChildController<
            RelationServices.AgreementArtifactLinker, Artifact, ArtifactView> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final PagedModel<ArtifactView> addResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content")})
        public final HttpEntity<Void> replaceResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "No content")})
        public final HttpEntity<Void> removeResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            throw new MethodNotAllowed();
        }
    }

    /**
     * Offers the endpoints for managing the relations between representations and artifacts.
     */
    @RestController
    @RequestMapping("/api/representations/{id}/artifacts")
    @Tag(name = ResourceNames.REPRESENTATIONS, description = ResourceDescriptions.REPRESENTATIONS)
    public static class RepresentationsToArtifacts
            extends BaseResourceChildController<RelationServices.RepresentationArtifactLinker,
            Artifact, ArtifactView> {
    }

    /**
     * Offers the endpoints for managing the relations between contracts and rules.
     */
    @RestController
    @RequestMapping("/api/contracts/{id}/rules")
    @Tag(name = ResourceNames.CONTRACTS, description = ResourceDescriptions.CONTRACTS)
    public static class ContractsToRules extends BaseResourceChildController<
            RelationServices.ContractRuleLinker, ContractRule, ContractRuleView> {
    }

    /**
     * Offers the endpoints for managing the relations between catalogs and offered resources.
     */
    @RestController
    @RequestMapping("/api/catalogs/{id}/offers")
    @Tag(name = ResourceNames.CATALOGS, description = ResourceDescriptions.CATALOGS)
    public static class CatalogsToOfferedResources extends BaseResourceChildController<
            AbstractCatalogResourceLinker<OfferedResource>, OfferedResource, OfferedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and contracts.
     */
    @RestController
    @RequestMapping("/api/offers/{id}/contracts")
    @Tag(name = ResourceNames.OFFERS, description = ResourceDescriptions.OFFERS)
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
    @Tag(name = ResourceNames.OFFERS, description = ResourceDescriptions.OFFERS)
    public static class OfferedResourcesToRepresentations
            extends BaseResourceChildController<AbstractResourceRepresentationLinker<
            OfferedResource>, Representation, RepresentationView> {
    }

    /**
     * Offers the endpoints for managing the relations between requested resources and contracts.
     */
    @RestController
    @RequestMapping("/api/requests/{id}/contracts")
    @Tag(name = ResourceNames.REQUESTS, description = ResourceDescriptions.REQUESTS)
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
    @Tag(name = ResourceNames.REQUESTS, description = ResourceDescriptions.REQUESTS)
    public static class RequestedResourcesToRepresentations
            extends BaseResourceChildController<AbstractResourceRepresentationLinker<
            RequestedResource>, Representation, RepresentationView> {
    }
}
