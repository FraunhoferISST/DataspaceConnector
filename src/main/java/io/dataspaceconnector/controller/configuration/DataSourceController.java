package io.dataspaceconnector.controller.configuration;

import io.dataspaceconnector.controller.resource.BaseResourceController;
import io.dataspaceconnector.controller.resource.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.tag.ResourceName;
import io.dataspaceconnector.model.datasource.DataSource;
import io.dataspaceconnector.model.datasource.DataSourceDesc;
import io.dataspaceconnector.service.configuration.DataSourceService;
import io.dataspaceconnector.view.datasource.DataSourceView;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing data sources.
 */
@RestController
@RequestMapping("/api/datasources")
@RequiredArgsConstructor
@Tag(name = ResourceName.DATA_SOURCES, description = ResourceDescription.DATA_SOURCES)
public class DataSourceController extends BaseResourceController<DataSource,
        DataSourceDesc, DataSourceView, DataSourceService> {
}
