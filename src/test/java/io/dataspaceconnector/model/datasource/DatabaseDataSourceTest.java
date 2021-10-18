package io.dataspaceconnector.model.datasource;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseDataSourceTest {

    @Test
    void constructor_newInstanceWithTypeDatabase() {
        /* ACT */
        final var dataSource = new DatabaseDataSource();

        /* ASSERT */
        assertEquals(DataSourceType.DATABASE, dataSource.getType());
    }

}
