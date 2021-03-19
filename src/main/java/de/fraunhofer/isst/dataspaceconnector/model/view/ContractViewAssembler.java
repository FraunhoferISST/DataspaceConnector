package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.ContractController;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ContractRules;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@Component
public class ContractViewAssembler implements RepresentationModelAssembler<Contract, ContractView> {

    @Override
    public ContractView toModel(final Contract entity) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(entity, ContractView.class);

        final var selfLink = linkTo(ContractController.class).slash(entity.getId()).withSelfRel();
        view.add(selfLink);

        final var rulesLink = linkTo(methodOn(ContractRules.class).getResource(entity.getId(), null, null, null)).withRel("rules");
        view.add(rulesLink);

        return view;
    }
}
