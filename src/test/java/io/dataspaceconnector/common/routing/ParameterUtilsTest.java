package io.dataspaceconnector.common.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParameterUtilsTest {

    @Test
    void getIdscp2ClientUri_validRecipient_willReturnIDscp2Address() {
        /* ARRANGE */
        final var recipient = "someOne";

        /* ACT */
        final var result = ParameterUtils.getIdscp2ClientUri(recipient);

        /* ASSERT */
        assertEquals(
                "idscp2client://" + recipient + "?awaitResponse=true"
                + "&sslContextParameters=#serverSslContext"
                + "&useIdsMessages=true",
                result
        );
    }
}
