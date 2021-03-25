package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationshipControllers;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.RuleController;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class ContractRuleViewAssembler implements RepresentationModelAssembler<ContractRule, ContractRuleView> {

    @Override
    public ContractRuleView toModel(final ContractRule entity) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(entity, ContractRuleView.class);

        final var selfLink = linkTo(RuleController.class).slash(entity.getId()).withSelfRel();
        view.add(selfLink);

        final var contractLink = linkTo(RelationshipControllers.RulesToContracts.class).slash(entity.getId()).withRel("contracts");
        view.add(contractLink);

        return view;
    }
}
