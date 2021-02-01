package de.fraunhofer.isst.dataspaceconnector.model.v2.view;

import de.fraunhofer.isst.dataspaceconnector.model.v2.Representation;
import org.springframework.stereotype.Component;

@Component
public class RepresentationViewer implements BaseViewer<Representation, RepresentationView>{
    @Override
    public RepresentationView create(Representation view) {
        return null;
    }
}
