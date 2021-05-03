package de.fraunhofer.isst.dataspaceconnector.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import kotlin.Pair;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import org.springframework.stereotype.Service;

/**
 * This class builds up http or httpS endpoint connections and sends GET requests.
 */
@Service
@RequiredArgsConstructor
public class HttpService { // TODO clean up code

    /**
     * Service for building and sending http requests.
     */
    private final @NonNull de.fraunhofer.isst.ids.framework.communication.http.HttpService
            httpSvc;


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
        private Pair<String, String> auth;
    }


    /**
     * The response to a http request.
     */
    @Data
    public static class Response {
        /**
         * The response code.
         */
        private int code;

        /**
         * The response body.
         */
        private InputStream body;
    }

    /**
     * Perform a get request.
     * @param target The recipient of the request.
     * @param args   The request arguments.
     * @return The response.
     * @throws IOException if the request failed.
     */
    public Response get(final URL target, final HttpArgs args) throws IOException {
        final var urlBuilder = HttpUrl.parse(target.toString()).newBuilder();

        if (args.getParams() != null) {
            for (final var key : args.getParams().keySet()) {
                urlBuilder.addQueryParameter(key, args.getParams().get(key));
            }
        }

        final var targetUri = urlBuilder.build().uri();

        okhttp3.Response response;
        if (args.getHeaders() == null) {
            response = httpSvc.get(targetUri);
        } else {
            /*
                Make a copy of the headers and insert sensitive data only into the copy.
             */
            final var headerCopy = Map.copyOf(args.getHeaders());
            if (args.getAuth() != null) {
                headerCopy.put("Authorization",
                        Credentials.basic(args.getAuth().getFirst(), args.getAuth().getSecond()));
            }

            response = httpSvc.getWithHeaders(targetUri, headerCopy);
        }

        final var output = new Response();
        output.setCode(response.code());
        output.setBody(response.body().byteStream());

        return output;
    }

    /**
     * Perform a get request.
     * @param target The recipient of the request.
     * @param input  The query inputs.
     * @return The response.
     * @throws IOException if the request failed.
     */
    public Response get(final URL target, final QueryInput input) throws IOException {
        final var url = buildTargetUrl(target, input.getOptional());
        return this.get(url, toArgs(input));
    }

    /**
     * Perform a get request.
     * @param target The recipient of the request.
     * @param input  The query inputs.
     * @param auth   The authentication information.
     * @return The response.
     * @throws IOException if the request failed.
     */
    public Response get(final URL target, final QueryInput input, final Pair<String, String> auth)
            throws IOException {
        final var url = buildTargetUrl(target, input.getOptional());
        return this.get(url, toArgs(input, auth));
    }

    private URL buildTargetUrl(final URL target, final String optional) {
        final var urlBuilder = HttpUrl.parse(target.toString()).newBuilder();
        if (optional != null) {
            urlBuilder.addEncodedPathSegment(optional);
        }

        return urlBuilder.build().url();
    }

    /**
     * Perform a http request.
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

        throw new RuntimeException("Not implemented.");
    }

    /**
     * Create http request parameters from query.
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
     * Create http request parameters from query inputs and
     * authentication information.
     * @param input The query inputs.
     * @param auth  The authentication information.
     * @return The http request arguments.
     */
    public HttpArgs toArgs(final QueryInput input, final Pair<String, String> auth) {
        final var args = toArgs(input);
        args.setAuth(auth);

        return args;
    }
}
