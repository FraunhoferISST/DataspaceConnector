package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Describing data source's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataSourceDesc extends AbstractDescription<DataSource> {

    /**
     * The relative path of the data source.
     */
    private String relativePath;

    /**
     * The authentication for the data source.
     */
    private Authentication authentication;

    /**
     * The type of the data source.
     */
    private DataSourceType dataSourceType;

    /**
     * The list of generic endpoints.
     */
    private List<GenericEndpoint> genericEndpoint;
}
