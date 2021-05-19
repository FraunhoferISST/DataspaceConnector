package io.dataspaceconnector.configuration;

import io.dataspaceconnector.config.BootstrapConfiguration;
import io.dataspaceconnector.model.RequestedResource;
import io.dataspaceconnector.model.RequestedResourceDesc;
import io.dataspaceconnector.services.resources.CatalogService;
import io.dataspaceconnector.services.resources.TemplateBuilder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BootstrapConfigurationTest {

    @Mock
    TemplateBuilder<RequestedResource, RequestedResourceDesc> templateBuilder;

    @Autowired
    CatalogService catalogService;

    @Autowired
    @InjectMocks
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
