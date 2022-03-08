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
package io.dataspaceconnector.service.resource.ids.builder;

import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.catalog.CatalogDesc;
import io.dataspaceconnector.model.catalog.CatalogFactory;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.contract.ContractDesc;
import io.dataspaceconnector.model.contract.ContractFactory;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.representation.RepresentationDesc;
import io.dataspaceconnector.model.representation.RepresentationFactory;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.model.resource.OfferedResourceFactory;
import io.dataspaceconnector.model.resource.Resource;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.model.rule.ContractRuleDesc;
import io.dataspaceconnector.model.rule.ContractRuleFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class IdsCatalogBuilderTest {

    @SpyBean
    private CatalogFactory catalogFactory;

    @SpyBean
    private OfferedResourceFactory resourceFactory;

    @SpyBean
    private RepresentationFactory representationFactory;

    @SpyBean
    private ArtifactFactory artifactFactory;

    @SpyBean
    private ContractFactory contractFactory;

    @SpyBean
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

        assertTrue(idsCatalog.getRequestedResource().isEmpty());

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

        assertTrue(idsCatalog.getRequestedResource().isEmpty());

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

        assertTrue(idsCatalog.getRequestedResource().isEmpty());
        assertTrue(idsCatalog.getOfferedResource().isEmpty());
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

        assertTrue(idsCatalog.getRequestedResource().isEmpty());

        final var offeredResources = idsCatalog.getOfferedResource();
        assertEquals(0, offeredResources.size());
    }

    @Test
    public void create_maxDepth2_returnCatalogWithoutResources() {
        /* ARRANGE */
        final var catalog = getCatalog();

        /* ACT */
        final var idsCatalog = idsCatalogBuilder.create(catalog, 2);

        /* ASSERT */
        assertTrue(idsCatalog.getId().isAbsolute());
        assertTrue(idsCatalog.getId().toString().contains(idsCatalog.getId().toString()));
        assertNull(idsCatalog.getProperties());

        assertTrue(idsCatalog.getRequestedResource().isEmpty());

        final var offeredResources = idsCatalog.getOfferedResource();
        assertEquals(0, offeredResources.size());
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

        assertTrue(idsCatalog.getRequestedResource().isEmpty());

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

        final var idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(artifact, UUID.randomUUID());

        final var creationDateField = Entity.class.getDeclaredField("creationDate");
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

        final var idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(representation, UUID.randomUUID());

        final var creationDateField = Entity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(representation, ZonedDateTime.now(ZoneOffset.UTC));

        final var modificationDateField = Entity.class.getDeclaredField("modificationDate");
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

        final var idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(rule, UUID.randomUUID());

        final var creationDateField = Entity.class.getDeclaredField("creationDate");
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

        final var idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(contract, UUID.randomUUID());

        final var creationDateField = Entity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(contract, date);

        final var modificationDateField = Entity.class.getDeclaredField("modificationDate");
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
        resourceDesc.setLicense(URI.create("http://license.com"));
        resourceDesc.setPublisher(URI.create("http://publisher.com"));
        resourceDesc.setSovereign(URI.create("http://sovereign.com"));

        final var resource = resourceFactory.create(resourceDesc);

        final var idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(resource, UUID.randomUUID());

        final var creationDateField = Entity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(resource, date);

        final var modificationDateField = Entity.class.getDeclaredField("modificationDate");
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

        final var idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(catalog, UUID.randomUUID());

        final var creationDateField = Entity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(catalog, date);

        final var modificationDateField = Entity.class.getDeclaredField("modificationDate");
        modificationDateField.setAccessible(true);
        modificationDateField.set(catalog, date);

        final var offeredResourcesField = Catalog.class.getDeclaredField("offeredResources");
        offeredResourcesField.setAccessible(true);
        offeredResourcesField.set(catalog, Collections.singletonList(getOfferedResource()));

        return catalog;
    }

    @SneakyThrows
    private Catalog getCatalogWithAdditional() {
        final var catalog = getCatalog();
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        final var additionalField = Entity.class.getDeclaredField("additional");
        additionalField.setAccessible(true);
        additionalField.set(catalog, additional);

        return catalog;
    }

}
