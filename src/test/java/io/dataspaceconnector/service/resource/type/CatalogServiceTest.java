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
package io.dataspaceconnector.service.resource.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.catalog.CatalogDesc;
import io.dataspaceconnector.model.catalog.CatalogFactory;
import io.dataspaceconnector.repository.CatalogRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogServiceTest {
    private CatalogFactory factory = Mockito.mock(CatalogFactory.class);
    private CatalogRepository repository = Mockito.mock(CatalogRepository.class);

    private CatalogService service;

    CatalogDesc catalogOneDesc = getCatalogOneDesc();
    CatalogDesc catalogTwoDesc = getCatalogTwoDesc();
    Catalog catalogOne = getCatalogOne();
    Catalog catalogTwo = getCatalogTwo();

    List<Catalog> catalogList = new ArrayList<>();

    /***********************************************************************************************
     * Setup                                                                                       *
     **********************************************************************************************/

    @BeforeEach
    public void init() {
        service = new CatalogService(repository, factory);

        Mockito.when(factory.create(catalogOneDesc)).thenReturn(catalogOne);
        Mockito.when(factory.create(catalogTwoDesc)).thenReturn(catalogTwo);
        Mockito.when(repository.findById(Mockito.eq(catalogOne.getId())))
                .thenReturn(Optional.of(catalogOne));
        Mockito.when(repository.findById(Mockito.eq(catalogTwo.getId())))
                .thenReturn(Optional.of(catalogTwo));
        Mockito.when(repository.saveAndFlush(Mockito.eq(catalogOne)))
                .thenReturn(catalogOne);
        Mockito.when(repository.saveAndFlush(Mockito.eq(catalogTwo)))
                .thenReturn(catalogTwo);

        Mockito.when(repository.saveAndFlush(Mockito.any())).thenAnswer(this::saveAndFlushMock);
        Mockito.when(repository.findById(AdditionalMatchers.not(Mockito.eq(catalogOne.getId()))))
                .thenReturn(Optional.empty());
        Mockito.when(repository.findById(Mockito.isNull()))
                .thenThrow(InvalidDataAccessApiUsageException.class);
        Mockito.when(repository.findAll(Pageable.unpaged())).thenAnswer(this::findAllMock);
        Mockito.doThrow(InvalidDataAccessApiUsageException.class)
                .when(repository)
                .deleteById(Mockito.isNull());
        Mockito.doAnswer(this::deleteByIdMock).when(repository).deleteById(Mockito.isA(UUID.class));
    }

    private static Page<Catalog> toPage(final List<Catalog> catalogList, final Pageable pageable) {
        return new PageImpl<>(catalogList.subList(0, catalogList.size()), pageable,
                catalogList.size());
    }

    private Page<Catalog> findAllMock(final InvocationOnMock invocation) {
        return toPage(catalogList, invocation.getArgument(0));
    }

    @SneakyThrows
    private Catalog saveAndFlushMock(final InvocationOnMock invocation) {
        final var obj = (Catalog) invocation.getArgument(0);
        ReflectionTestUtils.setField(obj, "id", UUID.randomUUID());

        catalogList.add(obj);
        return obj;
    }

    private Answer<?> deleteByIdMock(final InvocationOnMock invocation) {
        final var obj = (UUID) invocation.getArgument(0);
        catalogList.removeIf(x -> x.getId().equals(obj));
        return null;
    }

    /***********************************************************************************************
     * create                                                                                      *
     **********************************************************************************************/

    @Test
    public void create_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.create(null));
    }

    @Test
    public void create_ValidDesc_returnCatalog() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var catalog = service.create(catalogOneDesc);

        /* ACT && ASSERT */
        assertNotNull(catalog);
    }

    @Test
    public void create_ValidDesc_returnHasId() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var catalog = service.create(catalogOneDesc);

        /* ACT && ASSERT */
        assertEquals(catalogOne, catalog);
    }

    @Test
    public void create_ValidDesc_createOnlyOneCatalog() {
        /* ARRANGE */
        final var beforeCount = service.getAll(Pageable.unpaged()).getSize();

        /* ACT */
        service.create(catalogOneDesc);

        /* ASSERT */
        assertEquals(beforeCount + 1, service.getAll(Pageable.unpaged()).getSize());
    }

    /***********************************************************************************************
     * update                                                                                      *
     **********************************************************************************************/

    @Test
    public void update_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> service.update(catalogOne.getId(), null));
    }

    @Test
    public void update_nullId_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.update(null, catalogOneDesc));
    }

    @Test
    public void update_unknownId_throwResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(ResourceNotFoundException.class, () -> service.get(unknownUuid));
    }

    /***********************************************************************************************
     * get                                                                                         *
     **********************************************************************************************/

    @Test
    public void get_nullId_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.get(null));
    }

    @Test
    public void get_knownId_returnCatalog() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertNotNull(service.get(catalogOne.getId()));
    }

    @Test
    public void get_unknownId_throwResourceNotFoundException() {
        /* ARRANGE */
        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        final var msg = assertThrows(ResourceNotFoundException.class,
                () -> service.get(unknownUuid));
        assertEquals(service.getClass().getSimpleName() + ": " + unknownUuid.toString(),
                msg.getMessage());
    }

    /***********************************************************************************************
     * getAll
     * *
     **********************************************************************************************/

    @Test
    public void getAll_null_throwsIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.getAll(null));
    }

    /***********************************************************************************************
     * doesExist                                                                                   *
     **********************************************************************************************/

    @Test
    public void doesExist_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.doesExist(null));
    }

    @Test
    public void doesExist_knownId_returnTrue() {
        /* ARRANGE */
        final var knownUuid = catalogOne.getId();

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


    /***********************************************************************************************
     * delete
     **********************************************************************************************/

    @Test
    public void delete_nullId_throwsIllegalArgumentException() {
        /* ARRANGE */

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.delete(null));
    }

    @Test
    public void delete_knownId_removedObject() {
        /* ARRANGE */
        final var id = service.create(catalogOneDesc);
        service.create(catalogTwoDesc);

        final var beforeCount = service.getAll(Pageable.unpaged()).getSize();

        /* ACT */
        service.delete(id.getId());

        /* ASSERT */
        assertEquals(beforeCount - 1, service.getAll(Pageable.unpaged()).getSize());
    }

    @Test
    public void delete_knownId_removedObjectWithId() {
        /* ARRANGE */
        final var id = service.create(catalogOneDesc);
        service.create(catalogTwoDesc);

        /* ACT */
        service.delete(id.getId());

        /* ASSERT */
        assertEquals(0, (int) service.getAll(Pageable.unpaged())
                .stream()
                .filter(x -> x.getId().equals(id.getId())).count());
    }

    @Test
    public void delete_unknownId_removedObject() {
        /* ARRANGE */
        service.create(catalogTwoDesc);

        final var beforeCount = service.getAll(Pageable.unpaged()).getSize();

        final var unknownUuid = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT */
        service.delete(unknownUuid);

        /* ASSERT */
        assertEquals(beforeCount, service.getAll(Pageable.unpaged()).getSize());
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private CatalogDesc getCatalogOneDesc() {
        var desc = new CatalogDesc();
        desc.setTitle("The new title.");

        return desc;
    }

    private CatalogDesc getCatalogTwoDesc() {
        var desc = new CatalogDesc();
        desc.setTitle("The different title.");

        return desc;
    }

    @SneakyThrows
    private Catalog getCatalogOne() {
        final var desc = getCatalogOneDesc();

        final var catalogConstructor = Catalog.class.getConstructor();

        final var catalog = catalogConstructor.newInstance();
        ReflectionTestUtils.setField(catalog, "id", UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));
        ReflectionTestUtils.setField(catalog, "title", desc.getTitle());

        return catalog;
    }

    @SneakyThrows
    private Catalog getCatalogTwo() {
        final var desc = getCatalogTwoDesc();

        final var catalogConstructor = Catalog.class.getConstructor();

        final var catalog = catalogConstructor.newInstance();
        ReflectionTestUtils.setField(catalog, "id", UUID.fromString("afb43170-b8d4-4872-b923-3490de99a53b"));
        ReflectionTestUtils.setField(catalog, "title", desc.getTitle());

        return catalog;
    }
}
