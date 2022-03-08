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
package io.dataspaceconnector.common.util;

import io.dataspaceconnector.common.exception.UUIDCreationException;
import io.dataspaceconnector.common.exception.UUIDFormatException;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UUIDUtilsTests {

    @Test
    public void findUuids_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> UUIDUtils.findUuids(null));
    }

    @Test
    public void findUuids_oneUuidInInput_returnUuid() {
        /* ARRANGE */
        final var uuidString = "c5af2999-7a7f-4cc5-9ce1-4531c60a7151";
        final var input = "String with UUID: " + uuidString;

        /* ACT */
        final var uuids = UUIDUtils.findUuids(input);

        /* ASSERT */
        assertEquals(1, uuids.size());
        assertTrue(uuids.contains(uuidString));
    }

    @Test
    public void findUuids_twoUuidsInInput_returnUuids() {
        /* ARRANGE */
        final var uuidString = "e5e2ab04-633a-44b9-87d9-a097ae6da3cf";
        final var uuidString2 = "c5af2999-7a7f-4cc5-9ce1-4531c60a7151";
        final var input = "String with UUIDs: " + uuidString + " and " + uuidString2;

        /* ACT */
        final var uuids = UUIDUtils.findUuids(input);

        /* ASSERT */
        assertEquals(2, uuids.size());
        assertTrue(uuids.contains(uuidString));
        assertTrue(uuids.contains(uuidString2));
    }

    @Test
    public void findUuids_noUuidInInput_returnEmptyList() {
        /* ARRANGE */
        final var input = "String without UUID";

        /* ACT */
        final var uuids = UUIDUtils.findUuids(input);

        /* ASSERT */
        assertTrue(uuids.isEmpty());
    }

    @Test
    public void findUuids_oneDigitMissingForUuidInInput_returnEmptyList() {
        /* ARRANGE */
        final var input = "String without UUID: c5af2999-7a7f-4cc5-9ce1-4531c60a715";

        /* ACT */
        final var uuids = UUIDUtils.findUuids(input);

        /* ASSERT */
        assertTrue(uuids.isEmpty());
    }

    @Test
    public void uuidFromUri_uriWithoutUuid_throwUUIDFormatException() {
        /* ARRANGE */
        final var uuidString = "";
        final var uriString = "https://dsc/anon42/api/" + uuidString;

        final var inputUri = URI.create(uriString);

        /* ACT && ASSERT */
        assertThrows(UUIDFormatException.class, () -> UUIDUtils.uuidFromUri(inputUri));
    }

    @Test
    public void uuidFromUri_uriWithOneUuid_returnUuid() {
        /* ARRANGE */
        final var uuidString = "b7c9d390-837b-4edc-b47c-877c3d3570f0";
        final var uriString = "https://dsc/anon42/api/" + uuidString;

        final var inputUri = URI.create(uriString);
        final var expectedUUID = UUID.fromString(uuidString);

        /* ACT */
        final var resultUUID = UUIDUtils.uuidFromUri(inputUri);

        /* ASSERT */
        assertEquals(resultUUID, expectedUUID);
    }

    @Test
    public void uuidFromUri_uriWithTwoSeparatedUuids_returnLastUuid() {
        /* ARRANGE */
        final var uuidString = "b7c9d390-837b-4edc-b47c-877c3d3570f0";
        final var uuidString2 = "4ad9a9ba-1a0b-4012-87de-13a8734e3765";
        final var uriString = "https://dsc/anon42/api/" + uuidString + "/" + uuidString2;

        final var inputUri = URI.create(uriString);
        final var expectedUUID = UUID.fromString(uuidString2);

        /* ACT */
        final var resultUUID = UUIDUtils.uuidFromUri(inputUri);

        /* ASSERT */
        assertEquals(resultUUID, expectedUUID);
    }

    @Test
    public void uuidFromUri_uriWithTwoCombinedUuids_returnLastUuid() {
        /* ARRANGE */
        final var uuidString = "b7c9d390-837b-4edc-b47c-877c3d3570f0";
        final var uuidString2 = "4ad9a9ba-1a0b-4012-87de-13a8734e3765";
        final var uriString = "https://dsc/anon42/api/" + uuidString + uuidString2;

        final var inputUri = URI.create(uriString);
        final var expectedUUID = UUID.fromString(uuidString2);

        /* ACT */
        final var resultUUID = UUIDUtils.uuidFromUri(inputUri);

        /* ASSERT */
        assertEquals(resultUUID, expectedUUID);
    }

    @Test
    public void uuidFromUri_uriWithOneUuidCorrectIndex_returnUuid() {
        /* ARRANGE */
        final var uuidString = "b7c9d390-837b-4edc-b47c-877c3d3570f0";
        final var uriString = "https://dsc/anon42/api/" + uuidString;

        final var inputUri = URI.create(uriString);
        final var expectedUUID = UUID.fromString(uuidString);

        /* ACT */
        final var resultUUID = UUIDUtils.uuidFromUri(inputUri, 0);

        /* ASSERT */
        assertEquals(resultUUID, expectedUUID);
    }

    @Test
    public void uuidFromUri_uriWithTwoUuidsIndexZero_returnFirstUuid() {
        /* ARRANGE */
        final var uuidString = "b7c9d390-837b-4edc-b47c-877c3d3570f0";
        final var uuidString2 = "4ad9a9ba-1a0b-4012-87de-13a8734e3765";
        final var uriString = "https://dsc/anon42/api/" + uuidString + uuidString2;

        final var inputUri = URI.create(uriString);
        final var expectedUUID = UUID.fromString(uuidString);

        /* ACT */
        final var resultUUID = UUIDUtils.uuidFromUri(inputUri, 0);

        /* ASSERT */
        assertEquals(resultUUID, expectedUUID);
    }

    @Test
    public void uuidFromUri_uriWithTwoUuidsIndexOne_returnSecondUuid() {
        /* ARRANGE */
        final var uuidString = "b7c9d390-837b-4edc-b47c-877c3d3570f0";
        final var uuidString2 = "4ad9a9ba-1a0b-4012-87de-13a8734e3765";
        final var uriString = "https://dsc/anon42/api/" + uuidString + uuidString2;

        final var inputUri = URI.create(uriString);
        final var expectedUUID = UUID.fromString(uuidString2);

        /* ACT */
        final var resultUUID = UUIDUtils.uuidFromUri(inputUri, 1);

        /* ASSERT */
        assertEquals(resultUUID, expectedUUID);
    }

    @Test
    public void uuidFromUri_uriWithOneUuidInvalidIndex_throwIndexOutOfBoundsException() {
        /* ARRANGE */
        final var uuidString = "b7c9d390-837b-4edc-b47c-877c3d3570f0";
        final var uriString = "https://dsc/anon42/api/" + uuidString;

        final var inputUri = URI.create(uriString);

        /* ACT && ASSERT */
        assertThrows(IndexOutOfBoundsException.class, () -> UUIDUtils.uuidFromUri(inputUri, 1));
    }

    @Test
    public void createUuid_functionFalseForAllUuids_returnUuid() {
        /* ARRANGE */
        final Function<UUID, Boolean> function = uuid -> false;
        long maxNumTries = 10;

        /* ACT */
        final var uuid = UUIDUtils.createUUID(function, maxNumTries);

        /* ASSERT */
        assertNotNull(uuid);
    }

    @Test
    public void createUuid_functionTrueForAllUuids_throwUUIDCreationException() {
        /* ARRANGE */
        final Function<UUID, Boolean> function = uuid -> true;
        long maxNumTries = 10;

        /* ACT && ASSERT */
        assertThrows(UUIDCreationException.class, () -> UUIDUtils.createUUID(function, maxNumTries));
    }

    @Test
    public void createUuid_functionNull_throwNullPointerException() {
        /* ARRANGE */
        long maxNumTries = 10;

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> UUIDUtils.createUUID(null, maxNumTries));
    }

    @Test
    public void createUuid_maxNumTriesNull_throwIllegalArgumentException() {
        /* ARRANGE */
        final Function<UUID, Boolean> function = uuid -> false;
        long maxNumTries = 0;

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> UUIDUtils.createUUID(function, maxNumTries));
    }
}
