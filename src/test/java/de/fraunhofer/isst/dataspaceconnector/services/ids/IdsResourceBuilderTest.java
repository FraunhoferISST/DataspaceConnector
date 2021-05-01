package de.fraunhofer.isst.dataspaceconnector.services.ids;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactFactory;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractFactory;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleFactory;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceFactory;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationFactory;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsArtifactBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsContractBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsDutyBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsPermissionBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsProhibitionBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsRepresentationBuilder;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsResourceBuilder;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {OfferedResourceFactory.class, RepresentationFactory.class,
        ArtifactFactory.class, ContractFactory.class, ContractRuleFactory.class,
        IdsResourceBuilder.class, IdsRepresentationBuilder.class, IdsArtifactBuilder.class,
        IdsContractBuilder.class, IdsPermissionBuilder.class, IdsProhibitionBuilder.class,
        IdsDutyBuilder.class, DeserializationService.class, SerializerProvider.class})
public class IdsResourceBuilderTest {

    @Autowired
    private OfferedResourceFactory resourceFactory;

    @Autowired
    private RepresentationFactory representationFactory;

    @Autowired
    private ArtifactFactory artifactFactory;

    @Autowired
    private ContractFactory contractFactory;

    @Autowired
    private ContractRuleFactory ruleFactory;

    @Autowired
    private IdsResourceBuilder<OfferedResource> idsResourceBuilder;

    private final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    private final Language language = Language.EN;

    private final String title = "title";

    private final String description = "description";

    private final String keyword = "keyword";

    private final URI endpointDocumentation = URI.create("http://endpoint-doc.com");

    private final URI license = URI.create("http://license.com");

    private final URI publisher = URI.create("http://publisher.com");

    private final URI sovereign = URI.create("http://sovereign.com");

    @Test
    public void create_inputNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> idsResourceBuilder.create(null));
    }

    @Test
    public void create_defaultDepth_returnCompleteResource() {
        /* ARRANGE */
        final var resource = getOfferedResource();

        /* ACT */
        final var idsResource = idsResourceBuilder.create(resource);

        /* ASSERT */
        assertTrue(idsResource.getId().isAbsolute());
        assertTrue(idsResource.getId().toString().contains(idsResource.getId().toString()));

        assertEquals(publisher, idsResource.getPublisher());
        assertEquals(sovereign, idsResource.getSovereign());
        assertEquals(license, idsResource.getStandardLicense());
        assertEquals(1, idsResource.getDescription().size());
        assertEquals(description, idsResource.getDescription().get(0).getValue());
        assertEquals(1, idsResource.getKeyword().size());
        assertEquals(keyword, idsResource.getKeyword().get(0).getValue());
        assertEquals(1, idsResource.getTitle().size());
        assertEquals(title, idsResource.getTitle().get(0).getValue());
        assertNull(idsResource.getProperties());

        final var representations = idsResource.getRepresentation();
        assertEquals(1, representations.size());
        assertEquals(1, representations.get(0).getInstance().size());

        final var contracts = idsResource.getContractOffer();
        assertEquals(1, contracts.size());
        assertEquals(1, contracts.get(0).getPermission().size());
        assertTrue(contracts.get(0).getProhibition().isEmpty());
        assertTrue(contracts.get(0).getObligation().isEmpty());
    }

    @Test
    public void create_defaultDepthWithAdditional_returnCompleteResource() {
        /* ARRANGE */
        final var resource = getOfferedResourceWithAdditional();

        /* ACT */
        final var idsResource = idsResourceBuilder.create(resource);

        /* ASSERT */
        assertTrue(idsResource.getId().isAbsolute());
        assertTrue(idsResource.getId().toString().contains(idsResource.getId().toString()));

        assertEquals(publisher, idsResource.getPublisher());
        assertEquals(sovereign, idsResource.getSovereign());
        assertEquals(license, idsResource.getStandardLicense());
        assertEquals(1, idsResource.getDescription().size());
        assertEquals(description, idsResource.getDescription().get(0).getValue());
        assertEquals(1, idsResource.getKeyword().size());
        assertEquals(keyword, idsResource.getKeyword().get(0).getValue());
        assertEquals(1, idsResource.getTitle().size());
        assertEquals(title, idsResource.getTitle().get(0).getValue());

        assertNotNull(idsResource.getProperties());
        assertEquals(1, idsResource.getProperties().size());
        assertEquals("value", idsResource.getProperties().get("key"));

        final var representations = idsResource.getRepresentation();
        assertEquals(1, representations.size());
        assertEquals(1, representations.get(0).getInstance().size());

        final var contracts = idsResource.getContractOffer();
        assertEquals(1, contracts.size());
        assertEquals(1, contracts.get(0).getPermission().size());
        assertTrue(contracts.get(0).getProhibition().isEmpty());
        assertTrue(contracts.get(0).getObligation().isEmpty());
    }

    @Test
    public void create_maxDepth0_returnResourceWithoutRepresentationsAndContracts() {
        /* ARRANGE */
        final var resource = getOfferedResource();

        /* ACT */
        final var idsResource = idsResourceBuilder.create(resource, 0);

        /* ASSERT */
        assertTrue(idsResource.getId().isAbsolute());
        assertTrue(idsResource.getId().toString().contains(idsResource.getId().toString()));

        assertEquals(publisher, idsResource.getPublisher());
        assertEquals(sovereign, idsResource.getSovereign());
        assertEquals(license, idsResource.getStandardLicense());
        assertEquals(1, idsResource.getDescription().size());
        assertEquals(description, idsResource.getDescription().get(0).getValue());
        assertEquals(1, idsResource.getKeyword().size());
        assertEquals(keyword, idsResource.getKeyword().get(0).getValue());
        assertEquals(1, idsResource.getTitle().size());
        assertEquals(title, idsResource.getTitle().get(0).getValue());
        assertNull(idsResource.getProperties());

        assertNull(idsResource.getRepresentation());
        assertNull(idsResource.getContractOffer());
    }

    @Test
    public void create_maxDepth1_returnResourceWithoutArtifactsAndRules() {
        /* ARRANGE */
        final var resource = getOfferedResource();

        /* ACT */
        final var idsResource = idsResourceBuilder.create(resource, 1);

        /* ASSERT */
        assertTrue(idsResource.getId().isAbsolute());
        assertTrue(idsResource.getId().toString().contains(idsResource.getId().toString()));

        assertEquals(publisher, idsResource.getPublisher());
        assertEquals(sovereign, idsResource.getSovereign());
        assertEquals(license, idsResource.getStandardLicense());
        assertEquals(1, idsResource.getDescription().size());
        assertEquals(description, idsResource.getDescription().get(0).getValue());
        assertEquals(1, idsResource.getKeyword().size());
        assertEquals(keyword, idsResource.getKeyword().get(0).getValue());
        assertEquals(1, idsResource.getTitle().size());
        assertEquals(title, idsResource.getTitle().get(0).getValue());
        assertNull(idsResource.getProperties());

        final var representations = idsResource.getRepresentation();
        assertEquals(1, representations.size());
        assertNull(representations.get(0).getInstance());

        final var contracts = idsResource.getContractOffer();
        assertEquals(1, contracts.size());
        assertNull(contracts.get(0).getPermission());
        assertNull(contracts.get(0).getProhibition());
        assertNull(contracts.get(0).getProhibition());
    }

    @Test
    public void create_maxDepth5_returnCompleteResource() {
        /* ARRANGE */
        final var resource = getOfferedResource();

        /* ACT */
        final var idsResource = idsResourceBuilder.create(resource, 5);

        /* ASSERT */
        assertTrue(idsResource.getId().isAbsolute());
        assertTrue(idsResource.getId().toString().contains(idsResource.getId().toString()));

        assertEquals(publisher, idsResource.getPublisher());
        assertEquals(sovereign, idsResource.getSovereign());
        assertEquals(license, idsResource.getStandardLicense());
        assertEquals(1, idsResource.getDescription().size());
        assertEquals(description, idsResource.getDescription().get(0).getValue());
        assertEquals(1, idsResource.getKeyword().size());
        assertEquals(keyword, idsResource.getKeyword().get(0).getValue());
        assertEquals(1, idsResource.getTitle().size());
        assertEquals(title, idsResource.getTitle().get(0).getValue());
        assertNull(idsResource.getProperties());

        final var representations = idsResource.getRepresentation();
        assertEquals(1, representations.size());
        assertEquals(1, representations.get(0).getInstance().size());

        final var contracts = idsResource.getContractOffer();
        assertEquals(1, contracts.size());
        assertEquals(1, contracts.get(0).getPermission().size());
        assertTrue(contracts.get(0).getProhibition().isEmpty());
        assertTrue(contracts.get(0).getObligation().isEmpty());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    @SneakyThrows
    private Artifact getArtifact() {
        final var artifactDesc = new ArtifactDesc();
        artifactDesc.setTitle(title);
        artifactDesc.setAutomatedDownload(false);
        artifactDesc.setValue("value");
        final var artifact = artifactFactory.create(artifactDesc);

        final var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(artifact, UUID.randomUUID());

        final var creationDateField = AbstractEntity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(artifact, date);

        return artifact;
    }

    @SneakyThrows
    private Representation getRepresentation() {
        final var representationDesc = new RepresentationDesc();
        representationDesc.setTitle(title);
        representationDesc.setLanguage(language.name());
        representationDesc.setMediaType("plain/text");
        representationDesc.setStandard("http://standard.com");

        final var representation = representationFactory.create(representationDesc);

        final var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(representation, UUID.randomUUID());

        final var creationDateField = AbstractEntity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(representation, ZonedDateTime.now(ZoneOffset.UTC));

        final var modificationDateField = AbstractEntity.class.getDeclaredField("modificationDate");
        modificationDateField.setAccessible(true);
        modificationDateField.set(representation, date);

        final var artifactsField = Representation.class.getDeclaredField("artifacts");
        artifactsField.setAccessible(true);
        artifactsField.set(representation, Collections.singletonList(getArtifact()));

        return representation;
    }

    @SneakyThrows
    private ContractRule getRule() {
        final var value = "{\n"
                + "    \"@type\" : \"ids:Permission\",\n"
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/permission/ae138d4f-f01d-4358"
                + "-89a7-73e7c560f3de\",\n"
                + "    \"ids:description\" : [ {\n"
                + "      \"@value\" : \"provide-access\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ],\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:USE\"\n"
                + "    } ],\n"
                + "    \"ids:title\" : [ {\n"
                + "      \"@value\" : \"Example Usage Policy\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ]\n"
                + "  }";

        final var ruleDesc = new ContractRuleDesc();
        ruleDesc.setTitle(title);
        ruleDesc.setValue(value);
        final var rule = ruleFactory.create(ruleDesc);

        final var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(rule, UUID.randomUUID());

        final var creationDateField = AbstractEntity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(rule, date);

        return rule;
    }

    @SneakyThrows
    private Contract getContract() {
        final var contractDesc = new ContractDesc();
        contractDesc.setTitle(title);
        contractDesc.setStart(date);
        contractDesc.setEnd(date);
        contractDesc.setProvider(URI.create("http://provider.com"));
        contractDesc.setConsumer(URI.create("http://consumer.com"));

        final var contract = contractFactory.create(contractDesc);

        final var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(contract, UUID.randomUUID());

        final var creationDateField = AbstractEntity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(contract, date);

        final var modificationDateField = AbstractEntity.class.getDeclaredField("modificationDate");
        modificationDateField.setAccessible(true);
        modificationDateField.set(contract, date);

        final var rulesField = Contract.class.getDeclaredField("rules");
        rulesField.setAccessible(true);
        rulesField.set(contract, Collections.singletonList(getRule()));

        return contract;
    }

    @SneakyThrows
    private OfferedResource getOfferedResource() {
        final var resourceDesc = new OfferedResourceDesc();
        resourceDesc.setLanguage(language.name());
        resourceDesc.setTitle(title);
        resourceDesc.setDescription(description);
        resourceDesc.setKeywords(Collections.singletonList(keyword));
        resourceDesc.setEndpointDocumentation(endpointDocumentation);
        resourceDesc.setLicence(license);
        resourceDesc.setPublisher(publisher);
        resourceDesc.setSovereign(sovereign);

        final var resource = resourceFactory.create(resourceDesc);

        final var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(resource, UUID.randomUUID());

        final var creationDateField = AbstractEntity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(resource, date);

        final var modificationDateField = AbstractEntity.class.getDeclaredField("modificationDate");
        modificationDateField.setAccessible(true);
        modificationDateField.set(resource, date);

        final var contractsField = Resource.class.getDeclaredField("contracts");
        contractsField.setAccessible(true);
        contractsField.set(resource, Collections.singletonList(getContract()));

        final var representationsField = Resource.class.getDeclaredField("representations");
        representationsField.setAccessible(true);
        representationsField.set(resource, Collections.singletonList(getRepresentation()));

        return resource;
    }

    @SneakyThrows
    private OfferedResource getOfferedResourceWithAdditional() {
        final var resource = getOfferedResource();
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        final var additionalField = AbstractEntity.class.getDeclaredField("additional");
        additionalField.setAccessible(true);
        additionalField.set(resource, additional);

        return resource;
    }

}
