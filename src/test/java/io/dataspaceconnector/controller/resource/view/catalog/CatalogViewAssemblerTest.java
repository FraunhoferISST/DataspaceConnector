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
package io.dataspaceconnector.controller.resource.view.catalog;

import io.dataspaceconnector.controller.resource.relation.CatalogsToOfferedResourcesController;
import io.dataspaceconnector.controller.resource.type.CatalogController;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.catalog.CatalogDesc;
import io.dataspaceconnector.model.catalog.CatalogFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SpringBootTest(classes = {
        CatalogViewAssembler.class
})
public class CatalogViewAssemblerTest {

    @Autowired
    private CatalogViewAssembler catalogViewAssembler;

    @SpyBean
    private CatalogFactory catalogFactory;

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = CatalogController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = catalogViewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void getSelfLink_validInput_returnSelfLink() {
        /* ARRANGE */
        final var catalogId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = CatalogController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = catalogViewAssembler.getSelfLink(catalogId);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + catalogId, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void toModel_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> catalogViewAssembler.toModel(null));
    }

    @Test
    public void toModel_validInput_returnCatalogView() {
        /* ARRANGE */
        final var catalog = getCatalog();

        /* ACT */
        final var result = catalogViewAssembler.toModel(catalog);

        /* ASSERT */
        assertNotNull(result);
        Assertions.assertEquals(catalog.getTitle(), result.getTitle());
        Assertions.assertEquals(catalog.getDescription(), result.getDescription());
        Assertions.assertEquals(catalog.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(catalog.getModificationDate(), result.getModificationDate());
        Assertions.assertEquals(catalog.getAdditional(), result.getAdditional());

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

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

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
        final var path = CatalogController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + catalogId;
    }

    private String getCatalogOfferedResourcesLink(final UUID catalogId) {
        return WebMvcLinkBuilder.linkTo(methodOn(CatalogsToOfferedResourcesController.class)
                .getResource(catalogId, null, null)).toString();
    }

}
