package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationInstance;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ArtifactTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ContractTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RepresentationTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ResourceTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RuleTemplate;
import org.springframework.stereotype.Service;

@Service
public final class MappingUtils {

    private MappingUtils() {
        // not used
    }

    /**
     * Map ids resource to connector resource.
     *
     * @param resource The ids resource.
     * @return The connector resource.
     */
    public ResourceTemplate<RequestedResourceDesc> fromIdsResource(final Resource resource) {
        // TODO Mapping
        return null;
    }

    /**
     * Map ids representation to connector representation.
     *
     * @param representation The ids representation.
     * @return The connector representation.
     */
    public RepresentationTemplate fromIdsRepresentation(final Representation representation) {
        // TODO Mapping
        return null;
    }

    public ArtifactTemplate fromIdsArtifact(final RepresentationInstance instance) {
        // TODO Mapping
        return null;
    }

    public ContractTemplate fromIdsContract(final Contract contract) {
        final var desc = new ContractDesc();
        // desc.setTitle(contract.get);

        return null;
    }

    /**
     * Map ids rule to internal data model.
     *
     * @param rule The ids rule.
     * @return A rule template.
     */
    public RuleTemplate fromIdsRule(final Rule rule) {
        final var desc = new ContractRuleDesc();
        desc.setTitle(String.valueOf(rule.getTitle()));
        // desc.setContent(rule.toRdf());

        final var template = new RuleTemplate();
        template.setDesc(desc);

        return template;
    }
}
