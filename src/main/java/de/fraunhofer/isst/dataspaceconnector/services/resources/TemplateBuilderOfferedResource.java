package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
final class TemplateBuilderOfferedResource
        extends TemplateBuilder<OfferedResource, OfferedResourceDesc> {
    @Autowired
    public TemplateBuilderOfferedResource(
            final ResourceService<OfferedResource, OfferedResourceDesc> resourceService,
            final AbstractResourceRepresentationLinker<OfferedResource> resourceRepresentationLinker,
            final AbstractResourceContractLinker<OfferedResource> resourceContractLinker,
            final RepresentationService representationService,
            final RepresentationArtifactLinker representationArtifactLinker,
            final ContractService contractService, final ContractRuleLinker contractRuleLinker,
            final ArtifactService artifactService, final RuleService ruleService) {
        super(resourceService, resourceRepresentationLinker, resourceContractLinker,
                representationService, representationArtifactLinker, contractService,
                contractRuleLinker, artifactService, ruleService);
    }
}
