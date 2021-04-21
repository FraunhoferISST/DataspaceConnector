package de.fraunhofer.isst.dataspaceconnector.view;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationControllers.RulesToContracts;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceControllers.RuleController;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import lombok.NoArgsConstructor;

/**
 * Assembles the REST resource for an contract rule.
 */
@Component
@NoArgsConstructor
public class ContractRuleViewAssembler
        implements RepresentationModelAssembler<ContractRule, ContractRuleView>, SelfLinking {
    /**
     * Construct the ContractRuleView from a ContractRule.
     * @param rule The contract rule.
     * @return The new view.
     */
    @Override
    public ContractRuleView toModel(final ContractRule rule) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(rule, ContractRuleView.class);
        view.add(getSelfLink(rule.getId()));

        final var contractLink = linkTo(methodOn(RulesToContracts.class)
                .getResource(rule.getId(), null, null, null))
                .withRel("contracts");
        view.add(contractLink);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, RuleController.class);
    }
}
