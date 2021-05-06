package io.dataspaceconnector.view;

import io.dataspaceconnector.controller.resources.ResourceControllers.AgreementController;
import io.dataspaceconnector.model.Agreement;
import io.dataspaceconnector.controller.resources.RelationControllers;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AgreementViewAssembler
        implements RepresentationModelAssembler<Agreement, AgreementView>, SelfLinking {
    @Override
    public final AgreementView toModel(final Agreement agreement) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(agreement, AgreementView.class);
        view.add(getSelfLink(agreement.getId()));

        final var artifactLink = WebMvcLinkBuilder
                .linkTo(methodOn(RelationControllers.AgreementsToArtifacts.class)
                .getResource(agreement.getId(), null, null, null))
                .withRel("artifacts");
        view.add(artifactLink);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, AgreementController.class);
    }
}
