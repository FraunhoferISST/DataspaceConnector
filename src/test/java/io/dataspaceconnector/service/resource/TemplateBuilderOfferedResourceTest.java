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
package io.dataspaceconnector.service.resource;

import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.resource.OfferedResourceDesc;
import io.dataspaceconnector.model.template.ResourceTemplate;
import io.dataspaceconnector.service.resource.relation.AbstractResourceRepresentationLinker;
import io.dataspaceconnector.service.resource.relation.OfferedResourceContractLinker;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TemplateBuilderOfferedResourceTest {

    @MockBean
    private AbstractResourceRepresentationLinker<OfferedResource> offeredResourceRepresentationLinker;

    @MockBean
    private OfferedResourceContractLinker offeredResourceContractLinker;

    @Autowired
    TemplateBuilder<OfferedResource, OfferedResourceDesc> builder;

    /***********************************************************************************************
     * ResourceTemplate.                                                                           *
     **********************************************************************************************/

    @Test
    public void build_ResourceTemplateNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> builder.build((ResourceTemplate<OfferedResourceDesc>) null));
    }

    @Test
    public void build_ResourceTemplateOnlyDesc_returnOnlyResource() {
        /* ARRANGE */
        final var desc = new OfferedResourceDesc();
        final var template = new ResourceTemplate<>(desc);

        /* ACT */
        final var result = builder.build(template);

        /* ASSERT */
        assertNotNull(result);
        Mockito.verify(offeredResourceRepresentationLinker, Mockito.atLeastOnce()).add(Mockito.any(), Mockito.any());
        Mockito.verify(offeredResourceContractLinker, Mockito.atLeastOnce()).add(Mockito.any(),
                Mockito.any());
    }
}
