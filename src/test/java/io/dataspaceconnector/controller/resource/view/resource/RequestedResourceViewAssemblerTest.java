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

import io.dataspaceconnector.controller.resource.relation.RequestedResourcesToCatalogsController;
import io.dataspaceconnector.controller.resource.relation.RequestedResourcesToContractsController;
import io.dataspaceconnector.controller.resource.relation.RequestedResourcesToRepresentationsController;
import io.dataspaceconnector.controller.resource.type.RequestedResourceController;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.model.resource.RequestedResourceFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@SpringBootTest(classes = {
        RequestedResourceViewAssembler.class
})
public class RequestedResourceViewAssemblerTest {

    @Autowired
    private RequestedResourceViewAssembler requestedResourceViewAssembler;

    @SpyBean
    private RequestedResourceFactory requestedResourceFactory;

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = RequestedResourceController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = requestedResourceViewAssembler.getSelfLink(null);

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
        final var path = RequestedResourceController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = requestedResourceViewAssembler.getSelfLink(resourceId);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + resourceId, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void toModel_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> requestedResourceViewAssembler.toModel(null));
    }

    @Test
    public void toModel_validInput_returnRequestedResourceView() {
        /* ARRANGE */
        final var requestedResource = getRequestedResource();

        /* ACT */
        final var result = requestedResourceViewAssembler.toModel(requestedResource);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(requestedResource.getTitle(), result.getTitle());
        assertEquals(requestedResource.getDescription(), result.getDescription());
        assertEquals(requestedResource.getKeywords(), result.getKeywords());
        assertEquals(requestedResource.getPublisher(), result.getPublisher());
        assertEquals(requestedResource.getLanguage(), result.getLanguage());
        assertEquals(requestedResource.getLicense(), result.getLicense());
        assertEquals(requestedResource.getVersion(), result.getVersion());
        assertEquals(requestedResource.getSovereign(), result.getSovereign());
        assertEquals(requestedResource.getEndpointDocumentation(),
                result.getEndpointDocumentation());
        assertEquals(requestedResource.getRemoteId(), result.getRemoteId());
        assertEquals(requestedResource.getAdditional(), result.getAdditional());
        assertEquals(requestedResource.getCreationDate(), result.getCreationDate());
        assertEquals(requestedResource.getModificationDate(), result.getModificationDate());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getRequestedResourceLink(requestedResource.getId()), selfLink.get().getHref());

        final var contractsLink = result.getLink("contracts");
        assertTrue(contractsLink.isPresent());
        assertNotNull(contractsLink.get());
        assertEquals(getRequestedResourceContractsLink(requestedResource.getId()),
                contractsLink.get().getHref());

        final var representationsLink = result.getLink("representations");
        assertTrue(representationsLink.isPresent());
        assertNotNull(representationsLink.get());
        assertEquals(getRequestedResourceRepresentationsLink(requestedResource.getId()),
                representationsLink.get().getHref());

        final var catalogsLink = result.getLink("catalogs");
        assertTrue(catalogsLink.isPresent());
        assertNotNull(catalogsLink.get());
        assertEquals(getRequestedResourceCatalogsLink(requestedResource.getId()),
                catalogsLink.get().getHref());
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private RequestedResource getRequestedResource() {
        final var desc = new RequestedResourceDesc();
        desc.setLanguage("EN");
        desc.setTitle("title");
        desc.setDescription("description");
        desc.setKeywords(Collections.singletonList("keyword"));
        desc.setEndpointDocumentation(URI.create("https://endpointDocumentation.com"));
        desc.setLicense(URI.create("https://license.com"));
        desc.setPublisher(URI.create("https://publisher.com"));
        desc.setSovereign(URI.create("https://sovereign.com"));
        desc.setRemoteId(URI.create("https://remote-id.com"));
        final var resource = requestedResourceFactory.create(desc);

        final var date = ZonedDateTime.now(ZoneOffset.UTC);
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        ReflectionTestUtils.setField(resource, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(resource, "creationDate", date);
        ReflectionTestUtils.setField(resource, "modificationDate", date);
        ReflectionTestUtils.setField(resource, "additional", additional);

        return resource;
    }

    private String getRequestedResourceLink(final UUID resourceId) {
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = RequestedResourceController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + resourceId;
    }

    private String getRequestedResourceContractsLink(final UUID resourceId) {
        return WebMvcLinkBuilder.linkTo(methodOn(RequestedResourcesToContractsController.class)
                .getResource(resourceId, null, null)).toString();
    }

    private String getRequestedResourceRepresentationsLink(final UUID resourceId) {
        return linkTo(methodOn(RequestedResourcesToRepresentationsController.class)
                .getResource(resourceId, null, null)).toString();
    }

    private String getRequestedResourceCatalogsLink(final UUID resourceId) {
        return linkTo(methodOn(RequestedResourcesToCatalogsController.class)
                .getResource(resourceId, null, null)).toString();
    }
}
