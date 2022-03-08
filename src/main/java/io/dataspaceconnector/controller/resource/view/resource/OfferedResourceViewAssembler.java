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

import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.relation.OfferedResourcesToBrokersController;
import io.dataspaceconnector.controller.resource.relation.OfferedResourcesToCatalogsController;
import io.dataspaceconnector.controller.resource.relation.OfferedResourcesToContractsController;
import io.dataspaceconnector.controller.resource.relation.OfferedResourcesToRepresentationsController;
import io.dataspaceconnector.controller.resource.relation.OfferedResourcesToSubscriptionsController;
import io.dataspaceconnector.controller.resource.type.OfferedResourceController;
import io.dataspaceconnector.controller.resource.view.util.SelfLinking;
import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import io.dataspaceconnector.model.resource.OfferedResource;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Assembles the REST resource for an offered resource.
 */
@Component
@NoArgsConstructor
public class OfferedResourceViewAssembler extends SelfLinkHelper
        implements RepresentationModelAssembler<OfferedResource, OfferedResourceView>, SelfLinking {
    /**
     * Construct the OfferedResourceView from an OfferedResource.
     *
     * @param resource The resource.
     * @return The new view.
     */
    @Override
    public OfferedResourceView toModel(final OfferedResource resource) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(resource, OfferedResourceView.class);
        view.add(getSelfLink(resource.getId()));

        final var contractsLink = linkTo(methodOn(OfferedResourcesToContractsController.class)
                .getResource(resource.getId(), null, null))
                .withRel(BaseType.CONTRACTS);
        view.add(contractsLink);

        final var repLink = linkTo(methodOn(OfferedResourcesToRepresentationsController.class)
                .getResource(resource.getId(), null, null))
                .withRel(BaseType.REPRESENTATIONS);
        view.add(repLink);

        final var catalogLink = linkTo(methodOn(OfferedResourcesToCatalogsController.class)
                .getResource(resource.getId(), null, null))
                .withRel(BaseType.CATALOGS);
        view.add(catalogLink);

        final var subscriptionLink =
                linkTo(methodOn(OfferedResourcesToSubscriptionsController.class)
                .getResource(resource.getId(), null, null))
                .withRel(BaseType.SUBSCRIPTIONS);
        view.add(subscriptionLink);

        final var brokerLink = linkTo(methodOn(OfferedResourcesToBrokersController.class)
                .getResource(resource.getId(), null, null))
                .withRel(BaseType.BROKERS);
        view.add(brokerLink);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return getSelfLink(entityId, OfferedResourceController.class);
    }
}
