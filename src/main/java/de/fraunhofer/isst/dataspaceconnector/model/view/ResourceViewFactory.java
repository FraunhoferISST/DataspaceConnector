package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.EndpointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class ResourceViewFactory<T extends Resource> implements BaseViewFactory<T, ResourceView<T>> {
    @Autowired
    private EndpointService endpointService;

    @Override
    public ResourceView create(final Resource resource) {
        final var view = new ResourceView();
        view.setTitle(resource.getTitle());
        view.setDescription(resource.getDescription());
        view.setKeywords(resource.getKeywords());
        view.setPublisher(resource.getPublisher());
        view.setLanguage(resource.getLanguage());
        view.setLicence(resource.getLicence());
        view.setVersion(resource.getVersion());

        final var allRepresentationIds = resource.getRepresentations().keySet();
        final var allRepresentationEndpoints = new HashSet<EndpointId>();

        for(final var representationId : allRepresentationIds) {
            allRepresentationEndpoints.addAll(endpointService.getByEntity(representationId));
        }

        view.setRepresentations(allRepresentationEndpoints);

        final var allContractIds = resource.getContracts().keySet();
        final var allContractEndpoints = new HashSet<EndpointId>();

        for(final var contractId : allContractIds) {
            allContractEndpoints.addAll(endpointService.getByEntity(contractId));
        }

        view.setContracts(allContractEndpoints);

        return view;
    }
}
