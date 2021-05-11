package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DataSourceDesc extends AbstractDescription<DataSource> {

    private String relativePath;

    private Authentication authentication;

    private DataSourceType dataSourceType;

    private List<GenericEndpoint> genericEndpoint;
}
