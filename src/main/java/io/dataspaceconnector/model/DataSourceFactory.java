package io.dataspaceconnector.model;

import org.springframework.stereotype.Component;

/**
 * Creates and updates data sources
 */
@Component
public class DataSourceFactory implements AbstractFactory<DataSource, DataSourceDesc> {

    @Override
    public DataSource create(DataSourceDesc desc) {
        return null;
    }

    @Override
    public boolean update(DataSource entity, DataSourceDesc desc) {
        return false;
    }
}
