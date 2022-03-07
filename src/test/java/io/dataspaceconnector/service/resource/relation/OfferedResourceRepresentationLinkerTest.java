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
package io.dataspaceconnector.service.resource.relation;

import io.dataspaceconnector.model.contract.Contract;
import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.service.resource.type.OfferedResourceService;
import io.dataspaceconnector.service.resource.type.RepresentationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {OfferedResourceRepresentationLinker.class})
class OfferedResourceRepresentationLinkerTest {

    @MockBean
    OfferedResourceService resourceService;

    @MockBean
    RepresentationService representationService;

    @Autowired
    @InjectMocks
    OfferedResourceRepresentationLinker linker;

    OfferedResource resource = getResource();
    Representation representation = getRepresentation();

    /***********************************************************************************************
     * getInternal                                                                                 *
     **********************************************************************************************/

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

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    @SneakyThrows
    private OfferedResource getResource() {
        final var constructor = OfferedResource.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var resource = constructor.newInstance();
        ReflectionTestUtils.setField(resource, "title", "Hello");
        ReflectionTestUtils.setField(resource, "representations", new ArrayList<Contract>());
        ReflectionTestUtils.setField(resource, "id", UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return resource;
    }

    @SneakyThrows
    private Representation getRepresentation() {
        final var constructor = Representation.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        final var representation = constructor.newInstance();
        ReflectionTestUtils.setField(representation, "title", "Hello");
        ReflectionTestUtils.setField(representation, "id", UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        return representation;
    }
}
