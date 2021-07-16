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
package io.dataspaceconnector.model.configuration;

import java.net.URI;

import io.dataspaceconnector.model.keystore.KeystoreFactory;
import io.dataspaceconnector.model.proxy.ProxyFactory;
import io.dataspaceconnector.model.truststore.TruststoreFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConfigurationFactoryTest {

    final ConfigurationDesc desc = new ConfigurationDesc();

    final ProxyFactory proxyFactory = new ProxyFactory();
    final TruststoreFactory truststoreFactory = new TruststoreFactory();
    final KeystoreFactory keystoreFactory =  new KeystoreFactory();

    final ConfigurationFactory factory =
            new ConfigurationFactory(proxyFactory,truststoreFactory, keystoreFactory);

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var accessUrl = URI.create("https://localhost:8080/api/ids/data");
        final var curator = URI.create("https://www.isst.fraunhofer.de/");
        desc.setConnectorEndpoint(accessUrl);
        desc.setDeployMode(DeployMode.TEST);
        desc.setCurator(curator);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(accessUrl, result.getConnectorEndpoint());
        assertEquals(DeployMode.TEST, result.getDeployMode());
        assertEquals(curator, result.getCurator());
        assertNotNull(result.getInboundModelVersion());
    }
}
