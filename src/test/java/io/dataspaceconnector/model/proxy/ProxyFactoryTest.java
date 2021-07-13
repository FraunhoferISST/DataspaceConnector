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
package io.dataspaceconnector.model.proxy;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProxyFactoryTest {

    final ProxyDesc desc = new ProxyDesc();
    final ProxyFactory factory = new ProxyFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var location = URI.create("https://localhost:8080");
        final var exclusion = "https://localhost:8081";
        desc.setLocation(location);
        desc.setExclusions(List.of(exclusion));

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(location, result.getLocation());
        assertFalse(result.getExclusions().isEmpty());
        assertEquals(exclusion, result.getExclusions().get(0));
    }
}
