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
package io.dataspaceconnector.controller.resource.view;

import io.dataspaceconnector.controller.resource.RelationControllers;
import io.dataspaceconnector.controller.resource.ResourceControllers.ArtifactController;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.util.QueryInput;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles the REST resource for an artifact.
 */
@Component
@NoArgsConstructor
public class ArtifactViewAssembler
        implements RepresentationModelAssembler<Artifact, ArtifactView>, SelfLinking {
    /**
     * Construct the ArtifactView from an Artifact.
     *
     * @param artifact The artifact.
     * @return The new view.
     */
    @SneakyThrows
    @Override
    public ArtifactView toModel(final Artifact artifact) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(artifact, ArtifactView.class);
        view.add(getSelfLink(artifact.getId()));

        final var dataLink = linkTo(methodOn(ArtifactController.class)
                .getData(artifact.getId(), new QueryInput()))
                .withRel("data");
        view.add(dataLink);

        final var repLink = linkTo(methodOn(RelationControllers.ArtifactsToRepresentations.class)
                .getResource(artifact.getId(), null, null))
                .withRel("representations");
        view.add(repLink);

        final var agreementLink =
                linkTo(methodOn(RelationControllers.ArtifactsToAgreements.class)
                        .getResource(artifact.getId(), null, null))
                        .withRel("agreements");
        view.add(agreementLink);

        final var subscriptionLink =
                linkTo(methodOn(RelationControllers.ArtifactsToSubscriptions.class)
                        .getResource(artifact.getId(), null, null))
                        .withRel("subscriptions");
        view.add(subscriptionLink);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, ArtifactController.class);
    }
}
