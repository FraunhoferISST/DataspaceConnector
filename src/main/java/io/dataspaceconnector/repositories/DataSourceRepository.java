package io.dataspaceconnector.repositories;


import io.dataspaceconnector.model.DataSource;
import org.springframework.stereotype.Repository;

/**
 * Repository for data sources.
 */
@Repository
public interface DataSourceRepository extends BaseEntityRepository<DataSource> {
}
