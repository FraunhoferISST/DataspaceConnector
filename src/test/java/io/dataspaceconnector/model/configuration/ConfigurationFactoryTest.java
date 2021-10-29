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

import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.model.keystore.KeystoreDesc;
import io.dataspaceconnector.model.keystore.KeystoreFactory;
import io.dataspaceconnector.model.proxy.ProxyDesc;
import io.dataspaceconnector.model.proxy.ProxyFactory;
import io.dataspaceconnector.model.truststore.TruststoreDesc;
import io.dataspaceconnector.model.truststore.TruststoreFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigurationFactoryTest {

    private final ConnectorConfig connectorConfig = new ConnectorConfig();
    private final ProxyFactory proxyFactory = new ProxyFactory();
    private final TruststoreFactory truststoreFactory = new TruststoreFactory();
    private final KeystoreFactory keystoreFactory = new KeystoreFactory();

    private ConfigurationFactory factory;

    @BeforeEach
    public void init() {
        factory = new ConfigurationFactory(proxyFactory, truststoreFactory, keystoreFactory,
                connectorConfig);
        connectorConfig.setDefaultVersion("6.0.0");
    }

    @Test
    void create_emptyDesc_returnNew() {
        /* ARRANGE */

        /* ACT */
        final var result = factory.create(new ConfigurationDesc());

        /* ASSERT */
        assertEquals(ConfigurationFactory.DEFAULT_CONNECTOR_ID, result.getConnectorId());
        assertEquals(URI.create(ConfigurationFactory.DEFAULT_CONNECTOR_ID
                + ConfigurationFactory.DEFAULT_ENDPOINT), result.getDefaultEndpoint());
        assertEquals(ConfigurationFactory.DEFAULT_DEPLOY_MODE, result.getDeployMode());
        assertEquals(connectorConfig.getDefaultVersion(), result.getVersion());
        assertEquals(ConfigurationFactory.DEFAULT_CURATOR, result.getCurator());
        assertEquals(connectorConfig.getInboundVersions(), result.getInboundModelVersion());
        assertEquals(connectorConfig.getOutboundVersion(), result.getOutboundModelVersion());
        assertEquals(ConfigurationFactory.DEFAULT_SECURITY_PROFILE, result.getSecurityProfile());
        assertEquals(ConfigurationFactory.DEFAULT_STATUS, result.getStatus());
    }

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setConnectorId(URI.create("https://connectorId"));
        desc.setDefaultEndpoint(URI.create("https://endpoint"));
        desc.setSecurityProfile(SecurityProfile.TRUST_PLUS_SECURITY);
        desc.setOutboundModelVersion("fred");
        desc.setInboundModelVersion(List.of("Model", "Version"));
        desc.setMaintainer(URI.create("https://someMaintainer"));
        desc.setCurator(URI.create("https://someMaintainer"));
        desc.setVersion("fred");
        desc.setLogLevel(LogLevel.ERROR);
        desc.setDeployMode(DeployMode.PRODUCTIVE);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(desc.getConnectorId(), result.getConnectorId());
        assertEquals(desc.getDefaultEndpoint(), result.getDefaultEndpoint());
        assertEquals(desc.getDeployMode(), result.getDeployMode());
        assertEquals(desc.getVersion(), result.getVersion());
        assertEquals(desc.getCurator(), result.getCurator());
        assertEquals(desc.getOutboundModelVersion(), result.getOutboundModelVersion());
        assertEquals(desc.getSecurityProfile(), result.getSecurityProfile());
        assertEquals(desc.getInboundModelVersion(), result.getInboundModelVersion());
    }

    @Test
    void update_newConnectorEndpoint_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setDefaultEndpoint(URI.create("https://endpoint"));

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getDefaultEndpoint(), config.getDefaultEndpoint());
    }

    @Test
    void update_newSecurityProfile_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setSecurityProfile(SecurityProfile.TRUST_PLUS_SECURITY);

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getSecurityProfile(), config.getSecurityProfile());
    }

    @Test
    void update_newOutBoundModelVersion_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setOutboundModelVersion("fred");

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getOutboundModelVersion(), config.getOutboundModelVersion());
    }

    @Test
    void update_newInBoundModelVersion_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setInboundModelVersion(List.of("Model", "Version"));

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getInboundModelVersion(), config.getInboundModelVersion());
    }

    @Test
    void update_newConnectorId_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setConnectorId(URI.create("https://connectorId"));

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getConnectorId(), config.getConnectorId());
    }

    @Test
    void update_newMaintainer_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setMaintainer(URI.create("https://someMaintainer"));

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getMaintainer(), config.getMaintainer());
    }

    @Test
    void update_newCurator_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setCurator(URI.create("https://someMaintainer"));

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getCurator(), config.getCurator());
    }

    @Test
    void update_newVersion_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setVersion("fred");

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getVersion(), config.getVersion());
    }

    @Test
    void update_newLogLevel_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setLogLevel(LogLevel.ERROR);

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getLogLevel(), config.getLogLevel());
    }

    @Test
    void update_newDeployMode_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        desc.setDeployMode(DeployMode.PRODUCTIVE);

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getDeployMode(), config.getDeployMode());
    }

    @Test
    void update_newKeystore_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        final var keystoreSettings = new KeystoreDesc();
        keystoreSettings.setPassword("RANDOM");
        desc.setKeystore(keystoreSettings);
        final var expected = keystoreFactory.create(desc.getKeystore());

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(expected, config.getKeystore());
    }

    @Test
    void update_newTruststore_willUpdate() {
        /* ARRANGE */
        final var config = factory.create(new ConfigurationDesc());
        final var desc = new ConfigurationDesc();
        final var truststoreSettings = new TruststoreDesc();
        truststoreSettings.setPassword("RANDOM");
        desc.setTruststore(truststoreSettings);
        final var expected = truststoreFactory.create(desc.getTruststore());

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(expected, config.getTruststore());
    }

    @Test
    void update_sameConnectorEndpoint_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setDefaultEndpoint(URI.create("https://endpoint"));
        final var config = factory.create(desc);

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(desc.getDefaultEndpoint(), config.getDefaultEndpoint());
    }

    @Test
    void update_sameSecurityProfile_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setSecurityProfile(SecurityProfile.TRUST_PLUS_SECURITY);
        final var config = factory.create(desc);

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(desc.getSecurityProfile(), config.getSecurityProfile());
    }

    @Test
    void update_sameOutBoundModelVersion_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setOutboundModelVersion("fred");
        final var config = factory.create(desc);

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(desc.getOutboundModelVersion(), config.getOutboundModelVersion());
    }

    @Test
    void update_sameInBoundModelVersion_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setInboundModelVersion(List.of("Model", "Version"));
        final var config = factory.create(desc);

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(desc.getInboundModelVersion(), config.getInboundModelVersion());
    }

    @Test
    void update_sameConnectorId_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setConnectorId(URI.create("https://connectorId"));
        final var config = factory.create(desc);

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(desc.getConnectorId(), config.getConnectorId());
    }

    @Test
    void update_sameMaintainer_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setMaintainer(URI.create("https://someMaintainer"));
        final var config = factory.create(desc);

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(desc.getMaintainer(), config.getMaintainer());
    }

    @Test
    void update_sameCurator_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setCurator(URI.create("https://someMaintainer"));
        final var config = factory.create(desc);

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(desc.getCurator(), config.getCurator());
    }

    @Test
    void update_sameVersion_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setVersion("fred");
        final var config = factory.create(desc);

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(desc.getVersion(), config.getVersion());
    }

    @Test
    void update_sameLogLevel_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setLogLevel(LogLevel.ERROR);
        final var config = factory.create(desc);

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(desc.getLogLevel(), config.getLogLevel());
    }

    @Test
    void update_sameDeployMode_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setDeployMode(DeployMode.PRODUCTIVE);
        final var config = factory.create(desc);

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(desc.getDeployMode(), config.getDeployMode());
    }

    @Test
    void update_sameKeystore_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setKeystore(new KeystoreDesc());
        final var config = factory.create(desc);
        final var expected = keystoreFactory.create(desc.getKeystore());

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(expected, config.getKeystore());
    }

    @Test
    void update_sameTruststore_willNotUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setTruststore(new TruststoreDesc());
        final var config = factory.create(desc);
        final var expected = truststoreFactory.create(desc.getTruststore());

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(expected, config.getTruststore());
    }

    @Test
    void update_newNullProxy_willUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setProxy(new ProxyDesc());
        final var config = factory.create(desc);

        /* ACT */
        final var result = factory.update(config, new ConfigurationDesc());

        /* ASSERT */
        assertTrue(result);
        assertNull(config.getProxy());
    }

    @Test
    void update_newProxy_willUpdate() {
        /* ARRANGE */
        final var desc = new ConfigurationDesc();
        desc.setProxy(new ProxyDesc());
        final var config = factory.create(new ConfigurationDesc());
        final var expected = proxyFactory.create(desc.getProxy());

        /* ACT */
        final var result = factory.update(config, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(expected, config.getProxy());
    }
}
