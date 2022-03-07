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
package io.dataspaceconnector.model.app;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import io.dataspaceconnector.common.ids.policy.PolicyPattern;
import io.dataspaceconnector.model.artifact.LocalData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppFactoryTest {

    private AppFactory factory;

    @BeforeEach
    public void init() {
        this.factory = new AppFactory();
    }

    @Test
    void default_value_is_empty() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals("", AppFactory.DEFAULT_VALUE);
    }

    @Test
    void default_uri_is_app_com() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertEquals(URI.create("https://app.com"), AppFactory.DEFAULT_URI);
    }

    @Test
    void create_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.create(null));
    }

    @Test
    void create_validDesc_creationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new AppDesc());

        /* ASSERT */
        assertNull(result.getCreationDate());
        assertNotNull(result.getEndpoints());
    }

    @Test
    void create_validDesc_modificationDateNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new AppDesc());

        /* ASSERT */
        assertNull(result.getModificationDate());
    }

    @Test
    void create_validDesc_idNull() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        final var result = factory.create(new AppDesc());

        /* ASSERT */
        assertNull(result.getId());
    }

    @Test
    void update_sameDesc_returnFalse() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var versionBefore = app.getVersion();

        /* ACT */
        final var result = factory.update(app, new AppDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
        final var versionAfter = app.getVersion();
        assertEquals(versionBefore, versionAfter);
    }

    @Test
    void update_differentLanguage_setLanguage() {
        /* ARRANGE */
        final var desc = new AppDesc();
        desc.setLanguage("DE");

        final var app = factory.create(new AppDesc());

        /* ACT */
        factory.update(app, desc);

        /* ASSERT */
        assertEquals(desc.getLanguage(), app.getLanguage());
    }

    @Test
    void update_sameLanguage_returnFalse() {
        /* ARRANGE */
        final var desc = new AppDesc();
        desc.setLanguage("DE");

        final var app = factory.create(desc);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        Assertions.assertFalse(result);
    }

    @Test
    void update_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> factory.update(app, null));
    }

    @Test
    void update_newRemoteAddress_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var remoteAddress = URI.create("https://remote-address");
        final var desc = new AppDesc();
        desc.setRemoteAddress(remoteAddress);

        final var versionBefore = app.getVersion();

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
        final var versionAfter = app.getVersion();
        assertEquals(versionBefore + 1, versionAfter);
    }

    @Test
    void update_newKeywords_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var keywords = Collections.singletonList("keyword");
        final var desc = new AppDesc();
        desc.setKeywords(keywords);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    void update_newSupportedPolicies_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var policies = Collections.singletonList(PolicyPattern.PROVIDE_ACCESS);
        final var desc = new AppDesc();
        desc.setSupportedPolicies(policies);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    void update_newRemoteId_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var remoteId = URI.create("https://remote-id");
        final var desc = new AppDesc();
        desc.setRemoteId(remoteId);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    void update_newRuntimeEnvironment_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var environment = "runtime environment";
        final var desc = new AppDesc();
        desc.setRuntimeEnvironment(environment);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    void update_newDistributionService_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var distribution = URI.create("https://distribution-service");
        final var desc = new AppDesc();
        desc.setDistributionService(distribution);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    void update_newEndpointDocumentation_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var documentation = URI.create("https://endpoint-documentation");
        final var desc = new AppDesc();
        desc.setEndpointDocumentation(documentation);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    void update_newLicense_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var license = URI.create("https://license");
        final var desc = new AppDesc();
        desc.setLicense(license);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    void update_newLanguage_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var language = "EN";
        final var desc = new AppDesc();
        desc.setLanguage(language);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    void update_newSovereign_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var sovereign = URI.create("https://sovereign");
        final var desc = new AppDesc();
        desc.setSovereign(sovereign);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    void update_newPublisher_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var publisher = URI.create("https://publisher");
        final var desc = new AppDesc();
        desc.setPublisher(publisher);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    void update_newStorageConfig_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var storageConfig = "storage config";
        final var desc = new AppDesc();
        desc.setStorageConfig(storageConfig);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    void update_newEnvVariables_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var envVars = "environment variables";
        final var desc = new AppDesc();
        desc.setEnvVariables(envVars);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    void update_newDocs_willUpdate() {
        /* ARRANGE */
        final var app = factory.create(new AppDesc());
        final var docs = "docs";
        final var desc = new AppDesc();
        desc.setDocs(docs);

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    void setContainerId_validId_setId() {
        /* ARRANGE */
        final var app = new AppImpl();
        final var id = "newContainerId";

        /* ACT */
        factory.setContainerId(app, id);

        /* ASSERT */
        assertNotNull(app.getContainerId());
        assertEquals(id, app.getContainerId());
    }

    @Test
    void deleteContainerId_setId() {
        /* ARRANGE */
        final var app = new AppImpl();
        app.setContainerId("containerId");

        /* ACT */
        factory.deleteContainerId(app);

        /* ASSERT */
        assertNull(app.getContainerId());
    }

    @Test
    void setContainerName() {
        /* ARRANGE */
        final var app = new AppImpl();
        final var containerName = "container";

        /* ACT */
        factory.setContainerName(app, containerName);

        /* ASSERT */
        assertEquals(containerName, app.getContainerName());
    }

    /**
     * local data
     */

    @Test
    public void create_nullValue_returnEmpty() {
        /* ARRANGE */
        final var desc = new AppDesc();
        desc.setValue(null);

        /* ACT */
        final var result = (AppImpl) factory.create(desc);

        /* ASSERT */
        assertNull(((LocalData) result.getData()).getValue());
    }

    @Test
    public void update_setValue_returnValue() {
        /* ARRANGE */
        final var app = (AppImpl) factory.create(new AppDesc());

        final var desc = new AppDesc();
        desc.setValue("Some Value");

        /* ACT */
        factory.update(app, desc);

        /* ASSERT */
        assertArrayEquals(desc.getValue().getBytes(StandardCharsets.UTF_8),
                ((LocalData) app.getData()).getValue());
    }

    @Test
    public void update_differentValue_returnTrue() {
        /* ARRANGE */
        final var app = (AppImpl) factory.create(new AppDesc());

        final var desc = new AppDesc();
        desc.setValue("Random Value");

        /* ACT */
        final var result = factory.update(app, desc);

        /* ASSERT */
        Assertions.assertTrue(result);
    }

    @Test
    public void update_sameValue_returnFalse() {
        /* ARRANGE */
        final var app = (AppImpl) factory.create(new AppDesc());

        /* ACT */
        final var result = factory.update(app, new AppDesc());

        /* ASSERT */
        Assertions.assertFalse(result);
    }

}
