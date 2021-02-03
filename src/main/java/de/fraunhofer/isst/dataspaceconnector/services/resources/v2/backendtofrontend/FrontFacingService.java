package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseDescription;
import de.fraunhofer.isst.dataspaceconnector.model.v2.BaseResource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.Endpoint;
import de.fraunhofer.isst.dataspaceconnector.model.v2.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.BaseView;

import java.util.Set;

public interface FrontFacingService<T extends BaseResource,
        D extends BaseDescription<T>, V extends BaseView<T>> {

    EndpointId create(String basePath, D desc);

    EndpointId update(EndpointId endpointId, D desc);

    V get(EndpointId endpointId);

    Endpoint getEndpoint(EndpointId endpointId);

    Set<EndpointId> getAll();

    boolean doesExist(EndpointId endpointId);

    void delete(EndpointId endpointId);
}
