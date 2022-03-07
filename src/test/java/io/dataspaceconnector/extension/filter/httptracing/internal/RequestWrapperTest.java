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
package io.dataspaceconnector.extension.filter.httptracing.internal;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestWrapperTest {

    @Test
    public void getRequestBody_copyBody_returnContent() throws IOException {
        /* ARRANGE */
        final var request = new MockHttpServletRequest();
        request.setContent("HELLO".getBytes(StandardCharsets.UTF_8));
        request.setCharacterEncoding(String.valueOf(Charset.defaultCharset()));

        final var wrapper = new RequestWrapper(request);

        /* ACT */
        final var result = wrapper.getRequestBody();

        /* ASSERT */
        assertTrue(Arrays.equals("HELLO".getBytes(StandardCharsets.UTF_8), result));
    }

    @Test
    public void getInputStream_copyBody_returnContent() throws IOException {
        /* ARRANGE */
        final var request = new MockHttpServletRequest();
        request.setContent("HELLO".getBytes(StandardCharsets.UTF_8));
        request.setCharacterEncoding(String.valueOf(Charset.defaultCharset()));
        final var wrapper = new RequestWrapper(request);

        /* ACT */
        final var result = wrapper.getInputStream();

        /* ASSERT */
        assertTrue(Arrays.equals("HELLO".getBytes(StandardCharsets.UTF_8), result.readAllBytes()));
        assertTrue(result.isFinished());
        assertTrue(result.isReady());
    }

    @Test
    public void getReader_copyBody_returnContent() throws IOException {
        /* ARRANGE */
        final var request = new MockHttpServletRequest();
        request.setContent("HELLO".getBytes(StandardCharsets.UTF_8));
        request.setCharacterEncoding(String.valueOf(Charset.defaultCharset()));
        final var wrapper = new RequestWrapper(request);
        wrapper.getRequestBody();

        final var reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
                "HELLO".getBytes(StandardCharsets.UTF_8))));

        /* ACT && ASSERT */
        final var result = wrapper.getReader();

        while (true) {
            final var x = result.read();
            final var y = reader.read();
            assertEquals(x, y);
            if (x == -1 || y == -1) {
                break;
            }
        }
    }
}
