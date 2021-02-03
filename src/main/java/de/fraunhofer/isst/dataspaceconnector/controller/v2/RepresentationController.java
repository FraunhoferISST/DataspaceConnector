package de.fraunhofer.isst.dataspaceconnector.controller.v2;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.v2.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.RepresentationView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.CommonService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.CommonUniDirectionalLinkerService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.RepresentationArtifactLinker;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/representations")
public class RepresentationController
        extends BaseResourceController<Representation, RepresentationDesc, RepresentationView,
        CommonService<Representation, RepresentationDesc, RepresentationView>> {
}

@RestController
@RequestMapping("/representations/{id}/artifacts")
class RepresentationArtifactController extends BaseResourceChildController
        <CommonUniDirectionalLinkerService<RepresentationArtifactLinker>> {
}
