package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import de.fraunhofer.isst.dataspaceconnector.exceptions.controller.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogDesc;
import de.fraunhofer.isst.dataspaceconnector.model.CatalogFactory;
import de.fraunhofer.isst.dataspaceconnector.repositories.CatalogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest( classes = { CatalogService.class } )
class CatalogServiceTest {
    @SpyBean
    private CatalogFactory factory;

    @MockBean
    private CatalogRepository repository;

    List<Catalog> catalogList;

    @Autowired
    @InjectMocks
    private CatalogService service;

    @BeforeEach
    public void init() {
        catalogList = new ArrayList<>();

        final Catalog persistedCatalog = getCatalogFromValidDesc(
                getValidDesc().getStaticId(), getNewCatalog(getValidDesc()));
        Mockito.when(repository.saveAndFlush(Mockito.eq(getNewCatalog(getValidDesc()))))
               .thenReturn(persistedCatalog);

        Mockito.when(repository.saveAndFlush(Mockito.any())).thenAnswer(this::saveAndFlushMock);
        Mockito.when(repository.findById(Mockito.eq(persistedCatalog.getId())))
               .thenReturn(Optional.of(persistedCatalog));
        Mockito.when(repository.findById(
                AdditionalMatchers.not(Mockito.eq(persistedCatalog.getId()))))
               .thenReturn(Optional.empty());
        Mockito.when(repository.findById(Mockito.isNull()))
               .thenThrow(IllegalArgumentException.class);
        Mockito.when(repository.findAll(Pageable.unpaged())).thenAnswer(this::findAllMock);
        Mockito.doThrow(IllegalArgumentException.class)
               .when(repository)
               .deleteById(Mockito.isNull());
        Mockito.doAnswer(x -> deleteByIdMock(x))
               .when(repository)
               .deleteById(Mockito.isA(UUID.class));
    }

    private static Page<Catalog> toPage( final List<Catalog> catalogList, final Pageable pageable ) {
        return new PageImpl<>(
                catalogList.subList(0, catalogList.size()), pageable, catalogList.size());
    }

    private Page<Catalog> findAllMock( final InvocationOnMock invocation ) {
        return toPage(catalogList, invocation.getArgument(0));
    }

    private Catalog saveAndFlushMock( final InvocationOnMock invocation ) {
        final var obj = (Catalog) invocation.getArgument(0);
        obj.setId(UUID.randomUUID());
        catalogList.add(obj);
        return obj;
    }

    private Answer<?> deleteByIdMock( final InvocationOnMock invocation ) {
        final var obj = (UUID) invocation.getArgument(0);
        catalogList.removeIf(x -> x.getId().equals(obj));
        return null;
    }

    @Test
    public void create_nullDesc_throwNullPointerException() {
        /* ARRANGE */

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> service.create(null));
    }

    @Test
    public void create_ValidDesc_returnCatalog() {
        /* ARRANGE */
        final var desc = getValidDesc();

        /* ACT */
        final var catalog = service.create(desc);

        /* ACT && ASSERT */
        assertNotNull(catalog);
    }

    @Test
    public void create_ValidDesc_returnHasId() {
        /* ARRANGE */
        final var desc = getValidDesc();

        /* ACT */
        final var catalog = service.create(desc);

        /* ACT && ASSERT */
        assertEquals(catalog,
                     getCatalogFromValidDesc(
                             getValidDesc().getStaticId(), getNewCatalog(getValidDesc())));
    }

    @Test
    public void create_ValidDesc_createOnlyOneCatalog() {
        /* ARRANGE */
        final var desc = getValidDesc();
        final var beforeCount = service.getAll(Pageable.unpaged()).getSize();

        /* ACT */
        service.create(desc);

        /* ASSERT */
        assertEquals(beforeCount + 1, service.getAll(Pageable.unpaged()).getSize());
    }

    @Test
    public void update_nullDesc_throwNullPointerException() {
        /* ARRANGE */
        final var persistedCatalog = getCatalogFromValidDesc(
                getValidDesc().getStaticId(), getNewCatalog(getValidDesc()));

        /* ACT && ASSERT */
        assertThrows(
                NullPointerException.class, () -> service.update(persistedCatalog.getId(), null));
    }

    @Test
    public void update_nullId_throwIllegalArgumentException() {
        /* ARRANGE */

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.update(null, getValidDesc()));
    }

    @Test
    public void update_unknownId_throwResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> service.get(unknownUuid));
    }

    @Test
    public void get_nullId_throwIllegalArgumentException() {
        /* ARRANGE */

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.get(null));
    }

    @Test
    public void get_knownId_returnCatalog() {
        /* ARRANGE */
        final var persistedCatalog = getCatalogFromValidDesc(
                getValidDesc().getStaticId(), getNewCatalog(getValidDesc()));

        /* ACT && ASSERT */
        assertNotNull(service.get(persistedCatalog.getId()));
    }

    @Test
    public void get_unknownId_throwResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        final var msg = assertThrows(ResourceNotFoundException.class, () -> service.get(unknownUuid));
        assertEquals(unknownUuid.toString(), msg.getMessage());
    }

    @Test
    public void getAll() {}

    @Test
    public void doesExist_null_throwIllegalArgumentException() {
        /* ARRANGE */

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.doesExist(null));
    }

    @Test
    public void doesExist_knownId_returnTrue() {
        /* ARRANGE */
        final var knownUuid =
                getCatalogFromValidDesc(getValidDesc().getStaticId(), getNewCatalog(getValidDesc()))
                        .getId();

        /* ACT && ASSERT */
        assertTrue(service.doesExist(knownUuid));
    }

    @Test
    public void doesExist_unknownId_returnFalse() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertFalse(service.doesExist(unknownUuid));
    }

    @Test
    public void delete_nullId_throwsIllegalArgumentException() {
        /* ARRANGE */

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.delete(null));
    }

    @Test
    public void delete_knownId_removedObject() {
        /* ARRANGE */
        final var desc0 = getValidDesc();
        final var desc1 = getValidDesc();

        final var id = service.create(desc0);
        service.create(desc1);

        final var beforeCount = service.getAll(Pageable.unpaged()).getSize();

        /* ACT */
        service.delete(id.getId());

        /* ASSERT */
        assertEquals(beforeCount - 1, service.getAll(Pageable.unpaged()).getSize());
    }

    @Test
    public void delete_knownId_removedObjectWithId() {
        /* ARRANGE */
        final var desc0 = getValidDesc();
        final var desc1 = getValidDesc();

        final var id = service.create(desc0);
        service.create(desc1);

        /* ACT */
        service.delete(id.getId());

        /* ASSERT */
        assertEquals(service.getAll(Pageable.unpaged())
                            .stream()
                            .filter(x -> x.getId().equals(id.getId()))
                            .collect(Collectors.toList())
                            .size(),
                     0);
    }

    @Test
    public void delete_unknownId_removedObject() {
        /* ARRANGE */
        final var desc0 = getValidDesc();
        final var desc1 = getValidDesc();

        final var id = service.create(desc0);
        service.create(desc1);

        final var beforeCount = service.getAll(Pageable.unpaged()).getSize();

        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT */
        service.delete(unknownUuid);

        /* ASSERT */
        assertEquals(beforeCount, service.getAll(Pageable.unpaged()).getSize());
    }

    private CatalogDesc getValidDesc() {
        var desc = new CatalogDesc();
        desc.setDescription("The new description.");
        desc.setTitle("The new title.");
        desc.setStaticId(UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return desc;
    }

    private Catalog getNewCatalog( final CatalogDesc desc ) {
        return factory.create(desc);
    }

    private Catalog getCatalogFromValidDesc( final UUID id, final Catalog catalog ) {
        catalog.setId(id);

        return catalog;
    }
}
