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

import java.util.Optional;
import java.util.UUID;

import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.catalog.CatalogDesc;
import io.dataspaceconnector.model.catalog.CatalogFactory;
import io.dataspaceconnector.repository.CatalogRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CatalogServiceUpdateTest {
    private CatalogFactory factory = Mockito.spy(CatalogFactory.class);
    private CatalogRepository repository = Mockito.mock(CatalogRepository.class);

    Catalog newCatalog;
    Catalog updatedCatalog;

    UUID validId = UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e");

    private CatalogService service;

    @BeforeEach
    public void init() {
        service = new CatalogService(repository, factory);

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
        final var shouldLookLike = getCatalogFromValidDesc(validId,
                getNewCatalog(getUpdatedValidDesc()));

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

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

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
        ReflectionTestUtils.setField(catalog, "id", id);
        return catalog;
    }
}
