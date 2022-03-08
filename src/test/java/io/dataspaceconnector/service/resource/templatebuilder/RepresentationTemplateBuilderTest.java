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
package io.dataspaceconnector.service.resource.templatebuilder;

import io.dataspaceconnector.model.representation.RepresentationDesc;
import io.dataspaceconnector.model.representation.RepresentationFactory;
import io.dataspaceconnector.model.template.RepresentationTemplate;
import io.dataspaceconnector.repository.RepresentationRepository;
import io.dataspaceconnector.service.resource.relation.RepresentationArtifactLinker;
import io.dataspaceconnector.service.resource.type.RepresentationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

class RepresentationTemplateBuilderTest {

    private RepresentationRepository repository = Mockito.mock(RepresentationRepository.class);
    private RepresentationArtifactLinker representationArtifactLinker = Mockito.mock(RepresentationArtifactLinker.class);

    @Autowired
    private RepresentationTemplateBuilder builder = new RepresentationTemplateBuilder(
        new RepresentationService(repository, new RepresentationFactory()),
        representationArtifactLinker,
        Mockito.mock(ArtifactTemplateBuilder.class)
    );

    @BeforeEach
    public void setup() {
        Mockito.doAnswer(returnsFirstArg())
               .when(repository)
               .saveAndFlush(Mockito.any());
    }

    @Test
    public void build_RepresentationTemplateNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> builder.build(null));
    }

    @Test
    public void build_RepresentationTemplateValid_returnNewRule() {
        /* ARRANGE */
        final var desc = new RepresentationDesc();
        desc.setTitle("Some title");
        final var template = new RepresentationTemplate(desc);

        /* ACT */
        final var result = builder.build(template);

        /* ASSERT */
        assertEquals("Some title", result.getTitle());
        Mockito.verify(representationArtifactLinker, Mockito.atLeastOnce()).add(Mockito.any(), Mockito.any());
    }
}
