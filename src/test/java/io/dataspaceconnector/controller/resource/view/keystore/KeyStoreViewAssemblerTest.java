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
import io.dataspaceconnector.model.keystore.KeystoreDesc;
import io.dataspaceconnector.model.keystore.KeystoreFactory;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeyStoreViewAssemblerTest {

    @Test
    public void create_ValidKeyStore_returnKeyStoreView(){
        /* ARRANGE */
        final var shouldLookLike = getKeyStore();

        /* ACT */
        final var after = getKeyStoreView();

        /* ASSERT */
        assertEquals(after.getLocation(), shouldLookLike.getLocation());
    }

    private Keystore getKeyStore(){
        final var factory = new KeystoreFactory();
        return factory.create(getKeyStoreDesc());
    }

    private KeystoreDesc getKeyStoreDesc() {
        final var desc = new KeystoreDesc();
        desc.setLocation(URI.create("https://keystore"));
        desc.setPassword("secret");

        return desc;
    }

    private KeystoreView getKeyStoreView(){
        final var assembler = new KeystoreViewAssembler();
        return assembler.toModel(getKeyStore());
    }
}
