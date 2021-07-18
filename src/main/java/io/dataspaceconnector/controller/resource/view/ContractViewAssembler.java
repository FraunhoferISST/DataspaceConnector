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
package io.dataspaceconnector.controller.resource.view;

import java.util.UUID;

import io.dataspaceconnector.controller.resource.RelationControllers;
import io.dataspaceconnector.controller.resource.ResourceControllers.ContractController;
import io.dataspaceconnector.exception.UnreachableLineException;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.util.ErrorMessage;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Assembles the REST resource for a contracts.
 */
@Component
@NoArgsConstructor
public class ContractViewAssembler
        implements RepresentationModelAssembler<Contract, ContractView>, SelfLinking {
    /**
     * Construct the ContractView from a Contract.
     *
     * @param contract The contract.
     * @return The new view.
     */
    @Override
    public ContractView toModel(final Contract contract) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(contract, ContractView.class);
        view.add(getSelfLink(contract.getId()));

        final var rulesLink = linkTo(methodOn(RelationControllers.ContractsToRules.class)
                                             .getResource(contract.getId(), null, null))
                                      .withRel("rules");
        view.add(rulesLink);

        final var resourceType = contract.getResources();
        Link resourceLinker;
        if (resourceType.isEmpty()) {
            // No elements found, default to offered resources
            resourceLinker = linkTo(methodOn(RelationControllers.ContractsToOfferedResources.class)
                    .getResource(contract.getId(), null, null))
                    .withRel("offers");
        } else {
            // Construct the link for the right resource type.
            if (resourceType.get(0) instanceof OfferedResource) {
                resourceLinker =
                        linkTo(methodOn(RelationControllers.ContractsToOfferedResources.class)
                                .getResource(contract.getId(), null, null))
                                .withRel("offers");
            } else if (resourceType.get(0) instanceof RequestedResource) {
                resourceLinker =
                        linkTo(methodOn(RelationControllers.ContractsToRequestedResources.class)
                                .getResource(contract.getId(), null, null))
                                .withRel("requests");
            } else {
                throw new UnreachableLineException(ErrorMessage.UNKNOWN_TYPE);
            }
        }

        view.add(resourceLinker);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, ContractController.class);
    }
}
