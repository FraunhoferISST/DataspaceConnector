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

import io.dataspaceconnector.model.datasource.DataSource;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.repository.GenericEndpointRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class GenericEndpointServiceTest {

    @MockBean
    private DataSourceService dataSourceService;

    @MockBean
    private GenericEndpointRepository repository;

    @SpyBean
    private GenericEndpointService service;

    @Test
    public void setGenericEndpoint_validInput_setsEndpoint() {
        /* ARRANGE */
        final var endpointId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
        final var dataSourceId = UUID.fromString("550e8400-e29b-11d4-a716-446655441111");

        final var endpoint = new GenericEndpoint();
        Mockito.doReturn(endpoint).when(service).get(endpointId);

        final var datasource = new DataSource();
        Mockito.when(dataSourceService.get(eq(dataSourceId))).thenReturn(datasource);

        /* ACT */
        service.setGenericEndpointDataSource(endpointId, dataSourceId);

        /* ASSERT */
        Mockito.verify(repository, Mockito.atLeastOnce()).saveAndFlush(eq(endpoint));
        assertEquals(endpoint.getDataSource(), datasource);
    }
}
