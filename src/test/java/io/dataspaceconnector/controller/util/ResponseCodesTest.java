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
package io.dataspaceconnector.controller.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResponseCodeTest {

    @Test
    public void constructor_is_hidden() {
        assertThrows(UnsupportedOperationException.class, ResponseCode::new);
    }

    @Test
    public void ok_is_code_200() {
        assertEquals("200", ResponseCode.OK);
    }

    @Test
    public void created_is_code_201() {
        assertEquals("201", ResponseCode.CREATED);
    }

    @Test
    public void no_content_is_code_204() {
        assertEquals("204", ResponseCode.NO_CONTENT);
    }

    @Test
    public void no_content_is_code_401() {
        assertEquals("401", ResponseCode.UNAUTHORIZED);
    }

    @Test
    public void method_not_allowed_is_code_405() {
        assertEquals("405", ResponseCode.METHOD_NOT_ALLOWED);
    }
}
