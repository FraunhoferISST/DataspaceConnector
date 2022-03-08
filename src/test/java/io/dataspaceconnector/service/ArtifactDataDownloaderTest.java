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
package io.dataspaceconnector.service;

import io.dataspaceconnector.common.exception.UnexpectedResponseException;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.service.message.builder.type.ArtifactRequestService;
import io.dataspaceconnector.service.resource.type.AgreementService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = { ArtifactDataDownloader.class })
class ArtifactDataDownloaderTest {
    @MockBean
    private ArtifactRequestService artifactReqSvc;

    @MockBean
    private AgreementService agreementService;

    @MockBean
    private EntityPersistenceService persistenceSvc;

    @Autowired
    private ArtifactDataDownloader downloader;

    @Test
    public void download_validInput_downloadData() throws IOException, UnexpectedResponseException {
        /* ARRANGE */
        final var recipient = URI.create("");
        final var artifacts = Arrays.asList(URI.create("https://artifact1"));
        final var agreementId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        final var response = new HashMap<String, String>();
        response.put("Hi", "Bye");

        final var agreement = new Agreement();
        ReflectionTestUtils.setField(agreement, "remoteId", URI.create("https//remoteId"));

        Mockito.when(agreementService.get(eq(agreementId))).thenReturn(agreement);
        Mockito.when(artifactReqSvc.sendMessage(eq(recipient), eq(artifacts.get(0)), eq(agreement.getRemoteId()))).thenReturn(response);

        /* ACT */
        downloader.download(recipient, artifacts, agreementId);

        /* ASSERT */
        Mockito.verify(persistenceSvc, Mockito.atLeastOnce()).saveData(eq(response), eq(artifacts.get(0)));
    }

    @Test
    public void download_validInputButStorageFails_shouldFail() throws IOException, UnexpectedResponseException {
        /* ARRANGE */
        final var recipient = URI.create("");
        final var artifacts = Arrays.asList(URI.create("https://artifact1"));
        final var agreementId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        final var response = new HashMap<String, String>();
        response.put("Hi", "Bye");

        final var agreement = new Agreement();
        ReflectionTestUtils.setField(agreement, "remoteId", URI.create("https//remoteId"));

        Mockito.when(agreementService.get(eq(agreementId))).thenReturn(agreement);
        Mockito.when(artifactReqSvc.sendMessage(eq(recipient), eq(artifacts.get(0)), eq(agreement.getRemoteId()))).thenReturn(response);
        Mockito.doThrow(IOException.class).when(persistenceSvc).saveData(eq(response), eq(artifacts.get(0)));

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> downloader.download(recipient, artifacts, agreementId));
    }
}
