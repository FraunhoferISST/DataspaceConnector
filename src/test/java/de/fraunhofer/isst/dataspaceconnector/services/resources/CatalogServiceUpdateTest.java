package de.fraunhofer.isst.dataspaceconnector.services.resources;

import java.util.Optional;
import java.util.UUID;

import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogFactory;
import de.fraunhofer.isst.dataspaceconnector.repositories.CatalogRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest( classes = { CatalogService.class } )
public class CatalogServiceUpdateTest {

    @SpyBean
    private CatalogFactory factory;

    @MockBean
    private CatalogRepository repository;

    Catalog newCatalog;
    Catalog updatedCatalog;

    UUID validId = UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e");

    @Autowired
    @InjectMocks
    private CatalogService service;

    @BeforeEach
    public void init() {
        newCatalog = getCatalogFromValidDesc(validId, getNewCatalog(getValidDesc()));
        updatedCatalog = getCatalogFromValidDesc(validId, getNewCatalog(getUpdatedValidDesc()));

        Mockito.when(repository.saveAndFlush(Mockito.eq(newCatalog))).thenReturn(newCatalog);
        Mockito.when(repository.saveAndFlush(Mockito.eq(updatedCatalog))).thenReturn(updatedCatalog);
        Mockito.when(repository.findById(Mockito.eq(newCatalog.getId()))).thenReturn(Optional.of(newCatalog));
    }

    @Test
    public void update_sameDesc_returnSameCatalog() {
        /* ARRANGE */
        final var before = getCatalogFromValidDesc(validId, getNewCatalog(getValidDesc()));

        /* ACT */
        final var after = service.update(newCatalog.getId(), getValidDesc());

        /* ASSERT */
        assertEquals(before, after);
    }

    @Test
    public void update_updateDesc_returnUpdatedCatalog() {
        /* ARRANGE */
        final var shouldLookLike = getCatalogFromValidDesc(validId, getNewCatalog(getUpdatedValidDesc()));

        /* ACT */
        final var after = service.update(validId, getUpdatedValidDesc());

        /* ASSERT */
        assertEquals(after, shouldLookLike);
    }

    @Test
    public void update_sameDesc_notUpdatedDbCatalog() {
        /* ARRANGE */
        service.update(validId, getValidDesc());

        /* ACT */
        Mockito.verify(repository, Mockito.never()).saveAndFlush(Mockito.any());
    }

    @Test
    public void update_updatedDesc_UpdatedDbCatalog() {
        /* ARRANGE */
        service.update(validId, getUpdatedValidDesc());

        /* ACT */
        Mockito.verify(repository, Mockito.atLeastOnce()).saveAndFlush(Mockito.eq(updatedCatalog));
    }

    private CatalogDesc getValidDesc() {
        var desc = new CatalogDesc();
        desc.setDescription("The new description.");
        desc.setTitle("The new title.");

        return desc;
    }

    private CatalogDesc getUpdatedValidDesc() {
        var desc = new CatalogDesc();
        desc.setDescription("Something different.");
        desc.setTitle("The new title.");

        return desc;
    }

    private Catalog getNewCatalog( final CatalogDesc desc ) {
        return factory.create(desc);
    }

    @SneakyThrows
    private Catalog getCatalogFromValidDesc( final UUID id, final Catalog catalog ) {
        final var idField = catalog.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(catalog, id);

        return catalog;
    }
}
