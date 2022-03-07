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

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.BaseConnector;
import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractOfferBuilder;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.Duty;
import de.fraunhofer.iais.eis.DutyBuilder;
import de.fraunhofer.iais.eis.IANAMediaTypeBuilder;
import de.fraunhofer.iais.eis.KeyType;
import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.Prohibition;
import de.fraunhofer.iais.eis.PublicKeyBuilder;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.SecurityProfile;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.common.exception.RdfBuilderException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RdfConverterTest {

    private final ZonedDateTime date = ZonedDateTime.of(LocalDateTime.ofEpochSecond(
            1616772571804L, 0, ZoneOffset.UTC), ZoneId.of("Z"));

    @Test
    public void toRdf_inputNull_throwRdfBuilderException() {
        /* ARRANGE */
        Rule rule = null;

        /* ACT && ASSERT */
        assertThrows(RdfBuilderException.class, () -> RdfConverter.toRdf(rule));
    }

    @Test
    public void toRdf_baseConnector_returnBaseConnectorInRdf() {
        /* ACT && ASSERT */
        assertEquals(getBaseConnector().toRdf(), RdfConverter.toRdf(getBaseConnector()));
    }

    @Test
    public void toRdf_resource_returnResourceInRdf() {
        /* ACT && ASSERT */
        assertEquals(getResource().toRdf(), RdfConverter.toRdf(getResource()));
    }

    @Test
    public void toRdf_artifact_returnArtifactInRdf() {
        /* ACT && ASSERT */
        assertEquals(getArtifact().toRdf(), RdfConverter.toRdf(getArtifact()));
    }

    @Test
    public void toRdf_representation_returnRepresentationInRdf() {
        /* ACT && ASSERT */
        assertEquals(getRepresentation().toRdf(), RdfConverter.toRdf(getRepresentation()));
    }

    @Test
    public void toRdf_contractRequest_returnContractRequestInRdf() {
        /* ACT && ASSERT */
        assertEquals(getContractRequest().toRdf(), RdfConverter.toRdf(getContractRequest()));
    }

    @Test
    public void toRdf_contractAgreement_returnContractAgreementInRdf() {
        /* ACT && ASSERT */
        assertEquals(getContractAgreement().toRdf(), RdfConverter.toRdf(getContractAgreement()));
    }

    @Test
    public void toRdf_rule_returnRuleInRdf() {
        /* ACT && ASSERT */
        assertEquals(getRule().toRdf(), RdfConverter.toRdf(getRule()));
    }

    @Test
    public void getKeywordsAsTypedLiteral_keywordsNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> ToIdsObjectMapper.getKeywordsAsTypedLiteral(null,
                "en"));
    }

    @Test
    public void getKeywordsAsTypedLiteral_languageNull_createTypedLiteralWithLanguageNull() {
        /* ARRANGE */
        final var keyword = "keyword";
        final var keywords = Collections.singletonList(keyword);

        /* ACT */
        final var result = ToIdsObjectMapper.getKeywordsAsTypedLiteral(keywords, null);

        /* ASSERT */
        assertEquals(1, result.size());
        assertEquals(keyword, result.get(0).getValue());
        assertNull(result.get(0).getLanguage());
    }

    @Test
    public void getKeywordsAsTypedLiteral_keywordsEmpty_returnEmptyList() {
        /* ARRANGE */
        final var keywords = new ArrayList<String>();

        /* ACT */
        final var result = ToIdsObjectMapper.getKeywordsAsTypedLiteral(keywords, "en");

        /* ASSERT */
        assertTrue(result.isEmpty());
    }

    @Test
    public void getKeywordsAsTypedLiteral_oneKeyword_returnList() {
        /* ARRANGE */
        final var language = "en";
        final var keyword = "keyword";
        final var keywords = Collections.singletonList(keyword);

        /* ACT */
        final var result = ToIdsObjectMapper.getKeywordsAsTypedLiteral(keywords, language);

        /* ASSERT */
        assertEquals(1, result.size());
        assertEquals(keyword, result.get(0).getValue());
        assertEquals(language, result.get(0).getLanguage());
    }

    @Test
    public void getKeywordsAsTypedLiteral_twoKeywords_returnList() {
        /* ARRANGE */
        final var language = "en";
        final var keyword1 = "keyword1";
        final var keyword2 = "keyword2";
        final var keywords = Arrays.asList(keyword1, keyword2);

        /* ACT */
        final var result = ToIdsObjectMapper.getKeywordsAsTypedLiteral(keywords, language);

        /* ASSERT */
        assertEquals(2, result.size());
        assertEquals(language, result.get(0).getLanguage());
        assertEquals(language, result.get(1).getLanguage());
        assertTrue(typedLiteralListContainsValues(result, keywords));
    }

    @Test
    public void getLanguage_languageNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> ToIdsObjectMapper.getLanguage(null));
    }

    @Test
    public void getLanguage_languageEmpty_returnLanguageEnglish() {
        /* ACT && ASSERT */
        assertEquals(Language.EN, ToIdsObjectMapper.getLanguage(""));
    }

    @Test
    public void getLanguage_invalidLanguageString_returnLanguageEnglish() {
        /* ACT && ASSERT */
        assertEquals(Language.EN, ToIdsObjectMapper.getLanguage("noValidLanguage"));
    }

    @Test
    public void getLanguage_languageValueDe_returnLanguageGerman() {
        /* ACT && ASSERT */
        assertEquals(Language.DE, ToIdsObjectMapper.getLanguage("de"));
    }

    @Test
    public void getLanguage_languageValueEn_returnLanguageEnglish() {
        /* ACT && ASSERT */
        assertEquals(Language.EN, ToIdsObjectMapper.getLanguage("en"));
    }

    @Test
    public void getLanguage_languageValueDE_returnLanguageGerman() {
        /* ACT && ASSERT */
        assertEquals(Language.DE, ToIdsObjectMapper.getLanguage("DE"));
    }

    @Test
    public void getLanguage_languageValueIt_returnLanguageItalian() {
        /* ACT && ASSERT */
        assertEquals(Language.IT, ToIdsObjectMapper.getLanguage("it"));
    }

    @Test
    public void getLanguage_languageValueIT_returnLanguageItalian() {
        /* ACT && ASSERT */
        assertEquals(Language.IT, ToIdsObjectMapper.getLanguage("IT"));
    }

    @Test
    public void getKeywordsAsString_keywordsNull_returnEmptyList() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = MappingUtils.getKeywordsAsString(null);

        /* ASSERT */
        assertEquals(0, result.size());
    }

    @Test
    public void getKeywordsAsString_keywordsEmpty_returnsEmptyList() {
        /* ACT */
        final var result = MappingUtils.getKeywordsAsString(new ArrayList<>());

        /* ASSERT */
        assertTrue(result.isEmpty());
    }

    @Test
    public void getKeywordsAsString_oneKeyword_returnsKeywordValueInList() {
        /* ARRANGE */
        final var keywordAsString = "keyword";
        final var keyword = new TypedLiteral(keywordAsString, "en");
        final var keywords = new ArrayList<>(Collections.singletonList(keyword));

        /* ACT */
        final var result = MappingUtils.getKeywordsAsString(keywords);

        /* ASSERT */
        assertEquals(1, result.size());
        assertTrue(result.contains(keywordAsString));
    }

    @Test
    public void getKeywordsAsString_twoKeywords_returnsKeywordValuesInList() {
        /* ARRANGE */
        final var keywordAsString1 = "keyword";
        final var keyword1 = new TypedLiteral(keywordAsString1, "en");
        final var keywordAsString2 = "keyword";
        final var keyword2 = new TypedLiteral(keywordAsString2, "en");
        final var keywords = new ArrayList<>(Arrays.asList(keyword1, keyword2));

        /* ACT */
        final var result = MappingUtils.getKeywordsAsString(keywords);

        /* ASSERT */
        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(keywordAsString1, keywordAsString2)));
    }

    @Test
    public void getKeywordsAsString_keywordValueNull_returnsNullInList() {
        /* ARRANGE */
        final var keyword = new TypedLiteral(null, "en");
        final var keywords = new ArrayList<>(Collections.singletonList(keyword));

        /* ACT */
        final var result = MappingUtils.getKeywordsAsString(keywords);

        /* ASSERT */
        assertEquals(1, result.size());
        assertTrue(result.contains(null));
    }

    @Test
    public void getGregorianOf_inputNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class,
                () -> ToIdsObjectMapper.getGregorianOf((ZonedDateTime) null));
    }

    @Test
    public void getGregorianOf_validDate_returnDateAsXMLGregorianCalendar() {
        /* ACT */
        final var result = ToIdsObjectMapper.getGregorianOf(date);

        /* ASSERT */
        final var calendar = GregorianCalendar.from(date);

        assertEquals(calendar.get(Calendar.YEAR), result.getYear());
        assertEquals(calendar.get(Calendar.MONTH) + 1, result.getMonth());
        assertEquals(calendar.get(Calendar.DAY_OF_MONTH), result.getDay());
        assertEquals(calendar.get(Calendar.HOUR_OF_DAY), result.getHour());
        assertEquals(calendar.get(Calendar.MINUTE), result.getMinute());
        assertEquals(calendar.get(Calendar.SECOND), result.getSecond());

        final var resultMilliseconds = (int) (result.getFractionalSecond().doubleValue() * 1000);
        assertEquals(calendar.get(Calendar.MILLISECOND), resultMilliseconds);
    }

    @Test
    public void getGregorianOf_validDate_returnValidISO8601Time() {
        /* ACT */
        final var result = ToIdsObjectMapper.getGregorianOf(date).toString();

        /* ASSERT */
        assertEquals("53203-06-26T12:10:04.000Z", result);
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private boolean typedLiteralListContainsValues(final List<TypedLiteral> list,
                                                   List<String> values) {
        final var listValues = new ArrayList<>();
        for (var listElement : list) {
            listValues.add(listElement.getValue());
        }
        return listValues.containsAll(values);
    }

    private BaseConnector getBaseConnector() {
        return new BaseConnectorBuilder(URI.create("https://w3id.org/idsa/autogen/baseConnector" +
                "/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                ._maintainer_(URI.create("https://example.com"))
                ._curator_(URI.create("https://example.com"))
                ._securityProfile_(SecurityProfile.BASE_SECURITY_PROFILE)
                ._outboundModelVersion_("4.0.0")
                ._inboundModelVersion_(Util.asList("4.0.0"))
                ._title_(Util.asList(new TypedLiteral("Dataspace Connector")))
                ._description_(Util.asList(new TypedLiteral("Test Connector")))
                ._version_("v3.0.0")
                ._publicKey_(new PublicKeyBuilder(URI.create("https://w3id" +
                        ".org/idsa/autogen/publicKey/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                        ._keyType_(KeyType.RSA)
                        ._keyValue_("public key".getBytes())
                        .build()
                )
                ._hasDefaultEndpoint_(new ConnectorEndpointBuilder(URI.create("https://w3id" +
                        ".org/idsa/autogen/connectorEndpoint/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                        ._accessURL_(URI.create("/api/ids/data"))
                        .build())
                .build();
    }

    private Resource getResource() {
        return new ResourceBuilder(URI.create("https://w3id.org/idsa/autogen/resource/591467af" +
                "-9633-4a4e-8bcf-47ba4e6679ea"))
                ._contractOffer_(Util.asList(getContractOffer()))
                ._created_(getDateAsXMLGregorianCalendar())
                ._description_(Util.asList(new TypedLiteral("description", "EN")))
                ._language_(Util.asList(Language.EN))
                ._modified_(getDateAsXMLGregorianCalendar())
                ._publisher_(URI.create("http://publisher.com"))
                ._representation_(Util.asList(getRepresentation()))
                ._resourceEndpoint_(Util.asList(new ConnectorEndpointBuilder(URI.create("https" +
                        "://w3id.org/idsa/autogen/connectorEndpoint/591467af-9633-4a4e-8bcf" +
                        "-47ba4e6679ea"))
                        ._accessURL_(URI.create("http://connector-endpoint.com"))
                        .build()))
                ._sovereign_(URI.create("http://sovereign.com"))
                ._standardLicense_(URI.create("http://license.com"))
                ._title_(Util.asList(new TypedLiteral("title", "EN")))
                ._version_("1.0")
                .build();
    }

    private Artifact getArtifact() {
        return new ArtifactBuilder(URI.create("https://w3id.org/idsa/autogen/artifact/591467af" +
                "-9633-4a4e-8bcf-47ba4e6679ea"))
                ._byteSize_(BigInteger.ONE)
                ._creationDate_(getDateAsXMLGregorianCalendar())
                ._fileName_("file name")
                .build();
    }

    private Representation getRepresentation() {
        return new RepresentationBuilder(URI.create("https://w3id.org/idsa/autogen/representation" +
                "/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                ._created_(getDateAsXMLGregorianCalendar())
                ._instance_(Util.asList(getArtifact()))
                ._language_(Language.EN)
                ._mediaType_(new IANAMediaTypeBuilder(URI.create("https://w3id" +
                        ".org/idsa/autogen/mediaType/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                        ._filenameExtension_("json")
                        .build())
                ._modified_(getDateAsXMLGregorianCalendar())
                ._representationStandard_(URI.create("http://standard.com"))
                .build();
    }

    private ContractOffer getContractOffer() {
        return new ContractOfferBuilder(URI.create("https://w3id.org/idsa/autogen/contractOffer" +
                "/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                .build();
    }

    private ContractRequest getContractRequest() {
        final var permissions = new ArrayList<Permission>();
        final var prohibitions = new ArrayList<Prohibition>();
        final var obligations = new ArrayList<Duty>();

        permissions.add((Permission) getRule());

        return new ContractRequestBuilder(URI.create("https://w3id" +
                ".org/idsa/autogen/contractRequest/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                ._consumer_(URI.create("https://w3id.org/idsa/autogen/baseConnector/591467af-9633" +
                        "-4a4e-8bcf-47ba4e6679ea"))
                ._contractDate_(getDateAsXMLGregorianCalendar())
                ._contractStart_(getDateAsXMLGregorianCalendar())
                ._obligation_(obligations)
                ._permission_(permissions)
                ._prohibition_(prohibitions)
                .build();
    }

    private ContractAgreement getContractAgreement() {
        return new ContractAgreementBuilder(URI.create("https://w3id" +
                ".org/idsa/autogen/contractAgreement/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                ._provider_(URI.create("http://provider.com"))
                ._consumer_(URI.create("http://consumer.com"))
                ._permission_(Util.asList((Permission) getRule()))
                ._contractDate_(getDateAsXMLGregorianCalendar())
                ._contractStart_(getDateAsXMLGregorianCalendar())
                ._contractEnd_(getDateAsXMLGregorianCalendar())
                .build();
    }

    private Rule getRule() {
        return new PermissionBuilder(URI.create("https://w3id.org/idsa/autogen/permission" +
                "/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                ._description_(Util.asList(new TypedLiteral("usage-logging")))
                ._action_(Util.asList(Action.USE))
                ._postDuty_(Util.asList(new DutyBuilder(URI.create("https://w3id" +
                        ".org/idsa/autogen/duty/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                        ._action_(Util.asList(Action.LOG))
                        .build()))
                .build();
    }

    @SneakyThrows
    private XMLGregorianCalendar getDateAsXMLGregorianCalendar() {
        GregorianCalendar calendar = GregorianCalendar.from(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }
}
