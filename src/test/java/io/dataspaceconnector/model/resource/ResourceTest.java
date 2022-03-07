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

import java.util.ArrayList;

import io.dataspaceconnector.common.exception.NotImplemented;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceTest {
    @Test
    public void setCatalogs_anything_throwsNotImplemented() {
        /* ARRANGE */
        final var resource = new Resource();

        /* ACT && ASSERT */
        assertThrows(NotImplemented.class, () -> resource.setCatalogs(new ArrayList<>()));
    }

    @Test
    public void getCatalogs_nothing_throwsNotImplemented() {
        /* ARRANGE */
        final var resource = new Resource();

        /* ACT && ASSERT */
        assertThrows(NotImplemented.class, resource::getCatalogs);
    }

    @Test
    public void default_sample_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertTrue(ResourceFactory.DEFAULT_SAMPLES.length == 0);
    }

    @Test
    public void default_paymentMethod_is_free() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(PaymentMethod.UNDEFINED, ResourceFactory.DEFAULT_PAYMENT_MODALITY);
    }
}
