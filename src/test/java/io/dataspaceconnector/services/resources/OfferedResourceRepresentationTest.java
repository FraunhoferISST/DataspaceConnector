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

import io.dataspaceconnector.model.Contract;
import io.dataspaceconnector.model.OfferedResource;
import io.dataspaceconnector.model.Representation;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {OfferedResourceRepresentation.class})
class OfferedResourceRepresentationTest {

    @MockBean
    OfferedResourceService resourceService;

    @MockBean
    RepresentationService representationService;

    @Autowired
    @InjectMocks
    OfferedResourceRepresentation linker;

    OfferedResource resource = getResource();
    Representation representation = getRepresentation();

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
    public void getInternal_Valid_returnRepresentation() {
        /* ARRANGE */
        resource.getRepresentations().add(representation);

        /* ACT */
        final var representations = linker.getInternal(resource);

        /* ASSERT */
        final var expected = List.of(representation);
        assertEquals(expected, representations);
    }

    /**************************************************************************
     * Utilities
     *************************************************************************/

    @SneakyThrows
    private OfferedResource getResource() {
        final var constructor = OfferedResource.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var resource = constructor.newInstance();

        final var titleField = resource.getClass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(resource, "Hello");

        final var representationField =
                resource.getClass().getSuperclass().getDeclaredField("representations");
        representationField.setAccessible(true);
        representationField.set(resource, new ArrayList<Contract>());

        final var idField =
                resource.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(resource, UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return resource;
    }

    @SneakyThrows
    private Representation getRepresentation() {
        final var constructor = Representation.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var representation = constructor.newInstance();

        final var titleField = representation.getClass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(representation, "Hello");

        final var idField = representation.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(representation, UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return representation;
    }
}
