package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.v2.ResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.ResourceView;
import org.springframework.stereotype.Service;

@Service
public class BFFResourceService extends CommonService<Resource, ResourceDesc,
        ResourceView> {
}
