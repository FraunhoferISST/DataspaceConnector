/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.controller.resource.view.artifact;

import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.relation.ArtifactsToAgreementsController;
import io.dataspaceconnector.controller.resource.relation.ArtifactsToRepresentationsController;
import io.dataspaceconnector.controller.resource.relation.ArtifactsToSubscriptionsController;
import io.dataspaceconnector.controller.resource.type.ArtifactController;
import io.dataspaceconnector.controller.resource.view.util.SelfLinking;
import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import io.dataspaceconnector.model.artifact.Artifact;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles the REST resource for an artifact.
 */
@Component
@NoArgsConstructor
public class ArtifactViewAssembler extends SelfLinkHelper
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
                .getData(artifact.getId(), new ArrayList<>(), new QueryInput()))
                .withRel("data");
        view.add(dataLink);

        final var repLink = linkTo(methodOn(ArtifactsToRepresentationsController.class)
                .getResource(artifact.getId(), null, null))
                .withRel(BaseType.REPRESENTATIONS);
        view.add(repLink);

        final var agreementLink = linkTo(methodOn(ArtifactsToAgreementsController.class)
                .getResource(artifact.getId(), null, null))
                .withRel(BaseType.AGREEMENTS);
        view.add(agreementLink);

        final var subscriptionLink = linkTo(methodOn(ArtifactsToSubscriptionsController.class)
                .getResource(artifact.getId(), null, null))
                .withRel(BaseType.SUBSCRIPTIONS);
        view.add(subscriptionLink);

        final var routeLink = linkTo(methodOn(ArtifactController.class)
                .getRoute(artifact.getId()))
                .withRel("route");
        view.add(routeLink);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return getSelfLink(entityId, ArtifactController.class);
    }
}
