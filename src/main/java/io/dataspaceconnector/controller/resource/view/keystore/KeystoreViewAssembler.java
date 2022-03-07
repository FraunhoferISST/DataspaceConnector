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
package io.dataspaceconnector.controller.resource.view.keystore;

import io.dataspaceconnector.model.keystore.Keystore;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.server.RepresentationModelAssembler;

/**
 * The view assembler for the key store.
 */
public class KeystoreViewAssembler implements RepresentationModelAssembler<Keystore, KeystoreView> {

    @Override
    public final KeystoreView toModel(final Keystore store) {
        return new ModelMapper().map(store, KeystoreView.class);
    }
}
