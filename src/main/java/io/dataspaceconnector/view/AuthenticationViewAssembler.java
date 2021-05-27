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

import io.dataspaceconnector.controller.configurations.ConfigmanagerController.AuthenticationController;
import io.dataspaceconnector.model.Authentication;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Assembles the REST resource for an authentication.
 */
@Component
public class AuthenticationViewAssembler implements
        RepresentationModelAssembler<Authentication, AuthenticationView>, SelfLinking {


    @Override
    public Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId, AuthenticationController.class);
    }

    @Override
    public AuthenticationView toModel(final Authentication authentication) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(authentication, AuthenticationView.class);
        view.add(getSelfLink(authentication.getId()));

        return view;
    }
}
