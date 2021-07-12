/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.controller.resource.swagger.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResponseCodesTest {

    @Test
    public void ok_is_code_200() {
        assertEquals("200", ResponseCodes.OK);
    }

    @Test
    public void created_is_code_201() {
        assertEquals("201", ResponseCodes.CREATED);
    }

    @Test
    public void no_content_is_code_204() {
        assertEquals("204", ResponseCodes.NO_CONTENT);
    }

    @Test
    public void method_not_allowed_is_code_405() {
        assertEquals("405", ResponseCodes.METHOD_NOT_ALLOWED);
    }
}
