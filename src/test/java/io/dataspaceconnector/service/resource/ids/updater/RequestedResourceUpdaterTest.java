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
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.model.resource.RequestedResource;
import io.dataspaceconnector.model.resource.RequestedResourceDesc;
import io.dataspaceconnector.model.template.ResourceTemplate;
import io.dataspaceconnector.service.resource.type.RequestedResourceService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {RequestedResourceUpdater.class})
public class RequestedResourceUpdaterTest {
    @MockBean
    private RequestedResourceService requestedResourceService;

    @Autowired
    private RequestedResourceUpdater updater;

    private final UUID resourceId  = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
    private final Resource                                resource           = getResource();
    private final RequestedResource                       dscResource        = getDscResource();
    private final RequestedResource                       dscUpdatedResource = getUpdatedDscResource();
    private final ResourceTemplate<RequestedResourceDesc> template           = getTemplate();

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
                .when(requestedResourceService)
                .identifyByRemoteId(Mockito.eq(resource.getId()));

        /* ACT && ASSERT */
        final var result = assertThrows(ResourceNotFoundException.class,
                () -> updater.update(resource));
        assertEquals(resourceId.toString(), result.getMessage());
    }

    @Test
    public void update_knownId_returnUpdatedRepresentation() {
        /* ARRANGE */
        Mockito.doReturn(Optional.of(resourceId))
                .when(requestedResourceService)
                .identifyByRemoteId(Mockito.eq(resource.getId()));

        Mockito.doReturn(dscResource)
                .when(requestedResourceService)
                .get(Mockito.eq(resourceId));

        Mockito.doReturn(dscUpdatedResource)
                .when(requestedResourceService)
                .update(Mockito.eq(resourceId), Mockito.eq(template.getDesc()));

        /* ACT && ASSERT */
        final var result = updater.update(resource);
        assertEquals(dscUpdatedResource, result);
        Mockito.verify(requestedResourceService, Mockito.atLeastOnce())
                .update(Mockito.eq(resourceId), Mockito.eq(template.getDesc()));
    }

    private Resource getResource() {
        return new ResourceBuilder(URI.create(resourceId.toString()))
                ._language_(new ArrayList<>(List.of(Language.DE)))
                .build();
    }

    @SneakyThrows
    private RequestedResource getDscResource() {
        final var resourceConstructor = RequestedResource.class.getDeclaredConstructor();
        resourceConstructor.setAccessible(true);
        final var output = resourceConstructor.newInstance();
        ReflectionTestUtils.setField(output, "language", "SOME Language");
        return output;
    }

    @SneakyThrows
    private RequestedResource getUpdatedDscResource() {
        final var resourceConstructor = RequestedResource.class.getDeclaredConstructor();
        resourceConstructor.setAccessible(true);
        final var output = resourceConstructor.newInstance();
        ReflectionTestUtils.setField(output, "language", "https://w3id.org/idsa/code/DE");
        return output;
    }

    private ResourceTemplate<RequestedResourceDesc> getTemplate() {
        final var output = new ResourceTemplate<>(new RequestedResourceDesc());
        output.getDesc().setLanguage("https://w3id.org/idsa/code/DE");
        output.getDesc().setRemoteId(URI.create("550e8400-e29b-11d4-a716-446655440000"));
        output.getDesc().setAdditional(new ConcurrentHashMap<>());
        output.getDesc().setKeywords(new ArrayList<>());

        return output;
    }
}
