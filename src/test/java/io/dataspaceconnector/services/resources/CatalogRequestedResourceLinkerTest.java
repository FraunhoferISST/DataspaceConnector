/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.services.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.dataspaceconnector.model.Catalog;
import io.dataspaceconnector.model.RequestedResource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {CatalogRequestedResourceLinker.class})
class CatalogRequestedResourceLinkerTest {

    @MockBean
    CatalogService catalogService;

    @MockBean
    RequestedResourceService resourceService;

    @Autowired
    @InjectMocks
    CatalogRequestedResourceLinker linker;

    Catalog catalog = getCatalog();
    RequestedResource resource = getResource();

    /**************************************************************************
     * getInternal
     *************************************************************************/

    @Test
    public void getInternal_null_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> linker.getInternal(null));
    }

    @Test
    public void getInternal_Valid_returnOfferedResources() {
        /* ARRANGE */
        catalog.getRequestedResources().add(resource);

        /* ACT */
        final var resources = linker.getInternal(catalog);

        /* ASSERT */
        final var expected = List.of(resource);
        assertEquals(expected, resources);
    }

    /**************************************************************************
     * Utilities
     *************************************************************************/

    @SneakyThrows
    private Catalog getCatalog() {
        final var constructor = Catalog.class.getConstructor();
        constructor.setAccessible(true);

        final var catalog = constructor.newInstance();

        final var titleField = catalog.getClass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(catalog, "Catalog");

        final var requestedResourcesField = catalog.getClass().getDeclaredField("requestedResources");
        requestedResourcesField.setAccessible(true);
        requestedResourcesField.set(catalog, new ArrayList<RequestedResource>());

        final var idField = catalog.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(catalog, UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));

        return catalog;
    }

    @SneakyThrows
    private RequestedResource getResource() {
        final var constructor = RequestedResource.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var resource = constructor.newInstance();

        final var titleField = resource.getClass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(resource, "Hello");

        final var idField =
                resource.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(resource, UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return resource;
    }
}
