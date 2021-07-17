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
package io.dataspaceconnector.view.broker;

import io.dataspaceconnector.controller.configuration.BrokerControllers;
import io.dataspaceconnector.controller.configuration.BrokerControllers.BrokerToOfferedResources;
import io.dataspaceconnector.controller.resource.view.SelfLinking;
import io.dataspaceconnector.controller.resource.view.ViewAssemblerHelper;
import io.dataspaceconnector.model.broker.Broker;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles the REST resource for a broker.
 */
@Component
public class BrokerViewAssembler
        implements RepresentationModelAssembler<Broker, BrokerView>, SelfLinking {

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId,
                BrokerControllers.BrokerController.class);
    }

    @Override
    public final BrokerView toModel(final Broker broker) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(broker, BrokerView.class);
        view.add(getSelfLink(broker.getId()));

        final var offeredResourcesLink = linkTo(methodOn(BrokerToOfferedResources.class)
                .getResource(broker.getId(), null, null))
                .withRel("resources");
        view.add(offeredResourcesLink);

        return view;
    }
}
