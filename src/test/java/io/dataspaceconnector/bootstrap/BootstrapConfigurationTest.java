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
package io.dataspaceconnector.bootstrap;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import io.dataspaceconnector.services.messages.MessageService;
import io.dataspaceconnector.services.resources.CatalogService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;

import java.net.URI;
import java.util.HashMap;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {BootstrapConfiguration.class})
public class BootstrapConfigurationTest {

    @MockBean
    CatalogService catalogService;

    @Autowired
    BootstrapConfiguration configuration;

    @MockBean
    private MessageService messageService;

    @BeforeEach
    public void prepare() {
        catalogService.getAll(Pageable.unpaged()).forEach( catalog -> catalogService.delete(catalog.getId()));
    }

    @SneakyThrows
    @Test
    public void bootstrap_files_registerCatalogs() {
        /* ARRANGE */
        Mockito.doReturn(true).when(configuration).registerAtBroker(Mockito.any(), Mockito.any());

        /* ACT */
        configuration.bootstrap();

        /* ASSERT */
        assertEquals(2, catalogService.getAll(Pageable.unpaged()).getSize());
    }

    @Test
    @SneakyThrows
    void registerAtBroker_validInput_returnTrue() {
        /* ARRANGE */
        final var properties = new Properties();
        properties.put("broker.register.https://someBroker", "https://someBroker");
        final var idsResources = new HashMap<URI, Resource>();
        idsResources.put(URI.create("https://someResource"), new ResourceBuilder().build());

        Mockito.doReturn(true).when(messageService).sendConnectorUpdateMessage(Mockito.any());
        Mockito.doReturn(true).when(messageService).sendResourceUpdateMessage(Mockito.any(), Mockito.any());

        /* ACT */
        final var result = configuration.registerAtBroker(properties, idsResources);

        /* ASSERT */
        assertTrue(result);
    }
}
