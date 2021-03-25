package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationshipControllers;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.RepresentationArtifactController;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.RepresentationController;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
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

        final var artifactsLink = linkTo(methodOn(RepresentationArtifactController.class).getResource(entity.getId(), null, null, null)).withRel("artifacts");
        view.add(artifactsLink);

        final var resourceType = entity.getResources();
        final Link resourceLinker;
        if (resourceType.isEmpty()) {
            resourceLinker =
                    linkTo(methodOn(RelationshipControllers.ContractsToOfferedResources.class)
                                   .getResource(entity.getId(), null, null, null))
                            .withRel("offered");
        } else {
            if (resourceType.get(0) instanceof OfferedResource ) {
                resourceLinker =
                        linkTo(methodOn(RelationshipControllers.RepresentationsToOfferedResources.class)
                                       .getResource(entity.getId(), null, null, null))
                                .withRel("offered");
            } else if (resourceType.get(0) instanceof RequestedResource ) {
                resourceLinker =
                        linkTo(methodOn(RelationshipControllers.RepresentationsToRequestedResources.class)
                                       .getResource(entity.getId(), null, null, null))
                                .withRel("requested");
            } else {
                throw new UnreachableLineException(ErrorMessages.UNKNOWN_TYPE);
            }
        }

        view.add(resourceLinker);

        return view;
    }
}
