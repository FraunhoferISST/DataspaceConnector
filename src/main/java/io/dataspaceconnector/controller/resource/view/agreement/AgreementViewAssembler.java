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
package io.dataspaceconnector.controller.resource.view.agreement;

import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.relation.AgreementsToArtifactsController;
import io.dataspaceconnector.controller.resource.type.AgreementController;
import io.dataspaceconnector.controller.resource.view.util.SelfLinking;
import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import io.dataspaceconnector.model.agreement.Agreement;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles the REST resource for an agreement.
 */
@Component
public class AgreementViewAssembler extends SelfLinkHelper
        implements RepresentationModelAssembler<Agreement, AgreementView>, SelfLinking {
    @Override
    public final AgreementView toModel(final Agreement agreement) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(agreement, AgreementView.class);
        view.add(getSelfLink(agreement.getId()));

        final var artifactLink = WebMvcLinkBuilder
                .linkTo(methodOn(AgreementsToArtifactsController.class)
                        .getResource(agreement.getId(), null, null))
                .withRel(BaseType.ARTIFACTS);
        view.add(artifactLink);

        return view;
    }

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return getSelfLink(entityId, AgreementController.class);
    }
}
