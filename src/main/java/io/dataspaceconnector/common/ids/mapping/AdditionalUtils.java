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
package io.dataspaceconnector.common.ids.mapping;

import de.fraunhofer.iais.eis.AppEndpoint;
import de.fraunhofer.iais.eis.AppRepresentation;
import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.ConnectorEndpointImpl;
import de.fraunhofer.iais.eis.RepresentationInstance;
import de.fraunhofer.iais.eis.Resource;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.util.Utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provide utilities for additional mapping.
 */
public final class AdditionalUtils {

    /**
     * Utility class does not have to be instantiated.
     */
    private AdditionalUtils() {
    }

    /**
     * Map properties of resource to additional fields.
     *
     * @param resource The ids resource.
     * @return The additional map.
     */
    public static Map<String, String> buildAdditionalForResource(final Resource resource) {
        Utils.requireNonNull(resource, ErrorMessage.ENTITY_NULL);

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

        if (contentPart != null && !contentPart.isEmpty()) {
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

        if (representation != null && !representation.isEmpty()) {
            addListToAdditional(representation, additional, "ids:defaultRepresentation");
        }

        if (modified != null) {
            additional.put("ids:modified", modified.toXMLFormat());
        }

        if (resourceEndpoint != null && !resourceEndpoint.isEmpty()) {
            addListToAdditional(resourceEndpoint, additional, "ids:resourceEndpoint");
        }

        if (resourcePart != null && !resourcePart.isEmpty()) {
            addListToAdditional(resourcePart, additional, "ids:resourcePart");
        }

        if (shapesGraph != null) {
            additional.put("ids:shapesGraph", shapesGraph.toString());
        }

        if (spatialCoverage != null && !spatialCoverage.isEmpty()) {
            addListToAdditional(spatialCoverage, additional, "ids:spatialCoverage");
        }

        if (temporalCoverage != null && !temporalCoverage.isEmpty()) {
            addListToAdditional(temporalCoverage, additional, "ids:temporalCoverage");
        }

        if (temporalRes != null) {
            additional.put("ids:temporalResolution", temporalRes.toString());
        }

        if (theme != null && !theme.isEmpty()) {
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

    /**
     * Map properties of app representation to additional fields.
     *
     * @param representation The app representation.
     * @return The additional map.
     */
    public static Map<String, String> buildAdditionalForAppRepresentation(
            final AppRepresentation representation) {
        final var additional = propertiesToAdditional(representation.getProperties());

        final var title = representation.getTitle();
        final var description = representation.getDescription();
        final var instance = representation.getInstance();
        final var language = representation.getLanguage();
        final var mediaType = representation.getMediaType();
        final var representationStandard = representation.getRepresentationStandard();
        final var shapesGraph = representation.getShapesGraph();

        if (description != null && !description.isEmpty()) {
            additional.put("ids:description", description.size() == 1
                    ? description.get(0).getValue() : description.toString());
        }

        if (title != null && !title.isEmpty()) {
            additional.put("ids:title", title.size() == 1
                    ? title.get(0).getValue() : title.toString());
        }

        if (instance != null && !instance.isEmpty()) {
            addListToAdditional(instance, additional, "ids:instance");
        }
        if (language != null) {
            additional.put("ids:language", language.toString());
        }
        if (mediaType != null) {
            additional.put("ids:mediaType", mediaType.getFilenameExtension());
        }
        if (representationStandard != null) {
            additional.put("ids:representationStandard", representationStandard.toString());
        }
        if (shapesGraph != null) {
            additional.put("ids:shapesGraph", shapesGraph.toString());
        }

        return additional;
    }

    /**
     * Map properties of app representation to additional fields.
     *
     * @param appEndpoint The app representation.
     * @return The additional map.
     */
    public static Map<String, String> buildAdditionalForAppEndpoint(final AppEndpoint appEndpoint) {
        Utils.requireNonNull(appEndpoint, ErrorMessage.ENTITY_NULL);
        final var additional = propertiesToAdditional(appEndpoint.getProperties());

        final var inboundPath = appEndpoint.getInboundPath();
        final var outboundPath = appEndpoint.getOutboundPath();
        final var accessUrl = appEndpoint.getAccessURL();

        if (inboundPath != null) {
            additional.put("ids:inboundPath", inboundPath);
        }

        if (outboundPath != null) {
            additional.put("ids:outboundPath", outboundPath);
        }

        if (accessUrl != null) {
            additional.put("ids:accessUrl", accessUrl.toString());
        }

        return additional;
    }

    /***********************************************************************************************
     * UTILITIES                                                                                   *
     ***********************************************************************************************

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
        if (list.size() >= 1 && list.get(0) instanceof ConnectorEndpointImpl) {
            additional.put(key, ((ConnectorEndpointImpl) list.get(0)).getAccessURL().toString());
        } else if (list.size() >= 1 && list.get(0) instanceof RepresentationInstance) {
            // Note: Some attributes will get lost.
            additional.put("ids:checkSum", ((Artifact) list.get(0)).getCheckSum());
            additional.put("ids:byteSize", ((Artifact) list.get(0)).getByteSize().toString());
            additional.put("ids:fileName", ((Artifact) list.get(0)).getFileName());
        } else {
            additional.put(key, list.size() == 1 ? list.get(0).toString() : list.toString());
        }
    }

    /**
     * Map ids property map to additional map for the internal data model. If the argument is null,
     * an empty map will be returned.
     *
     * @param properties A string object map.
     * @return A map containing all properties that could be extracted.
     */
    public static Map<String, String> propertiesToAdditional(
            final Map<String, Object> properties) {
        final Map<String, String> additional = new ConcurrentHashMap<>();
        if (properties != null && !properties.isEmpty()) {
            for (final Map.Entry<String, Object> entry : properties.entrySet()) {
                if (entry.getValue() != null) {
                    additional.put(entry.getKey(), entry.getValue().toString());
                }
            }
        }

        return additional;
    }
}
