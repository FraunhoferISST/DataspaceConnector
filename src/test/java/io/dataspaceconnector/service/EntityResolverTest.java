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
package io.dataspaceconnector.service;

import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractOfferBuilder;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.ResourceCatalog;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.exception.InvalidResourceException;
import io.dataspaceconnector.common.ids.mapping.ToIdsObjectMapper;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.service.resource.ids.builder.IdsArtifactBuilder;
import io.dataspaceconnector.service.resource.ids.builder.IdsCatalogBuilder;
import io.dataspaceconnector.service.resource.ids.builder.IdsContractBuilder;
import io.dataspaceconnector.service.resource.ids.builder.IdsRepresentationBuilder;
import io.dataspaceconnector.service.resource.ids.builder.IdsResourceBuilder;
import io.dataspaceconnector.service.resource.type.AgreementService;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import io.dataspaceconnector.service.resource.type.CatalogService;
import io.dataspaceconnector.service.resource.type.ContractService;
import io.dataspaceconnector.service.resource.type.RepresentationService;
import io.dataspaceconnector.service.resource.type.ResourceService;
import io.dataspaceconnector.service.resource.type.RuleService;
import io.dataspaceconnector.common.usagecontrol.AllowAccessVerifier;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {EntityResolver.class})
public class EntityResolverTest {

    @MockBean
    private ArtifactService artifactService;

    @MockBean
    private RepresentationService representationService;

    @MockBean
    private ResourceService<OfferedResource, OfferedResourceDesc> offerService;

    @MockBean
    private ResourceService<RequestedResource, RequestedResourceDesc> requestedService;

    @MockBean
    private CatalogService catalogService;

    @MockBean
    private ContractService contractService;

    @MockBean
    private RuleService ruleService;

    @MockBean
    private AgreementService agreementService;

    @MockBean
    private IdsCatalogBuilder catalogBuilder;

    @MockBean
    private IdsResourceBuilder<OfferedResource> offerBuilder;

    @MockBean
    private IdsArtifactBuilder artifactBuilder;

    @MockBean
    private IdsRepresentationBuilder representationBuilder;

    @MockBean
    private IdsContractBuilder contractBuilder;

    @MockBean
    private AllowAccessVerifier allowAccessVerifier;

    @MockBean
    private MultipartArtifactRetriever artifactReceiver;

    @MockBean
    private DeserializationService deserializationService;

    @Autowired
    private EntityResolver resolver;

    private final UUID resourceId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

    @Test
    public void getEntityById_null_throwsResourceNotFoundException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> resolver.getEntityById(null));
    }

    @Test
    public void getEntityById_validArtifact_returnArtifact() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/api/artifacts/" + resourceId);
        final var resource = getArtifact();
        Mockito.doReturn(resource).when(artifactService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertTrue(result.isPresent());
        assertEquals(resource, result.get());
    }

    @Test
    public void getEntityById_validRepresentation_returnRepresentation() {
        /* ARRANGE */
        final var resourceUri =
                URI.create("https://localhost:8080/api/representations/" + resourceId);
        final var resource = getRepresentation();
        Mockito.doReturn(resource).when(representationService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertTrue(result.isPresent());
        assertEquals(resource, result.get());
    }

    @Test
    public void getEntityById_validOfferedResource_returnOfferedResource() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/api/offers/" + resourceId);
        final var resource = getOfferedResource();
        Mockito.doReturn(resource).when(offerService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertTrue(result.isPresent());
        assertEquals(resource, result.get());
    }

    @Test
    public void getEntityById_validCatalog_returnCatalog() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/api/catalogs/" + resourceId);
        final var resource = getCatalog();
        Mockito.doReturn(resource).when(catalogService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertTrue(result.isPresent());
        assertEquals(resource, result.get());
    }

    @Test
    public void getEntityById_validContract_returnContract() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/api/contracts/" + resourceId);
        final var resource = getContract();
        Mockito.doReturn(resource).when(contractService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertTrue(result.isPresent());
        assertEquals(resource, result.get());
    }

    @Test
    public void getEntityById_validRule_returnRule() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/api/rules/" + resourceId);
        final var resource = getRule();
        Mockito.doReturn(resource).when(ruleService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertTrue(result.isPresent());
        assertEquals(resource, result.get());
    }

    @Test
    public void getEntityById_validAgreement_returnAgreement() {
        /* ARRANGE */
        final var resourceUri = URI.create("https://localhost:8080/api/agreements/" + resourceId);
        final var resource = getAgreement();
        Mockito.doReturn(resource).when(agreementService).get(resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertTrue(result.isPresent());
        assertEquals(resource, result.get());
    }

    @Test
    public void getEntityById_malformedAgreement_throwsResourceNotFoundException() {
        /* ARRANGE */
        final var resourceUri =
                URI.create("https://localhost:8080/someWhereIdontKnow/" + resourceId);

        /* ACT */
        final var result = resolver.getEntityById(resourceUri);

        /* ASSERT */
        assertFalse(result.isPresent());
    }

    // get entityAsRdfString

    @Test
    public void getEntityAsRdfString_null_throwInvalidResourceException() {
        /* ACT && ASSERT */
        assertThrows(InvalidResourceException.class, () -> resolver.getEntityAsRdfString(null));
    }

    @Test
    public void getEntityAsRdfString_exceptionInBuilder_throwInvalidResourceException() {
        /* ARRANGE */
        final var artifact = getArtifact();
        when(artifactBuilder.create(artifact)).thenThrow(ConstraintViolationException.class);

        /* ACT && ASSERT */
        assertThrows(InvalidResourceException.class, () -> resolver.getEntityAsRdfString(artifact));
    }

    @Test
    public void getEntityAsRdfString_artifact_returnRdfString() {
        /* ARRANGE */
        final var artifact = getArtifact();
        final var idsArtifact = getIdsArtifact();
        when(artifactBuilder.create(artifact)).thenReturn(idsArtifact);

        /* ACT */
        final var result = resolver.getEntityAsRdfString(artifact);

        /* ASSERT */
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    public void getEntityAsRdfString_offeredResource_returnRdfString() {
        /* ARRANGE */
        final var resource = getOfferedResource();
        final var idsResource = getIdsResource();
        when(offerBuilder.create(resource)).thenReturn(idsResource);

        /* ACT */
        final var result = resolver.getEntityAsRdfString(resource);

        /* ASSERT */
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    public void getEntityAsRdfString_representation_returnRdfString() {
        /* ARRANGE */
        final var representation = getRepresentation();
        final var idsRepresentation = getIdsRepresentation();
        when(representationBuilder.create(representation)).thenReturn(idsRepresentation);

        /* ACT */
        final var result = resolver.getEntityAsRdfString(representation);

        /* ASSERT */
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    public void getEntityAsRdfString_catalog_returnRdfString() {
        /* ARRANGE */
        final var catalog = getCatalog();
        final var idsCatalog = getIdsCatalog();
        when(catalogBuilder.create(catalog)).thenReturn(idsCatalog);

        /* ACT */
        final var result = resolver.getEntityAsRdfString(catalog);

        /* ASSERT */
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    public void getEntityAsRdfString_contract_returnRdfString() {
        /* ARRANGE */
        final var contract = getContract();
        final var idsContract = getIdsContract();
        when(contractBuilder.create(contract)).thenReturn(idsContract);

        /* ACT */
        final var result = resolver.getEntityAsRdfString(contract);

        /* ASSERT */
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    public void getEntityAsRdfString_agreement_returnRdfString() {
        /* ARRANGE */
        final var value = "agreement value";
        final var agreement = getAgreement(value);

        /* ACT */
        final var result = resolver.getEntityAsRdfString(agreement);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(value, result);
    }

    @Test
    public void getEntityAsRdfString_contractRule_returnRdfString() {
        /* ARRANGE */
        final var value = "rule value";
        final var rule = getRule(value);

        /* ACT */
        final var result = resolver.getEntityAsRdfString(rule);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(value, result);
    }

    // getDataByArtifactId & getContractAgreementsByTarget
    @Test
    void getDataByArtifactId_validArtifactEmptyQuery_willReturnData() throws IOException {
        /* ARRANGE */
        final var endpointId = UUID.randomUUID();
        final var requestedArtifact = URI.create("https://requested/" + endpointId);
        final var queryInput = new QueryInput();
        final var expect = new ByteArrayInputStream(new byte[]{});

        Mockito.doReturn(expect).when(artifactService)
                .getData(any(), any(), eq(endpointId), eq(queryInput), any());

        /* ACT */
        final var result = resolver.getDataByArtifactId(requestedArtifact, queryInput);

        /* ASSERT */
        assertEquals(expect, result);
    }

    @Test
    void getContractAgreementsByTarget_validTarget_returnAgreementList() {
        /* ARRANGE */
        final var endpointId = UUID.randomUUID();
        final var target = URI.create("https://requested/" + endpointId);
        final var agreement = new Agreement();
        ReflectionTestUtils.setField(agreement, "value", "AGREEMENT");
        final var artifact = new ArtifactImpl();
        ReflectionTestUtils.setField(artifact, "agreements", List.of(agreement));

        Mockito.doReturn(artifact).when(artifactService).get(eq(endpointId));

        final var idsAgreement = new ContractAgreementBuilder()
                ._contractStart_(ToIdsObjectMapper.getGregorianOf(ZonedDateTime.now()))
                .build();

        Mockito.doReturn(idsAgreement).when(deserializationService).getContractAgreement(eq(agreement.getValue()));

        /* ACT */
        final var result = resolver.getContractAgreementsByTarget(target);

        /* ASSERT */
        assertEquals(List.of(idsAgreement), result);
    }


    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private Artifact getArtifact() {
        final var output = new ArtifactImpl();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }

    private Representation getRepresentation() {
        final var output = new Representation();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }

    @SneakyThrows
    private OfferedResource getOfferedResource() {
        final var constructor = OfferedResource.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }

    @SneakyThrows
    private Catalog getCatalog() {
        final var constructor = Catalog.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }

    @SneakyThrows
    private Contract getContract() {
        final var constructor = Contract.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }

    @SneakyThrows
    private ContractRule getRule() {
        final var constructor = ContractRule.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }

    @SneakyThrows
    private ContractRule getRule(final String value) {
        final var constructor = ContractRule.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", resourceId);
        ReflectionTestUtils.setField(output, "value", value);
        return output;
    }

    @SneakyThrows
    private Agreement getAgreement() {
        final var constructor = Agreement.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", resourceId);
        return output;
    }

    @SneakyThrows
    private Agreement getAgreement(final String value) {
        final var constructor = Agreement.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", resourceId);
        ReflectionTestUtils.setField(output, "value", value);
        return output;
    }

    private de.fraunhofer.iais.eis.Artifact getIdsArtifact() {
        return new ArtifactBuilder()
                ._creationDate_(IdsMessageUtils.getGregorianNow())
                ._fileName_("ARTIFACT")
                .build();
    }

    private Resource getIdsResource() {
        return new ResourceBuilder().build();
    }

    private de.fraunhofer.iais.eis.Representation getIdsRepresentation() {
        return new RepresentationBuilder().build();
    }

    private ResourceCatalog getIdsCatalog() {
        return new ResourceCatalogBuilder().build();
    }

    private ContractOffer getIdsContract() {
        return new ContractOfferBuilder().build();
    }

}
