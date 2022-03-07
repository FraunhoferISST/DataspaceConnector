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
package io.dataspaceconnector.controller.resource.view.representation;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import io.dataspaceconnector.common.exception.UnreachableLineException;
import io.dataspaceconnector.controller.resource.relation.RepresentationsToArtifactsController;
import io.dataspaceconnector.controller.resource.relation.RepresentationsToOfferedResourcesController;
import io.dataspaceconnector.controller.resource.relation.RepresentationsToRequestsController;
import io.dataspaceconnector.controller.resource.type.RepresentationController;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.representation.RepresentationDesc;
import io.dataspaceconnector.model.representation.RepresentationFactory;
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

@SpringBootTest(classes = {
        RepresentationViewAssembler.class
})
public class RepresentationViewAssemblerTest {

    @Autowired
    private RepresentationViewAssembler representationViewAssembler;

    @SpyBean
    private RepresentationFactory representationFactory;

    @SpyBean
    private OfferedResourceFactory offeredResourceFactory;

    @SpyBean
    private RequestedResourceFactory requestedResourceFactory;

    final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = RepresentationController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = representationViewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void getSelfLink_validInput_returnSelfLink() {
        /* ARRANGE */
        final var representationId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = RepresentationController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = representationViewAssembler.getSelfLink(representationId);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + representationId, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void toModel_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> representationViewAssembler.toModel(null));
    }

    @Test
    public void toModel_noResources_returnRepresentationViewWithOffersLink() {
        /* ARRANGE */
        final var representation = getRepresentation();

        /* ACT */
        final var result = representationViewAssembler.toModel(representation);

        /* ASSERT */
        assertNotNull(result);
        Assertions.assertEquals(representation.getTitle(), result.getTitle());
        Assertions.assertEquals(representation.getMediaType(), result.getMediaType());
        Assertions.assertEquals(representation.getLanguage(), result.getLanguage());
        Assertions.assertEquals(representation.getRemoteId(), result.getRemoteId());
        Assertions.assertEquals(representation.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(representation.getModificationDate(), result.getModificationDate());
        Assertions.assertEquals(representation.getAdditional(), result.getAdditional());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getRepresentationLink(representation.getId()), selfLink.get().getHref());

        final var artifactsLink = result.getLink("artifacts");
        assertTrue(artifactsLink.isPresent());
        assertNotNull(artifactsLink.get());
        assertEquals(getRepresentationArtifactsLink(representation.getId()),
                artifactsLink.get().getHref());

        final var offersLink = result.getLink("offers");
        assertTrue(offersLink.isPresent());
        assertNotNull(offersLink.get());
        assertEquals(getRepresentationOfferedResourcesLink(representation.getId()),
                offersLink.get().getHref());

        final var requestsLink = result.getLink("requests");
        assertTrue(requestsLink.isEmpty());
    }

    @Test
    public void toModel_withOfferedResources_returnRepresentationViewWithOffersLink() {
        /* ARRANGE */
        final var representation = getRepresentationWithOfferedResources();

        /* ACT */
        final var result = representationViewAssembler.toModel(representation);

        /* ASSERT */
        assertNotNull(result);
        Assertions.assertEquals(representation.getTitle(), result.getTitle());
        Assertions.assertEquals(representation.getMediaType(), result.getMediaType());
        Assertions.assertEquals(representation.getLanguage(), result.getLanguage());
        Assertions.assertEquals(representation.getRemoteId(), result.getRemoteId());
        Assertions.assertEquals(representation.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(representation.getModificationDate(), result.getModificationDate());
        Assertions.assertEquals(representation.getAdditional(), result.getAdditional());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getRepresentationLink(representation.getId()), selfLink.get().getHref());

        final var artifactsLink = result.getLink("artifacts");
        assertTrue(artifactsLink.isPresent());
        assertNotNull(artifactsLink.get());
        assertEquals(getRepresentationArtifactsLink(representation.getId()),
                artifactsLink.get().getHref());

        final var offersLink = result.getLink("offers");
        assertTrue(offersLink.isPresent());
        assertNotNull(offersLink.get());
        assertEquals(getRepresentationOfferedResourcesLink(representation.getId()),
                offersLink.get().getHref());

        final var requestsLink = result.getLink("requests");
        assertTrue(requestsLink.isEmpty());
    }

    @Test
    public void toModel_withRequestedResources_returnRepresentationViewWithRequestsLink() {
        /* ARRANGE */
        final var representation = getRepresentationWithRequestedResources();

        /* ACT */
        final var result = representationViewAssembler.toModel(representation);

        /* ASSERT */
        assertNotNull(result);
        Assertions.assertEquals(representation.getTitle(), result.getTitle());
        Assertions.assertEquals(representation.getMediaType(), result.getMediaType());
        Assertions.assertEquals(representation.getLanguage(), result.getLanguage());
        Assertions.assertEquals(representation.getRemoteId(), result.getRemoteId());
        Assertions.assertEquals(representation.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(representation.getModificationDate(), result.getModificationDate());
        Assertions.assertEquals(representation.getAdditional(), result.getAdditional());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getRepresentationLink(representation.getId()), selfLink.get().getHref());

        final var artifactsLink = result.getLink("artifacts");
        assertTrue(artifactsLink.isPresent());
        assertNotNull(artifactsLink.get());
        assertEquals(getRepresentationArtifactsLink(representation.getId()),
                artifactsLink.get().getHref());

        final var offersLink = result.getLink("offers");
        assertTrue(offersLink.isEmpty());

        final var requestsLink = result.getLink("requests");
        assertTrue(requestsLink.isPresent());
        assertNotNull(requestsLink.get());
        assertEquals(getRepresentationRequestedResourcesLink(representation.getId()),
                requestsLink.get().getHref());
    }

    @Test
    public void toModel_withUnknownResourceType_throwUnreachableLineException() {
        /* ARRANGE */
        final var representation = getRepresentationWithUnknownResources();

        /* ACT && ASSERT */
        assertThrows(UnreachableLineException.class,
                () -> representationViewAssembler.toModel(representation));
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private Representation getRepresentation() {
        final var desc = new RepresentationDesc();
        desc.setTitle("title");
        desc.setMediaType("application/json");
        desc.setLanguage("EN");
        desc.setRemoteId(URI.create("https://remote-id.com"));
        final var representation = representationFactory.create(desc);

        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        ReflectionTestUtils.setField(representation, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(representation, "creationDate", date);
        ReflectionTestUtils.setField(representation, "modificationDate", date);
        ReflectionTestUtils.setField(representation, "additional", additional);

        return representation;
    }

    private Representation getRepresentationWithOfferedResources() {
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

        final var representation = getRepresentation();
        ReflectionTestUtils.setField(representation, "resources",
                Collections.singletonList(resource));
        return representation;
    }

    private Representation getRepresentationWithRequestedResources() {
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

        final var representation = getRepresentation();
        ReflectionTestUtils.setField(representation, "resources",
                Collections.singletonList(resource));
        return representation;
    }

    private Representation getRepresentationWithUnknownResources() {
        final var resource = new UnknownResource();

        final var representation = getRepresentation();
        ReflectionTestUtils.setField(representation, "resources",
                Collections.singletonList(resource));
        return representation;
    }

    private String getRepresentationLink(final UUID representationId) {
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = RepresentationController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + representationId;
    }

    private String getRepresentationArtifactsLink(final UUID representationId) {
        return WebMvcLinkBuilder.linkTo(methodOn(RepresentationsToArtifactsController.class)
                .getResource(representationId, null, null)).toString();
    }

    private String getRepresentationOfferedResourcesLink(final UUID representationId) {
        return linkTo(methodOn(RepresentationsToOfferedResourcesController.class)
                .getResource(representationId, null, null)).toString();
    }

    private String getRepresentationRequestedResourcesLink(final UUID representationId) {
        return linkTo(methodOn(RepresentationsToRequestsController.class)
                .getResource(representationId, null, null)).toString();
    }

    private static class UnknownResource extends Resource {

        private static final long serialVersionUID = 1L;

        public UnknownResource() {
        }
    }
}
