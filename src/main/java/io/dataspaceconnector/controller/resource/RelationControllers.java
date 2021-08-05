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

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.config.BaseType;
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
import io.dataspaceconnector.service.resource.relation.AbstractCatalogResourceLinker;
import io.dataspaceconnector.service.resource.relation.AbstractResourceContractLinker;
import io.dataspaceconnector.service.resource.relation.AbstractResourceRepresentationLinker;
import io.dataspaceconnector.service.resource.relation.AgreementArtifactLinker;
import io.dataspaceconnector.service.resource.relation.ArtifactAgreementLinker;
import io.dataspaceconnector.service.resource.relation.ArtifactRepresentationLinker;
import io.dataspaceconnector.service.resource.relation.ArtifactSubscriptionLinker;
import io.dataspaceconnector.service.resource.relation.BrokerOfferedResourceLinker;
import io.dataspaceconnector.service.resource.relation.ContractOfferedResourceLinker;
import io.dataspaceconnector.service.resource.relation.ContractRequestedResourceLinker;
import io.dataspaceconnector.service.resource.relation.ContractRuleLinker;
import io.dataspaceconnector.service.resource.relation.OfferedResourceBrokerLinker;
import io.dataspaceconnector.service.resource.relation.OfferedResourceCatalogLinker;
import io.dataspaceconnector.service.resource.relation.OfferedResourceSubscriptionLinker;
import io.dataspaceconnector.service.resource.relation.RepresentationArtifactLinker;
import io.dataspaceconnector.service.resource.relation.RepresentationOfferedResourceLinker;
import io.dataspaceconnector.service.resource.relation.RepresentationSubscriptionLinker;
import io.dataspaceconnector.service.resource.relation.RequestedResourceCatalogLinker;
import io.dataspaceconnector.service.resource.relation.RequestedResourceSubscriptionLinker;
import io.dataspaceconnector.service.resource.relation.RouteArtifactLinker;
import io.dataspaceconnector.service.resource.relation.RouteStepLinker;
import io.dataspaceconnector.service.resource.relation.RuleContractLinker;
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
    @RequestMapping(BasePath.RULES + "/{id}/" + BaseType.CONTRACTS)
    @Tag(name = ResourceName.RULES, description = ResourceDescription.RULES)
    public static class RulesToContracts extends BaseResourceChildController<
            RuleContractLinker, Contract, ContractView> {
    }

    /**
     * Offers the endpoints for managing the relations between artifacts and representations.
     */
    @RestController
    @RequestMapping(BasePath.ARTIFACTS + "/{id}/" + BaseType.REPRESENTATIONS)
    @Tag(name = ResourceName.ARTIFACTS, description = ResourceDescription.ARTIFACTS)
    public static class ArtifactsToRepresentations extends BaseResourceChildController<
            ArtifactRepresentationLinker, Representation, RepresentationView> {
    }

    /**
     * Offers the endpoints for managing the relations between artifacts and subscriptions.
     */
    @RestController
    @RequestMapping(BasePath.ARTIFACTS + "/{id}/" + BaseType.SUBSCRIPTIONS)
    @Tag(name = ResourceName.ARTIFACTS, description = ResourceDescription.ARTIFACTS)
    public static class ArtifactsToSubscriptions extends BaseResourceChildRestrictedController<
            ArtifactSubscriptionLinker, Subscription, SubscriptionView> {
    }

    /**
     * Offers the endpoints for managing the relations between representations and offered
     * resources.
     */
    @RestController
    @RequestMapping(BasePath.REPRESENTATIONS + "/{id}/" + BaseType.OFFERS)
    @Tag(name = ResourceName.REPRESENTATIONS, description = ResourceDescription.REPRESENTATIONS)
    public static class RepresentationsToOfferedResources extends BaseResourceChildController<
            RepresentationOfferedResourceLinker, OfferedResource,
            OfferedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between representations and requested
     * resources.
     */
    @RestController
    @RequestMapping(BasePath.REPRESENTATIONS + "/{id}/" + BaseType.REQUESTS)
    @Tag(name = ResourceName.REPRESENTATIONS, description = ResourceDescription.REPRESENTATIONS)
    public static class RepresentationsToRequestedResources extends BaseResourceChildController<
            RepresentationOfferedResourceLinker, RequestedResource,
            RequestedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between representations and subscriptions.
     */
    @RestController
    @RequestMapping(BasePath.REPRESENTATIONS + "/{id}/" + BaseType.SUBSCRIPTIONS)
    @Tag(name = ResourceName.REPRESENTATIONS, description = ResourceDescription.REPRESENTATIONS)
    public static class RepresentationsToSubscriptions
            extends BaseResourceChildRestrictedController<
            RepresentationSubscriptionLinker, Subscription, SubscriptionView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and catalogs.
     */
    @RestController
    @RequestMapping(BasePath.OFFERS + "/{id}/" + BaseType.CATALOGS)
    @Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
    public static class OfferedResourcesToCatalogs extends BaseResourceChildController<
            OfferedResourceCatalogLinker, Catalog, CatalogView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and brokers.
     */
    @RestController
    @RequestMapping(BasePath.OFFERS + "/{id}/" + BaseType.BROKERS)
    @Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
    public static class OfferedResourcesToBrokers extends BaseResourceChildRestrictedController<
            OfferedResourceBrokerLinker, Broker, BrokerView> {
    }

    /**
     * Offers the endpoints for managing the relations between requested resources and catalogs.
     */
    @RestController
    @RequestMapping(BasePath.REQUESTS + "/{id}/" + BaseType.CATALOGS)
    @Tag(name = ResourceName.REQUESTS, description = ResourceDescription.REQUESTS)
    public static class RequestedResourcesToCatalogs extends BaseResourceChildController<
            RequestedResourceCatalogLinker, Catalog, CatalogView> {
    }

    /**
     * Offers the endpoints for managing the relations between contracts and offered resources.
     */
    @RestController
    @RequestMapping(BasePath.CONTRACTS + "/{id}/" + BaseType.OFFERS)
    @Tag(name = ResourceName.CONTRACTS, description = ResourceDescription.CONTRACTS)
    public static class ContractsToOfferedResources extends BaseResourceChildController<
            ContractOfferedResourceLinker, OfferedResource, OfferedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between contracts and requested resources.
     */
    @RestController
    @RequestMapping(BasePath.CONTRACTS + "/{id}/" + BaseType.REQUESTS)
    @Tag(name = ResourceName.CONTRACTS, description = ResourceDescription.CONTRACTS)
    public static class ContractsToRequestedResources extends BaseResourceChildController<
            ContractRequestedResourceLinker, RequestedResource,
            RequestedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between artifacts and agreements.
     */
    @RestController
    @RequestMapping(BasePath.ARTIFACTS + "/{id}/" + BaseType.AGREEMENTS)
    @Tag(name = ResourceName.ARTIFACTS, description = ResourceDescription.ARTIFACTS)
    public static class ArtifactsToAgreements extends BaseResourceChildRestrictedController<
            ArtifactAgreementLinker, Agreement, AgreementView> {
    }

    /**
     * Offers the endpoints for managing the relations between agreements and artifacts.
     */
    @RestController
    @RequestMapping(BasePath.AGREEMENTS + "/{id}/" + BaseType.ARTIFACTS)
    @Tag(name = ResourceName.AGREEMENTS, description = ResourceDescription.AGREEMENTS)
    public static class AgreementsToArtifacts extends BaseResourceChildRestrictedController<
            AgreementArtifactLinker, Artifact, ArtifactView> {
    }

    /**
     * Offers the endpoints for managing the relations between representations and artifacts.
     */
    @RestController
    @RequestMapping(BasePath.REPRESENTATIONS + "/{id}/" + BaseType.ARTIFACTS)
    @Tag(name = ResourceName.REPRESENTATIONS, description = ResourceDescription.REPRESENTATIONS)
    public static class RepresentationsToArtifacts extends BaseResourceChildController<
            RepresentationArtifactLinker, Artifact, ArtifactView> {
    }

    /**
     * Offers the endpoints for managing the relations between contracts and rules.
     */
    @RestController
    @RequestMapping(BasePath.CONTRACTS + "/{id}/" + BaseType.RULES)
    @Tag(name = ResourceName.CONTRACTS, description = ResourceDescription.CONTRACTS)
    public static class ContractsToRules extends BaseResourceChildController<
            ContractRuleLinker, ContractRule, ContractRuleView> {
    }

    /**
     * Offers the endpoints for managing the relations between catalogs and offered resources.
     */
    @RestController
    @RequestMapping(BasePath.CATALOGS + "/{id}/" + BaseType.OFFERS)
    @Tag(name = ResourceName.CATALOGS, description = ResourceDescription.CATALOGS)
    public static class CatalogsToOfferedResources extends BaseResourceChildController<
            AbstractCatalogResourceLinker<OfferedResource>, OfferedResource, OfferedResourceView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and contracts.
     */
    @RestController
    @RequestMapping(BasePath.OFFERS + "/{id}/" + BaseType.CONTRACTS)
    @Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
    public static class OfferedResourcesToContracts extends BaseResourceChildController<
            AbstractResourceContractLinker<OfferedResource>, Contract, ContractView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and
     * representations.
     */
    @RestController
    @RequestMapping(BasePath.OFFERS + "/{id}/" + BaseType.REPRESENTATIONS)
    @Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
    public static class OfferedResourcesToRepresentations
            extends BaseResourceChildController<AbstractResourceRepresentationLinker<
            OfferedResource>, Representation, RepresentationView> {
    }

    /**
     * Offers the endpoints for managing the relations between requested resources and contracts.
     */
    @RestController
    @RequestMapping(BasePath.REQUESTS + "/{id}/" + BaseType.CONTRACTS)
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
    @RequestMapping(BasePath.REQUESTS + "/{id}/" + BaseType.REPRESENTATIONS)
    @Tag(name = ResourceName.REQUESTS, description = ResourceDescription.REQUESTS)
    public static class RequestedResourcesToRepresentations
            extends BaseResourceChildController<AbstractResourceRepresentationLinker<
            RequestedResource>, Representation, RepresentationView> {
    }

    /**
     * Offers the endpoints for managing relations between requested resources and subscriptions.
     */
    @RestController
    @RequestMapping(BasePath.REQUESTS + "/{id}/" + BaseType.SUBSCRIPTIONS)
    @Tag(name = ResourceName.REQUESTS, description = ResourceDescription.REQUESTS)
    public static class RequestedResourcesToSubscriptions
            extends BaseResourceChildRestrictedController<
            RequestedResourceSubscriptionLinker, Subscription, SubscriptionView> {
    }

    /**
     * Offers the endpoints for managing the relations between offered resources and subscriptions.
     */
    @RestController
    @RequestMapping(BasePath.OFFERS + "/{id}/" + BaseType.SUBSCRIPTIONS)
    @Tag(name = ResourceName.OFFERS, description = ResourceDescription.OFFERS)
    public static class OfferedResourcesToSubscriptions
            extends BaseResourceChildRestrictedController<
            OfferedResourceSubscriptionLinker, Subscription, SubscriptionView> {
    }

    /**
     * Offers the endpoints for managing the relations between broker and offered resources.
     */
    @RestController
    @RequestMapping(BasePath.BROKERS + "/{id}/" + BaseType.OFFERS)
    @Tag(name = ResourceName.BROKERS, description = ResourceDescription.BROKERS)
    public static class BrokersToOfferedResources extends
            BaseResourceChildRestrictedController<BrokerOfferedResourceLinker,
                    OfferedResource, OfferedResourceView> {
    }

    /**
     * Offers the endpoints for managing steps.
     */
    @RestController
    @RequestMapping(BasePath.ROUTES + "/{id}/steps")
    @Tag(name = ResourceName.ROUTES, description = ResourceDescription.ROUTES)
    public static class RoutesToSteps extends BaseResourceChildController<
            RouteStepLinker, Route, RouteView> {
    }

    /**
     * Offers the endpoint for managing route artifacts.
     */
    @RestController
    @RequestMapping(BasePath.ROUTES + "/{id}/outputs")
    @Tag(name = ResourceName.ROUTES, description = ResourceDescription.ROUTES)
    public static class RoutesToArtifacts extends BaseResourceChildController<
            RouteArtifactLinker, Artifact, ArtifactView> {
    }
}
