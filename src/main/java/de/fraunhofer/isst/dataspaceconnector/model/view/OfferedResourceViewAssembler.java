package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.OfferedResourceController;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceContracts;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceRepresentations;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@Component
public class OfferedResourceViewAssembler implements RepresentationModelAssembler<OfferedResource, OfferedResourceView> {

    @Override
    public OfferedResourceView toModel(final OfferedResource entity) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(entity, OfferedResourceView.class);

        final var selfLink = linkTo(OfferedResourceController.class).slash(entity.getId()).withSelfRel();
        view.add(selfLink);

        final var contractsLink = linkTo(methodOn(ResourceContracts.class).getResource(entity.getId(), null, null, null)).withRel("contracts");
        view.add(contractsLink);

        final var representationLink = linkTo(methodOn(ResourceRepresentations.class).getResource(entity.getId(), null, null, null)).withRel("representations");
        view.add(representationLink);

        return view;
    }
}
