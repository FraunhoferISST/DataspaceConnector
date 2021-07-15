package io.dataspaceconnector.service.configuration;

import java.util.UUID;

import io.dataspaceconnector.model.datasource.DataSource;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.repository.GenericEndpointRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

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
