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
package io.dataspaceconnector.controller.resource.view.contract;

import java.util.UUID;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.UnreachableLineException;
import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.relation.ContractsToOfferedResourcesController;
import io.dataspaceconnector.controller.resource.relation.ContractsToRequestedResourcesController;
import io.dataspaceconnector.controller.resource.relation.ContractsToRulesController;
import io.dataspaceconnector.controller.resource.type.ContractController;
import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import io.dataspaceconnector.controller.resource.view.util.SelfLinking;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.RequestedResource;
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
public class ContractViewAssembler extends SelfLinkHelper
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

        final var rulesLink = linkTo(methodOn(ContractsToRulesController.class)
                .getResource(contract.getId(), null, null))
                .withRel(BaseType.RULES);
        view.add(rulesLink);

        final var resourceType = contract.getResources();
        Link resourceLinker;
        if (resourceType.isEmpty()) {
            // No elements found, default to offered resources
            resourceLinker = linkTo(methodOn(ContractsToOfferedResourcesController.class)
                    .getResource(contract.getId(), null, null))
                    .withRel(BaseType.OFFERS);
        } else {
            // Construct the link for the right resource type.
            if (resourceType.get(0) instanceof OfferedResource) {
                resourceLinker = linkTo(methodOn(ContractsToOfferedResourcesController.class)
                        .getResource(contract.getId(), null, null))
                        .withRel(BaseType.OFFERS);
            } else if (resourceType.get(0) instanceof RequestedResource) {
                resourceLinker = linkTo(methodOn(ContractsToRequestedResourcesController.class)
                        .getResource(contract.getId(), null, null))
                        .withRel(BaseType.REQUESTS);
            } else {
                throw new UnreachableLineException(ErrorMessage.UNKNOWN_TYPE);
            }
        }

        view.add(resourceLinker);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return getSelfLink(entityId, ContractController.class);
    }
}
