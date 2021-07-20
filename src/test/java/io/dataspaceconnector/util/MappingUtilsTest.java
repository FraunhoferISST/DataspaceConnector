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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.Artifact;
import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.Catalog;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.ContentType;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractOfferBuilder;
import de.fraunhofer.iais.eis.DutyBuilder;
import de.fraunhofer.iais.eis.Frequency;
import de.fraunhofer.iais.eis.GeoPointBuilder;
import de.fraunhofer.iais.eis.IANAMediaTypeBuilder;
import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.Permission;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.Rule;
import de.fraunhofer.iais.eis.TemporalEntityBuilder;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MappingUtilsTest {

    private final ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(1616772571804L),
            ZoneOffset.UTC);

    @Test
    public void fromIdsCatalog_inputNull_throwIllegalArgumentExceptionException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MappingUtils.fromIdsCatalog(null));
    }

    @Test
    public void fromIdsCatalog_validInput_returnCatalogTemplate() {
        /* ARRANGE */
        final var catalog = getCatalog();
        catalog.setProperty("test", "test");

        /* ACT */
        final var result = MappingUtils.fromIdsCatalog(catalog);

        /* ASSERT */
        assertEquals(catalog.getProperties().get("test"),
                result.getDesc().getAdditional().get("test"));
    }

    @Test
    public void fromIdsOfferedResource_inputNull_throwIllegalArgumentExceptionException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MappingUtils.fromIdsOfferedResource(null));
    }

    @Test
    public void fromIdsOfferedResource_validInput_returnResourceTemplate() {
        /* ARRANGE */
        final var resource = getResource();
        resource.setProperty("test", "test");

        /* ACT */
        final var result = MappingUtils.fromIdsOfferedResource(resource);

        /* ASSERT */
        assertEquals(resource.getKeyword().get(0).getValue(), result.getDesc().getKeywords().get(0));
        assertEquals(resource.getDescription().get(0).getValue(), result.getDesc().getDescription());
        assertEquals(resource.getPublisher(), result.getDesc().getPublisher());
        assertEquals(resource.getStandardLicense(), result.getDesc().getLicense());
        assertEquals(resource.getLanguage().get(0).toString(), result.getDesc().getLanguage());
        assertEquals(resource.getTitle().get(0).getValue(), result.getDesc().getTitle());
        assertEquals(resource.getSovereign(), result.getDesc().getSovereign());
        assertEquals(resource.getResourceEndpoint().get(0).getEndpointDocumentation().get(0), result.getDesc().getEndpointDocumentation());

        final var additional = result.getDesc().getAdditional();
        assertEquals(resource.getAccrualPeriodicity().toRdf(), additional.get("ids:accrualPeriodicity"));
        assertEquals(resource.getContentPart().get(0).toString(), additional.get("ids:contentPart"));
        assertEquals(resource.getContentStandard().toString(), additional.get("ids:contentStandard"));
        assertEquals(resource.getContentType().toRdf(), additional.get("ids:contentType"));
        assertEquals(resource.getCreated().toXMLFormat(), additional.get("ids:created"));
        assertEquals(resource.getCustomLicense().toString(), additional.get("ids:customLicense"));
        assertEquals(resource.getDefaultRepresentation().get(0).toString(), additional.get("ids:defaultRepresentation"));
        assertEquals(resource.getModified().toXMLFormat(), additional.get("ids:modified"));
        // assertEquals(resource.getResourceEndpoint().get(0).toString(), additional.get("ids:resourceEndpoint"));
        assertEquals(resource.getResourcePart().get(0).toString(), additional.get("ids:resourcePart"));
        assertEquals(resource.getSample().get(0).toString(), additional.get("ids:sample"));
        assertEquals(resource.getShapesGraph().toString(), additional.get("ids:shapesGraph"));
        assertEquals(resource.getSpatialCoverage().get(0).toString(), additional.get("ids:spatialCoverage"));
        assertEquals(resource.getTemporalCoverage().get(0).toString(), additional.get("ids:temporalCoverage"));
        assertEquals(resource.getTemporalResolution().toString(), additional.get("ids:temporalResolution"));
        assertEquals(resource.getTheme().get(0).toString(), additional.get("ids:theme"));
        assertEquals(resource.getVariant().toString(), additional.get("ids:variant"));
        assertEquals(resource.getVersion(), additional.get("ids:version"));
        assertEquals("test", additional.get("test"));
    }

    @Test
    public void fromIdsResource_inputNull_throwIllegalArgumentExceptionException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MappingUtils.fromIdsResource(null));
    }

    @Test
    public void fromIdsResource_validInput_returnResourceTemplate() {
        /* ARRANGE */
        final var resource = getResource();
        resource.setProperty("test", "test");

        /* ACT */
        final var result = MappingUtils.fromIdsResource(resource);

        /* ASSERT */
        assertEquals(resource.getId(), result.getDesc().getRemoteId());
        assertEquals(resource.getKeyword().get(0).getValue(), result.getDesc().getKeywords().get(0));
        assertEquals(resource.getDescription().get(0).getValue(), result.getDesc().getDescription());
        assertEquals(resource.getPublisher(), result.getDesc().getPublisher());
        assertEquals(resource.getStandardLicense(), result.getDesc().getLicense());
        assertEquals(resource.getLanguage().get(0).toString(), result.getDesc().getLanguage());
        assertEquals(resource.getTitle().get(0).getValue(), result.getDesc().getTitle());
        assertEquals(resource.getSovereign(), result.getDesc().getSovereign());
        assertEquals(resource.getResourceEndpoint().get(0).getEndpointDocumentation().get(0), result.getDesc().getEndpointDocumentation());

        final var additional = result.getDesc().getAdditional();
        assertEquals(resource.getAccrualPeriodicity().toRdf(), additional.get("ids:accrualPeriodicity"));
        assertEquals(resource.getContentPart().get(0).toString(), additional.get("ids:contentPart"));
        assertEquals(resource.getContentStandard().toString(), additional.get("ids:contentStandard"));
        assertEquals(resource.getContentType().toRdf(), additional.get("ids:contentType"));
        assertEquals(resource.getCreated().toXMLFormat(), additional.get("ids:created"));
        assertEquals(resource.getCustomLicense().toString(), additional.get("ids:customLicense"));
        assertEquals(resource.getDefaultRepresentation().get(0).toString(), additional.get("ids:defaultRepresentation"));
        assertEquals(resource.getModified().toXMLFormat(), additional.get("ids:modified"));
        // assertEquals(resource.getResourceEndpoint().get(0).toString(), additional.get("ids:resourceEndpoint"));
        assertEquals(resource.getResourcePart().get(0).toString(), additional.get("ids:resourcePart"));
        assertEquals(resource.getSample().get(0).toString(), additional.get("ids:sample"));
        assertEquals(resource.getShapesGraph().toString(), additional.get("ids:shapesGraph"));
        assertEquals(resource.getSpatialCoverage().get(0).toString(), additional.get("ids:spatialCoverage"));
        assertEquals(resource.getTemporalCoverage().get(0).toString(), additional.get("ids:temporalCoverage"));
        assertEquals(resource.getTemporalResolution().toString(), additional.get("ids:temporalResolution"));
        assertEquals(resource.getTheme().get(0).toString(), additional.get("ids:theme"));
        assertEquals(resource.getVariant().toString(), additional.get("ids:variant"));
        assertEquals(resource.getVersion(), additional.get("ids:version"));
        assertEquals("test", additional.get("test"));
    }

    @Test
    public void fromIdsRepresentation_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MappingUtils.fromIdsRepresentation(null));
    }

    @Test
    public void fromIdsRepresentation_validInput_returnRepresentationTemplate() {
        /* ARRANGE */
        final var representation = getRepresentation();
        representation.setProperty("test", "test");

        /* ACT */
        final var result = MappingUtils.fromIdsRepresentation(representation);

        /* ASSERT */
        assertEquals(representation.getId(), result.getDesc().getRemoteId());
        assertEquals(representation.getMediaType().getFilenameExtension(), result.getDesc().getMediaType());
        assertEquals(representation.getLanguage().toString(), result.getDesc().getLanguage());
        assertEquals(representation.getRepresentationStandard().toString(), result.getDesc().getStandard());

        final var additional = result.getDesc().getAdditional();
        assertEquals(representation.getCreated().toXMLFormat(), additional.get("ids:created"));
        assertEquals(representation.getModified().toXMLFormat(), additional.get("ids:modified"));
        assertEquals(representation.getShapesGraph().toString(), additional.get("ids:shapesGraph"));
    }

    @Test
    public void fromIdsArtifact_artifactNull_throwIllegalArgumentException() {
        /* ARRANGE */
        final var remoteAddress = URI.create("https://someURL");

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MappingUtils.fromIdsArtifact(null, true, remoteAddress));
    }

    @Test
    public void fromIdsArtifact_validInput_returnArtifactTemplate() {
        /* ARRANGE */
        final var artifact = getArtifact();
        artifact.setProperty("test", "test");
        final var download = true;
        final var remoteAddress = URI.create("https://someURL");

        /* ACT */
        final var result = MappingUtils.fromIdsArtifact(artifact, download, remoteAddress);

        /* ASSERT */
        assertEquals(artifact.getId(), result.getDesc().getRemoteId());
        assertEquals(artifact.getFileName(), result.getDesc().getTitle());
        assertEquals(download, result.getDesc().isAutomatedDownload());

        final var additional = result.getDesc().getAdditional();
        assertEquals(artifact.getByteSize().toString(), additional.get("ids:byteSize"));
        assertEquals(artifact.getCheckSum(), additional.get("ids:checkSum"));
        assertEquals(artifact.getCreationDate().toXMLFormat(), additional.get("ids:creationDate"));
        assertEquals(artifact.getDuration().toString(), additional.get("ids:duration"));
        assertEquals("test", additional.get("test"));
    }

    @Test
    public void fromIdsContract_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> MappingUtils.fromIdsContract(null));
    }

    @SneakyThrows
    @Test
    public void fromIdsContract_validInput_returnContractTemplate() {
        /* ARRANGE */
        final var contract = getContract();
        contract.setProperty("test", "test");

        /* ACT */
        final var result = MappingUtils.fromIdsContract(contract);

        /* ASSERT */
        assertEquals(contract.getId(), result.getDesc().getRemoteId());
        assertEquals(contract.getProvider(), result.getDesc().getProvider());
        assertEquals(contract.getConsumer(), result.getDesc().getConsumer());
        assertTrue(date.isEqual(result.getDesc().getStart()));
        assertTrue(date.isEqual(result.getDesc().getEnd()));

        final var additional = result.getDesc().getAdditional();
        assertEquals("test", additional.get("test"));
    }

    @Test
    public void fromIdsRule_validInput_returnRuleTemplate() {
        /* ARRANGE */
        final var rule = getRule();

        /* ACT */
        final var result = MappingUtils.fromIdsRule(rule);

        /* ASSERT */
        assertEquals(rule.getId(), result.getDesc().getRemoteId());
        assertEquals(rule.getTitle().toString(), result.getDesc().getTitle());
        assertEquals(rule.toRdf(), result.getDesc().getValue());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    @SneakyThrows
    private Catalog getCatalog() {
        return new ResourceCatalogBuilder()
                .build();
    }

    @SneakyThrows
    private Resource getResource() {
        return new ResourceBuilder(URI.create("https://w3id.org/idsa/autogen/resource/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                ._contractOffer_(Util.asList(getContractOffer()))
                ._created_(getDateAsXMLGregorianCalendar())
                ._description_(Util.asList(new TypedLiteral("description", "EN")))
                ._language_(Util.asList(Language.EN))
                ._modified_(getDateAsXMLGregorianCalendar())
                ._publisher_(URI.create("http://publisher.com"))
                ._representation_(Util.asList(getRepresentation()))
                ._resourceEndpoint_(Util.asList(new ConnectorEndpointBuilder(URI.create("https://w3id.org/idsa/autogen/connectorEndpoint/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                        ._accessURL_(URI.create("http://connector-endpoint.com"))
                        ._endpointDocumentation_(Util.asList(URI.create("http://connector-endpoint-docs.com")))
                        .build()))
                ._sovereign_(URI.create("http://sovereign.com"))
                ._standardLicense_(URI.create("http://license.com"))
                ._title_(Util.asList(new TypedLiteral("title", "EN")))
                ._version_("1.0")
                ._accrualPeriodicity_(Frequency.DAILY)
                ._contentPart_(Util.asList(new ResourceBuilder().build()))
                ._contentStandard_(URI.create("http://standard.com"))
                ._contentType_(ContentType.SCHEMA_DEFINITION)
                ._customLicense_(URI.create("http://license.com"))
                ._defaultRepresentation_(Util.asList(getRepresentation()))
                ._resourcePart_(Util.asList(new ResourceBuilder().build()))
                ._sample_(Util.asList(new ResourceBuilder().build()))
                ._shapesGraph_(URI.create("http://shapes-graph.com"))
                ._sovereign_(URI.create("http://sovereign.com"))
                ._spatialCoverage_(Util.asList(new GeoPointBuilder()
                        ._latitude_(12.3f)
                        ._longitude_(45.6f)
                        .build()))
                ._standardLicense_(URI.create("http://license.com"))
                ._temporalCoverage_(Util.asList(new TemporalEntityBuilder()
                        ._hasDuration_(DatatypeFactory.newInstance().newDuration("P3M"))
                        .build()))
                ._temporalResolution_(Frequency.DAILY)
                ._theme_(Util.asList(URI.create("http://theme.com")))
                ._variant_(new ResourceBuilder().build())
                ._keyword_(Util.asList(new TypedLiteral("keyword", "EN")))
                .build();
    }

    @SneakyThrows
    private Resource getResourceWithKeywordsNull() {
        return new ResourceBuilder(URI.create("https://w3id.org/idsa/autogen/resource/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                ._contractOffer_(Util.asList(getContractOffer()))
                ._created_(getDateAsXMLGregorianCalendar())
                ._description_(Util.asList(new TypedLiteral("description", "EN")))
                ._language_(Util.asList(Language.EN))
                ._modified_(getDateAsXMLGregorianCalendar())
                ._publisher_(URI.create("http://publisher.com"))
                ._representation_(Util.asList(getRepresentation()))
                ._resourceEndpoint_(Util.asList(new ConnectorEndpointBuilder(URI.create("https://w3id.org/idsa/autogen/connectorEndpoint/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                        ._accessURL_(URI.create("http://connector-endpoint.com"))
                        ._endpointDocumentation_(Util.asList(URI.create("http://connector-endpoint-docs.com")))
                        .build()))
                ._sovereign_(URI.create("http://sovereign.com"))
                ._standardLicense_(URI.create("http://license.com"))
                ._title_(Util.asList(new TypedLiteral("title", "EN")))
                ._version_("1.0")
                ._accrualPeriodicity_(Frequency.DAILY)
                ._contentPart_(Util.asList(new ResourceBuilder().build()))
                ._contentStandard_(URI.create("http://standard.com"))
                ._contentType_(ContentType.SCHEMA_DEFINITION)
                ._customLicense_(URI.create("http://license.com"))
                ._defaultRepresentation_(Util.asList(getRepresentation()))
                ._resourcePart_(Util.asList(new ResourceBuilder().build()))
                ._sample_(Util.asList(new ResourceBuilder().build()))
                ._shapesGraph_(URI.create("http://shapes-graph.com"))
                ._sovereign_(URI.create("http://sovereign.com"))
                ._spatialCoverage_(Util.asList(new GeoPointBuilder()
                        ._latitude_(12.3f)
                        ._longitude_(45.6f)
                        .build()))
                ._standardLicense_(URI.create("http://license.com"))
                ._temporalCoverage_(Util.asList(new TemporalEntityBuilder()
                        ._hasDuration_(DatatypeFactory.newInstance().newDuration("P3M"))
                        .build()))
                ._temporalResolution_(Frequency.DAILY)
                ._theme_(Util.asList(URI.create("http://theme.com")))
                ._variant_(new ResourceBuilder().build())
                .build();
    }

    private ContractOffer getContractOffer() {
        return new ContractOfferBuilder(URI.create("https://w3id.org/idsa/autogen/contractOffer/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                .build();
    }

    private Representation getRepresentation() {
        return new RepresentationBuilder(URI.create("https://w3id.org/idsa/autogen/representation/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                ._created_(getDateAsXMLGregorianCalendar())
                ._instance_(Util.asList(getArtifact()))
                ._language_(Language.EN)
                ._mediaType_(new IANAMediaTypeBuilder(URI.create("https://w3id.org/idsa/autogen/mediaType/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                        ._filenameExtension_("json")
                        .build())
                ._modified_(getDateAsXMLGregorianCalendar())
                ._representationStandard_(URI.create("http://standard.com"))
                ._shapesGraph_(URI.create("http://shapes-graph.com"))
                .build();
    }

    private Artifact getArtifact() {
        return new ArtifactBuilder(URI.create("https://w3id.org/idsa/autogen/artifact/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                ._byteSize_(BigInteger.ONE)
                ._checkSum_("check sum")
                ._creationDate_(getDateAsXMLGregorianCalendar())
                ._duration_(new BigDecimal("123.4"))
                ._fileName_("file name")
                .build();
    }

    private Contract getContract() {
        return new ContractAgreementBuilder(URI.create("https://w3id.org/idsa/autogen/contractAgreement/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                ._provider_(URI.create("http://provider.com"))
                ._consumer_(URI.create("http://consumer.com"))
                ._permission_(Util.asList((Permission) getRule()))
                ._contractDate_(getDateAsXMLGregorianCalendar())
                ._contractStart_(getDateAsXMLGregorianCalendar())
                ._contractEnd_(getDateAsXMLGregorianCalendar())
                .build();
    }

    private Rule getRule() {
        return new PermissionBuilder(URI.create("https://w3id.org/idsa/autogen/permission/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                ._title_(Util.asList(new TypedLiteral("Example Usage Policy")))
                ._description_(Util.asList(new TypedLiteral("usage-logging")))
                ._action_(Util.asList(Action.USE))
                ._postDuty_(Util.asList(new DutyBuilder(URI.create("https://w3id.org/idsa/autogen/duty/591467af-9633-4a4e-8bcf-47ba4e6679ea"))
                        ._action_(Util.asList(Action.LOG))
                        .build()))
                .build();
    }

    @SneakyThrows
    private XMLGregorianCalendar getDateAsXMLGregorianCalendar() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(Date.from(date.toInstant()));
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }

}
