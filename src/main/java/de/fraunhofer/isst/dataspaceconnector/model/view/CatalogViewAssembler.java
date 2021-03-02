package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.v2.CatalogController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.CatalogOfferedResources;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CatalogViewAssembler implements RepresentationModelAssembler<Catalog, CatalogView> {

    @Override
    public CatalogView toModel(final Catalog entity) {
        final var view = new CatalogView();
        view.setTitle(entity.getTitle());
        view.setDescription(entity.getDescription());

        final var selfLink = linkTo(CatalogController.class).slash(entity.getId()).withSelfRel();
        view.add(selfLink);

        final var offeredResLink = linkTo(methodOn(CatalogOfferedResources.class).getResource(entity.getId())).withRel("offeredresources");
        view.add(offeredResLink);

//        final var requestedResLink = linkTo(methodOn(CatalogRequestedResources.class).getResource(entity.getId())).withRel("offeredresources");
//        view.add(requestedResLink);

        return view;
    }
}
