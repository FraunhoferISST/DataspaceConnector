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
package io.dataspaceconnector.common.ids.model;

import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.Util;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TemplateUtilsTest {

    @Test
    public void getCatalogTemplate_validInput_returnCatalog() {
        /* ARRANGE */
        final var catalog = new ResourceCatalogBuilder().build();

        /* ACT */
        final var result = TemplateUtils.getCatalogTemplate(catalog);

        /* ASSERT */
        assertNotNull(result);
        assertNotNull(result.getDesc());

    }

    @Test
    public void getRepresentationTemplates_validInput_returnRepresentationList() {
        /* ARRANGE */
        final var requestedArtifact = URI.create("https://requestedArtifact");
        final var representation = new RepresentationBuilder()
                ._instance_(Util.asList(new ArtifactBuilder(requestedArtifact).build(),
                        new ArtifactBuilder().build())).build();
        final var requestedArtifacts = List.of(requestedArtifact);
        final var download = true;
        final var remoteUrl = URI.create("https://remoteAddress");
        final var resource = new ResourceBuilder()
                ._representation_(Util.asList(representation))
                .build();

        /* ACT */
        final var result = TemplateUtils.getRepresentationTemplates(
                resource, requestedArtifacts, download, remoteUrl);

        /* ASSERT */
        assertEquals(1, result.size());

    }

    @Test
    public void getRepresentationTemplates_missingRepresentations_returnEmptyList() {
        /* ARRANGE */
        final var resource = new ResourceBuilder().build();
        final var requestedArtifacts = List.of(URI.create("https://requestedArtifact"));
        final var download = true;
        final var remoteUrl = URI.create("https://remoteAddress");

        /* ACT */
        final var result = TemplateUtils.getRepresentationTemplates(
                resource, requestedArtifacts, download, remoteUrl);

        /* ASSERT */
        assertEquals(0, result.size());
    }

    @Test
    public void getArtifactTemplates_validInputsUnequalId_returnEmptyList() {
        /* ARRANGE */
        final var representation = new RepresentationBuilder()
                ._instance_(Util.asList(new ArtifactBuilder().build()))
                .build();
        final var requestedArtifacts = List.of(URI.create("https://requestedArtifact"));
        final var download = true;
        final var remoteUrl = URI.create("https://remoteAddress");

        /* ACT */
        final var result = TemplateUtils.getArtifactTemplates(
                representation, requestedArtifacts, download, remoteUrl);

        /* ASSERT */
        assertEquals(0, result.size());
    }

    @Test
    public void getArtifactTemplates_validInputsMatchingId_returnArtifactList() {
        /* ARRANGE */
        final var requestedArtifact = URI.create("https://requestedArtifact");
        final var representation = new RepresentationBuilder()
                ._instance_(Util.asList(new ArtifactBuilder(requestedArtifact).build()))
                .build();
        final var requestedArtifacts = List.of(requestedArtifact);
        final var download = true;
        final var remoteUrl = URI.create("https://remoteAddress");

        /* ACT */
        final var result = TemplateUtils.getArtifactTemplates(
                representation, requestedArtifacts, download, remoteUrl);

        /* ASSERT */
        assertEquals(1, result.size());
        final var template = result.get(0);
        final var desc = template.getDesc();
        assertEquals(requestedArtifact, desc.getRemoteId());
        assertEquals(remoteUrl, desc.getRemoteAddress());
        assertNull(desc.getTitle());
        assertNull(desc.getAccessUrl());
        assertNull(desc.getBasicAuth());
        assertNull(desc.getApiKey());
        assertNull(desc.getValue());
        assertEquals(download, desc.isAutomatedDownload());
        assertEquals(0, desc.getAdditional().size());
    }

    @Test
    public void getArtifactTemplates_missingRepresentationInstance_returnEmptyList() {
        /* ARRANGE */
        final var representation = new RepresentationBuilder().build();
        final var requestedArtifacts = List.of(URI.create("https://requestedArtifact"));
        final var download = true;
        final var remoteUrl = URI.create("https://remoteAddress");

        /* ACT */
        final var result = TemplateUtils.getArtifactTemplates(
                representation, requestedArtifacts, download, remoteUrl);

        /* ASSERT */
        assertEquals(0, result.size());
    }
}
