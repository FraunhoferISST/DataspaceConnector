package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.ConnectorEndpoint;
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

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maps ids resources to internal resources.
 */
public final class MappingUtils {
    private MappingUtils() {
        // not used
    }

    /**
     * Map ids resource to connector resource.
     *
     * @param resource The ids resource.
     * @return The connector resource.
     * @throws IllegalArgumentException if the passed resource is null.
     */
    public static ResourceTemplate<RequestedResourceDesc> fromIdsResource(final Resource resource) {
        Utils.requireNonNull(resource, ErrorMessages.ENTITY_NULL);

        final var periodicity = resource.getAccrualPeriodicity();
        final var contentPart = resource.getContentPart();
        final var contentStandard = resource.getContentStandard();
        final var contentType = resource.getContentType();
        final var created = resource.getCreated();
        final var customLicense = resource.getCustomLicense();
        final var representation = resource.getDefaultRepresentation();
        final var description = resource.getDescription();
        final var resourceId = resource.getId();
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
        final var temporalRes = resource.getTemporalResolution();
        final var theme = resource.getTheme();
        final var title = resource.getTitle();
        final var variant = resource.getVariant();
        final var version = resource.getVersion();

        // Add additional properties to map.
        final var additional = propertiesToAdditional(resource.getProperties());

        if (periodicity != null) {
            additional.put("ids:accrualPeriodicity", periodicity.toRdf());
        }

        if (contentPart != null) {
            additional.put("ids:contentPart", contentPart.toString());
        }

        if (contentStandard != null) {
            additional.put("ids:contentStandard", contentStandard.toString());
        }

        if (contentType != null) {
            additional.put("ids:contentType", contentType.toRdf());
        }

        if (created != null) {
            additional.put("ids:created", created.toXMLFormat());
        }

        if (customLicense != null) {
            additional.put("ids:customLicense", customLicense.toString());
        }

        if (representation != null) {
            additional.put("ids:defaultRepresentation", representation.toString());
        }

        if (modified != null) {
            additional.put("ids:modified", modified.toXMLFormat());
        }

        if (resourceEndpoint != null) {
            additional.put("ids:resourceEndpoint", resourceEndpoint.toString());
        }

        if (resourcePart != null) {
            additional.put("ids:resourcePart", resourcePart.toString());
        }

        if (sample != null) {
            additional.put("ids:sample", sample.toString());
        }

        if (shapesGraph != null) {
            additional.put("ids:shapesGraph", shapesGraph.toString());
        }

        if (spatialCoverage != null) {
            additional.put("ids:spatialCoverage", spatialCoverage.toString());
        }

        if (temporalCoverage != null) {
            additional.put("ids:temporalCoverage", temporalCoverage.toString());
        }

        if (temporalRes != null) {
            additional.put("ids:temporalResolution", temporalRes.toString());
        }

        if (theme != null) {
            additional.put("ids:theme", theme.toString());
        }

        if (variant != null) {
            additional.put("ids:variant", variant.toString());
        }

        if (version != null) {
            additional.put("ids:version", version);
        }

        final var desc = new RequestedResourceDesc();
        desc.setAdditional(additional);
        desc.setRemoteId(resourceId);
        desc.setKeywords(keywords);
        desc.setPublisher(publisher);
        desc.setLicence(standardLicense);
        desc.setSovereign(sovereign);

        if (description != null) {
            desc.setDescription(description.toString());
        }

        if (title != null) {
            desc.setTitle(title.toString());
        }

        if (language != null) {
            desc.setLanguage(language.toString());
        }

        if (resourceEndpoint != null) {
            getFirstEndpointDocumentation(resourceEndpoint)
                    .ifPresent(desc::setEndpointDocumentation);
        }

        return new ResourceTemplate<>(null, desc, null, null);
    }

    /**
     * Map ids representation to connector representation.
     *
     * @param representation The ids representation.
     * @return The connector representation.
     * @throws IllegalArgumentException if the passed representation is null.
     */
    public static RepresentationTemplate fromIdsRepresentation(
            final Representation representation) {
        Utils.requireNonNull(representation, ErrorMessages.ENTITY_NULL);

        final var created = representation.getCreated();
        final var representationId = representation.getId();
        final var language = representation.getLanguage();
        final var mediaType = representation.getMediaType();
        final var modified = representation.getModified();
        final var standard = String.valueOf(representation.getRepresentationStandard());
        final var shape = representation.getShapesGraph();

        // Add additional properties to map.
        final var additional = propertiesToAdditional(representation.getProperties());

        if (created != null) {
            additional.put("ids:created", created.toXMLFormat());
        }
        if (modified != null) {
            additional.put("ids:modified", modified.toXMLFormat());
        }

        if (shape != null) {
            additional.put("ids:shapesGraph", String.valueOf(shape));
        }

        final var desc = new RepresentationDesc();
        desc.setAdditional(additional);
        desc.setRemoteId(representationId);
        desc.setStandard(standard);

        if (language != null) {
            desc.setLanguage(language.toString());
        }

        if (mediaType != null) {
            desc.setMediaType(mediaType.getFilenameExtension());
        }

        return new RepresentationTemplate(null, desc, null);
    }

    /**
     * Build template from ids artifact.
     *
     * @param artifact  The ids artifact.
     * @param download  Whether the artifact will be downloaded automatically.
     * @param remoteUrl The provider's url for receiving artifact request messages.
     * @return The artifact template.
     * @throws IllegalArgumentException if the passed artifact is null.
     */
    public static ArtifactTemplate fromIdsArtifact(final Artifact artifact,
                                                   final boolean download, final URI remoteUrl) {
        Utils.requireNonNull(artifact, ErrorMessages.ENTITY_NULL);

        final var artifactId = artifact.getId();
        final var byteSize = artifact.getByteSize();
        final var checksum = artifact.getCheckSum();
        final var created = artifact.getCreationDate();
        final var duration = artifact.getDuration();
        final var filename = artifact.getFileName();

        // Add additional properties to map.
        final var additional = propertiesToAdditional(artifact.getProperties());

        if (byteSize != null) {
            additional.put("ids:byteSize", byteSize.toString());
        }

        if (checksum != null) {
            additional.put("ids:checkSum", checksum);
        }

        if (created != null) {
            additional.put("ids:creationDate", created.toXMLFormat());
        }

        if (duration != null) {
            additional.put("ids:duration", duration.toString());
        }

        final var desc = new ArtifactDesc();
        desc.setAdditional(additional);
        desc.setRemoteId(artifactId);
        desc.setTitle(filename);
        desc.setAutomatedDownload(download);
        desc.setRemoteAddress(remoteUrl);

        return new ArtifactTemplate(desc);
    }

    /**
     * Build template from ids contract.
     *
     * @param contract The ids contract offer.
     * @return The contract template.
     * @throws IllegalArgumentException if the passed contract is null.
     */
    public static ContractTemplate fromIdsContract(final Contract contract) {
        Utils.requireNonNull(contract, ErrorMessages.ENTITY_NULL);

        final var consumer = contract.getConsumer();
        final var date = contract.getContractDate();
        final var end = contract.getContractEnd();
        final var contractId = contract.getId();
        final var provider = contract.getProvider();
        final var start = contract.getContractStart();

        // Add additional properties to map.
        final var additional = propertiesToAdditional(contract.getProperties());

        if (date != null) {
            additional.put("ids:contractDate", date.toXMLFormat());
        }

        final var desc = new ContractDesc();
        desc.setAdditional(additional);
        desc.setConsumer(consumer);
        desc.setProvider(provider);
        desc.setRemoteId(contractId);

        try {
            desc.setEnd(getDateOf(end.toXMLFormat()));
        } catch (DateTimeParseException ignored) {
            // Default values don't need to be set here.
        }

        try {
            desc.setStart(getDateOf(start.toXMLFormat()));
        } catch (DateTimeParseException ignored) {
            // Default values don't need to be set here.
        }

        return new ContractTemplate(null, desc, null);
    }

    /**
     * Build template from ids rule.
     *
     * @param rule The ids rule.
     * @return The rule template.
     * @throws IllegalArgumentException if the rule is null.
     * @throws RdfBuilderException      if the rule cannot be converted to string.
     */
    public static RuleTemplate fromIdsRule(final Rule rule) {
        Utils.requireNonNull(rule, ErrorMessages.ENTITY_NULL);

        final var value = IdsUtils.toRdf(rule);
        final var desc = new ContractRuleDesc();
        desc.setRemoteId(rule.getId());
        desc.setValue(value);

        if (rule.getTitle() != null) {
            desc.setTitle(rule.getTitle().toString());
        }

        return new RuleTemplate(desc);
    }

    /**
     * Map ids property map to additional map for the internal data model.
     * If the argument is null an empty map will be returned.
     *
     * @param properties A string object map.
     * @return A map containing all properties that could be extracted.
     */
    private static Map<String, String> propertiesToAdditional(
            final Map<String, Object> properties) {
        final Map<String, String> additional = new ConcurrentHashMap<>();
        if (properties != null) {
            for (final Map.Entry<String, Object> entry : properties.entrySet()) {
                if (entry.getValue() != null) {
                    additional.put(entry.getKey(), entry.getValue().toString());
                }
            }
        }

        return additional;
    }

    /**
     * Convert a string to a {@link ZonedDateTime}.
     *
     * @param calendar The time as string.
     * @return The new ZonedDateTime object.
     * @throws DateTimeParseException if the string could not be converted.
     */
    public static ZonedDateTime getDateOf(final String calendar) {
        return ZonedDateTime.parse(calendar);
    }

    /**
     * Returns the first endpoint documentations of the first endpoint.
     *
     * @param endpoints The list of endpoints.
     * @return The endpoint documentation.
     */
    private static Optional<URI> getFirstEndpointDocumentation(
            final List<? extends ConnectorEndpoint> endpoints) {
        Optional<URI> output = Optional.empty();

        if (!endpoints.isEmpty()) {
            final var first = endpoints.get(0);

            if (first.getEndpointDocumentation() != null
                    && !first.getEndpointDocumentation().isEmpty()) {
                output = Optional.of(first.getEndpointDocumentation().get(0));
            }
        }

        return output;
    }
}
