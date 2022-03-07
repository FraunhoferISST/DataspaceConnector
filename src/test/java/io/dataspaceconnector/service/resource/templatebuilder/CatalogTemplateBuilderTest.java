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

import io.dataspaceconnector.model.catalog.CatalogDesc;
import io.dataspaceconnector.model.catalog.CatalogFactory;
import io.dataspaceconnector.model.template.CatalogTemplate;
import io.dataspaceconnector.repository.CatalogRepository;
import io.dataspaceconnector.service.resource.relation.CatalogOfferedResourceLinker;
import io.dataspaceconnector.service.resource.relation.CatalogRequestedResourceLinker;
import io.dataspaceconnector.service.resource.type.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;

class CatalogTemplateBuilderTest {

    private CatalogRepository repository = Mockito.mock(CatalogRepository.class);
    private CatalogOfferedResourceLinker catalogOfferedResourceLinker = Mockito.mock(CatalogOfferedResourceLinker.class);
    private CatalogRequestedResourceLinker catalogRequestedResourceLinker = Mockito.mock(CatalogRequestedResourceLinker.class);

    private CatalogTemplateBuilder builder = new CatalogTemplateBuilder(
            new CatalogService(repository, new CatalogFactory()),
            catalogOfferedResourceLinker,
            catalogRequestedResourceLinker,
            Mockito.mock(OfferedResourceTemplateBuilder.class),
            Mockito.mock(RequestedResourceTemplateBuilder.class)
    );

    @BeforeEach
    public void setup() {
        Mockito.doAnswer(returnsFirstArg())
               .when(repository)
               .saveAndFlush(Mockito.any());
    }

    @Test
    public void build_catalogTemplateNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> builder.build(null));
    }

    @Test
    public void build_catalogTemplateOnlyDesc_returnOnlyResource() {
        /* ARRANGE */
        final var desc = new CatalogDesc();
        final var template = new CatalogTemplate(desc);

        /* ACT */
        final var result = builder.build(template);

        /* ASSERT */
        assertNotNull(result);
        Mockito.verify(catalogOfferedResourceLinker, Mockito.atLeastOnce())
               .replace(Mockito.any(), Mockito.any());
        Mockito.verify(catalogRequestedResourceLinker, Mockito.atLeastOnce())
               .replace(Mockito.any(), Mockito.any());
    }
}
