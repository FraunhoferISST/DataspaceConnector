package de.fraunhofer.isst.dataspaceconnector.view;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationControllers;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.ResourceControllers;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogFactory;
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
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SpringBootTest(classes = {CatalogViewAssembler.class, ViewAssemblerHelper.class,
        CatalogFactory.class})
public class CatalogViewAssemblerTest {

    @Autowired
    private CatalogViewAssembler catalogViewAssembler;

    @Autowired
    private CatalogFactory catalogFactory;

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.CatalogController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = catalogViewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void getSelfLink_validInput_returnSelfLink() {
        /* ARRANGE */
        final var catalogId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.CatalogController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = catalogViewAssembler.getSelfLink(catalogId);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + catalogId, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void toModel_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> catalogViewAssembler.toModel(null));
    }

    @Test
    public void toModel_validInput_returnCatalogView() {
        /* ARRANGE */
        final var catalog = getCatalog();

        /* ACT */
        final var result = catalogViewAssembler.toModel(catalog);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(catalog.getTitle(), result.getTitle());
        assertEquals(catalog.getDescription(), result.getDescription());
        assertEquals(catalog.getCreationDate(), result.getCreationDate());
        assertEquals(catalog.getModificationDate(), result.getModificationDate());
        assertEquals(catalog.getAdditional(), result.getAdditional());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getCatalogLink(catalog.getId()), selfLink.get().getHref());

        final var offeredResourcesLink = result.getLink("offers");
        assertTrue(offeredResourcesLink.isPresent());
        assertNotNull(offeredResourcesLink.get());
        assertEquals(getCatalogOfferedResourcesLink(catalog.getId()),
                offeredResourcesLink.get().getHref());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private Catalog getCatalog() {
        final var desc = new CatalogDesc();
        desc.setTitle("title");
        desc.setDescription("description");
        final var catalog = catalogFactory.create(desc);

        final var date = ZonedDateTime.now(ZoneOffset.UTC);
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        ReflectionTestUtils.setField(catalog, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(catalog, "creationDate", date);
        ReflectionTestUtils.setField(catalog, "modificationDate", date);
        ReflectionTestUtils.setField(catalog, "additional", additional);

        return catalog;
    }

    private String getCatalogLink(final UUID catalogId) {
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = ResourceControllers.CatalogController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + catalogId;
    }

    private String getCatalogOfferedResourcesLink(final UUID catalogId) {
        return linkTo(methodOn(RelationControllers.CatalogsToOfferedResources.class)
                .getResource(catalogId, null, null, null)).toString();
    }

}
