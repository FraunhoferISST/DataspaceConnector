package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
final class TemplateBuilderRequestedResource
        extends TemplateBuilder<RequestedResource, RequestedResourceDesc> {
    @Autowired
    public TemplateBuilderRequestedResource(
            final ResourceService<RequestedResource, RequestedResourceDesc> resourceService,
            final AbstractResourceRepresentationLinker<RequestedResource> resourceRepresentationLinker,
            final AbstractResourceContractLinker<RequestedResource> resourceContractLinker,
            final RepresentationService representationService,
            final RepresentationArtifactLinker representationArtifactLinker,
            final ContractService contractService, final ContractRuleLinker contractRuleLinker,
            final ArtifactService artifactService, final RuleService ruleService) {
        super(resourceService, resourceRepresentationLinker, resourceContractLinker,
                representationService, representationArtifactLinker, contractService,
                contractRuleLinker, artifactService, ruleService);
    }
}
