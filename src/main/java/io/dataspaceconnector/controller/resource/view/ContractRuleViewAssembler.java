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

import io.dataspaceconnector.controller.resource.RelationControllers;
import io.dataspaceconnector.controller.resource.ResourceControllers.RuleController;
import io.dataspaceconnector.controller.resource.view.util.ViewAssemblerHelper;
import io.dataspaceconnector.model.rule.ContractRule;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles the REST resource for an contract rule.
 */
@Component
@NoArgsConstructor
public class ContractRuleViewAssembler
        implements RepresentationModelAssembler<ContractRule, ContractRuleView>, SelfLinking {
    /**
     * Construct the ContractRuleView from a ContractRule.
     *
     * @param rule The contract rule.
     * @return The new view.
     */
    @Override
    public ContractRuleView toModel(final ContractRule rule) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(rule, ContractRuleView.class);
        view.add(getSelfLink(rule.getId()));

        final var contractLink = WebMvcLinkBuilder
                .linkTo(methodOn(RelationControllers.RulesToContracts.class)
                        .getResource(rule.getId(), null, null))
                .withRel("contracts");
        view.add(contractLink);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, RuleController.class);
    }
}
