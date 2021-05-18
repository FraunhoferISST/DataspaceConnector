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
package io.dataspaceconnector.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import io.dataspaceconnector.model.QueryInput;
import kotlin.Pair;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {HttpService.class})
class HttpServiceTest {

    @MockBean
    de.fraunhofer.isst.ids.framework.communication.http.HttpService httpSvc;

    @Autowired
    HttpService service;

    @Test
    public void toArgs_null_emptyArgs() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = service.toArgs(null);

        /* ASSERT */
        assertEquals(new HttpService.HttpArgs(), result);
    }

    @Test
    public void toArgs_inputSetFields_ArgsSetFields() {
        /* ARRANGE */
        final var params = Map.of("A", "AV", "B", "BV");
        final var headers = Map.of("C", "CV", "D", "DV");

        final var input = new QueryInput();
        input.setParams(params);
        input.setHeaders(headers);

        final var expected = new HttpService.HttpArgs();
        expected.setParams(params);
        expected.setHeaders(headers);

        /* ACT */
        final var result = service.toArgs(input);

        /* ASSERT */
        assertEquals(expected, result);
    }

    @Test
    public void toArgs_nullAuth_emptyArgs() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = service.toArgs(null, null);

        /* ASSERT */
        assertEquals(new HttpService.HttpArgs(), result);
    }

    @Test
    public void toArgs_inputSetAuth_ArgsSetAuthField() {
        /* ARRANGE */
        final var params = Map.of("A", "AV", "B", "BV");
        final var headers = Map.of("C", "CV", "D", "DV");
        final var auth = new Pair<String, String>("X", "Y");

        final var input = new QueryInput();
        input.setParams(params);
        input.setHeaders(headers);

        final var expected = new HttpService.HttpArgs();
        expected.setParams(params);
        expected.setHeaders(headers);
        expected.setAuth(auth);

        /* ACT */
        final var result = service.toArgs(input, auth);

        /* ASSERT */
        assertEquals(expected, result);
    }

    @Test
    public void request_get_equalsToGetMethod() throws IOException, URISyntaxException {
        /* ARRANGE */
        final var target = new URL("https://someTarget");
        final var args = new HttpService.HttpArgs();

        final var response =
                new Response.Builder().request(new Request.Builder().url(target).build()).protocol(
                        Protocol.HTTP_1_1).code(200).message("Some message")
                                      .body(ResponseBody.create(
                                              "someBody", MediaType.parse("application/text")))
                                      .build();

        Mockito.doReturn(response).when(httpSvc).get(Mockito.any());

        /* ACT */
        final var result = service.request(HttpService.Method.GET, target, args);

        /* ASSERT */
        final var expected = service.get(target, args);
        assertEquals(expected.getCode(), result.getCode());
        assertTrue(Arrays.areEqual("someBody".getBytes(StandardCharsets.UTF_8), result.getBody().readAllBytes()));
    }

    @Test
    public void request_null_throwRuntimeException() throws IOException {
        /* ARRANGE */
        final var target = new URL("https://someTarget");
        final var args = new HttpService.HttpArgs();

        /* ACT && ASSERT */
        assertThrows(RuntimeException.class, () -> service.request(null, target, args));
    }

    @Test
    public void get_nullTarget_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.get(null, new HttpService.HttpArgs()));
    }

    @Test
    public void get_nullArgs_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.get(new URL("https://someWhere"), (HttpService.HttpArgs)null));
    }

    @Test
    public void get_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.get(null, (HttpService.HttpArgs) null));
    }
}
