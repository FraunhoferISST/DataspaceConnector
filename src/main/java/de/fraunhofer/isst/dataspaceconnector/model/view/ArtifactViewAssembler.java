package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.v2.ArtifactController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.CatalogOfferedResources;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ArtifactViewAssembler implements RepresentationModelAssembler<Artifact, ArtifactView> {

    @Override
    public ArtifactView toModel(final Artifact entity) {
        final var view = new ArtifactView();
        view.setTitle(entity.getTitle());

        final var selfLink = linkTo(ArtifactController.class).slash(entity.getId()).withSelfRel();
        view.add(selfLink);

        final var offeredResLink = linkTo(methodOn(CatalogOfferedResources.class).getResource(entity.getId())).withRel("offeredresources");
        // final var requestedResLink = linkTo(methodOn(CatalogResources.class).getResource(entity.getId())).withRel("offeredresources");

        return view;
    }
}
