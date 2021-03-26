package de.fraunhofer.isst.dataspaceconnector.utils;

import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RdfBuilderException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
     * @throws ConstraintViolationException If the response could not be extracted.
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

    /**
     * Get list of keywords as ids list of typed literals.
     *
     * @param keywords List of keywords.
     * @param language The language.
     * @return List of typed literal.
     */
    public static List<TypedLiteral> getKeywordsAsTypedLiteral(final List<String> keywords,
                                                 final String language) {
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
    public static Language getLanguage(final String language) {
        switch (language.toLowerCase()) {
            case "de":
                return Language.DE;
            case "en":
            default:
                return Language.EN;
        }
    }

    /**
     * Get list of ids keywords as list of strings.
     *
     * @param keywords List of typed literals.
     * @return List of strings.
     */
    public static List<String> getKeywordsAsString(final ArrayList<? extends TypedLiteral> keywords) {
        final var list = new ArrayList<String>();
        for (final var keyword : keywords) {
            list.add(keyword.getValue());
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

    /**
     * Convert string to date.
     *
     * @param calendar The XMLGregorianCalendar date as string.
     * @return The calender object.
     * @throws ParseException If parsing fails.
     */
    public static Date getDateOf(final String calendar) throws ParseException {
        final var sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(Calendar.getInstance().getTimeZone());
        return sdf.parse(calendar);
    }
}
