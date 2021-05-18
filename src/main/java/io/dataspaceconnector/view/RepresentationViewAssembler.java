/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.view;

import java.util.UUID;

import io.dataspaceconnector.controller.resources.RelationControllers;
import io.dataspaceconnector.controller.resources.ResourceControllers.RepresentationController;
import io.dataspaceconnector.exceptions.UnreachableLineException;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.Representation;
import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.utils.ErrorMessages;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Assembles the REST resource for an representation.
 */
@Component
@NoArgsConstructor
public class RepresentationViewAssembler
        implements RepresentationModelAssembler<Representation, RepresentationView>, SelfLinking {
    /**
     * Construct the RepresentationView from an Representation.
     *
     * @param representation The representation.
     * @return The new view.
     */
    @Override
    public RepresentationView toModel(final Representation representation) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(representation, RepresentationView.class);
        view.add(getSelfLink(representation.getId()));

        final var selfLink =
                linkTo(RepresentationController.class).slash(representation.getId()).withSelfRel();
        view.add(selfLink);

        final var artifactsLink =
                WebMvcLinkBuilder
                        .linkTo(methodOn(RelationControllers.RepresentationsToArtifacts.class)
                                        .getResource(representation.getId(), null, null))
                        .withRel("artifacts");
        view.add(artifactsLink);

        final var resourceType = representation.getResources();
        Link resourceLinker;
        if (resourceType.isEmpty()) {
            // No elements found, default to offered resources
            resourceLinker =
                    linkTo(methodOn(RelationControllers.RepresentationsToOfferedResources.class)
                                   .getResource(representation.getId(), null, null))
                            .withRel("offers");
        } else {
            // Construct the link for the right resource type.
            if (resourceType.get(0) instanceof OfferedResource) {
                resourceLinker =
                        linkTo(methodOn(RelationControllers.RepresentationsToOfferedResources.class)
                                       .getResource(representation.getId(), null, null))
                                .withRel("offers");
            } else if (resourceType.get(0) instanceof RequestedResource) {
                resourceLinker =
                        linkTo(methodOn(
                                RelationControllers.RepresentationsToRequestedResources.class)
                                       .getResource(representation.getId(), null, null))
                                .withRel("requests");
            } else {
                throw new UnreachableLineException(ErrorMessages.UNKNOWN_TYPE);
            }
        }

        view.add(resourceLinker);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, RepresentationController.class);
    }
}
