package de.fraunhofer.isst.dataspaceconnector.services.ids;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactFactory;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogFactory;
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
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsCatalogBuilder;
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

@SpringBootTest(classes = {CatalogFactory.class, OfferedResourceFactory.class,
        RepresentationFactory.class, ArtifactFactory.class, ContractFactory.class,
        ContractRuleFactory.class, IdsCatalogBuilder.class, IdsResourceBuilder.class,
        IdsRepresentationBuilder.class, IdsArtifactBuilder.class, IdsContractBuilder.class,
        IdsPermissionBuilder.class, IdsProhibitionBuilder.class, IdsDutyBuilder.class,
        DeserializationService.class, SerializerProvider.class})
public class IdsCatalogBuilderTest {

    @Autowired
    private CatalogFactory catalogFactory;

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
    private IdsCatalogBuilder idsCatalogBuilder;

    private final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    private final String title = "title";

    private final String description = "description";

    @Test
    public void create_inputNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> idsCatalogBuilder.create(null));
    }

    @Test
    public void create_defaultDepth_returnCompleteCatalog() {
        /* ARRANGE */
        final var catalog = getCatalog();

        /* ACT */
        final var idsCatalog = idsCatalogBuilder.create(catalog);

        /* ASSERT */
        assertTrue(idsCatalog.getId().isAbsolute());
        assertTrue(idsCatalog.getId().toString().contains(idsCatalog.getId().toString()));
        assertNull(idsCatalog.getProperties());

        assertNull(idsCatalog.getRequestedResource());

        final var offeredResources = idsCatalog.getOfferedResource();
        assertEquals(1, offeredResources.size());

        final var representations = offeredResources.get(0).getRepresentation();
        assertEquals(1, representations.size());
        assertEquals(1, representations.get(0).getInstance().size());

        final var contracts = offeredResources.get(0).getContractOffer();
        assertEquals(1, contracts.size());
        assertEquals(1, contracts.get(0).getPermission().size());
        assertTrue(contracts.get(0).getProhibition().isEmpty());
        assertTrue(contracts.get(0).getObligation().isEmpty());
    }

    @Test
    public void create_defaultDepthWithAdditional_returnCompleteCatalog() {
        /* ARRANGE */
        final var catalog = getCatalogWithAdditional();

        /* ACT */
        final var idsCatalog = idsCatalogBuilder.create(catalog);

        /* ASSERT */
        assertTrue(idsCatalog.getId().isAbsolute());
        assertTrue(idsCatalog.getId().toString().contains(idsCatalog.getId().toString()));

        assertNotNull(idsCatalog.getProperties());
        assertEquals(1, idsCatalog.getProperties().size());
        assertEquals("value", idsCatalog.getProperties().get("key"));

        assertNull(idsCatalog.getRequestedResource());

        final var offeredResources = idsCatalog.getOfferedResource();
        assertEquals(1, offeredResources.size());

        final var representations = offeredResources.get(0).getRepresentation();
        assertEquals(1, representations.size());
        assertEquals(1, representations.get(0).getInstance().size());

        final var contracts = offeredResources.get(0).getContractOffer();
        assertEquals(1, contracts.size());
        assertEquals(1, contracts.get(0).getPermission().size());
        assertTrue(contracts.get(0).getProhibition().isEmpty());
        assertTrue(contracts.get(0).getObligation().isEmpty());
    }

    @Test
    public void create_maxDepth0_returnCatalogWithoutResources() {
        /* ARRANGE */
        final var catalog = getCatalog();

        /* ACT */
        final var idsCatalog = idsCatalogBuilder.create(catalog, 0);

        /* ASSERT */
        assertTrue(idsCatalog.getId().isAbsolute());
        assertTrue(idsCatalog.getId().toString().contains(idsCatalog.getId().toString()));
        assertNull(idsCatalog.getProperties());

        assertNull(idsCatalog.getRequestedResource());
        assertNull(idsCatalog.getOfferedResource());
    }

    @Test
    public void create_maxDepth1_returnCatalogWithoutRepresentationsAndContracts() {
        /* ARRANGE */
        final var catalog = getCatalog();

        /* ACT */
        final var idsCatalog = idsCatalogBuilder.create(catalog, 1);

        /* ASSERT */
        assertTrue(idsCatalog.getId().isAbsolute());
        assertTrue(idsCatalog.getId().toString().contains(idsCatalog.getId().toString()));
        assertNull(idsCatalog.getProperties());

        assertNull(idsCatalog.getRequestedResource());

        final var offeredResources = idsCatalog.getOfferedResource();
        assertEquals(1, offeredResources.size());

        assertNull(offeredResources.get(0).getRepresentation());
        assertNull(offeredResources.get(0).getContractOffer());
    }

    @Test
    public void create_maxDepth2_returnCatalogWithoutArtifactsAndRules() {
        /* ARRANGE */
        final var catalog = getCatalog();

        /* ACT */
        final var idsCatalog = idsCatalogBuilder.create(catalog, 2);

        /* ASSERT */
        assertTrue(idsCatalog.getId().isAbsolute());
        assertTrue(idsCatalog.getId().toString().contains(idsCatalog.getId().toString()));
        assertNull(idsCatalog.getProperties());

        assertNull(idsCatalog.getRequestedResource());

        final var offeredResources = idsCatalog.getOfferedResource();
        assertEquals(1, offeredResources.size());

        final var representations = offeredResources.get(0).getRepresentation();
        assertEquals(1, representations.size());
        assertNull(representations.get(0).getInstance());

        final var contracts = offeredResources.get(0).getContractOffer();
        assertEquals(1, contracts.size());
        assertNull(contracts.get(0).getPermission());
        assertNull(contracts.get(0).getProhibition());
        assertNull(contracts.get(0).getProhibition());
    }

    @Test
    public void create_maxDepth5_returnCompleteCatalog() {
        /* ARRANGE */
        final var catalog = getCatalog();

        /* ACT */
        final var idsCatalog = idsCatalogBuilder.create(catalog, 5);

        /* ASSERT */
        assertTrue(idsCatalog.getId().isAbsolute());
        assertTrue(idsCatalog.getId().toString().contains(idsCatalog.getId().toString()));
        assertNull(idsCatalog.getProperties());

        assertNull(idsCatalog.getRequestedResource());

        final var offeredResources = idsCatalog.getOfferedResource();
        assertEquals(1, offeredResources.size());

        final var representations = offeredResources.get(0).getRepresentation();
        assertEquals(1, representations.size());
        assertEquals(1, representations.get(0).getInstance().size());

        final var contracts = offeredResources.get(0).getContractOffer();
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
        representationDesc.setLanguage("EN");
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
        resourceDesc.setLanguage("EN");
        resourceDesc.setTitle(title);
        resourceDesc.setDescription(description);
        resourceDesc.setKeywords(Collections.singletonList("keyword"));
        resourceDesc.setEndpointDocumentation(URI.create("http://endpoint-doc.com"));
        resourceDesc.setLicence(URI.create("http://license.com"));
        resourceDesc.setPublisher(URI.create("http://publisher.com"));
        resourceDesc.setSovereign(URI.create("http://sovereign.com"));

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
    private Catalog getCatalog() {
        final var catalogDesc = new CatalogDesc();
        catalogDesc.setTitle(title);
        catalogDesc.setDescription(description);
        final var catalog = catalogFactory.create(catalogDesc);

        final var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(catalog, UUID.randomUUID());

        final var creationDateField = AbstractEntity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(catalog, date);

        final var modificationDateField = AbstractEntity.class.getDeclaredField("modificationDate");
        modificationDateField.setAccessible(true);
        modificationDateField.set(catalog, date);

        final var offeredResourcesField = Catalog.class.getDeclaredField("offeredResources");
        offeredResourcesField.setAccessible(true);
        offeredResourcesField.set(catalog, Collections.singletonList(getOfferedResource()));;

        return catalog;
    }

    @SneakyThrows
    private Catalog getCatalogWithAdditional() {
        final var catalog = getCatalog();
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        final var additionalField = AbstractEntity.class.getDeclaredField("additional");
        additionalField.setAccessible(true);
        additionalField.set(catalog, additional);

        return catalog;
    }

}
