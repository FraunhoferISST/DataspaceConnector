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
package io.dataspaceconnector.view.endpoint;

import java.net.URI;

import io.dataspaceconnector.controller.configuration.EndpointController;
import io.dataspaceconnector.controller.resource.view.ViewAssemblerHelper;
import io.dataspaceconnector.model.endpoint.ConnectorEndpoint;
import io.dataspaceconnector.model.endpoint.ConnectorEndpointDesc;
import io.dataspaceconnector.model.endpoint.ConnectorEndpointFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ConnectorEndpointViewAssemblerTest {

    @Test
    public void create_ValidConnectorEndpoint_returnConnectorEndpointView() {
        /* ARRANGE */
        final var shouldLookLike = getConnectorEndpoint();
        final var link = ViewAssemblerHelper.
                getSelfLink(shouldLookLike.getId(), EndpointController.class);

        /* ACT */
        final var after = getConnectorEndpointView();

        /* ASSERT */
        assertEquals(after.getLocation(), shouldLookLike.getLocation());
        assertFalse(after.getLinks().isEmpty());
        assertTrue(after.getLinks().contains(link));
    }

    private ConnectorEndpoint getConnectorEndpoint() {
        final var factory = new ConnectorEndpointFactory();
        return factory.create(getConnectorEndpointDesc());
    }

    private ConnectorEndpointDesc getConnectorEndpointDesc() {
        final var desc = new ConnectorEndpointDesc();
        desc.setLocation(URI.create("https://ids.com"));
        return desc;
    }

    private ConnectorEndpointView getConnectorEndpointView() {
        final var assembler = new ConnectorEndpointViewAssembler();
        return assembler.toModel(getConnectorEndpoint());
    }
}
