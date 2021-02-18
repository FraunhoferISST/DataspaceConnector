package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.EndpointId;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ArtifactTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ContractTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RepresentationTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ResourceTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RuleTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

public class TemplateBuilder42<T extends Resource, D extends ResourceDesc<T>> {

    /***********************************************************************************************
    * Resources
     **********************************************************************************************/

    @Autowired
    private BFFResourceService<T, D, ?> resourceService;

    @Autowired
    private BFFResourceRepresentationLinker<T> resourceRepresentationLinker;

    @Autowired
    private BFFResourceContractLinker resourceContractLinker;

    public EndpointId build(final ResourceTemplate<D> template) {
        final var representationEndpointIds = new HashSet<EndpointId>();
        for (final var representation : template.getRepresentations()) {
            representationEndpointIds.add(this.build(representation));
        }

        final var contractEndpointIds = new HashSet<EndpointId>();
        for(final var contract : template.getContracts()) {
            contractEndpointIds.add(this.build(contract));
        }

        final EndpointId resourceEndpointId;
        final var desc = template.getDesc();
        if(template.getDesc().getStaticId() == null) {
            resourceEndpointId = resourceService.create(Basepaths.Resources.toString(), desc);
        } else{
            final var endpointId = new EndpointId(Basepaths.Resources.toString(), desc.getStaticId());
            if(resourceService.doesExist(endpointId)){
                resourceEndpointId = resourceService.update(endpointId, desc);
            }else{
                resourceEndpointId = resourceService.create(Basepaths.Resources.toString(), desc);
            }
        }

        try {
            resourceRepresentationLinker.replace(resourceEndpointId, representationEndpointIds);
            resourceContractLinker.replace(resourceEndpointId, contractEndpointIds);
        }catch(Exception exception) {
            System.out.println("FAILED");
        }

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

@Service
final class TemplateBuilderOfferedResource extends TemplateBuilder42<OfferedResource, OfferedResourceDesc> {}

@Service
final class TemplateBuilderRequestedResource extends TemplateBuilder42<RequestedResource, RequestedResourceDesc> {}
