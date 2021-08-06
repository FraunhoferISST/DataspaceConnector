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
package io.dataspaceconnector.controller.resource.view.configuration;

import io.dataspaceconnector.controller.resource.type.ConfigurationController;
import io.dataspaceconnector.controller.resource.view.keystore.KeystoreViewAssembler;
import io.dataspaceconnector.controller.resource.view.proxy.ProxyViewAssembler;
import io.dataspaceconnector.controller.resource.view.truststore.TruststoreViewAssembler;
import io.dataspaceconnector.controller.resource.view.util.SelfLinking;
import io.dataspaceconnector.controller.resource.view.util.ViewAssemblerHelper;
import io.dataspaceconnector.model.configuration.Configuration;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Assembles the REST resource for a configuration.
 */
@Component
public class ConfigurationViewAssembler implements
        RepresentationModelAssembler<Configuration, ConfigurationView>, SelfLinking {

    @Override
    public final Link getSelfLink(final UUID entityId) {
        return ViewAssemblerHelper.getSelfLink(entityId,
                ConfigurationController.class);
    }

    @Override
    public final ConfigurationView toModel(final Configuration configuration) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(configuration, ConfigurationView.class);
        view.add(getSelfLink(configuration.getId()));

        if (configuration.getProxy() != null) {
            view.setProxy(new ProxyViewAssembler().toModel(configuration.getProxy()));
        }

        if (configuration.getTruststore() != null) {
            view.setTrustStore(new TruststoreViewAssembler()
                    .toModel(configuration.getTruststore()));
        }

        if (configuration.getKeystore() != null) {
            view.setKeyStore(new KeystoreViewAssembler().toModel(configuration.getKeystore()));
        }

        return view;
    }
}
