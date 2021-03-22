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

//     * Maps a received Infomodel resource to the internal metadata model.
//     *
//     * @param resource The resource
//     * @return the metadata object.
//     */
//    private ResourceMetadata deserializeMetadata(final Resource resource) {
//        var metadata = new ResourceMetadata();
//
//        if (resource.getKeyword() != null) {
//            List<String> keywords = new ArrayList<>();
//            for (var t : resource.getKeyword()) {
//                keywords.add(t.getValue());
//            }
//            metadata.setKeywords(keywords);
//        }
//
//        if (resource.getRepresentation() != null) {
//            var representations = new HashMap<UUID, ResourceRepresentation>();
//            for (final var r : resource.getRepresentation()) {
//                int byteSize = 0;
//                String name = null;
//                String type = null;
//                if (r.getInstance() != null && !r.getInstance().isEmpty()) {
//                    final var artifact = (Artifact) r.getInstance().get(0);
//                    if (artifact.getByteSize() != null) {
//                        byteSize = artifact.getByteSize().intValue();
//                    }
//                    if (artifact.getFileName() != null) {
//                        name = artifact.getFileName();
//                    }
//                    if (r.getMediaType() != null) {
//                        type = r.getMediaType().getFilenameExtension();
//                    }
//                }
//
//                ResourceRepresentation representation = new ResourceRepresentation(
//                        UUIDUtils.uuidFromUri(r.getId()), type, byteSize, name,
//                        new BackendSource(BackendSource.Type.LOCAL, null, null, null));
//
//                representations.put(representation.getUuid(), representation);
//            }
//            metadata.setRepresentations(representations);
//        }
//
//        if (resource.getTitle() != null && !resource.getTitle().isEmpty()) {
//            metadata.setTitle(resource.getTitle().get(0).getValue());
//        }
//
//        if (resource.getDescription() != null && !resource.getDescription().isEmpty()) {
//            metadata.setDescription(resource.getDescription().get(0).getValue());
//        }
//
//        if (resource.getContractOffer() != null && !resource.getContractOffer().isEmpty()) {
//            metadata.setPolicy(resource.getContractOffer().get(0).toRdf());
//        }
//
//        if (resource.getPublisher() != null) {
//            metadata.setOwner(resource.getPublisher());
//        }
//
//        if (resource.getStandardLicense() != null) {
//            metadata.setLicense(resource.getStandardLicense());
//        }
//
//        if (resource.getVersion() != null) {
//            metadata.setVersion(resource.getVersion());
//        }
//
//        return metadata;
//    }
