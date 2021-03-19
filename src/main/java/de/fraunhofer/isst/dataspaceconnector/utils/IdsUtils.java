package de.fraunhofer.isst.dataspaceconnector.utils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.TypedLiteral;

/**
 *
 */
public final class IdsUtils {

    private IdsUtils() {
        // not used
    }

    /**
     * Get rdf string from instance of type {@link BaseConnector}.
     *
     * @param baseConnector The ids connector.
     * @return The ids connector as rdf string.
     * @throws ConstraintViolationException If the response could not be extracted.
     */
    public static String getConnectorAsRdf(final BaseConnector baseConnector)
            throws ConstraintViolationException {
        return baseConnector.toRdf();
    }

    /**
     * Get rdf string from instance of type {@link Resource}.
     *
     * @param resource The ids resource.
     * @return The ids resource as rdf string.
     * @throws ConstraintViolationException If the response could not be extracted.
     */
    public static String getResourceAsRdf(final Resource resource)
            throws ConstraintViolationException {
        return resource.toRdf();
    }

    /**
     * Get rdf string from instance of type {@link Artifact}.
     *
     * @param artifact The ids artifact.
     * @return The ids artifact as rdf string.
     * @throws ConstraintViolationException If the response could not be extracted.
     */
    public static String getArtifactAsRdf(final Artifact artifact)
            throws ConstraintViolationException {
        return artifact.toRdf();
    }

    /**
     * Get rdf string from instance of type {@link Representation}.
     *
     * @param representation The ids representation.
     * @return The ids representation as rdf string.
     * @throws ConstraintViolationException If the response could not be extracted.
     */
    public static String getRepresentationAsRdf(final Representation representation)
            throws ConstraintViolationException {
        return representation.toRdf();
    }

    /**
     * Get rdf string from instance of type {@link ContractRequest}.
     *
     * @param request The ids contract request.
     * @return The ids contract request as rdf string.
     * @throws ConstraintViolationException If the response could not be extracted.
     */
    public static String getContractRequestAsRdf(final ContractRequest request)
            throws ConstraintViolationException {
        return request.toRdf();
    }

    public static List<TypedLiteral> getKeywords(final List<String> keywords, final String language) {
        final var idsKeywords = new ArrayList<TypedLiteral>();
        for (final var keyword : keywords) {
            idsKeywords.add(new TypedLiteral(keyword, language));
        }

        return idsKeywords;
    }

    /**
     * Convert string to ids language.
     *
     * @param language The language as string.
     * @return The ids language object.
     */
    public static List<Language> getLanguages(final String language) {
        final var list = new ArrayList<Language>();

        switch (language) {
            case "de":
                list.add(Language.DE);
                break;
            case "en":
            default:
                list.add(Language.EN);
        }

        return list;
    }

    /**
     * Converts a date to XMLGregorianCalendar format.
     *
     * @param date the date object.
     * @return the XMLGregorianCalendar object or null.
     */
    public static XMLGregorianCalendar getGregorianOf(final Date date) {
        final var calendar = new GregorianCalendar();
        calendar.setTime(date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException exception) {
            // Rethrow but do not register in function header
            throw new RuntimeException(exception);
        }
    }
}
