package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.DataSource;
import io.dataspaceconnector.model.DataSourceFactory;
import io.dataspaceconnector.model.GenericEndpoint;
import io.dataspaceconnector.repositories.DataSourceRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = {DataSourceService.class})
public class DataSourceServiceTest {

    @MockBean
    private DataSourceRepository dataSourceRepository;

    @MockBean
    private DataSourceFactory dataSourceFactory;

    @Autowired
    @InjectMocks
    private DataSourceService dataSourceService;

    DataSource dataSource = getDataSource();

    GenericEndpoint genericEndpoint = getGenericEndpoint();

    /**********************************************************************
     * SETUP
     **********************************************************************/
    @BeforeEach
    public void init() {
        Mockito.when(dataSourceFactory.create(any())).thenReturn(dataSource);
        Mockito.when(dataSourceRepository.saveAndFlush(Mockito.eq(dataSource)))
                .thenReturn(dataSource);
        Mockito.when(dataSourceRepository.findById(Mockito.eq(dataSource.getId())))
                .thenReturn(Optional.of(dataSource));
    }

    /**********************************************************************
     * GET
     **********************************************************************/
    @Test
    public void get_DataSource_returnListOfGenericEndpoints() {
        /* ARRANGE */
        // Nothing to arrange here.
        dataSource.getGenericEndpoint().add(genericEndpoint);

        /* ACT */
        final var result = dataSourceService.get(dataSource.getId());

        assertEquals(dataSource.getGenericEndpoint().size(), result.getGenericEndpoint().size());
    }

    /**********************************************************************
     * UTILITIES
     **********************************************************************/
    @SneakyThrows
    private DataSource getDataSource() {
        final var dataSourceConstructor = DataSource.class.getConstructor();

        final var dataSource = dataSourceConstructor.newInstance();

        final var idField = dataSource.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(dataSource, UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        final var genericEndpointField = dataSource.getClass()
                .getDeclaredField("genericEndpoint");
        genericEndpointField.setAccessible(true);
        genericEndpointField.set(dataSource, new ArrayList<GenericEndpoint>());

        return dataSource;
    }

    @SneakyThrows
    private GenericEndpoint getGenericEndpoint() {
        final var constructor = GenericEndpoint.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var genericEndpoint = constructor.newInstance();

        final var absolutePathField = genericEndpoint.getClass().getDeclaredField("absolutePath");
        absolutePathField.setAccessible(true);
        absolutePathField.set(genericEndpoint, "https://absolutepath");

        return genericEndpoint;
    }

}
