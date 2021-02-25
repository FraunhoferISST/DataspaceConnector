package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ArtifactTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ContractTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RepresentationTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ResourceTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RuleTemplate;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ContractRuleLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ContractService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.RepresentationArtifactLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.RepresentationService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceContractLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceRepresentationLinker;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.RuleService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.UUID;

@RequiredArgsConstructor
public class TemplateBuilder42<T extends Resource, D extends ResourceDesc<T>> {
    private static Logger LOGGER = LoggerFactory.getLogger(TemplateBuilder42.class);

    /***********************************************************************************************
     * Resources
     **********************************************************************************************/

    private final @NonNull ResourceService<T, D> resourceService;

    private final @NonNull ResourceRepresentationLinker<T> resourceRepresentationLinker;

    private final @NonNull ResourceContractLinker<T> resourceContractLinker;

    public T build(final ResourceTemplate<D> template) {
        final var representationIds = new HashSet<UUID>();
        for (final var representation : template.getRepresentations()) {
            representationIds.add(this.build(representation).getId());
        }

        final var contractIds = new HashSet<UUID>();
        for (final var contract : template.getContracts()) {
            contractIds.add(this.build(contract));
        }

        final T resource;
        final var desc = template.getDesc();
        if (template.getDesc().getStaticId() == null) {
            resource = resourceService.create(desc);
        } else {
            if (resourceService.doesExist(desc.getStaticId())) {
                resource = resourceService.update(desc.getStaticId(), desc);
            } else {
                resource = resourceService.create(desc);
            }
        }

        try {
            resourceRepresentationLinker.replace(resource.getId(), representationIds);
            resourceContractLinker.replace(resource.getId(), contractIds);
        } catch (Exception exception) {
            LOGGER.debug("Failed to build resource. [exception=({})]", exception.getMessage());
        }

        return resource;
    }

    /***********************************************************************************************
     * Representations
     **********************************************************************************************/

    private final @NonNull RepresentationService representationService;

    private final @NonNull RepresentationArtifactLinker representationArtifactLinker;

    public Representation build(final RepresentationTemplate template) {
        final var artifactIds = new HashSet<UUID>();
        for (final var artifact : template.getArtifacts()) {
            artifactIds.add(this.build(artifact).getId());
        }

        final Representation representation;
        if (template.getDesc().getStaticId() == null) {
            representation = representationService.create(template.getDesc());
        } else {
            if (representationService.doesExist(template.getDesc().getStaticId())) {
                representation = representationService
                        .update(template.getDesc().getStaticId(), template.getDesc());
            } else {
                representation = representationService.create(template.getDesc());
            }
        }

        representationArtifactLinker.replace(representation.getId(), artifactIds);

        return representation;
    }

    /***********************************************************************************************
     * Contracts
     **********************************************************************************************/

    private final @NonNull ContractService contractService;

    private final @NonNull ContractRuleLinker contractRuleLinker;

    public UUID build(final ContractTemplate template) {
        final var ruleIds = new HashSet<UUID>();
        for (final var rule : template.getRules()) {
            ruleIds.add(this.build(rule).getId());
        }

        final var contractId = contractService.create(template.getDesc()).getId();
        contractRuleLinker.add(contractId, ruleIds);

        return contractId;
    }

    /***********************************************************************************************
     * Artifacts
     **********************************************************************************************/

    private final @NonNull ArtifactService artifactService;

    public Artifact build(final ArtifactTemplate template) {
        return artifactService.create(template.getDesc());
    }

    /***********************************************************************************************
     * Rules
     **********************************************************************************************/

    private final @NonNull RuleService ruleService;

    public ContractRule build(final RuleTemplate template) {
        return ruleService.create(template.getDesc());
    }
}

@Service
final class TemplateBuilderOfferedResource
        extends TemplateBuilder42<OfferedResource, OfferedResourceDesc> {
    @Autowired
    public TemplateBuilderOfferedResource(
            final ResourceService<OfferedResource, OfferedResourceDesc> resourceService,
            final ResourceRepresentationLinker<OfferedResource> resourceRepresentationLinker,
            final ResourceContractLinker<OfferedResource> resourceContractLinker,
            final RepresentationService representationService,
            final RepresentationArtifactLinker representationArtifactLinker,
            final ContractService contractService, final ContractRuleLinker contractRuleLinker,
            final ArtifactService artifactService, final RuleService ruleService) {
        super(resourceService, resourceRepresentationLinker, resourceContractLinker,
                representationService, representationArtifactLinker, contractService,
                contractRuleLinker, artifactService, ruleService);
    }
}

@Service
final class TemplateBuilderRequestedResource
        extends TemplateBuilder42<RequestedResource, RequestedResourceDesc> {
    @Autowired
    public TemplateBuilderRequestedResource(
            final ResourceService<RequestedResource, RequestedResourceDesc> resourceService,
            final ResourceRepresentationLinker<RequestedResource> resourceRepresentationLinker,
            final ResourceContractLinker<RequestedResource> resourceContractLinker,
            final RepresentationService representationService,
            final RepresentationArtifactLinker representationArtifactLinker,
            final ContractService contractService, final ContractRuleLinker contractRuleLinker,
            final ArtifactService artifactService, final RuleService ruleService) {
        super(resourceService, resourceRepresentationLinker, resourceContractLinker,
                representationService, representationArtifactLinker, contractService,
                contractRuleLinker, artifactService, ruleService);
    }
}
