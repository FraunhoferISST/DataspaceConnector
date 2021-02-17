package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.BaseDescription;
import de.fraunhofer.isst.dataspaceconnector.model.BaseEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.view.BaseView;

import java.util.Set;

public interface FrontFacingService<T extends BaseEntity,
        D extends BaseDescription<T>, V extends BaseView<T>> {

    EndpointId create(String basePath, D desc);

    EndpointId update(EndpointId endpointId, D desc);

    V get(EndpointId endpointId);

    Endpoint getEndpoint(EndpointId endpointId);

    Set<EndpointId> getAll();

    boolean doesExist(EndpointId endpointId);

    void delete(EndpointId endpointId);
}
