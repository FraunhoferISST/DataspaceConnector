package io.dataspaceconnector.configuration;

import io.dataspaceconnector.config.BootstrapConfiguration;
import io.dataspaceconnector.services.resources.CatalogService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BootstrapConfigurationTest {

    @Autowired
    CatalogService catalogService;

    @Autowired
    BootstrapConfiguration bootstrapConfiguration;

    @BeforeEach
    public void prepare() {
        catalogService.getAll(Pageable.unpaged()).forEach( catalog ->
                catalogService.delete(catalog.getId()));
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Test
    public void bootstrap_files_registerCatalogs() {
        /* ARRANGE */
        // nothing to arrange

        /* ACT */
        bootstrapConfiguration.bootstrap();

        /* ASSERT */
        assertEquals(2, catalogService.getAll(Pageable.unpaged()).getSize());
    }

}
