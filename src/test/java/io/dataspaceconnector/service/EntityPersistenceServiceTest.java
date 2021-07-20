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
package io.dataspaceconnector.service;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.model.resource.RequestedResourceFactory;
import io.dataspaceconnector.model.template.ResourceTemplate;
import io.dataspaceconnector.service.ids.DeserializationService;
import io.dataspaceconnector.service.resource.AgreementService;
import io.dataspaceconnector.service.resource.ArtifactService;
import io.dataspaceconnector.service.resource.RelationServices;
import io.dataspaceconnector.service.resource.TemplateBuilder;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = EntityPersistenceService.class)
public class EntityPersistenceServiceTest {

    @MockBean
    private DeserializationService deserializationService;

    @MockBean
    private TemplateBuilder<RequestedResource, RequestedResourceDesc> templateBuilder;

    @MockBean
    private ArtifactService artifactService;

    @MockBean
    private AgreementService agreementService;

    @MockBean
    private RelationServices.AgreementArtifactLinker linker;

    @MockBean
    private ContractManager contractManager;

    @Autowired
    private EntityPersistenceService entityPersistenceService;

    @Test
    @SuppressWarnings("unchecked")
    public void saveMetadata_validResource_persistEntities() {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("payload", "resource-json");

        final var resource = getResource();

        when(deserializationService.getResource("resource-json")).thenReturn(resource);
        when(templateBuilder.build(any(ResourceTemplate.class))).thenReturn(getRequestedResource());

        /* ACT */
        entityPersistenceService.saveMetadata(response, new ArrayList<>(), false,
                URI.create("https://remote.com"));

        /* ASSERT */
        verify(templateBuilder, times(1)).build(any(ResourceTemplate.class));
    }

    @Test
    @SneakyThrows
    public void saveData_artifactPresent_persistData() {
        /* ARRANGE */
        final var value = "some data";
        final var artifact = getArtifact(value);

        final var response = new HashMap<String, String>();
        response.put("payload", value);

        when(artifactService.identifyByRemoteId(any())).thenReturn(Optional.of(artifact.getId()));
        when(artifactService.get(artifact.getId())).thenReturn(artifact);
        when(artifactService.setData(any(), any())).thenReturn(new ByteArrayInputStream("".getBytes()));

        /* ACT */
        entityPersistenceService.saveData(response, URI.create("https://remote.com"));

        /* ASSERT */
        verify(artifactService, times(1)).setData(eq(artifact.getId()), any());
    }

    private Resource getResource() {
        return new ResourceBuilder().build();
    }

    private RequestedResource getRequestedResource() {
        return new RequestedResourceFactory().create(new RequestedResourceDesc());
    }

    private Artifact getArtifact(final String value) {
        final var data = new LocalData();
        ReflectionTestUtils.setField(data, "value", value.getBytes());

        final var artifact = new ArtifactImpl();
        ReflectionTestUtils.setField(artifact, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(artifact, "data", data);
        return artifact;
    }

}
