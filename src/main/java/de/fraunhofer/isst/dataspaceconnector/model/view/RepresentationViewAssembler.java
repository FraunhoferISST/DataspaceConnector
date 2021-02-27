package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.v2.RepresentationArtifactController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.RepresentationController;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@Component
public class RepresentationViewAssembler implements RepresentationModelAssembler<Representation, RepresentationView> {

    @Override
    public RepresentationView toModel(final Representation entity) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(entity, RepresentationView.class);

        final var selfLink = linkTo(RepresentationController.class).slash(entity.getId()).withSelfRel();
        view.add(selfLink);

        final var artifactsLink = linkTo(methodOn(RepresentationArtifactController.class).getResource(entity.getId())).withRel("artifacts");
        view.add(artifactsLink);

        return view;
    }
}
