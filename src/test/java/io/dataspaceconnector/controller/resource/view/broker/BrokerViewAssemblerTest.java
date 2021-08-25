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
package io.dataspaceconnector.controller.resource.view.broker;

import java.net.URI;

import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import io.dataspaceconnector.controller.resource.type.BrokerController;
import io.dataspaceconnector.model.base.RegistrationStatus;
import io.dataspaceconnector.model.broker.Broker;
import io.dataspaceconnector.model.broker.BrokerDesc;
import io.dataspaceconnector.model.broker.BrokerFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BrokerViewAssemblerTest {

    @Test
    public void create_ValidBroker_returnBrokerView() {
        /* ARRANGE */
        final var shouldLookLike = getBroker();
        final var link = new SelfLinkHelper()
                .getSelfLink(shouldLookLike.getId(), BrokerController.class);

        /* ACT */
        final var after = getBrokerView();

        /* ASSERT */
        assertEquals(after.getLocation(), shouldLookLike.getLocation());
        assertEquals(after.getTitle(), shouldLookLike.getTitle());
        assertEquals(after.getStatus(), shouldLookLike.getStatus());
        assertTrue(after.getLinks().contains(link));
    }

    private BrokerView getBrokerView() {
        final var assembler = new BrokerViewAssembler();
        return assembler.toModel(getBroker());
    }


    private Broker getBroker() {
        final var factory = new BrokerFactory();
        return factory.create(getBrokerDesc());
    }

    private BrokerDesc getBrokerDesc() {
        final var desc = new BrokerDesc();
        desc.setLocation(URI.create("https://broker.ids.isst.fraunhofer.de/infrastructure/"));
        desc.setTitle("IDS Broker");
        desc.setStatus(RegistrationStatus.UNREGISTERED);
        return desc;
    }
}
