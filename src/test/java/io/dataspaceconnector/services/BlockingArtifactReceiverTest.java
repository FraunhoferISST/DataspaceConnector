package io.dataspaceconnector.services;

import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

import io.dataspaceconnector.exceptions.PolicyRestrictionException;
import io.dataspaceconnector.model.Artifact;
import io.dataspaceconnector.model.ArtifactImpl;
import io.dataspaceconnector.services.messages.types.ArtifactRequestService;
import io.dataspaceconnector.services.resources.ArtifactService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.Base64Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {BlockingArtifactReceiver.class})
public class BlockingArtifactReceiverTest {

    @MockBean
    private ArtifactRequestService messageService;

    @MockBean
    private ArtifactService artifactService;

    @Autowired
    private BlockingArtifactReceiver blockingArtifactReceiver;

    @Test
    public void retrieve_artifactIdNull_throwIllegalArgumentException() {
        /* ARRANGE */
        final var recipient = URI.create("https://recipient.com");
        final var transferContract = URI.create("https://contract.com");

        when(artifactService.get(any())).thenCallRealMethod();

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> blockingArtifactReceiver
                .retrieve(null, recipient, transferContract, null));
    }

    @Test
    @SneakyThrows
    public void retrieve_validInput_returnData() {
        /* ARRANGE */
        final var artifactId = UUID.randomUUID();
        final var recipient = URI.create("https://recipient.com");
        final var transferContract = URI.create("https://contract.com");

        final var artifact = getArtifact();

        final var data = "DATA";
        final var response = new HashMap<String, String>();
        response.put("payload", data);

        when(artifactService.get(artifactId)).thenReturn(artifact);
        when(messageService.sendMessage(recipient, artifact.getRemoteId(), transferContract,
                null)).thenReturn(response);
        when(messageService.validateResponse(response)).thenReturn(true);

        /* ACT */
        final var result = blockingArtifactReceiver
                .retrieve(artifactId, recipient, transferContract);

        /* ASSERT */
        assertEquals(data, Base64Utils.encodeToString(result.readAllBytes()));
    }

    @Test
    public void retrieve_noValidResponse_throwPolicyRestrictionException() {
        /* ARRANGE */
        final var artifactId = UUID.randomUUID();
        final var recipient = URI.create("https://recipient.com");
        final var transferContract = URI.create("https://contract.com");

        final var artifact = getArtifact();

        final var data = "DATA";
        final var response = new HashMap<String, String>();
        response.put("payload", data);

        when(artifactService.get(artifactId)).thenReturn(artifact);
        when(messageService.sendMessage(recipient, artifact.getRemoteId(), transferContract,
                null)).thenReturn(response);
        when(messageService.validateResponse(response)).thenReturn(false);
        when(messageService.getResponseContent(response)).thenReturn(new HashMap<>());

        /* ACT && ASSERT */
        assertThrows(PolicyRestrictionException.class, () -> blockingArtifactReceiver
                .retrieve(artifactId, recipient, transferContract));
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    private Artifact getArtifact() {
        final var artifact = new ArtifactImpl();
        ReflectionTestUtils.setField(artifact, "remoteId", URI.create("https://artifact.com"));
        return artifact;
    }

}
