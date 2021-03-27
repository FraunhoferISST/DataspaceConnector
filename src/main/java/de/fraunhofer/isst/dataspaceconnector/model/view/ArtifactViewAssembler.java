package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.ArtifactController;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationshipControllers;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles the REST resource for an artifact.
 */
@Component
@NoArgsConstructor
public class ArtifactViewAssembler implements RepresentationModelAssembler<Artifact, ArtifactView> {
    /**
     * Construct the ArtifactView from an Artifact.
     *
     * @param artifact The artifact.
     * @return The new view.
     */
    @Override
    public ArtifactView toModel(final Artifact artifact) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(artifact, ArtifactView.class);

        final var selfLink = linkTo(ArtifactController.class).slash(artifact.getId()).withSelfRel();
        view.add(selfLink);

        final var dataLink = linkTo(methodOn(ArtifactController.class).getData(artifact.getId()))
                .withRel("data");
        view.add(dataLink);

        final var repLink = linkTo(RelationshipControllers.ArtifactsToRepresentations.class)
                .slash(artifact.getId())
                .withRel("representations");
        view.add(repLink);

        final var agreementLink = linkTo(RelationshipControllers.ArtifactsToAgreements.class)
                .slash(artifact.getId())
                .withRel("agreements");
        view.add(agreementLink);

        return view;
    }
}
