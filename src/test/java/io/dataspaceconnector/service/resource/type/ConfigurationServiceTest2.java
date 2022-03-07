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
package io.dataspaceconnector.service.resource.type;

import io.dataspaceconnector.model.configuration.Configuration;
import io.dataspaceconnector.model.configuration.ConfigurationDesc;
import io.dataspaceconnector.repository.ConfigurationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ConfigurationServiceTest2 {

    @SpyBean
    private ConfigurationRepository repo;

    @SpyBean
    private ConfigurationService service;

    @Test
    public void update_validInput_returnConfiguration() {
        /* ARRANGE */
        final String title = "Title";
        final var config = new Configuration();
        final var desc = new ConfigurationDesc();
        desc.setTitle(title);
        final var entityId = UUID.randomUUID();

        Mockito.doReturn(config).when(service).update(entityId, desc);
        Mockito.doReturn(Optional.of(config)).when(repo).findActive();
        Mockito.doReturn(Optional.of(config)).when(service).findActiveConfig();

        /* ACT */
        final var result = service.update(entityId, desc);

        /* ASSERT */
        assertNotNull(result);
    }
}
