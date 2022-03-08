/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.controller.resource.view.resource;

import java.util.UUID;

import io.dataspaceconnector.controller.resource.relation.RequestedResourcesToCatalogsController;
import io.dataspaceconnector.controller.resource.relation.RequestedResourcesToContractsController;
import io.dataspaceconnector.controller.resource.relation.RequestedResourcesToRepresentationsController;
import io.dataspaceconnector.controller.resource.relation.RequestedResourcesToSubscriptionsController;
import io.dataspaceconnector.controller.resource.type.RequestedResourceController;
import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import io.dataspaceconnector.controller.resource.view.util.SelfLinking;
import io.dataspaceconnector.model.resource.RequestedResource;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Assembles the REST resource for a requested resource.
 */
@Component
@NoArgsConstructor
public class RequestedResourceViewAssembler extends SelfLinkHelper
        implements RepresentationModelAssembler<RequestedResource, RequestedResourceView>,
        SelfLinking {
    /**
     * Construct the RequestedResourceView from a RequestedResource.
     *
     * @param resource The resource.
     * @return The new view.
     */
    @Override
    public RequestedResourceView toModel(final RequestedResource resource) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(resource, RequestedResourceView.class);
        view.add(getSelfLink(resource.getId()));

        final var contractsLink = linkTo(methodOn(RequestedResourcesToContractsController.class)
                .getResource(resource.getId(), null, null))
                .withRel("contracts");
        view.add(contractsLink);

        final var representationLink
                = linkTo(methodOn(RequestedResourcesToRepresentationsController.class)
                .getResource(resource.getId(), null, null))
                .withRel("representations");
        view.add(representationLink);

        final var catalogLink = linkTo(methodOn(RequestedResourcesToCatalogsController.class)
                .getResource(resource.getId(), null, null))
                .withRel("catalogs");
        view.add(catalogLink);

        final var subscriptionLink
                = linkTo(methodOn(RequestedResourcesToSubscriptionsController.class)
                .getResource(resource.getId(), null, null))
                .withRel("subscriptions");
        view.add(subscriptionLink);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return getSelfLink(entityId, RequestedResourceController.class);
    }
}
