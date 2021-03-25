package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.ContractController;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ContractRules;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationshipControllers;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Assembles the REST resource for a contracts.
 */
@Component
@NoArgsConstructor
public class ContractViewAssembler implements RepresentationModelAssembler<Contract, ContractView> {
    /**
     * Construct the ContractView from a Contract.
     * @param contract The contract.
     * @return The new view.
     */
    @Override
    public ContractView toModel(final Contract contract) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(contract, ContractView.class);

        final var selfLink = linkTo(ContractController.class).slash(contract.getId()).withSelfRel();
        view.add(selfLink);

        final var rulesLink = linkTo(
                methodOn(ContractRules.class).getResource(contract.getId(), null, null, null))
                                      .withRel("rules");
        view.add(rulesLink);

        final var resourceType = contract.getResources();
        final Link resourceLinker;
        if (resourceType.isEmpty()) {
            // No elements found, default to offered resources
            resourceLinker =
                    linkTo(methodOn(RelationshipControllers.ContractsToOfferedResources.class)
                                    .getResource(contract.getId(), null, null, null))
                            .withRel("offered");
        } else {
            // Construct the link for the right resource type.
            if (resourceType.get(0) instanceof OfferedResource) {
                resourceLinker =
                        linkTo(methodOn(RelationshipControllers.ContractsToOfferedResources.class)
                                        .getResource(contract.getId(), null, null, null))
                                .withRel("offered");
            } else if (resourceType.get(0) instanceof RequestedResource) {
                resourceLinker =
                        linkTo(methodOn(RelationshipControllers.ContractsToRequestedResources.class)
                                        .getResource(contract.getId(), null, null, null))
                                .withRel("requested");
            } else {
                throw new UnreachableLineException(ErrorMessages.UNKNOWN_TYPE);
            }
        }

        view.add(resourceLinker);

        return view;
    }
}
