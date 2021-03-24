package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RdfBuilderException;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ArtifactTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ContractTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RepresentationTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ResourceTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RuleTemplate;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

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
    public static ResourceTemplate<RequestedResourceDesc> fromIdsResource(final Resource resource) {
        final var accrualPeriodicity = resource.getAccrualPeriodicity();
        final var assetRefinement = resource.getAssetRefinement();
        final var contentPart = resource.getContentPart();
        final var contentStandard = resource.getContentStandard();
        final var contentType = resource.getContentType();
        final var created = resource.getCreated();
        final var customLicense = resource.getCustomLicense();
        final var defaultRepresentation = resource.getDefaultRepresentation();
        final var description = resource.getDescription();
        final var id = resource.getId();
        final var keywords = IdsUtils.getKeywordsAsString(resource.getKeyword());
        final var language = resource.getLanguage();
        final var modified = resource.getModified();
        final var publisher = resource.getPublisher();
        final var resourceEndpoint = resource.getResourceEndpoint();
        final var resourcePart = resource.getResourcePart();
        final var sample = resource.getSample();
        final var shapesGraph = resource.getShapesGraph();
        final var sovereign = resource.getSovereign();
        final var spatialCoverage = resource.getSpatialCoverage();
        final var standardLicense = resource.getStandardLicense();
        final var temporalCoverage = resource.getTemporalCoverage();
        final var temporalResolution = resource.getTemporalResolution();
        final var theme = resource.getTheme();
        final var title = resource.getTitle();
        final var variant = resource.getVariant();
        final var version = resource.getVersion();

        final var desc = new RequestedResourceDesc();
        desc.setRemoteId(id);
        desc.setKeywords(keywords);
        desc.setDescription(description.toString());
        desc.setPublisher(publisher);
        desc.setLicence(standardLicense);
        desc.setLanguage(language.toString());
        desc.setTitle(title.toString());

        final var additional = new HashMap<String, String>() {{
            put("ids:accrualPeriodicity", accrualPeriodicity.toRdf());
            put("ids:assetRefinement", assetRefinement.toRdf());
            put("ids:contentPart", contentPart.toString());
            put("ids:contentStandard", contentStandard.toString());
            put("ids:contentType", contentType.toRdf());
            put("ids:created", created.toXMLFormat());
            put("ids:customLicense", customLicense.toString());
            put("ids:defaultRepresentation", defaultRepresentation.toString());
            put("ids:modified", modified.toXMLFormat());
            put("ids:resourceEndpoint", resourceEndpoint.toString());
            put("ids:resourcePart", resourcePart.toString());
            put("ids:sample", sample.toString());
            put("ids:shapesGraph", shapesGraph.toString());
            put("ids:sovereign", sovereign.toString());
            put("ids:spatialCoverage", spatialCoverage.toString());
            put("ids:temporalCoverage", temporalCoverage.toString());
            put("ids:temporalResolution", temporalResolution.toString());
            put("ids:theme", theme.toString());
            put("ids:variant", variant.toString());
            put("ids:version", temporalCoverage.toString());
        }};
        desc.setAdditional(additional);

        final var template = new ResourceTemplate<RequestedResourceDesc>();
        template.setDesc(desc);

        return template;
    }

    /**
     * Map ids representation to connector representation.
     *
     * @param representation The ids representation.
     * @return The connector representation.
     */
    public static RepresentationTemplate fromIdsRepresentation(final Representation representation) {
        final var created = representation.getCreated();
        final var id = representation.getId();
        final var language = representation.getLanguage().toString();
        final var mediaType = representation.getMediaType().getFilenameExtension();
        final var modified = representation.getModified();
        final var standard = String.valueOf(representation.getRepresentationStandard());
        final var shape = representation.getShapesGraph();

        final var desc = new RepresentationDesc();
        desc.setRemoteId(id);
        desc.setType(mediaType);
        desc.setLanguage(language);
        desc.setStandard(standard);

        // Add additional properties to map.
        final var additional = new HashMap<String, String>() {{
            put("ids:created", created.toXMLFormat());
            put("ids:modified", modified.toXMLFormat());
            put("ids:shapesGraph", String.valueOf(shape));
        }};
        desc.setAdditional(additional);

        final var template = new RepresentationTemplate();
        template.setDesc(desc);

        return template;
    }

    /**
     * Build template from ids artifact.
     *
     * @param artifact The ids artifact.
     * @return The artifact template.
     */
    public static ArtifactTemplate fromIdsArtifact(final Artifact artifact) {
        final var id = artifact.getId();
        final var byteSize = artifact.getByteSize();
        final var checksum = artifact.getCheckSum();
        final var created = artifact.getCreationDate();
        final var duration = artifact.getDuration();
        final var filename = artifact.getFileName();

        final var desc = new ArtifactDesc();
        desc.setRemoteId(id);
        desc.setTitle(filename);

        // Add additional properties to map.
        final var additional = new HashMap<String, String>() {{
            put("ids:byteSize", byteSize.toString());
            put("ids:checkSum", checksum);
            put("ids:creationDate", created.toXMLFormat());
            put("ids:duration", duration.toString());
        }};
        desc.setAdditional(additional);

        final var template = new ArtifactTemplate();
        template.setDesc(desc);

        return template;
    }

    /**
     * Build template from ids contract.
     *
     * @param contract The ids contract offer.
     * @return The contract template.
     */
    public static ContractTemplate fromIdsContract(final Contract contract) {
        final var consumer = contract.getConsumer();
        final var date = contract.getContractDate();
        final var end = contract.getContractEnd();
        final var id = contract.getId();
        final var provider = contract.getProvider();
        final var start = contract.getContractStart();
        final var properties = contract.getProperties();

        final var additional = propertiesToAdditional(properties);

        final var desc = new ContractDesc();
        desc.setAdditional(additional);
        desc.setConsumer(consumer);
        desc.setProvider(provider);
        desc.setRemoteId(id);

        try {
            desc.setDate(IdsUtils.getDateOf(date.toXMLFormat()));
        } catch (ParseException ignored) {
            // Default values don't need to be set here.
        }

        try {
            desc.setEnd(IdsUtils.getDateOf(end.toXMLFormat()));
        } catch (ParseException ignored) {
            // Default values don't need to be set here.
        }

        try {
            desc.setStart(IdsUtils.getDateOf(start.toXMLFormat()));
        } catch (ParseException ignored) {
            // Default values don't need to be set here.
        }

        final var template = new ContractTemplate();
        template.setDesc(desc);

        return template;
    }

    /**
     * Build template from ids rule.
     *
     * @param rule The ids rule.
     * @return The rule template.
     */
    public static RuleTemplate fromIdsRule(final Rule rule) throws RdfBuilderException {
        final var value = IdsUtils.toRdf(rule);

        final var desc = new ContractRuleDesc();
        desc.setRemoteId(rule.getId());
        desc.setTitle(rule.getTitle().toString());
        desc.setValue(value);

        final var template = new RuleTemplate();
        template.setDesc(desc);

        return template;
    }

    /**
     * Map ids property map to additional map for the internal data model.
     *
     * @param properties A string object map.
     * @return A string string map.
     */
    private static Map<String, String> propertiesToAdditional(final Map<String, Object> properties) {
        final var additional = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            additional.put(entry.getKey(), (String) entry.getValue());
        }

        return additional;
    }
}
