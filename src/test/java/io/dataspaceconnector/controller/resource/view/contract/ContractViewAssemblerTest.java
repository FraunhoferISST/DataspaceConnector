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
package io.dataspaceconnector.controller.resource.view.contract;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import io.dataspaceconnector.common.exception.UnreachableLineException;
import io.dataspaceconnector.controller.resource.relation.ContractsToOfferedResourcesController;
import io.dataspaceconnector.controller.resource.relation.ContractsToRequestedResourcesController;
import io.dataspaceconnector.controller.resource.relation.ContractsToRulesController;
import io.dataspaceconnector.controller.resource.type.ContractController;
import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.contract.ContractDesc;
import io.dataspaceconnector.model.contract.ContractFactory;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.model.resource.OfferedResourceFactory;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.model.resource.RequestedResourceFactory;
import io.dataspaceconnector.model.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@SpringBootTest(classes = {ContractViewAssembler.class})
public class ContractViewAssemblerTest {

    @Autowired
    private ContractViewAssembler contractViewAssembler;

    @SpyBean
    private ContractFactory contractFactory;

    @SpyBean
    private OfferedResourceFactory offeredResourceFactory;

    @SpyBean
    private RequestedResourceFactory requestedResourceFactory;

    final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = ContractController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = contractViewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void getSelfLink_validInput_returnSelfLink() {
        /* ARRANGE */
        final var contractId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = ContractController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = contractViewAssembler.getSelfLink(contractId);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + contractId, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void toModel_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> contractViewAssembler.toModel(null));
    }

    @Test
    public void toModel_noResources_returnContractViewWithOffersLink() {
        /* ARRANGE */
        final var contract = getContract();

        /* ACT */
        final var result = contractViewAssembler.toModel(contract);

        /* ASSERT */
        assertNotNull(result);
        Assertions.assertEquals(contract.getTitle(), result.getTitle());
        Assertions.assertEquals(contract.getStart(), result.getStart());
        Assertions.assertEquals(contract.getEnd(), result.getEnd());
        Assertions.assertEquals(contract.getConsumer(), result.getConsumer());
        Assertions.assertEquals(contract.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(contract.getModificationDate(), result.getModificationDate());
        Assertions.assertEquals(contract.getAdditional(), result.getAdditional());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getContractLink(contract.getId()), selfLink.get().getHref());

        final var rulesLink = result.getLink("rules");
        assertTrue(rulesLink.isPresent());
        assertNotNull(rulesLink.get());
        assertEquals(getContractRulesLink(contract.getId()), rulesLink.get().getHref());

        final var offersLink = result.getLink("offers");
        assertTrue(offersLink.isPresent());
        assertNotNull(offersLink.get());
        assertEquals(getContractOfferedResourcesLink(contract.getId()), offersLink.get().getHref());

        final var requestsLink = result.getLink("requests");
        assertTrue(requestsLink.isEmpty());
    }

    @Test
    public void toModel_withOfferedResources_returnContractViewWithOffersLink() {
        /* ARRANGE */
        final var contract = getContractWithOfferedResources();

        /* ACT */
        final var result = contractViewAssembler.toModel(contract);

        /* ASSERT */
        assertNotNull(result);
        Assertions.assertEquals(contract.getTitle(), result.getTitle());
        Assertions.assertEquals(contract.getStart(), result.getStart());
        Assertions.assertEquals(contract.getEnd(), result.getEnd());
        Assertions.assertEquals(contract.getConsumer(), result.getConsumer());
        Assertions.assertEquals(contract.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(contract.getModificationDate(), result.getModificationDate());
        Assertions.assertEquals(contract.getAdditional(), result.getAdditional());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getContractLink(contract.getId()), selfLink.get().getHref());

        final var rulesLink = result.getLink("rules");
        assertTrue(rulesLink.isPresent());
        assertNotNull(rulesLink.get());
        assertEquals(getContractRulesLink(contract.getId()), rulesLink.get().getHref());

        final var offersLink = result.getLink("offers");
        assertTrue(offersLink.isPresent());
        assertNotNull(offersLink.get());
        assertEquals(getContractOfferedResourcesLink(contract.getId()), offersLink.get().getHref());

        final var requestsLink = result.getLink("requests");
        assertTrue(requestsLink.isEmpty());
    }

    @Test
    public void toModel_withRequestedResources_returnContractViewWithRequestsLink() {
        /* ARRANGE */
        final var contract = getContractWithRequestedResources();

        /* ACT */
        final var result = contractViewAssembler.toModel(contract);

        /* ASSERT */
        assertNotNull(result);
        Assertions.assertEquals(contract.getTitle(), result.getTitle());
        Assertions.assertEquals(contract.getStart(), result.getStart());
        Assertions.assertEquals(contract.getEnd(), result.getEnd());
        Assertions.assertEquals(contract.getConsumer(), result.getConsumer());
        Assertions.assertEquals(contract.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(contract.getModificationDate(), result.getModificationDate());
        Assertions.assertEquals(contract.getAdditional(), result.getAdditional());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getContractLink(contract.getId()), selfLink.get().getHref());

        final var rulesLink = result.getLink("rules");
        assertTrue(rulesLink.isPresent());
        assertNotNull(rulesLink.get());
        assertEquals(getContractRulesLink(contract.getId()), rulesLink.get().getHref());

        final var offersLink = result.getLink("offers");
        assertTrue(offersLink.isEmpty());

        final var requestsLink = result.getLink("requests");
        assertTrue(requestsLink.isPresent());
        assertNotNull(requestsLink.get());
        assertEquals(getContractRequestedResourcesLink(contract.getId()),
                requestsLink.get().getHref());
    }

    @Test
    public void toModel_withUnknownResourceType_throwUnreachableLineException() {
        /* ARRANGE */
        final var contract = getContractWithUnknownResources();

        /* ACT && ASSERT */
        assertThrows(UnreachableLineException.class, () -> contractViewAssembler.toModel(contract));
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private Contract getContract() {
        final var desc = new ContractDesc();
        desc.setTitle("title");
        desc.setConsumer(URI.create("https://consumer.com"));
        desc.setStart(date);
        desc.setEnd(date);
        final var contract = contractFactory.create(desc);

        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        ReflectionTestUtils.setField(contract, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(contract, "creationDate", date);
        ReflectionTestUtils.setField(contract, "modificationDate", date);
        ReflectionTestUtils.setField(contract, "additional", additional);

        return contract;
    }

    private Contract getContractWithOfferedResources() {
        final var desc = new OfferedResourceDesc();
        desc.setLanguage("EN");
        desc.setTitle("title");
        desc.setDescription("description");
        desc.setKeywords(Collections.singletonList("keyword"));
        desc.setEndpointDocumentation(URI.create("https://endpointDocumentation.com"));
        desc.setLicense(URI.create("https://license.com"));
        desc.setPublisher(URI.create("https://publisher.com"));
        desc.setSovereign(URI.create("https://sovereign.com"));
        final var resource = offeredResourceFactory.create(desc);

        ReflectionTestUtils.setField(resource, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(resource, "creationDate", date);
        ReflectionTestUtils.setField(resource, "modificationDate", date);

        final var contract = getContract();
        ReflectionTestUtils.setField(contract, "resources", Collections.singletonList(resource));
        return contract;
    }

    private Contract getContractWithRequestedResources() {
        final var desc = new RequestedResourceDesc();
        desc.setLanguage("EN");
        desc.setTitle("title");
        desc.setDescription("description");
        desc.setKeywords(Collections.singletonList("keyword"));
        desc.setEndpointDocumentation(URI.create("https://endpointDocumentation.com"));
        desc.setLicense(URI.create("https://license.com"));
        desc.setPublisher(URI.create("https://publisher.com"));
        desc.setSovereign(URI.create("https://sovereign.com"));
        final var resource = requestedResourceFactory.create(desc);

        ReflectionTestUtils.setField(resource, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(resource, "creationDate", date);
        ReflectionTestUtils.setField(resource, "modificationDate", date);

        final var contract = getContract();
        ReflectionTestUtils.setField(contract, "resources", Collections.singletonList(resource));
        return contract;
    }

    private Contract getContractWithUnknownResources() {
        final var resource = new UnknownResource();

        final var contract = getContract();
        ReflectionTestUtils.setField(contract, "resources", Collections.singletonList(resource));
        return contract;
    }

    private String getContractLink(final UUID contractId) {
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = ContractController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + contractId;
    }

    private String getContractRulesLink(final UUID contractId) {
        return WebMvcLinkBuilder.linkTo(methodOn(ContractsToRulesController.class)
                .getResource(contractId, null, null)).toString();
    }

    private String getContractOfferedResourcesLink(final UUID contractId) {
        return linkTo(methodOn(ContractsToOfferedResourcesController.class)
                .getResource(contractId, null, null)).toString();
    }

    private String getContractRequestedResourcesLink(final UUID contractId) {
        return linkTo(methodOn(ContractsToRequestedResourcesController.class)
                .getResource(contractId, null, null)).toString();
    }

    private static class UnknownResource extends Resource {

        private static final long serialVersionUID = 1L;

        public UnknownResource() {
        }
    }
}
