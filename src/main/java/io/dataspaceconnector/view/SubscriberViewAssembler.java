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

import java.util.UUID;

import io.dataspaceconnector.controller.resources.RelationControllers;
import io.dataspaceconnector.controller.resources.ResourceControllers.SubscriberController;
import io.dataspaceconnector.model.Subscriber;
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
public class SubscriberViewAssembler
        implements RepresentationModelAssembler<Subscriber, SubscriberView>, SelfLinking {

    /**
     * Constructs the SubscriberView from a Subscriber.
     * @param subscriber the subscriber.
     * @return the corresponding subscriber view.
     */
    @Override
    public SubscriberView toModel(final Subscriber subscriber) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(subscriber, SubscriberView.class);
        view.add(getSelfLink(subscriber.getId()));

        final var resourcesLink =
                        linkTo(methodOn(RelationControllers.SubscribersToRequestedResources.class)
                                .getResource(subscriber.getId(), null, null))
                        .withRel("requests");
        view.add(resourcesLink);

        return view;
    }

    /**
     * Returns the self link for a subscriber.
     * @param entityId the ID of the subscriber.
     * @return the self link.
     */
    @Override
    public Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, SubscriberController.class);
    }

}
