package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.v2.OfferedResourceController;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.ResourceContracts;
import de.fraunhofer.isst.dataspaceconnector.controller.v2.ResourceRepresentations;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@Component
public class OfferedResourceViewAssembler implements RepresentationModelAssembler<OfferedResource, OfferedResourceView> {

    @Override
    public OfferedResourceView toModel(final OfferedResource entity) {
        final var view = new OfferedResourceView();
        view.setTitle(entity.getTitle());
        view.setDescription(entity.getDescription());
        view.setKeywords(entity.getKeywords());
        view.setLanguage(entity.getLanguage());
        view.setVersion(entity.getVersion());
        view.setLicence(entity.getLicence());
        view.setPublisher(entity.getPublisher());

        final var selfLink = linkTo(OfferedResourceController.class).slash(entity.getId()).withSelfRel();
        view.add(selfLink);

        final var contractsLink = linkTo(methodOn(ResourceContracts.class).getResource(entity.getId())).withRel("contracts");
        view.add(contractsLink);

        final var representationLink = linkTo(methodOn(ResourceRepresentations.class).getResource(entity.getId())).withRel("representations");
        view.add(representationLink);

        return view;
    }
}
