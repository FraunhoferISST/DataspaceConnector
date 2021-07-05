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
package io.dataspaceconnector.view;

import io.dataspaceconnector.controller.resources.RelationControllers;
import io.dataspaceconnector.controller.resources.ResourceControllers.SubscriptionController;
import io.dataspaceconnector.exceptions.UnreachableLineException;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.model.Subscription;
import io.dataspaceconnector.utils.ErrorMessages;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Assembles the REST resource for a subscription.
 */
@Component
@NoArgsConstructor
public class SubscriptionViewAssembler
        implements RepresentationModelAssembler<Subscription, SubscriptionView>, SelfLinking {

    /**
     * Constructs the SubscriberView from a Subscription.
     * @param subscription the subscription.
     * @return the corresponding subscription view.
     */
    @Override
    public SubscriptionView toModel(final Subscription subscription) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(subscription, SubscriptionView.class);
        view.add(getSelfLink(subscription.getId()));

        final var resourceType = subscription.getResources();
        Link resourceLinker;
        if (resourceType.isEmpty()) {
            // No elements found, default to offered resources
            resourceLinker =
                    linkTo(methodOn(RelationControllers.SubscriptionsToOfferedResources.class)
                            .getResource(subscription.getId(), null, null))
                            .withRel("offers");
        } else {
            // Construct the link for the right resource type.
            if (resourceType.get(0) instanceof OfferedResource) {
                resourceLinker =
                        linkTo(methodOn(RelationControllers.SubscriptionsToOfferedResources.class)
                                .getResource(subscription.getId(), null, null))
                                .withRel("offers");
            } else if (resourceType.get(0) instanceof RequestedResource) {
                resourceLinker =
                        linkTo(methodOn(
                                RelationControllers.SubscriptionsToRequestedResources.class)
                                .getResource(subscription.getId(), null, null))
                                .withRel("requests");
            } else {
                throw new UnreachableLineException(ErrorMessages.UNKNOWN_TYPE);
            }
        }

        view.add(resourceLinker);

        return view;
    }

    /**
     * Returns the self link for a subscription.
     * @param entityId the ID of the subscription.
     * @return the self link.
     */
    @Override
    public Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, SubscriptionController.class);
    }

}
