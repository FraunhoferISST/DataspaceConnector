package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.view.CatalogView;
import de.fraunhofer.isst.dataspaceconnector.view.CatalogViewAssembler;
import de.fraunhofer.isst.dataspaceconnector.services.resources.CatalogService;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import lombok.SneakyThrows;

@SpringBootTest(classes = {ResourceControllers.CatalogController.class})
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
    private ResourceControllers.CatalogController controller;

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
    public void getAll_noElements_returnStatusCode200() {
        /* ARRANGE */
        final var request = PageRequest.of(0, 1, Sort.unsorted());
        Mockito.when(service.getAll(Mockito.eq(request))).thenReturn(Page.empty());

        /* ACT */
        final var result = controller.getAll(null, 1, null);

        /* ASSERT */
        assertEquals(HttpStatus.OK.value(), result.getStatusCodeValue());
    }

    @Test
    public void getAll_nullPage_returnFirstPage() {
        /* ARRANGE */
        final var pageSize = 1;
        final var request = PageRequest.of(0, pageSize);
        Mockito.when(service.getAll(Mockito.eq(request)))
                .thenReturn(Utils.toPage(catalogList, request));

        /* ACT */
        final var result = controller.getAll(null, pageSize, null);

        /* ASSERT */
        final var body = (PagedModel<CatalogView>) result.getBody();
        assertEquals(body.getLink("self").get().getHref(), body.getLink("first").get().getHref());
        assertEquals(1, body.getContent().size());
        assertEquals(new CatalogViewAssembler().toModel(catalogList.get(0)),
                body.getContent().stream().findFirst().get());
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
        final var result = controller.getAll(page, pageSize, null);

        /* ASSERT */
        final var body = (PagedModel<CatalogView>) result.getBody();
        assertEquals(page, body.getMetadata().getNumber());
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
        final var result = controller.getAll(page, pageSize, null);

        /* ASSERT */
        final var body = (PagedModel<CatalogView>) result.getBody();
        assertEquals(page, body.getMetadata().getNumber());
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
        final var result = controller.getAll(toFar, pageSize, null);

        /* ASSERT */
        final var body = (PagedModel<CatalogView>) result.getBody();
        assertEquals(1, body.getContent().size());
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
        final var result = controller.getAll(toEarly, pageSize, null);

        /* ASSERT */

        final var body = (PagedModel<CatalogView>) result.getBody();
        assertEquals(0, body.getMetadata().getNumber());
    }

    /**
     * Utilities
     */
    @SneakyThrows
    private Catalog getCatalog(final String title) {
        final var constructor = Catalog.class.getConstructor();
        constructor.setAccessible(true);

        final var catalog = constructor.newInstance();

        final var titleField = catalog.getClass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(catalog, title);

        final var idField = catalog.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(catalog, UUID.randomUUID());

        return catalog;
    }
}
