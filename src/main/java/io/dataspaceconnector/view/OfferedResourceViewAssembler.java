package io.dataspaceconnector.view;

import java.util.UUID;

import io.dataspaceconnector.controller.resources.RelationControllers;
import io.dataspaceconnector.controller.resources.ResourceControllers.OfferedResourceController;
import io.dataspaceconnector.model.OfferedResource;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Assembles the REST resource for an offered resource.
 */
@Component
@NoArgsConstructor
public class OfferedResourceViewAssembler
        implements RepresentationModelAssembler<OfferedResource, OfferedResourceView>, SelfLinking {
    /**
     * Construct the OfferedResourceView from an OfferedResource.
     *
     * @param resource The resource.
     * @return The new view.
     */
    @Override
    public OfferedResourceView toModel(final OfferedResource resource) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(resource, OfferedResourceView.class);
        view.add(getSelfLink(resource.getId()));

        final var contractsLink =
                WebMvcLinkBuilder
                        .linkTo(methodOn(RelationControllers.OfferedResourcesToContracts.class)
                                        .getResource(resource.getId(), null, null, null))
                        .withRel("contracts");
        view.add(contractsLink);

        final var repLink =
                linkTo(methodOn(RelationControllers.OfferedResourcesToRepresentations.class)
                        .getResource(resource.getId(), null, null, null))
                        .withRel("representations");
        view.add(repLink);

        final var catalogLink =
                linkTo(methodOn(RelationControllers.OfferedResourcesToCatalogs.class)
                        .getResource(resource.getId(), null, null, null))
                        .withRel("catalogs");
        view.add(catalogLink);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, OfferedResourceController.class);
    }
}
