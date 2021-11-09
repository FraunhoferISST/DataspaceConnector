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

import io.dataspaceconnector.common.routing.dataretrieval.DataRetrievalService;
import io.dataspaceconnector.common.routing.dataretrieval.Response;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.NotImplemented;
import io.dataspaceconnector.common.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class builds up http or httpS endpoint connections and sends GET requests.
 */
@Service
@RequiredArgsConstructor
public class HttpService implements DataRetrievalService {

    /**
     * Service for building and sending http requests.
     */
    private final @NonNull de.fraunhofer.ids.messaging.protocol.http.HttpService httpSvc;

    /**
     * The request method.
     */
    public enum Method {
        /**
         * http GET.
         */
        GET,
        //        OPTIONS,
        //        HEAD,
        //        POST,
        //        PUT,
        //        PATCH,
        //        DELETE
    }


    /**
     * Pair of strings.
     */
    @Data
    @AllArgsConstructor
    public static class Pair {

        /**
         * First element.
         */
        private String first;
        /**
         * Second element.
         */
        private String second;
    }

    /**
     * The http request arguments.
     */
    @Data
    public static class HttpArgs {

        /**
         * The request headers.
         */
        private Map<String, String> headers;

        /**
         * The request parameters.
         */
        private Map<String, String> params;

        /**
         * Authentication information. Will overwrite entry in headers.
         */
        private Pair auth;
    }

    /**
     * Send post requests using the http service of the messaging services.
     *
     * @param target The target url.
     * @param args   Request arguments.
     * @param data   The data that should be sent.
     * @return The response.
     * @throws IOException if the request failed.
     */
    public Response post(final URL target, final HttpArgs args, final InputStream data)
            throws IOException {
        Utils.requireNonNull(target, ErrorMessage.URI_NULL);
        Utils.requireNonNull(args, ErrorMessage.HTTP_ARGS_NULL);

        final var urlBuilder = createUrlBuilder(target);

        if (args.getParams() != null) {
            for (final var key : args.getParams().keySet()) {
                urlBuilder.addQueryParameter(key, args.getParams().get(key));
            }
        }

        final var targetUrl = urlBuilder.build();

        final var body = RequestBody.create(data.readAllBytes(),
                MediaType.get("application/octet-stream"));

        final var requestBuilder = new Request.Builder().url(targetUrl).post(body);

        if (args.getHeaders() != null && !args.getHeaders().isEmpty()) {
            args.getHeaders().forEach(requestBuilder::header);
        }

        final var response = httpSvc.send(requestBuilder.build());

        final var output = new HttpResponse(response.code(), getBody(response));
        response.close();

        return output;
    }

    /**
     * Perform a get request.
     *
     * @param target The recipient of the request.
     * @param args   The request arguments.
     * @return The response.
     * @throws IOException              if the request failed.
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    public Response get(final URL target, final HttpArgs args) throws IOException {
        Utils.requireNonNull(target, ErrorMessage.URI_NULL);
        Utils.requireNonNull(args, ErrorMessage.HTTP_ARGS_NULL);

        final var urlBuilder = createUrlBuilder(target);

        if (args.getParams() != null) {
            for (final var key : args.getParams().keySet()) {
                urlBuilder.addQueryParameter(key, args.getParams().get(key));
            }
        }

        final var targetUri = urlBuilder.build().uri();

        okhttp3.Response response;
        if (args.getHeaders() == null && args.getAuth() == null) {
            response = httpSvc.get(targetUri);
        } else {
            /*
                Make a copy of the headers and insert sensitive data only into the copy.
             */
            final var headerCopy = args.getHeaders() == null
                    ? new HashMap<String, String>() : new HashMap<>(args.getHeaders());
            if (args.getAuth() != null) {
                headerCopy.put(args.getAuth().getFirst(), args.getAuth().getSecond());
            }

            response = httpSvc.getWithHeaders(targetUri, headerCopy);
        }

        final var output = new HttpResponse(response.code(), getBody(response));
        response.close();

        return output;
    }

    private InputStream getBody(final okhttp3.Response response) throws IOException {
        final var body = response.body();
        if (body != null) {
            final var tmp = body.bytes();
            body.close();
            return new ByteArrayInputStream(tmp);
        }

        return InputStream.nullInputStream();
    }

    /**
     * Perform a get request.
     *
     * @param target The recipient of the request.
     * @param input  The query inputs.
     * @return The response.
     * @throws IOException if the request failed.
     */
    @Override
    public Response get(final URL target, final QueryInput input) throws IOException {
        final var url = (input == null) ? buildTargetUrl(target, null)
                : buildTargetUrl(target, input.getOptional());
        return this.get(url, toArgs(input));
    }

    /**
     * Perform a get request.
     *
     * @param target The recipient of the request.
     * @param input  The query inputs.
     * @param auth   The authentication information.
     * @return The response.
     * @throws IOException if the request failed.
     */
    @Override
    public Response get(final URL target, final QueryInput input,
                        final List<? extends HttpAuthentication> auth) throws IOException {
        final var url = (input == null) ? buildTargetUrl(target, null)
                : buildTargetUrl(target, input.getOptional());
        return this.get(url, toArgs(input, auth));
    }

    private URL buildTargetUrl(final URL target, final String optional) {
        final var urlBuilder = createUrlBuilder(target);
        if (optional != null) {
            urlBuilder.addPathSegments(optional.startsWith("/") ? optional.substring(1) : optional);
        }

        return urlBuilder.build().url();
    }

    private HttpUrl.Builder createUrlBuilder(final URL target) {
        return toUrl(target).newBuilder();
    }

    private HttpUrl toUrl(final URL target) {
        final var url = HttpUrl.get(target);
        if (url == null) {
            throw new IllegalArgumentException();
        }

        return url;
    }

    /**
     * Perform a http request.
     *
     * @param method The request method.
     * @param target The recipient of the request.
     * @param args   The request arguments.
     * @return The response.
     * @throws IOException if the request failed.
     */
    public Response request(final Method method, final URL target, final HttpArgs args)
            throws IOException {
        if (method == Method.GET) {
            return get(target, args);
        }

        throw new NotImplemented();
    }

    /**
     * Create http request parameters from query.
     *
     * @param input The query inputs.
     * @return The Http request arguments.
     */
    public HttpArgs toArgs(final QueryInput input) {
        final var args = new HttpArgs();
        if (input != null) {
            args.setParams(input.getParams());
            args.setHeaders(input.getHeaders());
        }

        return args;
    }

    /**
     * Create http request parameters from query inputs and authentication information.
     *
     * @param input The query inputs.
     * @param auth  The authentication information.
     * @return The http request arguments.
     */
    public HttpArgs toArgs(final QueryInput input, final List<? extends HttpAuthentication> auth) {
        final var args = toArgs(input);
        if (auth != null) {
            for (final var x : auth) {
                x.setAuth(args);
            }
        }
        return args;
    }
}
