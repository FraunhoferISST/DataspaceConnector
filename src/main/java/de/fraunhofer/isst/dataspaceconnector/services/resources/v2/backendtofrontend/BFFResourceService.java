package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.BaseDescription;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.view.BaseView;
import de.fraunhofer.isst.dataspaceconnector.model.view.OfferedResourceView;
import de.fraunhofer.isst.dataspaceconnector.model.view.RequestedResourceView;
import org.springframework.stereotype.Service;

public class BFFResourceService<T extends Resource, D extends BaseDescription<T>, V extends BaseView<T>> extends CommonService<T, D, V> {
}

@Service
final class BFFRequestedResourceService extends BFFResourceService<RequestedResource,
        RequestedResourceDesc, RequestedResourceView> {
}

@Service
final class BFFOfferedResourceService extends BFFResourceService<OfferedResource,
        OfferedResourceDesc,
        OfferedResourceView> {
}
