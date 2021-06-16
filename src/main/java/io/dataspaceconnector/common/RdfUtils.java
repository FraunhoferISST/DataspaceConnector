package io.dataspaceconnector.common;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.Catalog;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.common.exceptions.RdfBuilderException;
import io.dataspaceconnector.common.exceptions.messages.ErrorMessages;

public final class RdfUtils {

    /**
     * Get rdf string from instance of type {@link BaseConnector}.
     *
     * @param baseConnector The ids connector.
     * @return The ids connector as rdf string.
     * @throws RdfBuilderException If the response could not be extracted.
     */
    public static String toRdf(final BaseConnector baseConnector) throws RdfBuilderException {
        try {
            return baseConnector.toRdf();
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessages.RDF_FAILED);
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
            return resource.toRdf();
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessages.RDF_FAILED);
        }
    }

    /**
     * Get rdf string from instance of type {@link Artifact}.
     *
     * @param artifact The ids artifact.
     * @return The ids artifact as rdf string.
     * @throws de.fraunhofer.iais.eis.util.ConstraintViolationException
     *         If the response could not be extracted.
     */
    public static String toRdf(final Artifact artifact) throws RdfBuilderException {
        try {
            return artifact.toRdf();
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessages.RDF_FAILED);
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
            return representation.toRdf();
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessages.RDF_FAILED);
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
            return catalog.toRdf();
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessages.RDF_FAILED);
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
            return request.toRdf();
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessages.RDF_FAILED);
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
            return offer.toRdf();
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessages.RDF_FAILED);
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
            return agreement.toRdf();
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessages.RDF_FAILED);
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
            return rule.toRdf();
        } catch (Exception exception) {
            throw new RdfBuilderException(ErrorMessages.RDF_FAILED);
        }
    }
}
