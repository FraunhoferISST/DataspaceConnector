package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ArtifactImpl;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ArtifactTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.ResourceTemplate;
import de.fraunhofer.isst.dataspaceconnector.model.templates.RuleTemplate;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TemplateBuilderTest {

    @MockBean
    private ArtifactService artifactService;

    @MockBean
    private AbstractResourceRepresentationLinker<OfferedResource> offeredResourceRepresentationLinker;

    @MockBean
    private AbstractResourceRepresentationLinker<RequestedResource> requestedResourceRepresentationLinker;

    @MockBean
    private OfferedResourceContractLinker offeredResourceContractLinker;

    @MockBean
    private RequestedResourceContractLinker requestedResourceContractLinker;

    @Autowired
    TemplateBuilder<OfferedResource, OfferedResourceDesc> builder;

    @Test
    public void build_ResourceTemplateNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> builder.build(( ResourceTemplate<OfferedResourceDesc> ) null));
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
        Mockito.verify(offeredResourceRepresentationLinker, Mockito.atLeastOnce()).replace(Mockito.any(), Mockito.any());
        Mockito.verify(offeredResourceContractLinker, Mockito.atLeastOnce()).replace(Mockito.any(), Mockito.any());
    }

    /**
     * ArtifactTemplate.
     */

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

    /**
     * RuleTemplate.
     */

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

    /**
     * Utilities
     */

    @SneakyThrows
    private Artifact getArtifact(ArtifactDesc desc) {
        final var artifactConstructor = ArtifactImpl.class.getConstructor();
        final var artifact = artifactConstructor.newInstance();

        final var titleField = artifact.getClass().getSuperclass().getDeclaredField("title");
        titleField.setAccessible(true);
        titleField.set(artifact, desc.getTitle());

        return artifact;
    }
}
