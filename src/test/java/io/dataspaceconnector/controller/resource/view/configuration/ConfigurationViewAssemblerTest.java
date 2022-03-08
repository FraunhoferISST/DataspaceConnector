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
package io.dataspaceconnector.controller.resource.view.configuration;

import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.controller.resource.type.ConfigurationController;
import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import io.dataspaceconnector.model.configuration.Configuration;
import io.dataspaceconnector.model.configuration.ConfigurationDesc;
import io.dataspaceconnector.model.configuration.ConfigurationFactory;
import io.dataspaceconnector.model.configuration.DeployMode;
import io.dataspaceconnector.model.configuration.LogLevel;
import io.dataspaceconnector.model.configuration.SecurityProfile;
import io.dataspaceconnector.model.keystore.KeystoreDesc;
import io.dataspaceconnector.model.keystore.KeystoreFactory;
import io.dataspaceconnector.model.proxy.ProxyDesc;
import io.dataspaceconnector.model.proxy.ProxyFactory;
import io.dataspaceconnector.model.truststore.TruststoreDesc;
import io.dataspaceconnector.model.truststore.TruststoreFactory;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigurationViewAssemblerTest {

    private final ConnectorConfig connectorConfig = new ConnectorConfig();

    @Test
    public void create_ValidBroker_returnBrokerView() {
        /* ARRANGE */
        final var shouldLookLike = getConfiguration();
        final var link = new SelfLinkHelper().
                getSelfLink(shouldLookLike.getId(), ConfigurationController.class);

        /* ACT */
        final var after = getConfigurationView();

        /* ASSERT */
        assertEquals(after.getCurator(), shouldLookLike.getCurator());
        assertEquals(after.getMaintainer(), shouldLookLike.getMaintainer());
        assertEquals(after.getDeployMode(), shouldLookLike.getDeployMode());
        assertEquals(after.getDefaultEndpoint(), shouldLookLike.getDefaultEndpoint());
        assertEquals(after.getLogLevel(), shouldLookLike.getLogLevel());
        assertTrue(after.getInboundModelVersion().isEmpty());
        assertEquals(after.getOutboundModelVersion(), shouldLookLike.getOutboundModelVersion());
        assertEquals(after.getSecurityProfile(), shouldLookLike.getSecurityProfile());
        assertEquals(after.getVersion(), shouldLookLike.getVersion());
        assertEquals(after.getTitle(), shouldLookLike.getTitle());
        assertEquals(after.getDescription(), shouldLookLike.getDescription());
        assertEquals(after.getProxy().getLocation(), shouldLookLike.getProxy().getLocation());
        assertTrue(after.getProxy().getExclusions().isEmpty());
        assertEquals(after.getKeyStore().getLocation(), shouldLookLike.getKeystore().getLocation());
        assertEquals(after.getProxy().getLocation(), shouldLookLike.getProxy().getLocation());
        assertTrue(after.getProxy().getExclusions().isEmpty());
        assertTrue(after.getLinks().contains(link));
    }

    private ConfigurationView getConfigurationView() {
        final var assembler = new ConfigurationViewAssembler();
        return assembler.toModel(getConfiguration());
    }

    private Configuration getConfiguration() {
        final var proxyFactory = new ProxyFactory();
        final var trustStoreFactory = new TruststoreFactory();
        final var keyStoreFactory = new KeystoreFactory();
        final var factory = new ConfigurationFactory(proxyFactory, trustStoreFactory,
                keyStoreFactory, connectorConfig);
        return factory.create(getConfigurationDesc());
    }

    private ConfigurationDesc getConfigurationDesc(){
        final var desc = new ConfigurationDesc();
        desc.setCurator(URI.create("https://ids.com"));
        desc.setMaintainer(URI.create("https://ids.com"));
        desc.setDeployMode(DeployMode.TEST);
        desc.setDefaultEndpoint(URI.create("https://localhost:8080/api/ids/data"));
        desc.setLogLevel(LogLevel.OFF);
        desc.setInboundModelVersion(new ArrayList<>());
        desc.setOutboundModelVersion("4.1.0");
        desc.setSecurityProfile(SecurityProfile.BASE_SECURITY);
        desc.setVersion("6.0.0");
        desc.setTitle("MyConfig");
        desc.setDescription("My Config Description");

        final var trustStoreDesc = new TruststoreDesc();
        trustStoreDesc.setLocation(URI.create("https://truststore.com"));
        trustStoreDesc.setPassword("password");
        desc.setTruststore(trustStoreDesc);

        final var keyStoreDesc = new KeystoreDesc();
        keyStoreDesc.setLocation(URI.create("https://keystore.com"));
        keyStoreDesc.setPassword("password");
        desc.setKeystore(keyStoreDesc);

        final var proxyDesc = new ProxyDesc();
        proxyDesc.setLocation(URI.create("https://localhost:8081"));
        proxyDesc.setExclusions(new ArrayList<>());
        desc.setProxy(proxyDesc);

        return desc;
    }
}
