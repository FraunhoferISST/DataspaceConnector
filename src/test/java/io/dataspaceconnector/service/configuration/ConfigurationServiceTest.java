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
package io.dataspaceconnector.service.configuration;

import java.util.Optional;
import java.util.UUID;

import de.fraunhofer.ids.messaging.core.config.ConfigProducer;
import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
import io.dataspaceconnector.repository.ConfigurationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class ConfigurationServiceTest {

    @SpyBean
    private ConfigurationRepository repo;

    @SpyBean
    private ConfigProducer configProducer;

    @Autowired
    private ConfigurationService service;

    @Test
    public void swapActiveConfig_hasActiveConfig_willSetPassedConfigAsActiveAndTheOldOneAsInActive()
            throws ConfigUpdateException {
        /* ARRANGE */
        final var configId = UUID.randomUUID();
        Mockito.doReturn(Optional.empty()).when(repo).findActive();

        /* ACT */
        service.swapActiveConfig(configId);

        /* ASSERT */
        Mockito.verify(repo, Mockito.atLeastOnce()).setActive(eq(configId));
        Mockito.verify(repo, Mockito.atLeastOnce()).unsetActive();
    }
}
