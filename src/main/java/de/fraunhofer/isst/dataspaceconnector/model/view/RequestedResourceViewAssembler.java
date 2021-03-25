package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationshipControllers;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.RequestedResourceController;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceContracts;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceRepresentations;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Assembles the REST resource for a requested resource.
 */
@Component
public class RequestedResourceViewAssembler
        implements RepresentationModelAssembler<RequestedResource, RequestedResourceView> {
    /**
     * Construct the RequestedResourceView from a RequestedResource.
     * @param resource The resource.
     * @return The new view.
     */
    @Override
    public RequestedResourceView toModel(final RequestedResource resource) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(resource, RequestedResourceView.class);

        final var selfLink =
                linkTo(RequestedResourceController.class).slash(resource.getId()).withSelfRel();
        view.add(selfLink);

        final var contractsLink = linkTo(
                methodOn(ResourceContracts.class).getResource(resource.getId(), null, null, null))
                                          .withRel("contracts");
        view.add(contractsLink);

        final var representationLink =
                linkTo(methodOn(ResourceRepresentations.class)
                                .getResource(resource.getId(), null, null, null))
                        .withRel("representations");
        view.add(representationLink);

        final var catalogLink =
                linkTo(methodOn(RelationshipControllers.RequestedResourcesToCatalogs.class)
                                .getResource(resource.getId(), null, null, null))
                        .withRel("catalogs");
        view.add(catalogLink);

        return view;
    }
}
