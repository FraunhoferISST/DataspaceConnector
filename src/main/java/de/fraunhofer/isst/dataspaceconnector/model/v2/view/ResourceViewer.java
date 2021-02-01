package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceViewer implements BaseViewer<Resource, ResourceView>{
    @Override
    public ResourceView create(Resource view) {
        return null;
    }
}
