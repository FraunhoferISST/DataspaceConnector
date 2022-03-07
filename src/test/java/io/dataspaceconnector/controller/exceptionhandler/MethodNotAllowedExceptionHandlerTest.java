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
package io.dataspaceconnector.controller.exceptionhandler;

import io.dataspaceconnector.controller.resource.base.exceptionhandler.MethodNotAllowedExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MethodNotAllowedExceptionHandlerTest {

    @Test
    public void handlePolicyRestrictionException_nothing_returnMethodNotAllowed() {
        /* ARRANGE */
        final var handler = new MethodNotAllowedExceptionHandler();

        /* ACT && ASSERT */
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, handler.handlePolicyRestrictionException().getStatusCode());
    }
}
