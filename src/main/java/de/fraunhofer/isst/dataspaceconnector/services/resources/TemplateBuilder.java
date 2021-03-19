package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.util.HashSet;
import java.util.UUID;

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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Builds and links entities from templates.
 * @param <T> The resource type.
 * @param <D>  The resource description type.
 */
@RequiredArgsConstructor
public class TemplateBuilder<T extends Resource, D extends ResourceDesc<T>> {
    /**
     * The class level logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateBuilder.class);

    /**
     * The service for resources.
     */
    private final @NonNull ResourceService<T, D> resourceService;

    /**
     * The linker for resource-representation relations.
     */
    private final @NonNull AbstractResourceRepresentationLinker<T> resourceRepresentationLinker;

    /**
     * The linker for resource-contract relations.
     */
    private final @NonNull AbstractResourceContractLinker<T> resourceContractLinker;

    /**
     * The service for representations.
     */
    private final @NonNull RepresentationService representationService;

    /**
     * The linker for representation-artifact relations.
     */
    private final @NonNull RepresentationArtifactLinker representationArtifactLinker;

    /**
     * The service for contracts.
     */
    private final @NonNull ContractService contractService;

    /**
     * The linker for contract-rule relations.
     */
    private final @NonNull ContractRuleLinker contractRuleLinker;

    /**
     * The service for artifacts.
     */
    private final @NonNull ArtifactService artifactService;

    /**
     * The service for rules.
     */
    private final @NonNull RuleService ruleService;

    /**
     * Build a resource and dependencies from a template.
     * @param template The resource template.
     * @return The new resource.
     */
    public T build(final ResourceTemplate<D> template) {
        final var representationIds = new HashSet<UUID>();
        for (final var representation : template.getRepresentations()) {
            representationIds.add(this.build(representation).getId());
        }

        final var contractIds = new HashSet<UUID>();
        for (final var contract : template.getContracts()) {
            contractIds.add(this.build(contract));
        }

        T resource;
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

    /**
     * Build a representation and dependencies from template.
     * @param template The representation template.
     * @return The new representation.
     */
    public Representation build(final RepresentationTemplate template) {
        final var artifactIds = new HashSet<UUID>();
        for (final var artifact : template.getArtifacts()) {
            artifactIds.add(this.build(artifact).getId());
        }

        Representation representation;
        if (template.getDesc().getStaticId() == null) {
            representation = representationService.create(template.getDesc());
        } else {
            if (representationService.doesExist(template.getDesc().getStaticId())) {
                representation = representationService.update(
                        template.getDesc().getStaticId(), template.getDesc());
            } else {
                representation = representationService.create(template.getDesc());
            }
        }

        representationArtifactLinker.replace(representation.getId(), artifactIds);

        return representation;
    }

    /**
     * Build a contract and dependencies from a template.
     * @param template The contract template.
     * @return The new contract.
     */
    public UUID build(final ContractTemplate template) {
        final var ruleIds = new HashSet<UUID>();
        for (final var rule : template.getRules()) {
            ruleIds.add(this.build(rule).getId());
        }

        final var contractId = contractService.create(template.getDesc()).getId();
        contractRuleLinker.add(contractId, ruleIds);

        return contractId;
    }

    /**
     * Build an artifact and dependencies from a template.
     * @param template The artifact template.
     * @return The new artifact.
     */
    public Artifact build(final ArtifactTemplate template) {
        return artifactService.create(template.getDesc());
    }

    /**
     * Build a rule and dependencies from a template.
     * @param template The rule template.
     * @return The new rule.
     */
    public ContractRule build(final RuleTemplate template) {
        return ruleService.create(template.getDesc());
    }
}

/**
 * Template builder for offered resources.
 */
@Service
final class TemplateBuilderOfferedResource
        extends TemplateBuilder<OfferedResource, OfferedResourceDesc> {
    /**
     * Default constructor.
     * @param resourceService The resource service.
     * @param resourceRepresentationLinker The resource-representation service.
     * @param resourceContractLinker The resource-contract service.
     * @param representationService The representation service.
     * @param representationArtifactLinker The representation-artifact service.
     * @param contractService The contract service.
     * @param contractRuleLinker The contract-rule service.
     * @param artifactService The artifact service.
     * @param ruleService The rule service.
     */
    @Autowired
    TemplateBuilderOfferedResource(
            final ResourceService<OfferedResource, OfferedResourceDesc> resourceService,
            final AbstractResourceRepresentationLinker<OfferedResource>
                    resourceRepresentationLinker,
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

/**
 * Template builder for requested resources.
 */
@Service
final class TemplateBuilderRequestedResource
        extends TemplateBuilder<RequestedResource, RequestedResourceDesc> {
    /**
     * Default constructor.
     * @param resourceService The resource service.
     * @param resourceRepresentationLinker The resource-representation service.
     * @param resourceContractLinker The resource-contract service.
     * @param representationService The representation service.
     * @param representationArtifactLinker The representation-artifact service.
     * @param contractService The contract service.
     * @param contractRuleLinker The contract-rule service.
     * @param artifactService The artifact service.
     * @param ruleService The rule service.
     */
    @Autowired
    TemplateBuilderRequestedResource(
            final ResourceService<RequestedResource, RequestedResourceDesc> resourceService,
            final AbstractResourceRepresentationLinker<RequestedResource>
                    resourceRepresentationLinker,
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
