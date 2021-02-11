package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.view.RepresentationView;
import org.springframework.stereotype.Service;

@Service
public class BFFRepresentationService extends CommonService<Representation,
        RepresentationDesc, RepresentationView> {
}
