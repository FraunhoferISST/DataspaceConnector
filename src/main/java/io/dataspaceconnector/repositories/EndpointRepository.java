package io.dataspaceconnector.repositories;

import io.dataspaceconnector.model.endpoint.Endpoint;
import org.springframework.stereotype.Repository;

@Repository
public interface EndpointRepository extends BaseEntityRepository<Endpoint>  {
}
