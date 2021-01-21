package de.fraunhofer.isst.dataspaceconnector;

import de.fraunhofer.isst.dataspaceconnector.exceptions.UUIDFormatException;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import java.net.URI;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UUIDUtilsTests {

    @Test(expected = UUIDFormatException.class)
    public void UUIDUtils_uuidFromUri_Uri_without_Uuid_In() {
        final var uuidString = "";
        final var uriString = "https://dsc/anon42/api/" + uuidString;

        final var inputUri = URI.create(uriString);

        UUIDUtils.uuidFromUri(inputUri);
    }

    @Test
    public void UUIDUtils_uuidFromUri_Uri_with_one_Uuid_InOut() {
        final var uuidString = "b7c9d390-837b-4edc-b47c-877c3d3570f0";
        final var uriString = "https://dsc/anon42/api/" + uuidString;

        final var inputUri = URI.create(uriString);
        final var expectedUUID = UUID.fromString(uuidString);

        final var resultUUID = UUIDUtils.uuidFromUri(inputUri);

        Assert.assertEquals(resultUUID, expectedUUID);
    }

    @Test
    public void UUIDUtils_uuidFromUri_Uri_with_two_seperated_Uuids_In_LastOut() {
        final var uuidString = "b7c9d390-837b-4edc-b47c-877c3d3570f0";
        final var uuidString2 = "4ad9a9ba-1a0b-4012-87de-13a8734e3765";
        final var uriString = "https://dsc/anon42/api/" + uuidString + "/" + uuidString2;

        final var inputUri = URI.create(uriString);
        final var expectedUUID = UUID.fromString(uuidString2);

        final var resultUUID = UUIDUtils.uuidFromUri(inputUri);

        Assert.assertEquals(resultUUID, expectedUUID);
    }


    @Test
    public void UUIDUtils_uuidFromUri_Uri_with_two_combined_Uuids_In_LastOut() {
        final var uuidString = "b7c9d390-837b-4edc-b47c-877c3d3570f0";
        final var uuidString2 = "4ad9a9ba-1a0b-4012-87de-13a8734e3765";
        final var uriString = "https://dsc/anon42/api/" + uuidString + uuidString2;

        final var inputUri = URI.create(uriString);
        final var expectedUUID = UUID.fromString(uuidString2);

        final var resultUUID = UUIDUtils.uuidFromUri(inputUri);

        Assert.assertEquals(resultUUID, expectedUUID);
    }

}
