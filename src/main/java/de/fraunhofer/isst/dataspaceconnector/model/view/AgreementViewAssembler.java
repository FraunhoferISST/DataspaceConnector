package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.AgreementController;
import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Component
public class AgreementViewAssembler implements RepresentationModelAssembler<Agreement, AgreementView> {

    @Override
    public AgreementView toModel(final Agreement entity) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(entity, AgreementView.class);

        final var selfLink = linkTo(AgreementController.class).slash(entity.getId()).withSelfRel();
        view.add(selfLink);

        return view;
    }
}
