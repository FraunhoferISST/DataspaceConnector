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

import io.dataspaceconnector.controller.resource.base.BaseResourceChildController;
import io.dataspaceconnector.controller.resource.base.BaseResourceChildRestrictedController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.agreement.AgreementView;
import io.dataspaceconnector.controller.resource.view.artifact.ArtifactView;
import io.dataspaceconnector.controller.resource.view.broker.BrokerView;
import io.dataspaceconnector.controller.resource.view.catalog.CatalogView;
import io.dataspaceconnector.controller.resource.view.contract.ContractView;
import io.dataspaceconnector.controller.resource.view.representation.RepresentationView;
import io.dataspaceconnector.controller.resource.view.resource.OfferedResourceView;
import io.dataspaceconnector.controller.resource.view.resource.RequestedResourceView;
import io.dataspaceconnector.controller.resource.view.route.RouteView;
import io.dataspaceconnector.controller.resource.view.rule.ContractRuleView;
import io.dataspaceconnector.controller.resource.view.subscription.SubscriptionView;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.service.resource.AbstractCatalogResourceLinker;
import io.dataspaceconnector.service.resource.AbstractResourceContractLinker;
import io.dataspaceconnector.service.resource.AbstractResourceRepresentationLinker;
import io.dataspaceconnector.service.resource.RelationServices;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public static class ArtifactsToSubscriptions extends BaseResourceChildRestrictedController<
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
    public static class RepresentationsToSubscriptions
            extends BaseResourceChildRestrictedController<
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
    public static class OfferedResourcesToBrokers extends BaseResourceChildRestrictedController<
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
    public static class ArtifactsToAgreements extends BaseResourceChildRestrictedController<
            RelationServices.ArtifactAgreementLinker, Agreement, AgreementView> {
    }

    /**
     * Offers the endpoints for managing the relations between agreements and artifacts.
     */
    @RestController
    @RequestMapping("/api/agreements/{id}/artifacts")
    @Tag(name = ResourceName.AGREEMENTS, description = ResourceDescription.AGREEMENTS)
    public static class AgreementsToArtifacts extends BaseResourceChildRestrictedController<
            RelationServices.AgreementArtifactLinker, Artifact, ArtifactView> {
    }

    /**
     * Offers the endpoints for managing the relations between representations and artifacts.
     */
    @RestController
    @RequestMapping("/api/representations/{id}/artifacts")
    @Tag(name = ResourceName.REPRESENTATIONS, description = ResourceDescription.REPRESENTATIONS)
    public static class RepresentationsToArtifacts extends BaseResourceChildController<
            RelationServices.RepresentationArtifactLinker, Artifact, ArtifactView> {
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
    public static class OfferedResourcesToContracts extends BaseResourceChildController<
            AbstractResourceContractLinker<OfferedResource>, Contract, ContractView> {
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
    public static class RequestedResourcesToSubscriptions
            extends BaseResourceChildRestrictedController<
            RelationServices.RequestedResourceSubscriptionLinker, Subscription, SubscriptionView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and subscriptions.
     */
    @RestController
    @RequestMapping("/api/offers/{id}/subscriptions")
    @Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
    public static class OfferedResourcesToSubscriptions
            extends BaseResourceChildRestrictedController<
            RelationServices.OfferedResourceSubscriptionLinker, Subscription, SubscriptionView> {
    }

    /**
     * Offers the endpoints for managing the relations between broker and offered resources.
     */
    @RestController
    @RequestMapping("/api/brokers/{id}/offers")
    @Tag(name = ResourceName.BROKERS, description = ResourceDescription.BROKERS)
    public static class BrokersToOfferedResources extends
            BaseResourceChildRestrictedController<RelationServices.BrokerOfferedResourceLinker,
                    OfferedResource, OfferedResourceView> {
    }

    /**
     * Offers the endpoints for managing steps.
     */
    @RestController
    @RequestMapping("/api/routes/{id}/steps")
    @Tag(name = ResourceName.ROUTES, description = ResourceDescription.ROUTES)
    public static class RoutesToSteps extends BaseResourceChildController<
            RelationServices.RouteStepLinker, Route, RouteView> {
    }

    /**
     * Offers the endpoint for managing route artifacts.
     */
    @RestController
    @RequestMapping("/api/routes/{id}/outputs")
    @Tag(name = ResourceName.ROUTES, description = ResourceDescription.ROUTES)
    public static class RoutesToArtifacts extends BaseResourceChildController<
            RelationServices.RouteArtifactLinker, Artifact, ArtifactView> {
    }
}
