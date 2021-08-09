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

import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.catalog.CatalogDesc;
import io.dataspaceconnector.model.contract.ContractDesc;
import io.dataspaceconnector.model.representation.RepresentationDesc;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.model.rule.ContractRuleDesc;
import io.dataspaceconnector.model.template.ArtifactTemplate;
import io.dataspaceconnector.model.template.CatalogTemplate;
import io.dataspaceconnector.model.template.ContractTemplate;
import io.dataspaceconnector.model.template.RepresentationTemplate;
import io.dataspaceconnector.model.template.ResourceTemplate;
import io.dataspaceconnector.model.template.RuleTemplate;
import io.dataspaceconnector.service.resource.relation.AbstractResourceRepresentationLinker;
import io.dataspaceconnector.service.resource.relation.CatalogRequestedResourceLinker;
import io.dataspaceconnector.service.resource.relation.ContractRuleLinker;
import io.dataspaceconnector.service.resource.relation.RepresentationArtifactLinker;
import io.dataspaceconnector.service.resource.relation.RequestedResourceContractLinker;
import io.dataspaceconnector.service.resource.type.ArtifactService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TemplateBuilderRequestedResourceTest {

    @MockBean
    private ArtifactService artifactService;
    @MockBean
    private AbstractResourceRepresentationLinker<RequestedResource> requestedResourceRepresentationLinker;

    @MockBean
    private CatalogRequestedResourceLinker catalogRequestedResourceLinker;

    @MockBean
    private RequestedResourceContractLinker requestedResourceContractLinker;

    @MockBean
    private ContractRuleLinker contractRuleLinker;

    @MockBean
    private RepresentationArtifactLinker representationArtifactLinker;

    @Autowired
    TemplateBuilder<RequestedResource, RequestedResourceDesc> builder;

    /***********************************************************************************************
     * ContractTemplate.                                                                           *
     **********************************************************************************************/

    @Test
    public void build_catalogTemplateNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> builder.build((CatalogTemplate) null));
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
        Mockito.verify(catalogRequestedResourceLinker, Mockito.atLeastOnce())
                .replace(Mockito.any(), Mockito.any());
    }

    /***********************************************************************************************
     * ResourceTemplate.                                                                           *
     **********************************************************************************************/

    @Test
    public void build_ResourceTemplateNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> builder.build((ResourceTemplate<RequestedResourceDesc>) null));
    }

    @Test
    public void build_ResourceTemplateOnlyDesc_returnOnlyResource() {
        /* ARRANGE */
        final var desc = new RequestedResourceDesc();
        final var template = new ResourceTemplate<>(desc);

        /* ACT */
        final var result = builder.build(template);

        /* ASSERT */
        assertNotNull(result);
        Mockito.verify(requestedResourceRepresentationLinker, Mockito.atLeastOnce()).add(Mockito.any(), Mockito.any());
        Mockito.verify(requestedResourceContractLinker, Mockito.atLeastOnce()).add(Mockito.any(),
                Mockito.any());
    }

    /***********************************************************************************************
     * ArtifactTemplate.                                                                           *
     **********************************************************************************************/

    @Test
    public void build_ArtifactTemplateNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> builder.build((ArtifactTemplate) null));
    }

    @Test
    public void build_ArtifactTemplateValid_returnNewArtifact() {
        /* ARRANGE */
        final var desc = new ArtifactDesc();
        desc.setTitle("Some title");
        final var template = new ArtifactTemplate(desc);

        final var artifact = getArtifact(desc);

        Mockito.when(artifactService.create(desc)).thenReturn(artifact);

        /* ACT */
        final var result = builder.build(template);

        /* ASSERT */
        assertEquals("Some title", result.getTitle());
    }

    /***********************************************************************************************
     * RuleTemplate.                                                                               *
     **********************************************************************************************/

    @Test
    public void build_RuleTemplateNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> builder.build((RuleTemplate) null));
    }

    @Test
    public void build_RuleTemplateValid_returnNewRule() {
        /* ARRANGE */
        final var desc = new ContractRuleDesc();
        desc.setTitle("Some title");
        final var template = new RuleTemplate(desc);

        /* ACT */
        final var result = builder.build(template);

        /* ASSERT */
        assertEquals("Some title", result.getTitle());
    }

    /***********************************************************************************************
     * ContractTemplate.                                                                               *
     **********************************************************************************************/

    @Test
    public void build_ContractTemplateNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> builder.build((ContractTemplate) null));
    }

    @Test
    public void build_ContractTemplateValid_returnNewRule() {
        /* ARRANGE */
        final var desc = new ContractDesc();
        desc.setTitle("Some title");
        final var template = new ContractTemplate(desc);

        /* ACT */
        final var result = builder.build(template);

        /* ASSERT */
        assertEquals("Some title", result.getTitle());
        Mockito.verify(contractRuleLinker, Mockito.atLeastOnce()).add(Mockito.any(), Mockito.any());
    }

    /***********************************************************************************************
     * RepresentationTemplate.                                                                               *
     **********************************************************************************************/

    @Test
    public void build_RepresentationTemplateNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> builder.build((RepresentationTemplate) null));
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

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    @SneakyThrows
    private Artifact getArtifact(ArtifactDesc desc) {
        final var artifactConstructor = ArtifactImpl.class.getConstructor();
        final var artifact = artifactConstructor.newInstance();

        final var titleField = artifact.getClass().getSuperclass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(artifact, desc.getTitle());

        return artifact;
    }
}
