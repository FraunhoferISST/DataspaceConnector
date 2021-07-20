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
package io.dataspaceconnector.model.endpoint;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = {EndpointFactoryProxy.class, ConnectorEndpointFactory.class, GenericEndpointFactory.class})
class EndpointFactoryProxyTest {

    @SpyBean
    private ConnectorEndpointFactory connectorFactory;

    @SpyBean
    private GenericEndpointFactory genericFactory;

    @Autowired
    private EndpointFactoryProxy proxy;

    @Test
    public void create_newConnectorEndpoint() {
        assertTrue(proxy.create(new ConnectorEndpointDesc()) instanceof ConnectorEndpoint);
    }

    @Test
    public void create_newGenericEndpoint() {
        assertTrue(proxy.create(new GenericEndpointDesc()) instanceof GenericEndpoint);
    }

    @Test
    public void update_callConnectorEndpointFactory_andWillUpdate() {
        /* ARRANGE */
        final var endpoint = new ConnectorEndpoint();
        final var desc = new ConnectorEndpointDesc();

        /* ACT */
        assertTrue(proxy.update(endpoint, desc));

        /* ASSERT */
        Mockito.verify(connectorFactory, Mockito.atLeastOnce()).updateInternal(eq(endpoint), eq(desc));
    }

    @Test
    public void update_callConnectorEndpointFactory_andWillNotUpdate() {
        /* ARRANGE */
        final var endpoint = connectorFactory.create(new ConnectorEndpointDesc());
        final var desc = new ConnectorEndpointDesc();

        /* ACT */
        assertFalse(proxy.update(endpoint, desc));

        /* ASSERT */
        Mockito.verify(connectorFactory, Mockito.atLeastOnce()).updateInternal(eq(endpoint), eq(desc));
    }

    @Test
    public void update_callGenericEndpointFactory_andWillUpdate() {
        /* ARRANGE */
        final var endpoint = new GenericEndpoint();
        final var desc = new GenericEndpointDesc();

        /* ACT */
        assertTrue(proxy.update(endpoint, desc));

        /* ASSERT */
        Mockito.verify(genericFactory, Mockito.atLeastOnce()).updateInternal(eq(endpoint), eq(desc));
    }

    @Test
    public void update_callGenericEndpointFactory_andWillNotUpdate() {
        /* ARRANGE */
        final var endpoint = genericFactory.create(new GenericEndpointDesc());
        final var desc = new GenericEndpointDesc();

        /* ACT */
        assertFalse(proxy.update(endpoint, desc));

        /* ASSERT */
        Mockito.verify(genericFactory, Mockito.atLeastOnce()).updateInternal(eq(endpoint), eq(desc));
    }
}
