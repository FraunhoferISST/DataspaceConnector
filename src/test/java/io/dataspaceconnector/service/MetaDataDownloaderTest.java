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
import io.dataspaceconnector.service.message.builder.type.DescriptionRequestService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = { MetadataDownloader.class })
class MetaDataDownloaderTest {

    @MockBean
    private DescriptionRequestService descReqSvc;

    @MockBean
    private EntityPersistenceService persistenceSvc;

    @Autowired
    MetadataDownloader downloader;

    @Test
    void download_validInput_isSuccessfull() throws UnexpectedResponseException {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("Hi", "Bye");

        final var recipient = URI.create("https://someOne");
        final var resourceList = Arrays.asList(URI.create("https://resource1"));
        final var artifactList = Arrays.asList(URI.create("https://artifact1"));
        final var download = false;

        Mockito.when(descReqSvc.sendMessage(eq(recipient), eq(resourceList.get(0))))
               .thenReturn(response);

        /* ACT */
        downloader.download(recipient, resourceList, artifactList, download);

        /* ASSERT */
        Mockito.verify(persistenceSvc, Mockito.atLeastOnce())
               .saveMetadata(eq(response), eq(artifactList), eq(download), eq(recipient));
    }
}
