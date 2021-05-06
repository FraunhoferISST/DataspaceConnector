package de.fraunhofer.isst.dataspaceconnector.view;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationControllers;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceControllers;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceFactory;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RepresentationFactory;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceFactory;
import de.fraunhofer.isst.dataspaceconnector.model.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@SpringBootTest(classes = {RepresentationViewAssembler.class, ViewAssemblerHelper.class,
        RepresentationFactory.class, OfferedResourceFactory.class, RequestedResourceFactory.class})
public class RepresentationViewAssemblerTest {

    @Autowired
    private RepresentationViewAssembler representationViewAssembler;

    @Autowired
    private RepresentationFactory representationFactory;

    @Autowired
    private OfferedResourceFactory offeredResourceFactory;

    @Autowired
    private RequestedResourceFactory requestedResourceFactory;

    final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.RepresentationController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = representationViewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void getSelfLink_validInput_returnSelfLink() {
        /* ARRANGE */
        final var representationId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.RepresentationController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = representationViewAssembler.getSelfLink(representationId);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + representationId, result.getHref());
        assertEquals(rel, result.getRel().value());
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
        assertEquals(representation.getTitle(), result.getTitle());
        assertEquals(representation.getMediaType(), result.getMediaType());
        assertEquals(representation.getLanguage(), result.getLanguage());
        assertEquals(representation.getRemoteId(), result.getRemoteId());
        assertEquals(representation.getCreationDate(), result.getCreationDate());
        assertEquals(representation.getModificationDate(), result.getModificationDate());
        assertEquals(representation.getAdditional(), result.getAdditional());

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
        assertEquals(representation.getTitle(), result.getTitle());
        assertEquals(representation.getMediaType(), result.getMediaType());
        assertEquals(representation.getLanguage(), result.getLanguage());
        assertEquals(representation.getRemoteId(), result.getRemoteId());
        assertEquals(representation.getCreationDate(), result.getCreationDate());
        assertEquals(representation.getModificationDate(), result.getModificationDate());
        assertEquals(representation.getAdditional(), result.getAdditional());

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
        assertEquals(representation.getTitle(), result.getTitle());
        assertEquals(representation.getMediaType(), result.getMediaType());
        assertEquals(representation.getLanguage(), result.getLanguage());
        assertEquals(representation.getRemoteId(), result.getRemoteId());
        assertEquals(representation.getCreationDate(), result.getCreationDate());
        assertEquals(representation.getModificationDate(), result.getModificationDate());
        assertEquals(representation.getAdditional(), result.getAdditional());

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

    /**************************************************************************
     * Utilities.
     *************************************************************************/

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
        desc.setLicence(URI.create("https://license.com"));
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
        desc.setLicence(URI.create("https://license.com"));
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
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.RepresentationController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + representationId;
    }

    private String getRepresentationArtifactsLink(final UUID representationId) {
        return linkTo(methodOn(RelationControllers.RepresentationsToArtifacts.class)
                .getResource(representationId, null, null, null)).toString();
    }

    private String getRepresentationOfferedResourcesLink(final UUID representationId) {
        return linkTo(methodOn(RelationControllers.RepresentationsToOfferedResources.class)
                .getResource(representationId, null, null, null)).toString();
    }

    private String getRepresentationRequestedResourcesLink(final UUID representationId) {
        return linkTo(methodOn(RelationControllers.RepresentationsToRequestedResources.class)
                .getResource(representationId, null, null, null)).toString();
    }

    private static class UnknownResource extends Resource {
        public UnknownResource() {}
    }

}
