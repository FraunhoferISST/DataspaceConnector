package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.ArtifactController;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationshipControllers;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ArtifactViewAssembler implements RepresentationModelAssembler<Artifact, ArtifactView> {

    @Override
    public ArtifactView toModel(final Artifact entity) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(entity, ArtifactView.class);

        final var selfLink = linkTo(ArtifactController.class).slash(entity.getId()).withSelfRel();
        view.add(selfLink);

        final var dataLink = linkTo(methodOn(ArtifactController.class).getData(entity.getId())).withRel("data");
        view.add(dataLink);

        final var representationLink = linkTo(RelationshipControllers.ArtifactsToRepresentations.class).slash(entity.getId()).withRel("representations");
        view.add(representationLink);

        return view;
    }
}
