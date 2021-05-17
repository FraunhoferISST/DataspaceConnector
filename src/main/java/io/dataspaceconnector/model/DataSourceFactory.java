package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

/**
 * Creates and updates data sources
 */
@Component
public class DataSourceFactory implements AbstractFactory<DataSource, DataSourceDesc> {

    /**
     * The default string.
     */
    private final static String DEFAULT_STRING = "unknown";

    /**
     * @param desc The description of the entity.
     * @return The new data source entity.
     */
    @Override
    public DataSource create(DataSourceDesc desc) {
        return null;
    }

    /**
     * @param dataSource The data source entity.
     * @param desc       The description of the new entity.
     * @return True, if data source is updated.
     */
    @Override
    public boolean update(DataSource entity, DataSourceDesc desc) {
        return false;
    }
}
