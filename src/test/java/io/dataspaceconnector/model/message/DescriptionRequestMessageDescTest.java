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
package io.dataspaceconnector.model.message;

import java.net.URI;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DescriptionRequestMessageDescTest {
    @Test
    public void defaultConstructor_nothing_emptyDesc() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = new DescriptionRequestMessageDesc();

        /* ASSERT */
        assertNull(result.getRequestedElement());
        assertNull(result.getRecipient());
    }

    @Test
    public void allArgsConstructor_valid_validDesc() {
        /* ARRANGE */
        final var recipient = URI.create("someRecipient");
        final var element = URI.create("someElement");

        /* ACT */
        final var result = new DescriptionRequestMessageDesc(recipient, element);

        /* ASSERT */
        assertEquals(recipient, result.getRecipient());
        assertEquals(element, result.getRequestedElement());
    }

    @Test
    public void equals_everything_valid() {
        EqualsVerifier.simple().forClass(DescriptionRequestMessageDesc.class).verify();
    }
}
