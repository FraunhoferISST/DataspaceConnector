package de.fraunhofer.isst.dataspaceconnector.view;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationControllers.AgreementsToArtifacts;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceControllers.AgreementController;
import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AgreementViewAssembler implements RepresentationModelAssembler<Agreement, AgreementView> {

    @Override
    public AgreementView toModel(final Agreement agreement) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(agreement, AgreementView.class);

        final var selfLink = linkTo(AgreementController.class).slash(agreement.getId()).withSelfRel();
        view.add(selfLink);

        final var artifactLink = linkTo(methodOn(AgreementsToArtifacts.class)
                .getResource(agreement.getId(), null, null, null))
                .withRel("artifacts");
        view.add(artifactLink);

        return view;
    }
}
