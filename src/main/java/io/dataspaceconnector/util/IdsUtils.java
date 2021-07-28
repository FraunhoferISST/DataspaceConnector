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
import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.BasicAuthentication;
import de.fraunhofer.iais.eis.BasicAuthenticationBuilder;
import de.fraunhofer.iais.eis.Catalog;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ConnectorDeployMode;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.LogLevel;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.Proxy;
import de.fraunhofer.iais.eis.ProxyBuilder;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dataspaceconnector.exception.RdfBuilderException;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.model.configuration.Configuration;
import io.dataspaceconnector.model.configuration.DeployMode;
import lombok.SneakyThrows;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URI;
import java.text.Normalizer;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 *
 */
public final class IdsUtils {

    /**
     * Default constructor.
     */
    private IdsUtils() {
        // not used
    }

    /**
     * Serializer for Infomodel objects.
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
    @SuppressFBWarnings("IMPROPER_UNICODE")
    public static Language getLanguage(final String language) {
        for (final var idsLanguage : Language.values()) {
            if (Normalizer.normalize(language.toLowerCase(Locale.ROOT),
                    Normalizer.Form.NFC).equals(Normalizer.normalize(
                    idsLanguage.name().toLowerCase(Locale.ROOT), Normalizer.Form.NFC))) {
                return idsLanguage;
            }
        }

        return Language.EN;
    }

    /**
     * Get list of ids keywords as list of strings.
     * If the passed list is null, an empty list is returned.
     *
     * @param keywords List of typed literals.
     * @return List of strings.
     */
    public static List<String> getKeywordsAsString(final List<? extends TypedLiteral> keywords) {

        final var list = new ArrayList<String>();
        if (keywords != null) {
            for (final var keyword : keywords) {
                list.add(keyword.getValue());
            }
        }

        return list;
    }

    /**
     * Converts a date to XMLGregorianCalendar format.
     *
     * @param date the date object.
     * @return the XMLGregorianCalendar object or null.
     */
    @SneakyThrows
    public static XMLGregorianCalendar getGregorianOf(final ZonedDateTime date) {
        final var calendar = GregorianCalendar.from(date);
        calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.ofOffset("", ZoneOffset.UTC)));
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }

    /**
     * Get security profile from string.
     *
     * @param input The input value.
     * @return A security profile, if the value matches the provided enums.
     */
    public static Optional<SecurityProfile> getSecurityProfile(final String input) {
        switch (input) {
            case "idsc:BASE_SECURITY_PROFILE":
            case "BASE_SECURITY_PROFILE":
            case "idsc:BASE_CONNECTOR_SECURITY_PROFILE":
                return Optional.of(SecurityProfile.BASE_SECURITY_PROFILE);
            case "idsc:TRUST_SECURITY_PROFILE":
            case "TRUST_SECURITY_PROFILE":
            case "idsc:TRUST_CONNECTOR_SECURITY_PROFILE":
                return Optional.of(SecurityProfile.TRUST_SECURITY_PROFILE);
            case "idsc:TRUST_PLUS_SECURITY_PROFILE":
            case "TRUST_PLUS_SECURITY_PROFILE":
            case "idsc:TRUST_PLUS_CONNECTOR_SECURITY_PROFILE":
                return Optional.of(SecurityProfile.TRUST_PLUS_SECURITY_PROFILE);
            default:
                return Optional.empty();
        }
    }

    /**
     * Get ids deploy mode from dsc deploy mode.
     *
     * @param deployMode The internal deploy mode.
     * @return The ids deploy mode.
     */
    public static ConnectorDeployMode getConnectorDeployMode(final DeployMode deployMode) {
        return deployMode == DeployMode.TEST ? ConnectorDeployMode.TEST_DEPLOYMENT
                : ConnectorDeployMode.PRODUCTIVE_DEPLOYMENT;
    }

    /**
     * Get ids log level from dsc log level.
     *
     * @param logLevel The internal log level.
     * @return The ids log level.
     */
    public static LogLevel getLogLevel(
            final io.dataspaceconnector.model.configuration.LogLevel logLevel) {
        switch (logLevel) {
            // TODO infomodel has less log levels than DSC, info will get lost.
            case INFO:
            case WARN:
            case ERROR:
            case TRACE:
                return LogLevel.MINIMAL_LOGGING;
            case DEBUG:
                return LogLevel.DEBUG_LEVEL_LOGGING;
            default:
                return LogLevel.NO_LOGGING;
        }
    }

    /**
     * Get ids proxy from dsc proxy.
     *
     * @param proxy The internal proxy.
     * @return The ids proxy.
     */
    public static Proxy getProxy(final io.dataspaceconnector.model.proxy.Proxy proxy) {
        // TODO auth from DSC has ID field, not available in InfoModel. Create auth build service.
        return new ProxyBuilder()._noProxy_(proxy.getExclusions()
                .stream()
                .map(URI::create)
                .collect(Collectors.toList()))
                ._proxyAuthentication_(proxy.getAuthentication() == null
                                               ? null
                                               : getBasicAuthHeader(proxy.getAuthentication()))
                ._proxyURI_(proxy.getLocation())
                .build();
    }

    private static BasicAuthentication getBasicAuthHeader(final BasicAuth auth) {
        return new BasicAuthenticationBuilder()
                ._authPassword_(auth.getPassword())
                ._authUsername_(auth.getUsername())
                .build();
    }


    /**
     * Fill ids connector with config properties.
     *
     * @param config The internal configuration model.
     * @return The filled ids connector.
     */
    public static Connector getConnectorFromConfiguration(final Configuration config) {
        return new BaseConnectorBuilder()
                ._title_(Util.asList(new TypedLiteral(config.getTitle())))
                ._description_(Util.asList(new TypedLiteral(config.getDescription())))
                ._curator_(config.getCurator())
                ._maintainer_(config.getMaintainer())
                ._securityProfile_(getSecurityProfile(config.getSecurityProfile()))
                ._hasDefaultEndpoint_(new ConnectorEndpointBuilder()
                        ._accessURL_(config.getDefaultEndpoint())
                        .build())
                ._outboundModelVersion_(config.getOutboundModelVersion())
                ._inboundModelVersion_(config.getInboundModelVersion())
                .build();
    }

    /**
     * Get ids security profile from dsc security profile.
     *
     * @param securityProfile The internal security profile.
     * @return The ids security profile.
     */
    private static SecurityProfile getSecurityProfile(
            final io.dataspaceconnector.model.configuration.SecurityProfile securityProfile) {
        switch (securityProfile) {
            case TRUST_SECURITY:
                return SecurityProfile.TRUST_SECURITY_PROFILE;
            case TRUST_PLUS_SECURITY:
                return SecurityProfile.TRUST_PLUS_SECURITY_PROFILE;
            default:
                return SecurityProfile.BASE_SECURITY_PROFILE;
        }
    }
}
