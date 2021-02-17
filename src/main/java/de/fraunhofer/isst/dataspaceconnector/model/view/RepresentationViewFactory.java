package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class RepresentationViewFactory implements BaseViewFactory<Representation, RepresentationView> {
    @Autowired
    private EndpointService endpointService;

    @Override
    public RepresentationView create(Representation representation) {
            final var view = new RepresentationView();
            view.setTitle(representation.getTitle());
            view.setLanguage(representation.getLanguage());
            view.setMediaType(representation.getMediaType());

            final var allArtifactIds = representation.getArtifacts().keySet();
            final var allArtifactEndpoints = new HashSet<EndpointId>();

            for(final var artifactId : allArtifactIds) {
                allArtifactEndpoints.addAll(endpointService.getByEntity(artifactId));
            }

            view.setArtifacts(allArtifactEndpoints);

            return view;
    }
}
