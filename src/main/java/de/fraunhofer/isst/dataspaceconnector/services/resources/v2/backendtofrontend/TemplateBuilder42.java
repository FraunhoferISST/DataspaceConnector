package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ArtifactTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ContractTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RepresentationTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ResourceTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RuleTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class TemplateBuilder42 {

    /***********************************************************************************************
    * Resources
     **********************************************************************************************/

    @Autowired
    private BFFResourceService resourceService;

    @Autowired
    private BFFResourceRepresentationLinker resourceRepresentationLinker;

    @Autowired
    private BFFResourceContractLinker resourceContractLinker;

    public EndpointId build(final ResourceTemplate template) {
        final var representationEndpointIds = new HashSet<EndpointId>();
        for (final var representation : template.getRepresentations()) {
            representationEndpointIds.add(this.build(representation));
        }

        final var contractEndpointIds = new HashSet<EndpointId>();
        for(final var contract : template.getContracts()) {
            contractEndpointIds.add(this.build(contract));
        }

        final EndpointId resourceEndpointId;
        if(template.getDesc().getStaticId() == null) {
            resourceEndpointId = resourceService.create(Basepaths.Resources.toString(), template.getDesc());
        } else{
            final var endpointId = new EndpointId(Basepaths.Resources.toString(), template.getDesc().getStaticId());
            if(resourceService.doesExist(endpointId)){
                resourceEndpointId = resourceService.update(endpointId, template.getDesc());
            }else{
                resourceEndpointId = resourceService.create(Basepaths.Resources.toString(), template.getDesc());
            }
        }

        resourceRepresentationLinker.replace(resourceEndpointId, representationEndpointIds);
        resourceContractLinker.replace(resourceEndpointId, contractEndpointIds);

        return resourceEndpointId;
    }

    /***********************************************************************************************
     * Representations
     **********************************************************************************************/

    @Autowired
    private BFFRepresentationService representationService;

    @Autowired
    private BFFRepresentationArtifactLinker representationArtifactLinker;

    public EndpointId build(final RepresentationTemplate template) {
        final var artifactEndpointIds = new HashSet<EndpointId>();
        for(final var artifact : template.getArtifacts()) {
            artifactEndpointIds.add(this.build(artifact));
        }

        final EndpointId representationEndpointId;
        if(template.getDesc().getStaticId() == null) {
            representationEndpointId = representationService.create(Basepaths.Representations.toString(), template.getDesc());
        } else{
            final var endpointId = new EndpointId(Basepaths.Representations.toString(), template.getDesc().getStaticId());
            if(representationService.doesExist(endpointId)){
                representationEndpointId = representationService.update(endpointId, template.getDesc());
            }else{
                representationEndpointId = representationService.create(Basepaths.Representations.toString(), template.getDesc());
            }
        }

        representationArtifactLinker.replace(representationEndpointId, artifactEndpointIds);

        return representationEndpointId;
    }


    /***********************************************************************************************
     * Contracts
     **********************************************************************************************/

    @Autowired
    private BFFContractService contractService;

    @Autowired
    private BFFContractRuleLinker contractRuleLinker;

    public EndpointId build(final ContractTemplate template) {
        final var ruleEndpointIds = new HashSet<EndpointId>();
        for(final var rule : template.getRules()) {
            ruleEndpointIds.add(this.build(rule));
        }

        final var endpointId = contractService.create(Basepaths.Contracts.toString(), template.getDesc());

        contractRuleLinker.add(endpointId, ruleEndpointIds);

        return endpointId;
    }

    /***********************************************************************************************
     * Artifacts
     **********************************************************************************************/

    @Autowired
    private ArtifactBFFService artifactService;

    public EndpointId build(final ArtifactTemplate template) {
        return artifactService.create(Basepaths.Artifacts.toString(), template.getDesc());
    }

    /***********************************************************************************************
     * Rules
     **********************************************************************************************/

    @Autowired
    private RuleBFFService ruleService;

    public EndpointId build(final RuleTemplate template) {
        return ruleService.create(Basepaths.Rules.toString(), template.getDesc());
    }
}
