/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.util;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.Catalog;
import de.fraunhofer.iais.eis.ConnectorEndpoint;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.model.ArtifactDesc;
import io.dataspaceconnector.model.CatalogDesc;
import io.dataspaceconnector.model.ContractDesc;
import io.dataspaceconnector.model.ContractRuleDesc;
import io.dataspaceconnector.model.OfferedResourceDesc;
import io.dataspaceconnector.model.RepresentationDesc;
import io.dataspaceconnector.model.RequestedResourceDesc;
import io.dataspaceconnector.model.ResourceDesc;
import io.dataspaceconnector.model.template.ArtifactTemplate;
import io.dataspaceconnector.model.template.CatalogTemplate;
import io.dataspaceconnector.model.template.ContractTemplate;
import io.dataspaceconnector.model.template.RepresentationTemplate;
import io.dataspaceconnector.model.template.ResourceTemplate;
import io.dataspaceconnector.model.template.RuleTemplate;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maps ids resources to internal resources.
 */
public final class MappingUtils {

    /**
     * Default constructor.
     */
    private MappingUtils() {
        // not used
    }

    /**
     * Map ids catalog to connector catalog.
     *
     * @param catalog The ids catalog.
     * @return The connector catalog.
     * @throws IllegalArgumentException if the passed resource is null.
     */
    public static CatalogTemplate fromIdsCatalog(final Catalog catalog) {
        Utils.requireNonNull(catalog, ErrorMessages.ENTITY_NULL);

        final var additional = new HashMap<String, String>();
        if (catalog.getProperties() != null) {
            catalog.getProperties().forEach((key, value) -> additional.put(key, value.toString()));
        }

        final var catalogDesc = new CatalogDesc();
        catalogDesc.setAdditional(additional);
        catalogDesc.setTitle("IDS Catalog");
        catalogDesc.setDescription("This catalog is created from an IDS infomodel catalog.");
        catalogDesc.setBootstrapId(catalog.getId().toString());

        return new CatalogTemplate(catalogDesc, null, null);
    }

    private static Map<String, String> buildAdditionalForResource(final Resource resource) {
        Utils.requireNonNull(resource, ErrorMessages.ENTITY_NULL);

        final var additional = propertiesToAdditional(resource.getProperties());

        final var periodicity = resource.getAccrualPeriodicity();
        final var contentPart = resource.getContentPart();
        final var contentStandard = resource.getContentStandard();
        final var contentType = resource.getContentType();
        final var created = resource.getCreated();
        final var customLicense = resource.getCustomLicense();
        final var representation = resource.getDefaultRepresentation();
        final var modified = resource.getModified();
        final var resourceEndpoint = resource.getResourceEndpoint();
        final var resourcePart = resource.getResourcePart();
        final var sample = resource.getSample();
        final var shapesGraph = resource.getShapesGraph();
        final var spatialCoverage = resource.getSpatialCoverage();
        final var temporalCoverage = resource.getTemporalCoverage();
        final var temporalRes = resource.getTemporalResolution();
        final var theme = resource.getTheme();
        final var variant = resource.getVariant();
        final var version = resource.getVersion();

        if (periodicity != null) {
            additional.put("ids:accrualPeriodicity", periodicity.toRdf());
        }

        if (contentPart != null) {
            addListToAdditional(contentPart, additional, "ids:contentPart");
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
            addListToAdditional(representation, additional, "ids:defaultRepresentation");
        }

        if (modified != null) {
            additional.put("ids:modified", modified.toXMLFormat());
        }

        if (resourceEndpoint != null) {
            addListToAdditional(resourceEndpoint, additional, "ids:resourceEndpoint");
        }

        if (resourcePart != null) {
            addListToAdditional(resourcePart, additional, "ids:resourcePart");
        }

        if (sample != null) {
            addListToAdditional(sample, additional, "ids:sample");
        }

        if (shapesGraph != null) {
            additional.put("ids:shapesGraph", shapesGraph.toString());
        }

        if (spatialCoverage != null) {
            addListToAdditional(spatialCoverage, additional, "ids:spatialCoverage");
        }

        if (temporalCoverage != null) {
            addListToAdditional(temporalCoverage, additional, "ids:temporalCoverage");
        }

        if (temporalRes != null) {
            additional.put("ids:temporalResolution", temporalRes.toString());
        }

        if (theme != null) {
            addListToAdditional(theme, additional, "ids:theme");
        }

        if (variant != null) {
            additional.put("ids:variant", variant.toString());
        }

        if (version != null) {
            additional.put("ids:version", version);
        }

        return additional;
    }

    private static <T extends io.dataspaceconnector.model.Resource> void
    fillResourceDesc(final ResourceDesc<T> desc, final Resource resource) {
        final var description = resource.getDescription();
        final var keywords = IdsUtils.getKeywordsAsString(resource.getKeyword());
        final var language = resource.getLanguage();
        final var publisher = resource.getPublisher();
        final var sovereign = resource.getSovereign();
        final var standardLicense = resource.getStandardLicense();
        final var title = resource.getTitle();
        final var resourceEndpoint = resource.getResourceEndpoint();

        desc.setAdditional(buildAdditionalForResource(resource));
        desc.setKeywords(keywords);
        desc.setPublisher(publisher);
        desc.setLicense(standardLicense);
        desc.setSovereign(sovereign);

        if (description != null) {
            desc.setDescription(description.size() == 1 ? description.get(0).toString()
                    : description.toString());
        }

        if (title != null) {
            desc.setTitle(title.size() == 1 ? title.get(0).toString() : title.toString());
        }

        if (language != null) {
            desc.setLanguage(
                    language.size() == 1 ? language.get(0).toString() : language.toString());
        }

        if (resourceEndpoint != null) {
            getFirstEndpointDocumentation(resourceEndpoint)
                    .ifPresent(desc::setEndpointDocumentation);
        }
    }

    /**
     * Map ids resource to connector resource.
     *
     * @param resource The ids resource.
     * @return The connector resource.
     * @throws IllegalArgumentException if the passed resource is null.
     */
    public static ResourceTemplate<OfferedResourceDesc> fromIdsOfferedResource(
            final Resource resource) {
        Utils.requireNonNull(resource, ErrorMessages.ENTITY_NULL);

        final var desc = new OfferedResourceDesc();
        fillResourceDesc(desc, resource);

        return new ResourceTemplate<>(null, desc, null, null);
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

        final var desc = new RequestedResourceDesc();
        desc.setRemoteId(resource.getId());
        fillResourceDesc(desc, resource);

        return new ResourceTemplate<>(null, desc, null, null);
    }

    /**
     * Adds the string value of a given list as an additional property. If the list only contains
     * one element, the string value will not contain brackets.
     *
     * @param list       the list.
     * @param additional the map of additional properties.
     * @param key        the map key to use.
     */
    private static void addListToAdditional(final List<?> list,
                                            final Map<String, String> additional,
                                            final String key) {
        additional.put(key, list.size() == 1 ? list.get(0).toString() : list.toString());
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
        final var standard = representation.getRepresentationStandard();
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

        if (standard != null) {
            desc.setStandard(String.valueOf(standard));
        }

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
        if (artifactId != null) {
            desc.setBootstrapId(artifactId.toString());
        }

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

        if (end != null) {
            try {
                desc.setEnd(getDateOf(end.toXMLFormat()));
            } catch (DateTimeParseException ignored) {
                // Default values don't need to be set here.
            }
        }

        if (start != null) {
            try {
                desc.setStart(getDateOf(start.toXMLFormat()));
            } catch (DateTimeParseException ignored) {
                // Default values don't need to be set here.
            }
        }

        return new ContractTemplate(null, desc, null);
    }

    /**
     * Build template from ids rule.
     *
     * @param rule The ids rule.
     * @return The rule template.
     * @throws IllegalArgumentException                             if the rule is null.
     * @throws io.dataspaceconnector.exception.RdfBuilderException if the rule cannot be
     * converted to string.
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
