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
package io.dataspaceconnector.controller.resource.view.resource;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import io.dataspaceconnector.controller.resource.relation.OfferedResourcesToCatalogsController;
import io.dataspaceconnector.controller.resource.relation.OfferedResourcesToContractsController;
import io.dataspaceconnector.controller.resource.relation.OfferedResourcesToRepresentationsController;
import io.dataspaceconnector.controller.resource.type.OfferedResourceController;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.model.resource.OfferedResourceFactory;
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

@SpringBootTest(classes = {
        OfferedResourceViewAssembler.class
})
public class OfferedResourceViewAssemblerTest {

    @Autowired
    private OfferedResourceViewAssembler offeredResourceViewAssembler;

    @SpyBean
    private OfferedResourceFactory offeredResourceFactory;

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = OfferedResourceController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = offeredResourceViewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void getSelfLink_validInput_returnSelfLink() {
        /* ARRANGE */
        final var resourceId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = OfferedResourceController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = offeredResourceViewAssembler.getSelfLink(resourceId);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + resourceId, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void toModel_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> offeredResourceViewAssembler.toModel(null));
    }

    @Test
    public void toModel_validInput_returnOfferedResourceView() {
        /* ARRANGE */
        final var offeredResource = getOfferedResource();

        /* ACT */
        final var result = offeredResourceViewAssembler.toModel(offeredResource);

        /* ASSERT */
        assertNotNull(result);
        Assertions.assertEquals(offeredResource.getTitle(), result.getTitle());
        Assertions.assertEquals(offeredResource.getDescription(), result.getDescription());
        Assertions.assertEquals(offeredResource.getKeywords(), result.getKeywords());
        Assertions.assertEquals(offeredResource.getPublisher(), result.getPublisher());
        Assertions.assertEquals(offeredResource.getLanguage(), result.getLanguage());
        Assertions.assertEquals(offeredResource.getLicense(), result.getLicense());
        Assertions.assertEquals(offeredResource.getVersion(), result.getVersion());
        Assertions.assertEquals(offeredResource.getSovereign(), result.getSovereign());
        Assertions.assertEquals(offeredResource.getEndpointDocumentation(),
                result.getEndpointDocumentation());
        Assertions.assertEquals(offeredResource.getAdditional(), result.getAdditional());
        Assertions.assertEquals(offeredResource.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(offeredResource.getModificationDate(),
                result.getModificationDate());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getOfferedResourceLink(offeredResource.getId()), selfLink.get().getHref());

        final var contractsLink = result.getLink("contracts");
        assertTrue(contractsLink.isPresent());
        assertNotNull(contractsLink.get());
        assertEquals(getOfferedResourceContractsLink(offeredResource.getId()),
                contractsLink.get().getHref());

        final var representationsLink = result.getLink("representations");
        assertTrue(representationsLink.isPresent());
        assertNotNull(representationsLink.get());
        assertEquals(getOfferedResourceRepresentationsLink(offeredResource.getId()),
                representationsLink.get().getHref());

        final var catalogsLink = result.getLink("catalogs");
        assertTrue(catalogsLink.isPresent());
        assertNotNull(catalogsLink.get());
        assertEquals(getOfferedResourceCatalogsLink(offeredResource.getId()),
                catalogsLink.get().getHref());
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private OfferedResource getOfferedResource() {
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

        final var date = ZonedDateTime.now(ZoneOffset.UTC);
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        ReflectionTestUtils.setField(resource, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(resource, "creationDate", date);
        ReflectionTestUtils.setField(resource, "modificationDate", date);
        ReflectionTestUtils.setField(resource, "additional", additional);

        return resource;
    }

    private String getOfferedResourceLink(final UUID resourceId) {
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = OfferedResourceController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + resourceId;
    }

    private String getOfferedResourceContractsLink(final UUID resourceId) {
        return WebMvcLinkBuilder.linkTo(methodOn(OfferedResourcesToContractsController.class)
                .getResource(resourceId, null, null)).toString();
    }

    private String getOfferedResourceRepresentationsLink(final UUID resourceId) {
        return linkTo(methodOn(OfferedResourcesToRepresentationsController.class)
                .getResource(resourceId, null, null)).toString();
    }

    private String getOfferedResourceCatalogsLink(final UUID resourceId) {
        return linkTo(methodOn(OfferedResourcesToCatalogsController.class)
                .getResource(resourceId, null, null)).toString();
    }
}
