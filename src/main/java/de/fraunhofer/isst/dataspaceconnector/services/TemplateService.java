package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ArtifactTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ContractTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RepresentationTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ResourceTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RuleTemplate;
import de.fraunhofer.isst.dataspaceconnector.utils.MappingUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    /**
     * Service for mapping ids to connector objects.
     */
    private final @NonNull MappingUtils mapper;

    public ResourceTemplate<RequestedResourceDesc> getResourceTemplate(final Resource resource) {
        return mapper.fromIdsResource(resource);
    }

    public List<ContractTemplate> getContractTemplates(final Resource resource) {
        final var list = new ArrayList<ContractTemplate>();
        final var contractList = resource.getContractOffer();
        for (final var contract : contractList) {
            final var contractTemplate = mapper.fromIdsContract(contract);

            contractTemplate.setRules(getRuleTemplates(contract));
            list.add(contractTemplate);
        }

        return list;
    }

    public List<RepresentationTemplate> getRepresentationTemplates(final Resource resource) {
        final var list = new ArrayList<RepresentationTemplate>();
        final var representationList = resource.getRepresentation();
        for (final var representation : representationList) {
            final var representationTemplate = mapper.fromIdsRepresentation(representation);
            final var artifactTemplateList = getArtifactTemplates(representation);

            representationTemplate.setArtifacts(artifactTemplateList);
            list.add(representationTemplate);
        }

        return list;
    }

    public List<ArtifactTemplate> getArtifactTemplates(final Representation representation) {
        final var list = new ArrayList<ArtifactTemplate>();
        final var artifactList = representation.getInstance();
        for (final var artifact : artifactList) {
            final var artifactTemplate = mapper.fromIdsArtifact(artifact);
            list.add(artifactTemplate);
        }

        return list;
    }

    public List<RuleTemplate> getRuleTemplates(final Contract contract) {
        final var list = new ArrayList<RuleTemplate>();

        final var permissionList = contract.getPermission();
        for (final var rule : permissionList) {
            final var ruleTemplate = mapper.fromIdsRule(rule);
            list.add(ruleTemplate);
        }

        final var prohibitionList = contract.getProhibition();
        for (final var rule : prohibitionList) {
            final var ruleTemplate = mapper.fromIdsRule(rule);
            list.add(ruleTemplate);
        }

        final var obligationList = contract.getObligation();
        for (final var rule : obligationList) {
            final var ruleTemplate = mapper.fromIdsRule(rule);
            list.add(ruleTemplate);
        }

        return list;
    }
}
