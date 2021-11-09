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
package io.dataspaceconnector.common.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.dataspaceconnector.common.exception.NotImplemented;
import io.dataspaceconnector.model.auth.Authentication;
import io.dataspaceconnector.model.auth.BasicAuth;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpServiceTest {

    private de.fraunhofer.ids.messaging.protocol.http.HttpService httpSvc = Mockito
            .mock(de.fraunhofer.ids.messaging.protocol.http.HttpService.class);

    private HttpService service = new HttpService(httpSvc);

    private Response response = Mockito.mock(Response.class);

    private ResponseBody responseBody = mock(ResponseBody.class);

    @Test
    void toArgs_null_emptyArgs() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = service.toArgs(null);

        /* ASSERT */
        assertEquals(new HttpService.HttpArgs(), result);
    }

    @Test
    void toArgs_inputSetFields_ArgsSetFields() {
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
    void toArgs_nullAuth_emptyArgs() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = service.toArgs(null, null);

        /* ASSERT */
        assertEquals(new HttpService.HttpArgs(), result);
    }

    @Test
    void toArgs_inputSetAuth_ArgsSetAuthField() {
        /* ARRANGE */
        final var params = Map.of("A", "AV", "B", "BV");
        final var headers = Map.of("C", "CV", "D", "DV");
        final var authType = new BasicAuth("X", "Y");
        final var auth = new ArrayList<Authentication>();

        auth.add(authType);

        final var input = new QueryInput();
        input.setParams(params);
        input.setHeaders(headers);

        final var expected = new HttpService.HttpArgs();
        expected.setParams(params);
        expected.setHeaders(headers);
        authType.setAuth(expected);

        /* ACT */
        final var result = service.toArgs(input, auth);

        /* ASSERT */
        assertEquals(expected, result);
    }

    @Test
    void request_get_equalsToGetMethod() throws IOException {
        /* ARRANGE */
        final var target = new URL("https://someTarget");
        final var args = new HttpService.HttpArgs();

        final var response = new Response.Builder()
                .request(new Request.Builder().url(target).build())
                .protocol(Protocol.HTTP_1_1).code(200).message("Some message")
                .body(ResponseBody.create("someBody", MediaType.parse("application/text")))
                .build();

        // The first response will be consumed by the first request, duplicate
        final var response2 = new Response.Builder()
                .request(new Request.Builder().url(target).build())
                .protocol(Protocol.HTTP_1_1).code(200).message("Some message")
                .body(ResponseBody.create("someBody", MediaType.parse("application/text")))
                .build();

        Mockito.doReturn(response).when(httpSvc).get(any());

        /* ACT */
        final var result = (HttpResponse) service.request(HttpService.Method.GET, target, args);

        /* ASSERT */
        Mockito.doReturn(response2).when(httpSvc).get(any());
        final var expected = (HttpResponse) service.get(target, args);
        assertEquals(expected.getCode(), result.getCode());
        assertTrue(Arrays.areEqual("someBody".getBytes(StandardCharsets.UTF_8),
                result.getData().readAllBytes()));
    }

    @Test
    void request_null_throwNotImplemented() throws IOException {
        /* ARRANGE */
        final var target = new URL("https://someTarget");
        final var args = new HttpService.HttpArgs();

        /* ACT && ASSERT */
        assertThrows(NotImplemented.class, () -> service.request(null, target, args));
    }

    @Test
    void get_nullTarget_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> service.get(null, new HttpService.HttpArgs()));
    }

    @Test
    void get_nullArgs_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> service.get(new URL("https://someWhere"), (HttpService.HttpArgs) null));
    }

    @Test
    void get_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.get(null,
                (HttpService.HttpArgs) null));
    }

    @Test
    @SneakyThrows
    void post_withParamsAndHeaders_makePostRequest() {
        /* ARRANGE */
        final var target = new URL("https://target");
        final var args = new HttpService.HttpArgs();
        args.setParams(new HashMap<>() {{
            put("key", "value");
        }});
        args.setHeaders(new HashMap<>() {{
            put("key", "value");
        }});
        final var data = new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8));

        final var responseCode = 200;
        final var bytes = "response".getBytes(StandardCharsets.UTF_8);

        when(httpSvc.send(any())).thenReturn(response);
        when(response.code()).thenReturn(200);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.bytes()).thenReturn(bytes);

        /* ACT */
        final var result = (HttpResponse) service.post(target, args, data);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(responseCode, result.getCode());
        assertArrayEquals(bytes, result.getData().readAllBytes());
    }
}
