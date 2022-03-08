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

import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.controller.resource.view.catalog.CatalogViewAssembler;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.service.resource.type.CatalogService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {CatalogController.class})
class CatalogControllerTest_getAll {
    @MockBean
    private CatalogService service;

    @SpyBean
    private CatalogViewAssembler assembler;

    @SpyBean
    private PagedResourcesAssembler<Catalog> pagedAssembler;

    private List<Catalog> catalogList = new ArrayList<>();

    @Autowired
    // private MockMvc mockMvc;
    private CatalogController controller;

    /**
     * Setup
     */

    @BeforeEach
    public void init() {
        for (int i = 0; i < 50; i++) catalogList.add(getCatalog(String.valueOf(i)));
    }

    /**
     * getAll
     */
    @Test
    public void getAll_nullPage_returnFirstPage() {
        /* ARRANGE */
        final var pageSize = 1;
        final var request = PageRequest.of(0, pageSize);
        Mockito.when(service.getAll(Mockito.eq(request)))
                .thenReturn(Utils.toPage(catalogList, request));

        /* ACT */
        final var result = controller.getAll(null, pageSize);

        /* ASSERT */
        assertEquals(result.getLink("self").get().getHref(), result.getLink("first").get().getHref());
        assertEquals(1, result.getContent().size());
        assertEquals(new CatalogViewAssembler().toModel(catalogList.get(0)),
                     result.getContent().stream().findFirst().get());
    }

    @Test
    public void getAll_firstPage_returnFirstPage() {
        /* ARRANGE */
        final var pageSize = 1;
        final var page = 0;
        final var request = PageRequest.of(page, pageSize);
        Mockito.when(service.getAll(Mockito.eq(request)))
                .thenReturn(Utils.toPage(catalogList, request));

        /* ACT */
        final var result = controller.getAll(page, pageSize);

        /* ASSERT */
        assertEquals(page, result.getMetadata().getNumber());
    }

    @Test
    public void getAll_validPage_returnValidPage() {
        /* ARRANGE */
        final var pageSize = 13;
        final var page = 3;
        final var request = PageRequest.of(page, pageSize);
        Mockito.when(service.getAll(Mockito.eq(request)))
                .thenReturn(Utils.toPage(catalogList, request));

        /* ACT */
        final var result = controller.getAll(page, pageSize);

        /* ASSERT */
        assertEquals(page, result.getMetadata().getNumber());
    }

    @Test
    public void getAll_toFarPage_returnEmptyPage() {
        /* ARRANGE */
        final var pageSize = 1;
        final var toFar = catalogList.size() + 1;
        final var request = PageRequest.of(toFar, pageSize);
        final var returnPage = Utils.toPage(catalogList, request);
        Mockito.when(service.getAll(Mockito.eq(request))).thenReturn(returnPage);

        /* ACT */
        final var result = controller.getAll(toFar, pageSize);

        /* ASSERT */
        assertEquals(1, result.getContent().size());
    }

    @Test
    public void getAll_toEarlyPage_returnEmptyPage() {
        /* ARRANGE */
        final var pageSize = 1;
        final var toEarly = -1;
        final var request = PageRequest.of(0, pageSize);
        final var returnPage = Utils.toPage(catalogList, request);
        Mockito.when(service.getAll(Mockito.eq(request))).thenReturn(returnPage);

        /* ACT */
        final var result = controller.getAll(toEarly, pageSize);

        /* ASSERT */
        assertEquals(0, result.getMetadata().getNumber());
    }

    /**
     * Utilities
     */
    @SneakyThrows
    private Catalog getCatalog(final String title) {
        final var constructor = Catalog.class.getConstructor();
        constructor.setAccessible(true);

        final var catalog = constructor.newInstance();

        final var titleField = catalog.getClass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(catalog, title);

        final var idField = catalog.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(catalog, UUID.randomUUID());

        return catalog;
    }
}
