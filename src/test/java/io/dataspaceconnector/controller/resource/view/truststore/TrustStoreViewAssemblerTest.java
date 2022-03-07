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
package io.dataspaceconnector.controller.resource.view.truststore;

import java.net.URI;

import io.dataspaceconnector.model.truststore.Truststore;
import io.dataspaceconnector.model.truststore.TruststoreDesc;
import io.dataspaceconnector.model.truststore.TruststoreFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrustStoreViewAssemblerTest {

    @Test
    public void create_ValidTrustStore_returnTrustStoreView(){
        /* ARRANGE */
        final var shouldLookLike = getTrustStore();

        /* ACT */
        final var after = getTrustStoreView();

        /* ASSERT */
        assertEquals(after.getLocation(), shouldLookLike.getLocation());
    }

    private Truststore getTrustStore(){
        final var factory = new TruststoreFactory();
        return factory.create(getTrustStoreDesc());
    }

    private TruststoreDesc getTrustStoreDesc() {
        final var desc = new TruststoreDesc();
        desc.setLocation(URI.create("https://truststore"));
        desc.setPassword("secret");

        return desc;
    }

    private TruststoreView getTrustStoreView(){
        final var assembler = new TruststoreViewAssembler();
        return assembler.toModel(getTrustStore());
    }
}
