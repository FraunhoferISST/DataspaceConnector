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

import io.dataspaceconnector.controller.configurations.AppControllers.AppStoreController;
import io.dataspaceconnector.controller.configurations.AppControllers.AppStoreToApps;
import io.dataspaceconnector.model.appstore.AppStore;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembles the REST resource for an app store.
 */
@Component
public class AppStoreViewAssembler
        implements RepresentationModelAssembler<AppStore, AppStoreView>, SelfLinking {

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, AppStoreController.class);
    }

    @Override
    public final AppStoreView toModel(final AppStore appStore) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(appStore, AppStoreView.class);
        view.add(getSelfLink(appStore.getId()));

        final var appLink = linkTo(methodOn(AppStoreToApps.class)
                .getResource(appStore.getId(), null, null))
                .withRel("apps");
        view.add(appLink);

        return view;
    }
}
