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
package io.dataspaceconnector.service.resource.ids.updater;

import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.representation.RepresentationDesc;
import io.dataspaceconnector.model.template.RepresentationTemplate;
import io.dataspaceconnector.service.resource.type.RepresentationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {RepresentationUpdater.class})
public class RepresentationUpdaterTest {

    @MockBean
    private RepresentationService representationService;

    @Autowired
    private RepresentationUpdater updater;

    private final UUID representationId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
    private final Representation representation = getRepresentation();
    private final io.dataspaceconnector.model.representation.Representation dscRepresentation = getDscRepresentation();
    private final io.dataspaceconnector.model.representation.Representation
            dscUpdatedRepresentation = getUpdatedDscRepresentation();
    private final RepresentationTemplate template = getTemplate();

    @Test
    public void update_null_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> updater.update(null));
    }

    @Test
    public void update_entityUnknownRemoteId_throwsResourceNotFoundException() {
        /* ARRANGE */
        Mockito.doReturn(Optional.empty())
                .when(representationService)
                .identifyByRemoteId(Mockito.eq(representation.getId()));

        /* ACT && ASSERT */
        final var result = assertThrows(ResourceNotFoundException.class,
                () -> updater.update(representation));
        assertEquals(representationId.toString(), result.getMessage());
    }

    @Test
    public void update_knownId_returnUpdatedRepresentation() {
        /* ARRANGE */
        Mockito.doReturn(Optional.of(representationId))
                .when(representationService)
                .identifyByRemoteId(Mockito.eq(representation.getId()));

        Mockito.doReturn(dscRepresentation)
                .when(representationService)
                .get(Mockito.eq(representationId));

        Mockito.doReturn(dscUpdatedRepresentation)
                .when(representationService)
                .update(Mockito.eq(representationId), Mockito.eq(template.getDesc()));

        /* ACT && ASSERT */
        final var result = updater.update(representation);
        assertEquals(dscUpdatedRepresentation, result);
        Mockito.verify(representationService, Mockito.atLeastOnce()).update(Mockito.eq(representationId),
                Mockito.eq(template.getDesc()));
    }

    private Representation getRepresentation() {
        return new RepresentationBuilder(URI.create(representationId.toString()))
                ._language_(Language.DE).build();
    }

    private io.dataspaceconnector.model.representation.Representation getDscRepresentation() {
        final var output = new io.dataspaceconnector.model.representation.Representation();
        ReflectionTestUtils.setField(output, "language", "SOME Language");
        return output;
    }

    private io.dataspaceconnector.model.representation.Representation getUpdatedDscRepresentation() {
        final var output = new io.dataspaceconnector.model.representation.Representation();
        ReflectionTestUtils.setField(output, "language", "https://w3id.org/idsa/code/DE");
        return output;
    }

    private RepresentationTemplate getTemplate() {
        final var output = new RepresentationTemplate(new RepresentationDesc());
        output.getDesc().setLanguage("https://w3id.org/idsa/code/DE");
        output.getDesc().setRemoteId(URI.create("550e8400-e29b-11d4-a716-446655440000"));
        output.getDesc().setAdditional(new ConcurrentHashMap<>());

        return output;
    }
}
