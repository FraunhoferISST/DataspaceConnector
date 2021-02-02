package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendTofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.v2.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.v2.view.RepresentationView;
import org.springframework.stereotype.Service;

@Service
class BFFRepresentationService extends CommonService<Representation,
        RepresentationDesc, RepresentationView> {
}
