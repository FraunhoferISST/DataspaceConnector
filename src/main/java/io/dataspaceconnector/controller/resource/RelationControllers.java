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
package io.dataspaceconnector.controller.resource;

import io.dataspaceconnector.controller.resource.exception.MethodNotAllowed;
import io.dataspaceconnector.controller.resource.swagger.response.ResponseCode;
import io.dataspaceconnector.controller.resource.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.AgreementView;
import io.dataspaceconnector.controller.resource.view.ArtifactView;
import io.dataspaceconnector.controller.resource.view.CatalogView;
import io.dataspaceconnector.controller.resource.view.ContractRuleView;
import io.dataspaceconnector.controller.resource.view.ContractView;
import io.dataspaceconnector.controller.resource.view.OfferedResourceView;
import io.dataspaceconnector.controller.resource.view.RepresentationView;
import io.dataspaceconnector.controller.resource.view.RequestedResourceView;
import io.dataspaceconnector.controller.resource.view.SubscriptionView;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.service.resource.AbstractCatalogResourceLinker;
import io.dataspaceconnector.service.resource.AbstractResourceContractLinker;
import io.dataspaceconnector.service.resource.AbstractResourceRepresentationLinker;
import io.dataspaceconnector.service.resource.RelationServices;
import io.dataspaceconnector.view.broker.BrokerView;
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

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * This class contains all implementations of the {@link BaseResourceChildController}.
 */
public final class RelationControllers {

    /**
     * Offers the endpoints for managing the relations between rules and contracts.
     */
    @RestController
    @RequestMapping("/api/rules/{id}/contracts")
    @Tag(name = ResourceName.RULES, description = ResourceDescription.RULES)
    public static class RulesToContracts extends BaseResourceChildController<
            RelationServices.RuleContractLinker, Contract, ContractView> {
    }

    /**
     * Offers the endpoints for managing the relations between artifacts and representations.
     */
    @RestController
    @RequestMapping("/api/artifacts/{id}/representations")
    @Tag(name = ResourceName.ARTIFACTS, description = ResourceDescription.ARTIFACTS)
    public static class ArtifactsToRepresentations extends BaseResourceChildController<
            RelationServices.ArtifactRepresentationLinker, Representation, RepresentationView> {
    }

    /**
     * Offers the endpoints for managing the relations between artifacts and subscriptions.
     */
    @RestController
    @RequestMapping("/api/artifacts/{id}/subscriptions")
    @Tag(name = ResourceName.ARTIFACTS, description = ResourceDescription.ARTIFACTS)
    public static class ArtifactsToSubscriptions extends BaseResourceChildController<
            RelationServices.ArtifactSubscriptionLinker, Subscription, SubscriptionView> {
    }

    /**
     * Offers the endpoints for managing the relations between representations and offered
     * resources.
     */
    @RestController
    @RequestMapping("/api/representations/{id}/offers")
    @Tag(name = ResourceName.REPRESENTATIONS, description = ResourceDescription.REPRESENTATIONS)
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
    @Tag(name = ResourceName.REPRESENTATIONS, description = ResourceDescription.REPRESENTATIONS)
    public static class RepresentationsToRequestedResources extends BaseResourceChildController<
            RelationServices.RepresentationOfferedResourceLinker, RequestedResource,
            RequestedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between representations and subscriptions.
     */
    @RestController
    @RequestMapping("/api/representations/{id}/subscriptions")
    @Tag(name = ResourceName.REPRESENTATIONS, description = ResourceDescription.REPRESENTATIONS)
    public static class RepresentationsToSubscriptions extends BaseResourceChildController<
            RelationServices.RepresentationSubscriptionLinker, Subscription, SubscriptionView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and catalogs.
     */
    @RestController
    @RequestMapping("/api/offers/{id}/catalogs")
    @Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
    public static class OfferedResourcesToCatalogs extends BaseResourceChildController<
            RelationServices.OfferedResourceCatalogLinker, Catalog, CatalogView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and brokers.
     */
    @RestController
    @RequestMapping("/api/offers/{id}/brokers")
    @Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
    public static class OfferedResourcesToBrokers extends BaseResourceChildController<
            RelationServices.OfferedResourceBrokerLinker, Broker, BrokerView> {
    }

    /**
     * Offers the endpoints for managing the relations between requested resources and catalogs.
     */
    @RestController
    @RequestMapping("/api/requests/{id}/catalogs")
    @Tag(name = ResourceName.REQUESTS, description = ResourceDescription.REQUESTS)
    public static class RequestedResourcesToCatalogs extends BaseResourceChildController<
            RelationServices.RequestedResourceCatalogLinker, Catalog, CatalogView> {
    }

    /**
     * Offers the endpoints for managing the relations between contracts and offered resources.
     */
    @RestController
    @RequestMapping("/api/contracts/{id}/offers")
    @Tag(name = ResourceName.CONTRACTS, description = ResourceDescription.CONTRACTS)
    public static class ContractsToOfferedResources extends BaseResourceChildController<
            RelationServices.ContractOfferedResourceLinker, OfferedResource, OfferedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between contracts and requested resources.
     */
    @RestController
    @RequestMapping("/api/contracts/{id}/requests")
    @Tag(name = ResourceName.CONTRACTS, description = ResourceDescription.CONTRACTS)
    public static class ContractsToRequestedResources extends BaseResourceChildController<
            RelationServices.ContractRequestedResourceLinker, RequestedResource,
            RequestedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between artifacts and agreements.
     */
    @RestController
    @RequestMapping("/api/artifacts/{id}/agreements")
    @Tag(name = ResourceName.ARTIFACTS, description = ResourceDescription.ARTIFACTS)
    public static class ArtifactsToAgreements extends BaseResourceChildController<
            RelationServices.ArtifactAgreementLinker, Agreement, AgreementView> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                description = "Not allowed")})
        public final PagedModel<AgreementView> addResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                description = "No content")})
        public final HttpEntity<Void> replaceResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                description = "No content")})
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
    @Tag(name = ResourceName.AGREEMENTS, description = ResourceDescription.AGREEMENTS)
    public static class AgreementsToArtifacts extends BaseResourceChildController<
            RelationServices.AgreementArtifactLinker, Artifact, ArtifactView> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                description = "Not allowed")})
        public final PagedModel<ArtifactView> addResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                description = "No content")})
        public final HttpEntity<Void> replaceResources(
                @Valid @PathVariable(name = "id") final UUID ownerId,
                @Valid @RequestBody final List<URI> resources) {
            throw new MethodNotAllowed();
        }

        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = ResponseCode.METHOD_NOT_ALLOWED,
                description = "No content")})
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
    @Tag(name = ResourceName.REPRESENTATIONS, description = ResourceDescription.REPRESENTATIONS)
    public static class RepresentationsToArtifacts
            extends BaseResourceChildController<RelationServices.RepresentationArtifactLinker,
            Artifact, ArtifactView> {
    }

    /**
     * Offers the endpoints for managing the relations between contracts and rules.
     */
    @RestController
    @RequestMapping("/api/contracts/{id}/rules")
    @Tag(name = ResourceName.CONTRACTS, description = ResourceDescription.CONTRACTS)
    public static class ContractsToRules extends BaseResourceChildController<
            RelationServices.ContractRuleLinker, ContractRule, ContractRuleView> {
    }

    /**
     * Offers the endpoints for managing the relations between catalogs and offered resources.
     */
    @RestController
    @RequestMapping("/api/catalogs/{id}/offers")
    @Tag(name = ResourceName.CATALOGS, description = ResourceDescription.CATALOGS)
    public static class CatalogsToOfferedResources extends BaseResourceChildController<
            AbstractCatalogResourceLinker<OfferedResource>, OfferedResource, OfferedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and contracts.
     */
    @RestController
    @RequestMapping("/api/offers/{id}/contracts")
    @Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
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
    @Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
    public static class OfferedResourcesToRepresentations
            extends BaseResourceChildController<AbstractResourceRepresentationLinker<
            OfferedResource>, Representation, RepresentationView> {
    }

    /**
     * Offers the endpoints for managing the relations between requested resources and contracts.
     */
    @RestController
    @RequestMapping("/api/requests/{id}/contracts")
    @Tag(name = ResourceName.REQUESTS, description = ResourceDescription.REQUESTS)
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
    @Tag(name = ResourceName.REQUESTS, description = ResourceDescription.REQUESTS)
    public static class RequestedResourcesToRepresentations
            extends BaseResourceChildController<AbstractResourceRepresentationLinker<
            RequestedResource>, Representation, RepresentationView> {
    }

    /**
     * Offers the endpoints for managing relations between requested resources and subscriptions.
     */
    @RestController
    @RequestMapping("/api/requests/{id}/subscriptions")
    @Tag(name = ResourceName.REQUESTS, description = ResourceDescription.REQUESTS)
    public static class RequestedResourcesToSubscriptions extends BaseResourceChildController<
            RelationServices.RequestedResourceSubscriptionLinker, Subscription, SubscriptionView> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final PagedModel<SubscriptionView> addResources(
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
     * Offers the endpoints for managing the relations between offered resources and subscriptions.
     */
    @RestController
    @RequestMapping("/api/offers/{id}/subscriptions")
    @Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
    public static class OfferedResourcesToSubscriptions
            extends BaseResourceChildController<RelationServices.OfferedResourceSubscriptionLinker,
            Subscription, SubscriptionView> {
        @Override
        @Hidden
        @ApiResponses(value = {@ApiResponse(responseCode = "405", description = "Not allowed")})
        public final PagedModel<SubscriptionView> addResources(
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
}
