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

        // Add additional properties to map.
        final var properties = resource.getProperties();
        final var additional = propertiesToAdditional(properties);

        additional.put("ids:accrualPeriodicity", accrualPeriodicity.toRdf());
        additional.put("ids:assetRefinement", assetRefinement.toRdf());
        additional.put("ids:contentPart", contentPart.toString());
        additional.put("ids:contentStandard", contentStandard.toString());
        additional.put("ids:contentType", contentType.toRdf());
        additional.put("ids:created", created.toXMLFormat());
        additional.put("ids:customLicense", customLicense.toString());
        additional.put("ids:defaultRepresentation", defaultRepresentation.toString());
        additional.put("ids:modified", modified.toXMLFormat());
        additional.put("ids:resourceEndpoint", resourceEndpoint.toString());
        additional.put("ids:resourcePart", resourcePart.toString());
        additional.put("ids:sample", sample.toString());
        additional.put("ids:shapesGraph", shapesGraph.toString());
        additional.put("ids:sovereign", sovereign.toString());
        additional.put("ids:spatialCoverage", spatialCoverage.toString());
        additional.put("ids:temporalCoverage", temporalCoverage.toString());
        additional.put("ids:temporalResolution", temporalResolution.toString());
        additional.put("ids:theme", theme.toString());
        additional.put("ids:variant", variant.toString());
        additional.put("ids:version", version);

        final var desc = new RequestedResourceDesc();
        desc.setAdditional(additional);
        desc.setRemoteId(id);
        desc.setKeywords(keywords);
        desc.setDescription(description.toString());
        desc.setPublisher(publisher);
        desc.setLicence(standardLicense);
        desc.setLanguage(language.toString());
        desc.setTitle(title.toString());

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

        // Add additional properties to map.
        final var properties = representation.getProperties();
        final var additional = propertiesToAdditional(properties);

        additional.put("ids:created", created.toXMLFormat());
        additional.put("ids:modified", modified.toXMLFormat());
        additional.put("ids:shapesGraph", String.valueOf(shape));

        final var desc = new RepresentationDesc();
        desc.setAdditional(additional);
        desc.setRemoteId(id);
        desc.setType(mediaType);
        desc.setLanguage(language);
        desc.setStandard(standard);

        final var template = new RepresentationTemplate();
        template.setDesc(desc);

        return template;
    }

    /**
     * Build template from ids artifact.
     *
     * @param artifact The ids artifact.
     * @param download Indicated whether the artifact is going to be downloaded
     *                 automatically.
     * @return The artifact template.
     */
    public static ArtifactTemplate fromIdsArtifact(final Artifact artifact,
                                                   final boolean download) {
        final var id = artifact.getId();
        final var byteSize = artifact.getByteSize();
        final var checksum = artifact.getCheckSum();
        final var created = artifact.getCreationDate();
        final var duration = artifact.getDuration();
        final var filename = artifact.getFileName();

        // Add additional properties to map.
        final var properties = artifact.getProperties();
        final var additional = propertiesToAdditional(properties);

        additional.put("ids:byteSize", byteSize.toString());
        additional.put("ids:checkSum", checksum);
        additional.put("ids:creationDate", created.toXMLFormat());
        additional.put("ids:duration", duration.toString());

        final var desc = new ArtifactDesc();
        desc.setAdditional(additional);
        desc.setRemoteId(id);
        desc.setTitle(filename);
        desc.setAutomatedDownload(download);

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

        // Add additional properties to map.
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
