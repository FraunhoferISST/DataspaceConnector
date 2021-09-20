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
package io.dataspaceconnector.common.net;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.exception.UnreachableLineException;
import io.dataspaceconnector.controller.resource.view.agreement.AgreementViewAssembler;
import io.dataspaceconnector.controller.resource.view.artifact.ArtifactViewAssembler;
import io.dataspaceconnector.controller.resource.view.catalog.CatalogViewAssembler;
import io.dataspaceconnector.controller.resource.view.contract.ContractViewAssembler;
import io.dataspaceconnector.controller.resource.view.endpoint.ConnectorEndpointViewAssembler;
import io.dataspaceconnector.controller.resource.view.endpoint.GenericEndpointViewAssembler;
import io.dataspaceconnector.controller.resource.view.representation.RepresentationViewAssembler;
import io.dataspaceconnector.controller.resource.view.resource.OfferedResourceViewAssembler;
import io.dataspaceconnector.controller.resource.view.resource.RequestedResourceViewAssembler;
import io.dataspaceconnector.controller.resource.view.route.RouteViewAssembler;
import io.dataspaceconnector.controller.resource.view.rule.ContractRuleViewAssembler;
import io.dataspaceconnector.controller.resource.view.subscription.SubscriptionViewAssembler;
import io.dataspaceconnector.controller.resource.view.util.SelfLinking;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.endpoint.ConnectorEndpoint;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.model.subscription.Subscription;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * This is a helper class for retrieving self-links of a database entity.
 */
@Component
@RequiredArgsConstructor
public final class SelfLinkHelper {
    /**
     * View assembler for catalogs.
     */
    private final @NonNull CatalogViewAssembler catalogAssembler;

    /**
     * View assembler for offered resources.
     */
    private final @NonNull OfferedResourceViewAssembler offeredResourceAssembler;

    /**
     * View assembler for requested resources.
     */
    private final @NonNull RequestedResourceViewAssembler requestedResourceAssembler;

    /**
     * View assembler for representations.
     */
    private final @NonNull RepresentationViewAssembler representationAssembler;

    /**
     * View assembler for artifacts.
     */
    private final @NonNull ArtifactViewAssembler artifactAssembler;

    /**
     * View assembler for contracts.
     */
    private final @NonNull ContractViewAssembler contractAssembler;

    /**
     * View assembler for contract rules.
     */
    private final @NonNull ContractRuleViewAssembler ruleAssembler;

    /**
     * View assembler for contract agreements.
     */
    private final @NonNull AgreementViewAssembler agreementAssembler;

    /**
     * View assembler for generic endpoints.
     */
    private final @NonNull GenericEndpointViewAssembler genericEndpointAssembler;

    /**
     * View assembler for connector endpoints.
     */
    private final @NonNull ConnectorEndpointViewAssembler connectorEndpointAssembler;

    /**
     * View assembler for routes.
     */
    private final @NonNull RouteViewAssembler routeAssembler;

    /**
     * View assembler for subscriptions.
     */
    private final @NonNull SubscriptionViewAssembler subscriptionAssembler;

    /**
     * This function is a helper function for hiding the problem that the self-link is always
     * received through the concrete assembler.
     *
     * @param entity The entity.
     * @param <T>    Generic type of database entity.
     * @return The abstract entity.
     */
    public <T extends Entity> URI getSelfLink(final T entity) {
        if (entity instanceof Catalog) {
            return getSelfLink((Catalog) entity);
        } else if (entity instanceof OfferedResource) {
            return getSelfLink((OfferedResource) entity);
        } else if (entity instanceof RequestedResource) {
            return getSelfLink((RequestedResource) entity);
        } else if (entity instanceof Representation) {
            return getSelfLink((Representation) entity);
        } else if (entity instanceof Artifact) {
            return getSelfLink((Artifact) entity);
        } else if (entity instanceof Contract) {
            return getSelfLink((Contract) entity);
        } else if (entity instanceof ContractRule) {
            return getSelfLink((ContractRule) entity);
        } else if (entity instanceof Agreement) {
            return getSelfLink((Agreement) entity);
        } else if (entity instanceof GenericEndpoint) {
            return getSelfLink((GenericEndpoint) entity);
        } else if (entity instanceof ConnectorEndpoint) {
            return getSelfLink((ConnectorEndpoint) entity);
        } else if (entity instanceof Route) {
            return getSelfLink((Route) entity);
        } else if (entity instanceof Subscription) {
            return getSelfLink((Subscription) entity);
        }

        throw new UnreachableLineException(ErrorMessage.UNKNOWN_TYPE);
    }

    /**
     * Get self-link from abstract entity.
     *
     * @param entity    The entity.
     * @param describer The entity view assembler.
     * @param <T>       The type of the entity.
     * @param <S>       The type of the assembler.
     * @return The abstract entity and its self-link.
     * @throws ResourceNotFoundException If the entity could not be found.
     */
    public <T extends Entity, S extends SelfLinking> URI getSelfLink(
            final T entity, final S describer) throws ResourceNotFoundException {
        try {
            return describer.getSelfLink(entity.getId()).toUri();
        } catch (IllegalStateException exception) {
            throw new ResourceNotFoundException(ErrorMessage.EMTPY_ENTITY.toString(), exception);
        }
    }

    /**
     * Get self-link of catalog.
     *
     * @param catalog The catalog.
     * @return The self-link of the catalog.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public URI getSelfLink(final Catalog catalog) throws ResourceNotFoundException {
        return getSelfLink(catalog, catalogAssembler);
    }

    /**
     * Get self-link of offered resource.
     *
     * @param resource The offered resource.
     * @return The self-link of the offered resource.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public URI getSelfLink(final OfferedResource resource) throws ResourceNotFoundException {
        return getSelfLink(resource, offeredResourceAssembler);
    }

    /**
     * Get self-link of requested resource.
     *
     * @param resource The requested resource.
     * @return The self-link of the requested resource.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public URI getSelfLink(final RequestedResource resource)
            throws ResourceNotFoundException {
        return getSelfLink(resource, requestedResourceAssembler);
    }

    /**
     * Get self-link of representation.
     *
     * @param representation The representation.
     * @return The self-link of the representation.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public URI getSelfLink(final Representation representation)
            throws ResourceNotFoundException {
        return getSelfLink(representation, representationAssembler);
    }

    /**
     * Get self-link of artifact.
     *
     * @param artifact The artifact.
     * @return The self-link of the artifact.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public URI getSelfLink(final Artifact artifact) throws ResourceNotFoundException {
        return getSelfLink(artifact, artifactAssembler);
    }

    /**
     * Get self-link of contract.
     *
     * @param contract The contract.
     * @return The self-link of the contract.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public URI getSelfLink(final Contract contract) throws ResourceNotFoundException {
        return getSelfLink(contract, contractAssembler);
    }

    /**
     * Get self-link of rule.
     *
     * @param rule The rule.
     * @return The self-link of the rule.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public URI getSelfLink(final ContractRule rule) throws ResourceNotFoundException {
        return getSelfLink(rule, ruleAssembler);
    }

    /**
     * Get self-link of agreement.
     *
     * @param agreement The agreement.
     * @return The self-link of the agreement.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public URI getSelfLink(final Agreement agreement) throws ResourceNotFoundException {
        return getSelfLink(agreement, agreementAssembler);
    }

    /**
     * Get self-link of generic endpoint.
     *
     * @param endpoint the generic endpoint.
     * @return the self-link of the generic endpoint.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    private URI getSelfLink(final GenericEndpoint endpoint)
            throws ResourceNotFoundException {
        return getSelfLink(endpoint, genericEndpointAssembler);
    }

    /**
     * Get self-link of connector endpoint.
     *
     * @param endpoint the connector endpoint.
     * @return the self-link to the connector endpoint.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    private URI getSelfLink(final ConnectorEndpoint endpoint)
            throws ResourceNotFoundException {
        return getSelfLink(endpoint, connectorEndpointAssembler);
    }

    /**
     * Get self-link of route.
     *
     * @param route the route.
     * @return the self-link to the route.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    private URI getSelfLink(final Route route) throws ResourceNotFoundException {
        return getSelfLink(route, routeAssembler);
    }

    /**
     * Get self-link of subscription.
     *
     * @param subscription The subscription.
     * @return The self-link of the subscription.
     * @throws ResourceNotFoundException If the resource could not be loaded.
     */
    public URI getSelfLink(final Subscription subscription)
            throws ResourceNotFoundException {
        return getSelfLink(subscription, subscriptionAssembler);
    }
}
