package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.ResourceView;
import org.springframework.stereotype.Service;

@Service
public class BFFResourceService extends CommonService<Resource, ResourceDesc,
        ResourceView> {
}
