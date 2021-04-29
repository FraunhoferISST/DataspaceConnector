package de.fraunhofer.isst.dataspaceconnector.view;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationControllers;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceControllers.RequestedResourceController;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Assembles the REST resource for a requested resource.
 */
@Component
@NoArgsConstructor
public class RequestedResourceViewAssembler
        implements RepresentationModelAssembler<RequestedResource, RequestedResourceView>,
        SelfLinking {
    /**
     * Construct the RequestedResourceView from a RequestedResource.
     *
     * @param resource The resource.
     * @return The new view.
     */
    @Override
    public RequestedResourceView toModel(final RequestedResource resource) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(resource, RequestedResourceView.class);
        view.add(getSelfLink(resource.getId()));

        final var contractsLink =
                linkTo(methodOn(RelationControllers.OfferedResourcesToContracts.class)
                        .getResource(resource.getId(), null, null, null))
                        .withRel("contracts");
        view.add(contractsLink);

        final var representationLink =
                linkTo(methodOn(RelationControllers.OfferedResourcesToRepresentations.class)
                        .getResource(resource.getId(), null, null, null))
                        .withRel("representations");
        view.add(representationLink);

        final var catalogLink =
                linkTo(methodOn(RelationControllers.RequestedResourcesToCatalogs.class)
                        .getResource(resource.getId(), null, null, null))
                        .withRel("catalogs");
        view.add(catalogLink);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, RequestedResourceController.class);
    }
}
