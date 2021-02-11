package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.RepresentationView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.CommonService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/representations")
public class RepresentationController
        extends BaseResourceController<Representation, RepresentationDesc, RepresentationView,
        CommonService<Representation, RepresentationDesc, RepresentationView>> {
}
