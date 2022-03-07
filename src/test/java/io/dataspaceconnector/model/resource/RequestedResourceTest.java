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
package io.dataspaceconnector.model.resource;

import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.representation.Representation;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class RequestedResourceTest {

    @Test
    public void equalsAndHash_will_pass() {
        final var c1 = new Catalog();
        final var c2 = new Catalog();
         ReflectionTestUtils.setField(c2, "title", "haha");

         final var r1 = new Representation();
         final var r2 = new Representation();
         ReflectionTestUtils.setField(r2, "title", "haha");

         final var co1 = new Contract();
         final var co2 = new Contract();
         ReflectionTestUtils.setField(co2, "title", "haha");

        EqualsVerifier.simple().forClass(RequestedResource.class)
                      .withPrefabValues(Catalog.class, c1, c2)
                      .withPrefabValues(Representation.class, r1, r2)
                      .withPrefabValues(Contract.class, co1, co2)
                      .withIgnoredFields("id")
                      .verify();
    }
}
