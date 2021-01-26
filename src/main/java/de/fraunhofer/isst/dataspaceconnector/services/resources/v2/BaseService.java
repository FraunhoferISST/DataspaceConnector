package de.fraunhofer.isst.dataspaceconnector.services.resources.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseDescription;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseResource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;

import java.util.List;
import java.util.UUID;

public interface BaseService<T extends BaseResource,
        D extends BaseDescription<T>> {
    T create(D desc);

    EndpointId create(String basePath, D desc);

    T update(UUID id, D desc);

    EndpointId update(EndpointId endpointId, D desc);

    T get(UUID id);

    T get(EndpointId endpointId);

    Endpoint getEndpoint(EndpointId endpointId);

    List<EndpointId> getAll();

    boolean doesExist(UUID id);

    boolean doesExist(EndpointId endpointId);

    void delete(EndpointId endpointId);
}
