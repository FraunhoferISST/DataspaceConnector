package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.v2.CatalogController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.CatalogOfferedResources;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CatalogViewAssembler implements RepresentationModelAssembler<Catalog, CatalogView> {

    @Override
    public CatalogView toModel(final Catalog entity) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(entity, CatalogView.class);

        final var selfLink = linkTo(CatalogController.class).slash(entity.getId()).withSelfRel();
        view.add(selfLink);

        final var offeredResLink = linkTo(methodOn(CatalogOfferedResources.class).getResource(entity.getId(), null, null, null)).withRel("resources");
        view.add(offeredResLink);

        return view;
    }
}
