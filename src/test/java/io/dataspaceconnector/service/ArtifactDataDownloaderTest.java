package io.dataspaceconnector.service;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.service.message.type.ArtifactRequestService;
import io.dataspaceconnector.service.message.type.exceptions.InvalidResponse;
import io.dataspaceconnector.service.resource.AgreementService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

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
    public void download_validInput_downloadData() throws InvalidResponse, IOException {
        /* ARRANGE */
        final var recipient = URI.create("");
        final var artifacts = Arrays.asList(URI.create("https://artifact1"));
        final var agreementId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        final var response = new HashMap<String, String>();
        response.put("Hi", "Bye");

        final var agreement = new Agreement();
        ReflectionTestUtils.setField(agreement, "remoteId", URI.create("https//remoteId"));

        Mockito.when(agreementService.get(eq(agreementId))).thenReturn(agreement);
        Mockito.when(artifactReqSvc.sendMessageAndValidate(eq(recipient), eq(artifacts.get(0)), eq(agreement.getRemoteId()))).thenReturn(response);

        /* ACT */
        downloader.download(recipient, artifacts, agreementId);

        /* ASSERT */
        Mockito.verify(persistenceSvc, Mockito.atLeastOnce()).saveData(eq(response), eq(artifacts.get(0)));
    }
}
