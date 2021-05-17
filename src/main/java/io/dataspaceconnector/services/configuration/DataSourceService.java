package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.DataSource;
import io.dataspaceconnector.model.DataSourceDesc;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for data sources.
 */
@Service
@NoArgsConstructor
public class DataSourceService extends BaseEntityService<DataSource, DataSourceDesc> {
}
