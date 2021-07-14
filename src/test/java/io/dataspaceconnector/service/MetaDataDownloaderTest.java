package io.dataspaceconnector.service;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;

import io.dataspaceconnector.service.message.type.DescriptionRequestService;
import io.dataspaceconnector.service.message.type.exceptions.InvalidResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = { MetaDataDownloader.class })
class MetaDataDownloaderTest {

    @MockBean
    private DescriptionRequestService descReqSvc;

    @MockBean
    private EntityPersistenceService persistenceSvc;

    @Autowired
    MetaDataDownloader downloader;

    @Test
    void download_validInput_isSucessfull() throws InvalidResponse {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("Hi", "Bye");

        final var recipient = URI.create("https://someOne");
        final var resourceList = Arrays.asList(URI.create("https://resource1"));
        final var artifactList = Arrays.asList(URI.create("https://artifact1"));
        final var download = false;

        Mockito.when(descReqSvc.sendMessageAndValidate(eq(recipient), eq(resourceList.get(0))))
               .thenReturn(response);

        /* ACT */
        downloader.download(recipient, resourceList, artifactList, download);

        /* ASSERT */
        Mockito.verify(persistenceSvc, Mockito.atLeastOnce())
               .saveMetadata(eq(response), eq(artifactList), eq(download), eq(recipient));
    }
}
