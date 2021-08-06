package io.dataspaceconnector.controller.resource.type;

import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.controller.resource.base.BaseResourceController;
import io.dataspaceconnector.controller.resource.base.tag.ResourceDescription;
import io.dataspaceconnector.controller.resource.base.tag.ResourceName;
import io.dataspaceconnector.controller.resource.view.datasource.DataSourceView;
import io.dataspaceconnector.model.datasource.DataSource;
import io.dataspaceconnector.model.datasource.DataSourceDesc;
import io.dataspaceconnector.service.resource.type.DataSourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Offers the endpoints for managing data sources.
 */
@RestController
@RequestMapping(BasePath.DATA_SOURCES)
@RequiredArgsConstructor
@Tag(name = ResourceName.DATA_SOURCES, description = ResourceDescription.DATA_SOURCES)
public class DataSourceController extends BaseResourceController<DataSource,
        DataSourceDesc, DataSourceView, DataSourceService> {
}
