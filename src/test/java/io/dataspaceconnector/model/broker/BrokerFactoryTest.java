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
package io.dataspaceconnector.model.broker;

import io.dataspaceconnector.model.base.RegistrationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BrokerFactoryTest {

    final BrokerDesc desc = new BrokerDesc();
    final BrokerFactory factory = new BrokerFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var title = "MyBroker";
        desc.setTitle(title);
        desc.setStatus(RegistrationStatus.UNREGISTERED);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(title, result.getTitle());
        assertEquals(RegistrationStatus.UNREGISTERED, result.getStatus());
    }
}
