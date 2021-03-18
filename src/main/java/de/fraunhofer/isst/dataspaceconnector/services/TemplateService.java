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
import de.fraunhofer.isst.dataspaceconnector.utils.PolicyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    public ResourceTemplate<RequestedResourceDesc> getResourceTemplate(final Resource resource) {
        return MappingUtils.fromIdsResource(resource);
    }

    public List<ContractTemplate> getContractTemplates(final Resource resource) {
        final var list = new ArrayList<ContractTemplate>();
        final var contractList = resource.getContractOffer();
        for (final var contract : contractList) {
            final var contractTemplate = MappingUtils.fromIdsContract(contract);

            contractTemplate.setRules(getRuleTemplates(contract));
            list.add(contractTemplate);
        }

        return list;
    }

    public List<RepresentationTemplate> getRepresentationTemplates(final Resource resource) {
        final var list = new ArrayList<RepresentationTemplate>();
        final var representationList = resource.getRepresentation();
        for (final var representation : representationList) {
            final var representationTemplate = MappingUtils.fromIdsRepresentation(representation);
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
            final var artifactTemplate = MappingUtils.fromIdsArtifact(artifact);
            list.add(artifactTemplate);
        }

        return list;
    }

    public List<RuleTemplate> getRuleTemplates(final Contract contract) {
        final var list = new ArrayList<RuleTemplate>();
        final var ruleList = PolicyUtils.extractRulesFromContract(contract);

        for (final var rule : ruleList) {
            final var ruleTemplate = MappingUtils.fromIdsRule(rule);
            list.add(ruleTemplate);
        }

        return list;
    }
}
