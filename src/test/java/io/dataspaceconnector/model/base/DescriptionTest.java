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
package io.dataspaceconnector.model.base;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DescriptionTest {
    @Test
    public void defaultConstructor_nothing_nullAdditional() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = new Description();

        /* ASSERT */
        assertNull(result.getAdditional());
        assertNull(result.getBootstrapId());
    }

    @Test
    public void addOverflow_repeatSameInsert_oneKeyValuePair() {
        /* ARRANGE */
        final var key = "Some key";
        final var value = "Some value";

        /* ACT */
        final var result = new Description();
        result.addOverflow(key, value);
        result.addOverflow(key, value);

        /* ASSERT */
        assertEquals(1, result.getAdditional().size());
        assertTrue(result.getAdditional().containsKey(key));
        assertEquals(value, result.getAdditional().get(key));
    }

    @Test
    public void addOverflow_nullKey_noInsert() {
        /* ARRANGE */
        final var value = "Some value";

        /* ACT */
        final var result = new Description();
        result.addOverflow(null, value);

        /* ASSERT */
        assertEquals(0, result.getAdditional().size());
    }

    @Test
    public void addOverflow_nullValue_noInsert() {
        /* ARRANGE */
        final var key = "Some key";

        /* ACT */
        final var result = new Description();
        result.addOverflow(key, null);

        /* ASSERT */
        assertEquals(0, result.getAdditional().size());
    }

    @Test
    public void addOverflow_null_noInsert() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = new Description();
        result.addOverflow(null, null);

        /* ASSERT */
        assertEquals(0, result.getAdditional().size());
    }

    @Test
    public void equals_everything_valid() {
        EqualsVerifier.simple().forClass(Description.class).verify();
    }
}
