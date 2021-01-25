package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.springframework.stereotype.Component;

@Component
public class RepresentationFactory implements BaseFactory<Representation,
        RepresentationDesc> {
    @Override
    public Representation create(final RepresentationDesc desc) {
        var representation = new Representation();
        update(representation, desc);

        return representation;
    }

    @Override
    public boolean update(final Representation representation,
                          final RepresentationDesc desc) {
        var hasBeenUpdated = false;

        var newTitle = desc.getTitle() != null ? desc.getTitle() : "";
        if (!newTitle.equals(representation.getTitle())) {
            representation.setTitle(newTitle);
            hasBeenUpdated = true;
        }

        var newLanguage = desc.getLanguage() != null ? desc.getLanguage() : "";
        if (!newLanguage.equals(representation.getLanguage())) {
            representation.setLanguage(newLanguage);
            hasBeenUpdated = true;
        }

        var newMediaType = desc.getType() != null ? desc.getType() : "";
        if (!newMediaType.equals(representation.getMediaType())) {
            representation.setMediaType(newMediaType);
            hasBeenUpdated = true;
        }

        return hasBeenUpdated;
    }
}
