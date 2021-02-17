package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend.BFFResourceService;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;

@Service
public class IdsResourceService {

    @Autowired
    private BFFResourceService<OfferedResource, ?, ?> resourceService;

    @Autowired
    IdsUtils idsUtils;

//    public List<Resource> getAllResources() {
//        return resourceService.getAll().parallelStream().map(x -> idsUtils.getAsResource(resourceService.get(x)))
//                .collect(Collectors.toList());
//    }


    public de.fraunhofer.iais.eis.Resource getResource(EndpointId endpointId) {
        return null;
        //return idsUtils.getAsResource(resourceService.get(endpointId));
    }

    public de.fraunhofer.iais.eis.Resource getResource(URI resourceUri) {
        // TODO map to endpoint id
        // return getResource(null);
        return null;
    }

    // TODO ArrayList really?
    public ArrayList<Resource> getAllOfferedResources() {
        // TODO
        return null;
    }

}
