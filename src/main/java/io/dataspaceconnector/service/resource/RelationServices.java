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
package io.dataspaceconnector.service.resource;

import io.dataspaceconnector.model.subscription.Subscription;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.service.configuration.BrokerService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class contains all implementations of {@link OwningRelationService} and
 * {@link NonOwningRelationService}.
 */
public final class RelationServices {

    /**
     * Handles the relation between rules and contracts.
     */
    @Service
    @NoArgsConstructor
    public static class RuleContractLinker extends NonOwningRelationService<ContractRule, Contract,
            RuleService, ContractService> {

        @Override
        protected final List<Contract> getInternal(final ContractRule owner) {
            return owner.getContracts();
        }
    }

    /**
     * Handles the relation between artifacts and representations.
     */
    @Service
    @NoArgsConstructor
    public static class ArtifactRepresentationLinker
            extends NonOwningRelationService<Artifact, Representation, ArtifactService,
            RepresentationService> {

        @Override
        protected final List<Representation> getInternal(final Artifact owner) {
            return owner.getRepresentations();
        }
    }

    /**
     * Handles the relation between artifacts and subscriptions.
     */
    @Service
    @NoArgsConstructor
    public static class ArtifactSubscriptionLinker
            extends OwningRelationService<Artifact, Subscription, ArtifactService,
            SubscriptionService> {

        @Override
        protected final List<Subscription> getInternal(final Artifact owner) {
            return owner.getSubscriptions();
        }
    }

    /**
     * Handles the relation between representations and offered resources.
     */
    @Service
    @NoArgsConstructor
    public static class RepresentationOfferedResourceLinker
            extends NonOwningRelationService<Representation, OfferedResource,
            RepresentationService, OfferedResourceService> {

        @Override
        @SuppressWarnings("unchecked")
        protected final List<OfferedResource> getInternal(final Representation owner) {
            return (List<OfferedResource>) (List<?>) owner.getResources();
        }
    }

    /**
     * Handles the relation between representations and requested resources.
     */
    @Service
    @NoArgsConstructor
    public static class RepresentationRequestedResourceLinker
            extends NonOwningRelationService<Representation, RequestedResource,
            RepresentationService, RequestedResourceService> {

        @Override
        @SuppressWarnings("unchecked")
        protected final List<RequestedResource> getInternal(final Representation owner) {
            return (List<RequestedResource>) (List<?>) owner.getResources();
        }
    }

    /**
     * Handles the relation between representations and subscriptions.
     */
    @Service
    @NoArgsConstructor
    public static class RepresentationSubscriptionLinker
            extends OwningRelationService<Representation, Subscription,
            RepresentationService, SubscriptionService> {

        @Override
        protected final List<Subscription> getInternal(final Representation owner) {
            return owner.getSubscriptions();
        }
    }

    /**
     * Handles the relation between offered resources and catalogs.
     */
    @Service
    @NoArgsConstructor
    public static class OfferedResourceCatalogLinker
            extends NonOwningRelationService<OfferedResource, Catalog, OfferedResourceService,
            CatalogService> {

        @Override
        protected final List<Catalog> getInternal(final OfferedResource owner) {
            return owner.getCatalogs();
        }
    }

    /**
     * Handles the relation between offered resources and brokers.
     */
    @Service
    @NoArgsConstructor
    public static class OfferedResourceBrokerLinker
            extends OwningRelationService<OfferedResource, Broker, OfferedResourceService,
            BrokerService> {

        @Override
        protected final List<Broker> getInternal(final OfferedResource owner) {
            return owner.getBrokers();
        }
    }

    /**
     * Handles the relation between requested resources and catalogs.
     */
    @Service
    @NoArgsConstructor
    public static class RequestedResourceCatalogLinker
            extends NonOwningRelationService<RequestedResource, Catalog, RequestedResourceService,
            CatalogService> {

        @Override
        protected final List<Catalog> getInternal(final RequestedResource owner) {
            return owner.getCatalogs();
        }
    }

    /**
     * Handles the relation between contracts and offered resources.
     */
    @Service
    @NoArgsConstructor
    public static class ContractOfferedResourceLinker
            extends NonOwningRelationService<Contract, OfferedResource, ContractService,
            OfferedResourceService> {

        @Override
        @SuppressWarnings("unchecked")
        protected final List<OfferedResource> getInternal(final Contract owner) {
            return (List<OfferedResource>) (List<?>) owner.getResources();
        }
    }

    /**
     * Handles the relation between contracts and requested resources.
     */
    @Service
    @NoArgsConstructor
    public static class ContractRequestedResourceLinker
            extends NonOwningRelationService<Contract, RequestedResource, ContractService,
            RequestedResourceService> {

        @Override
        @SuppressWarnings("unchecked")
        protected final List<RequestedResource> getInternal(final Contract owner) {
            return (List<RequestedResource>) (List<?>) owner.getResources();
        }
    }

    /**
     * Handles the relation between agreements and artifacts.
     */
    @Service
    @NoArgsConstructor
    public static class AgreementArtifactLinker
            extends OwningRelationService<Agreement, Artifact, AgreementService, ArtifactService> {

        @Override
        protected final List<Artifact> getInternal(final Agreement owner) {
            return owner.getArtifacts();
        }
    }

    /**
     * Handles the relation between artifacts and agreements.
     */
    @Service
    @NoArgsConstructor
    public static class ArtifactAgreementLinker
            extends NonOwningRelationService<Artifact, Agreement, ArtifactService,
            AgreementService> {

        @Override
        protected final List<Agreement> getInternal(final Artifact owner) {
            return owner.getAgreements();
        }
    }

    /**
     * Handles the relation between representations and artifacts.
     */
    @Service
    @NoArgsConstructor
    public static class RepresentationArtifactLinker extends OwningRelationService<Representation,
            Artifact, RepresentationService, ArtifactService> {
        /**
         * Get the list of artifacts owned by the representation.
         *
         * @param owner The owner of the artifacts.
         * @return The list of owned artifacts.
         */
        @Override
        protected List<Artifact> getInternal(final Representation owner) {
            return owner.getArtifacts();
        }
    }

    /**
     * Handles the relation between contracts and rules.
     */
    @Service
    @NoArgsConstructor
    public static class ContractRuleLinker extends OwningRelationService<Contract, ContractRule,
            ContractService, RuleService> {
        /**
         * Get the list of rules owned by the contract.
         *
         * @param owner The owner of the rules.
         * @return The list of owned rules.
         */
        @Override
        protected List<ContractRule> getInternal(final Contract owner) {
            return owner.getRules();
        }
    }

    /**
     * Handles the relation between requested resources and subscriptions.
     */
    @Service
    @NoArgsConstructor
    public static class RequestedResourceSubscriptionLinker
            extends OwningRelationService<RequestedResource, Subscription,
            RequestedResourceService, SubscriptionService> {

        /**
         * Returns the list of subscriptions owned by a given requested resource.
         *
         * @param owner the requested resource whose subscriptions should be received.
         * @return the list of owned subscriptions.
         */
        @Override
        protected List<Subscription> getInternal(final RequestedResource owner) {
            return owner.getSubscriptions();
        }
    }

    /**
     * Handles the relation between offered resources and subscriptions.
     */
    @Service
    @NoArgsConstructor
    public static class OfferedResourceSubscriptionLinker
            extends OwningRelationService<OfferedResource, Subscription,
            OfferedResourceService, SubscriptionService> {

        /**
         * Returns the list of subscriptions owned by a given offered resource.
         *
         * @param owner the offered resource whose subscriptions should be received.
         * @return the list of owned subscriptions.
         */
        @Override
        protected List<Subscription> getInternal(final OfferedResource owner) {
            return owner.getSubscriptions();
        }
    }
}
