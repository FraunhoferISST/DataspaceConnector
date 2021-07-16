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
import java.util.List;

import io.dataspaceconnector.model.keystore.KeystoreFactory;
import io.dataspaceconnector.model.proxy.ProxyFactory;
import io.dataspaceconnector.model.truststore.TruststoreFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        /* ACT */
        final var result = factory.create(new ConfigurationDesc());

        /* ASSERT */
        assertEquals(ConfigurationFactory.DEFAULT_CONNECTOR_ENDPOINT, result.getConnectorEndpoint());
        assertEquals(ConfigurationFactory.DEFAULT_DEPLOY_MODE, result.getDeployMode());
        assertEquals(ConfigurationFactory.DEFAULT_VERSION, result.getVersion());
        assertEquals(ConfigurationFactory.DEFAULT_CURATOR, result.getCurator());
        assertEquals(ConfigurationFactory.DEFAULT_OUTBOUND_VERSION, result.getOutboundModelVersion());
        assertEquals(ConfigurationFactory.DEFAULT_SECURITY_PROFILE, result.getSecurityProfile());
        assertTrue(result.getInboundModelVersion().isEmpty());
    }

    @Test
    void update_newConnectorEndpoint_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setConnectorEndpoint(URI.create("https://endpoint"));

        /* ACT */
        factory.update(config, desc);

        /* ASSERT */
        assertEquals(desc.getConnectorEndpoint(), config.getConnectorEndpoint());
    }

    @Test
    void update_newSecurityProfile_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setSecurityProfile(SecurityProfile.TRUST_PLUS_SECURITY);

        /* ACT */
        factory.update(config, desc);

        /* ASSERT */
        assertEquals(desc.getSecurityProfile(), config.getSecurityProfile());
    }

    @Test
    void update_newOutBoundModelVersion_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setOutboundModelVersion("fred");

        /* ACT */
        factory.update(config, desc);

        /* ASSERT */
        assertEquals(desc.getOutboundModelVersion(), config.getOutboundModelVersion());
    }

    @Test
    void update_newInBoundModelVersion_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setInboundModelVersion(List.of("Model", "Version"));

        /* ACT */
        factory.update(config, desc);

        /* ASSERT */
        assertEquals(desc.getInboundModelVersion(), config.getInboundModelVersion());
    }

    @Test
    void update_newMaintainer_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setMaintainer(URI.create("https://someMaintainer"));

        /* ACT */
        factory.update(config, desc);

        /* ASSERT */
        assertEquals(desc.getMaintainer(), config.getMaintainer());
    }

    @Test
    void update_newCurator_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setCurator(URI.create("https://someMaintainer"));

        /* ACT */
        factory.update(config, desc);

        /* ASSERT */
        assertEquals(desc.getCurator(), config.getCurator());
    }

    @Test
    void update_newVersion_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setVersion("fred");

        /* ACT */
        factory.update(config, desc);

        /* ASSERT */
        assertEquals(desc.getVersion(), config.getVersion());
    }

    @Test
    void update_newLogLevel_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setLogLevel(LogLevel.ERROR);

        /* ACT */
        factory.update(config, desc);

        /* ASSERT */
        assertEquals(desc.getLogLevel(), config.getLogLevel());
    }

    @Test
    void update_newDeployMode_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setDeployMode(DeployMode.PRODUCTIVE);

        /* ACT */
        factory.update(config, desc);

        /* ASSERT */
        assertEquals(desc.getDeployMode(), config.getDeployMode());
    }
}
