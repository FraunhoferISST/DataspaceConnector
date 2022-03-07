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
package io.dataspaceconnector.model.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FactoryUtilsTest {

    @Test
    void updateInteger_same_returnOldInt() {
        /* ARRANGE */
        final var oldInt = 1;
        final var newInt = 1;

        /* ACT */
        final var result = FactoryUtils.updateInteger(oldInt, newInt);

        /* ASSERT */
        assertEquals(oldInt, result);
    }

    @Test
    void updateInteger_differentNewInt_returnNewInt() {
        /* ARRANGE */
        final var oldInt = 1;
        final var newInt = 2;

        /* ACT */
        final var result = FactoryUtils.updateInteger(oldInt, newInt);

        /* ASSERT */
        assertEquals(newInt, result);
    }

    @Test
    void updateBoolean_newBooleanNull_returnDefault() {
        /* ACT */
        final var result = FactoryUtils.updateBoolean(true, null, false);

        /* ASSERT */
        assertTrue(result.isPresent());
        assertFalse(result.get());
    }

    @Test
    void updateDate_oldDateNull_ReturnNewDate() {
        /* ARRANGE */
        final var newDate = ZonedDateTime.of(2000, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault());
        final var defaultDate = ZonedDateTime.now();

        /* ACT */
        final var result = FactoryUtils.updateDate(null, newDate, defaultDate);

        /* ASSERT */
        assertTrue(result.isPresent());
        assertEquals(newDate, result.get());
    }

    @Test
    void updateDate_oldAndNewDateNull_ReturnDefaultDate() {
        /* ARRANGE */
        final var defaultDate = ZonedDateTime.now();

        /* ACT */
        final var result = FactoryUtils.updateDate(null, null, defaultDate);

        /* ASSERT */
        assertTrue(result.isPresent());
        assertEquals(defaultDate, result.get());
    }

    @Test
    void removeEmptyStringFromList_emptyStringPresent_emptyStringRemoved() {
        /* ARRANGE */
        final var string1 = "not empty";
        final var string2 = "";
        final var string3 = "also not empty";
        final var list = Arrays.asList(string1, string2, string3);

        /* ACT */
        final var result = FactoryUtils.removeEmptyStringFromList(list);

        /* ASSERT */
        assertEquals(2, result.size());
        assertTrue(result.contains(string1));
        assertFalse(result.contains(string2));
        assertTrue(result.contains(string3));
    }

    @Test
    void removeEmptyStringFromList_emptyStringNotPresent_sameListReturned() {
        /* ARRANGE */
        final var string1 = "not empty";
        final var string2 = "also not empty";
        final var list = Arrays.asList(string1, string2);

        /* ACT */
        final var result = FactoryUtils.removeEmptyStringFromList(list);

        /* ASSERT */
        assertEquals(list, result);
    }

}
