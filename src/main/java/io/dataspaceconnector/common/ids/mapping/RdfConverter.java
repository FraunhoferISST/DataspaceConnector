/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.Catalog;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.RdfBuilderException;

/**
 * Converting ids objects to rdf strings.
 */
public final class RdfConverter {

    /**
     * Default constructor.
     */
    private RdfConverter() {
        // not used
    }

    /**
     * Serializer for ids objects.
     */
    private static final Serializer SERIALIZER = new Serializer();

    /**
     * Get rdf string from instance of type {@link BaseConnector}.
     *
     * @param baseConnector The ids connector.
     * @return The ids connector as rdf string.
     * @throws RdfBuilderException If the response could not be extracted.
     */
    public static String toRdf(final BaseConnector baseConnector) throws RdfBuilderException {
        try {
            var rdf = baseConnector.toRdf();
            if (rdf == null || rdf.isEmpty()) {
                rdf = SERIALIZER.serialize(baseConnector);
            }
            return rdf;
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessage.RDF_FAILED);
        }
    }

    /**
     * Get rdf string from instance of type {@link Resource}.
     *
     * @param resource The ids resource.
     * @return The ids resource as rdf string.
     * @throws RdfBuilderException If the response could not be extracted.
     */
    public static String toRdf(final Resource resource) throws RdfBuilderException {
        try {
            var rdf = resource.toRdf();
            if (rdf == null || rdf.isEmpty()) {
                rdf = SERIALIZER.serialize(resource);
            }
            return rdf;
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessage.RDF_FAILED);
        }
    }

    /**
     * Get rdf string from instance of type {@link Artifact}.
     *
     * @param artifact The ids artifact.
     * @return The ids artifact as rdf string.
     * @throws de.fraunhofer.iais.eis.util.ConstraintViolationException If the response could not
     *                                                                  be extracted.
     */
    public static String toRdf(final Artifact artifact) throws RdfBuilderException {
        try {
            var rdf = artifact.toRdf();
            if (rdf == null || rdf.isEmpty()) {
                rdf = SERIALIZER.serialize(artifact);
            }
            return rdf;
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessage.RDF_FAILED);
        }
    }

    /**
     * Get rdf string from instance of type {@link Representation}.
     *
     * @param representation The ids representation.
     * @return The ids representation as rdf string.
     * @throws RdfBuilderException If the response could not be extracted.
     */
    public static String toRdf(final Representation representation) throws RdfBuilderException {
        try {
            var rdf = representation.toRdf();
            if (rdf == null || rdf.isEmpty()) {
                rdf = SERIALIZER.serialize(representation);
            }
            return rdf;
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessage.RDF_FAILED);
        }
    }

    /**
     * Get rdf string from instance of type {@link Catalog}.
     *
     * @param catalog The ids catalog.
     * @return The ids catalog as rdf string.
     * @throws RdfBuilderException If the response could not be extracted.
     */
    public static String toRdf(final Catalog catalog) throws RdfBuilderException {
        try {
            var rdf = catalog.toRdf();
            if (rdf == null || rdf.isEmpty()) {
                rdf = SERIALIZER.serialize(catalog);
            }
            return rdf;
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessage.RDF_FAILED);
        }
    }

    /**
     * Get rdf string from instance of type {@link ContractRequest}.
     *
     * @param request The ids contract request.
     * @return The ids contract request as rdf string.
     * @throws RdfBuilderException If the response could not be extracted.
     */
    public static String toRdf(final ContractRequest request) throws RdfBuilderException {
        try {
            var rdf = request.toRdf();
            if (rdf == null || rdf.isEmpty()) {
                rdf = SERIALIZER.serialize(request);
            }
            return rdf;
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessage.RDF_FAILED);
        }
    }

    /**
     * Get rdf string from instance of type {@link ContractOffer}.
     *
     * @param offer The ids contract offer.
     * @return The ids contract offer as rdf string.
     * @throws RdfBuilderException If the response could not be extracted.
     */
    public static String toRdf(final ContractOffer offer) throws RdfBuilderException {
        try {
            var rdf = offer.toRdf();
            if (rdf == null || rdf.isEmpty()) {
                rdf = SERIALIZER.serialize(offer);
            }
            return rdf;
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessage.RDF_FAILED);
        }
    }

    /**
     * Get rdf string from instance of type {@link ContractAgreement}.
     *
     * @param agreement The ids contract agreement.
     * @return The ids contract agreement as rdf string.
     * @throws RdfBuilderException If the response could not be extracted.
     */
    public static String toRdf(final ContractAgreement agreement) throws RdfBuilderException {
        try {
            var rdf = agreement.toRdf();
            if (rdf == null || rdf.isEmpty()) {
                rdf = SERIALIZER.serialize(agreement);
            }
            return rdf;
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessage.RDF_FAILED);
        }
    }

    /**
     * Get rdf string from instance of type {@link Rule}.
     *
     * @param rule The ids rule.
     * @return The ids rule as rdf string.
     * @throws RdfBuilderException If the response could not be extracted.
     */
    public static String toRdf(final Rule rule) throws RdfBuilderException {
        try {
            var rdf = rule.toRdf();
            if (rdf == null || rdf.isEmpty()) {
                rdf = SERIALIZER.serialize(rule);
            }
            return rdf;
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessage.RDF_FAILED);
        }
    }

    /**
     * Get rdf string from instance of type {@link Message}.
     *
     * @param message The ids message.
     * @return The ids message as rdf string.
     * @throws RdfBuilderException If the response could not be extracted.
     */
    public static String toRdf(final Message message) throws RdfBuilderException {
        try {
            var rdf = message.toRdf();
            if (rdf == null || rdf.isEmpty()) {
                rdf = SERIALIZER.serialize(message);
            }
            return rdf;
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessage.RDF_FAILED);
        }
    }


}
