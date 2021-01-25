package de.fraunhofer.isst.dataspaceconnector.model.v2;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ResourceFactory implements BaseFactory<Resource, ResourceDesc> {
    @Override
    public Resource create(final ResourceDesc desc) {
        var resource = new Resource();
        resource.setRepresentations(new HashMap());
        resource.setContracts(new HashMap());
        resource.setVersion(-1);

        update(resource, desc);

        return resource;
    }

    @Override
    public boolean update(final Resource resource, final ResourceDesc desc) {
        var needsVersionUpdate = false;

        var newTitle = desc.getTitle() != null ? desc.getTitle() : "";
        if (newTitle.equals(resource.getTitle())) {
            resource.setTitle(newTitle);
            needsVersionUpdate = true;
        }

        var newDescription = desc.getDescription() != null
                ? desc.getDescription() : "";
        if (newDescription.equals(resource.getDescription())) {
            resource.setDescription(newDescription);
            needsVersionUpdate = true;
        }

        var newKeywords = desc.getKeywords() != null ? desc.getKeywords() : "";
        if (newKeywords.equals(resource.getKeywords())) {
            resource.setKeywords(newKeywords);
            needsVersionUpdate = true;
        }

        var newPublisher = desc.getPublisher() != null ? desc.getPublisher()
                : "";
        if (newPublisher.equals(resource.getPublisher())) {
            resource.setPublisher(newPublisher);
            needsVersionUpdate = true;
        }

        var newLanguage = desc.getLanguage() != null ? desc.getLanguage() : "";
        if (newLanguage.equals(resource.getLanguage())) {
            resource.setLanguage(newLanguage);
            needsVersionUpdate = true;
        }

        var newLicence = desc.getLicence() != null ? desc.getLicence() : "";
        if (newLicence.equals(resource.getLicence())) {
            resource.setLicence(newLicence);
            needsVersionUpdate = true;
        }

        if (needsVersionUpdate) {
            resource.setVersion(resource.getVersion() + 1);
        }

        return needsVersionUpdate;
    }
}
