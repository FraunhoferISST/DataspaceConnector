package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationshipControllers;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.RuleController;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles the REST resource for an contract rule.
 */
@Component
@NoArgsConstructor
public class ContractRuleViewAssembler
        implements RepresentationModelAssembler<ContractRule, ContractRuleView> {
    /**
     * Construct the ContractRuleView from a ContractRule.
     * @param rule The contract rule.
     * @return The new view.
     */
    @Override
    public ContractRuleView toModel(final ContractRule rule) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(rule, ContractRuleView.class);

        final var selfLink = linkTo(RuleController.class).slash(rule.getId()).withSelfRel();
        view.add(selfLink);

        final var contractLink = linkTo(methodOn(RelationshipControllers.RulesToContracts.class)
                                                .getResource(rule.getId(), null, null, null))
                                         .withRel("contracts");
        view.add(contractLink);

        return view;
    }
}
