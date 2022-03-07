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
package io.dataspaceconnector.controller.resource.type;

import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.controller.resource.view.catalog.CatalogViewAssembler;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.catalog.CatalogDesc;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.service.resource.type.CatalogService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {CatalogController.class})
class CatalogControllerTest {
    @MockBean
    private CatalogService catalogService;

    @SpyBean
    private CatalogViewAssembler assembler;

    @SpyBean
    private PagedResourcesAssembler<Catalog> pagedAssembler;

    @Autowired
    private CatalogController controller;

    private CatalogDesc desc = getDesc();
    private CatalogDesc updatedDescOne = getUpdatedDesc();
    private Catalog catalogOne = getCatalogOne();
    private Catalog updatedCatalogOne = getUpdatedCatalogOne();
    private boolean doesCatalogOneExist = true;
    final UUID unknownUUid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

    /**
     * Setup
     */

    @BeforeEach
    public void init() {
        Mockito.when(catalogService.create(Mockito.eq(getDesc()))).thenReturn(catalogOne);
        Mockito.when(catalogService.get(Mockito.eq(unknownUUid)))
                .thenThrow(ResourceNotFoundException.class);
        Mockito.when(catalogService.get(Mockito.eq(catalogOne.getId()))).thenReturn(catalogOne);
        Mockito.when(catalogService.update(Mockito.eq(catalogOne.getId()), Mockito.eq(desc)))
                .thenReturn(catalogOne);
        Mockito.when(catalogService.update(
                             Mockito.eq(catalogOne.getId()), Mockito.eq(updatedDescOne)))
                .thenReturn(updatedCatalogOne);

        Mockito.when(catalogService.update(Mockito.isNull(), Mockito.eq(new CatalogDesc())))
                .thenThrow(IllegalArgumentException.class);
        Mockito.when(catalogService.update(Mockito.eq(catalogOne.getId()), Mockito.isNull()))
                .thenThrow(IllegalArgumentException.class);
        Mockito.when(catalogService.update(Mockito.eq(unknownUUid), Mockito.isNull()))
                .thenThrow(IllegalArgumentException.class);
        Mockito.when(catalogService.update(Mockito.eq(unknownUUid), Mockito.isNotNull()))
               .thenThrow(ResourceNotFoundException.class);
        Mockito.when(catalogService.update(Mockito.isNull(), Mockito.isNull()))
                .thenThrow(IllegalArgumentException.class);

        Mockito.doThrow(IllegalArgumentException.class)
                .when(catalogService)
                .delete(Mockito.isNull());
        Mockito.doAnswer(invocation -> {
                   doesCatalogOneExist = false;
                   return null;
               })
                .when(catalogService)
                .delete(Mockito.eq(catalogOne.getId()));
    }

    /**
     * create
     */

    @Test
    public void create_validDesc_hasStatusCode201() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = controller.create(desc);

        /* ASSERT */
        assertEquals(HttpStatus.CREATED.value(), result.getStatusCodeValue());
    }

    @Test
    public void create_validDesc_hasLocationHeader() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = controller.create(desc);

        /* ASSERT */
        assertTrue(result.getHeaders().containsKey("Location"));
    }

    @Test
    public void create_nullDesc_throwsIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> controller.create(null));
    }

    @Test
    public void create_validDesc_returnValidCatalog() {
        /* ARRANGE */
        final var catalogView = new CatalogViewAssembler().toModel(catalogService.create(desc));

        /* ACT */
        final var result = controller.create(desc);

        /* ASSERT */
        assertEquals(catalogView, result.getBody());
    }

    /**
     * get
     */

    @Test
    public void get_null_throwsIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> controller.get(null));
    }

    @Test
    public void get_unknownId_throwsResourceNotFoundException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        assertThrows(ResourceNotFoundException.class, () -> controller.get(unknownUUid));
    }

    @Test
    public void get_knownId_getCatalog() {
        /* ARRANGE */
        final var catalogView = new CatalogViewAssembler().toModel(catalogOne);

        /* ACT */
        final var result = controller.get(catalogOne.getId());

        /* ASSERT */
        assertEquals(catalogView, result);
    }

    /**
     * update
     */

    @Test
    public void update_nullResourceId_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(
                IllegalArgumentException.class, () -> controller.update(null, new CatalogDesc()));
    }

    @Test
    public void update_knownIdNullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(
                IllegalArgumentException.class, () -> controller.update(catalogOne.getId(), null));
    }

    @Test
    public void update_unknownIdNullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> controller.update(unknownUUid, null));
    }

    @Test
    public void update_knownIdValidDesc_throwResourceNotFoundException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> controller.update(unknownUUid, new CatalogDesc()));
    }

    @Test
    public void update_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> controller.update(null, null));
    }

    @Test
    public void update_sameDesc_returnSameElement() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT */
        controller.update(catalogOne.getId(), desc);

        /* ASSERT */
        Mockito.verify(catalogService).update(catalogOne.getId(), desc);
    }

    @Test
    public void update_sameDesc_hasStatusCode204() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = controller.update(catalogOne.getId(), desc);

        /* ASSERT */
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatusCodeValue());
    }

    @Test
    public void update_differentDesc_hasStatusCode204() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = controller.update(catalogOne.getId(), updatedDescOne);

        /* ASSERT */
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatusCodeValue());
    }

    @Test
    public void update_differentDesc_doesUpdateElement() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        controller.update(catalogOne.getId(), updatedDescOne);

        /* ASSERT */
        Mockito.verify(catalogService).update(catalogOne.getId(), updatedDescOne);
    }

    /**
     * delete
     */

    @Test
    public void delete_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> controller.delete(null));
    }

    @Test
    public void delete_unknownId_returnNoContentResponse() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = controller.delete(unknownUUid);

        /* ASSERT */
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatusCodeValue());
        assertNull(result.getBody());
    }

    @Test
    public void delete_knownId_returnNoContentResponse() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = controller.delete(catalogOne.getId());

        /* ASSERT */
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatusCodeValue());
        assertNull(result.getBody());
    }

    @Test
    public void delete_knownId_returnNoElement() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        controller.delete(catalogOne.getId());

        /* ASSERT */
        assertFalse(doesCatalogOneExist);
    }

    /**
     * Utilities
     */
    private CatalogDesc getDesc() {
        final var desc = new CatalogDesc();
        desc.setTitle("Catalog");
        desc.setDescription("Some description");

        return desc;
    }

    private CatalogDesc getUpdatedDesc() {
        final var desc = new CatalogDesc();
        desc.setTitle("Some other Catalog");
        desc.setDescription("Some other description");

        return desc;
    }

    @SneakyThrows
    private Catalog getCatalogOne() {
        final var desc = getDesc();
        final var constructor = Catalog.class.getConstructor();
        constructor.setAccessible(true);

        final var catalog = constructor.newInstance();

        final var titleField = catalog.getClass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(catalog, desc.getTitle());

        final var descriptionField = catalog.getClass().getSuperclass().getDeclaredField("description");
        descriptionField.setAccessible(true);
        descriptionField.set(catalog, desc.getDescription());

        final var offeredResourcesField = catalog.getClass().getDeclaredField("offeredResources");
        offeredResourcesField.setAccessible(true);
        offeredResourcesField.set(catalog, new ArrayList<OfferedResource>());

        final var idField = catalog.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(catalog, UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        return catalog;
    }

    @SneakyThrows
    private Catalog getUpdatedCatalogOne() {
        final var desc = getUpdatedDesc();
        final var constructor = Catalog.class.getConstructor();
        constructor.setAccessible(true);

        final var catalog = constructor.newInstance();

        final var titleField = catalog.getClass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(catalog, desc.getTitle());

        final var descriptionField = catalog.getClass().getSuperclass().getDeclaredField("description");
        descriptionField.setAccessible(true);
        descriptionField.set(catalog, desc.getDescription());

        final var offeredResourcesField = catalog.getClass().getDeclaredField("offeredResources");
        offeredResourcesField.setAccessible(true);
        offeredResourcesField.set(catalog, new ArrayList<OfferedResource>());

        final var idField = catalog.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(catalog, UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        return catalog;
    }
}
