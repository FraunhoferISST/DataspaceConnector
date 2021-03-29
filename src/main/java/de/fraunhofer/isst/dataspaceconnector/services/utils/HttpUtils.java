package de.fraunhofer.isst.dataspaceconnector.services.utils;

import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.ids.framework.communication.http.HttpService;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * This class builds up HTTP or HTTPS endpoint connections and sends GET requests.
 */
@Service
public class HttpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);

    private final HttpService httpService;

    /**
     * Constructor for HttpUtils.
     *
     * @throws IllegalArgumentException if any of the parameters is null.
     */
    @Autowired
    public HttpUtils(final HttpService httpService) throws IllegalArgumentException {
        if (httpService == null) {
            throw new IllegalArgumentException("The HttpService cannot be null.");
        }

        this.httpService = httpService;
    }

    /**
     * Sends a GET request to an external HTTP endpoint
     *
     * @param address the URL.
     * @param queryInput Header and params for data request from backend.
     * @return the HTTP response if HTTP code is OK (200).
     * @throws URISyntaxException if the input address is not a valid URI.
     * @throws RuntimeException if an error occurred when connecting or processing the HTTP
     *                               request.
     */
    public String sendHttpGetRequest(String address, QueryInput queryInput) throws
        RuntimeException, URISyntaxException {
        if(queryInput != null) {
            address = replacePathVariablesInUrl(address, queryInput.getPathVariables());
            address = addQueryParamsToURL(address, queryInput.getParams());
        } else {
            if (address.contains("{")) {
                throw new IllegalArgumentException("Missing path variables.");
            }
        }

        try {
            final var uri = new URI(address);

            Response response;
            if (queryInput != null) {
                response = httpService.getWithHeaders(uri,queryInput.getHeaders());
            } else {
                response = httpService.get(uri);
            }

            final var responseCodeOk = 200;
            final var responseCodeUnauthorized = 401;
            final var responseMalformed = -1;

            final var responseCode = response.code();

            if(responseCode == responseCodeOk){
                return Objects.requireNonNull(response.body()).string();
            } else if (responseCode == responseCodeUnauthorized) {
                // The request is not authorized.
                LOGGER.debug("Could not retrieve data. Unauthorized access. [url=({})]", address);
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
            } else if (responseCode == responseMalformed) {
                // The response code could not be read.
                LOGGER.debug("Could not retrieve data. Expectation failed. [url=({})]", address);
                throw new HttpClientErrorException(HttpStatus.EXPECTATION_FAILED);
            } else {
                // This function should never be thrown.
                LOGGER.warn("Could not retrieve data. Something else went wrong. [url=({})]", address);
                throw new NotImplementedException("Unsupported return value " +
                        "from getResponseCode.");
            }
        } catch (IOException exception) {
            // Catch all the HTTP, IOExceptions.
            LOGGER.warn("Failed to send the http get request. [url=({})]", address);
            throw new RuntimeException("Failed to send the http get request.", exception);
        }
    }

    /**
     * Sends a GET request to an external HTTPS endpoint
     *
     * @param address the URL.
     * @param queryInput Header and params for data request from backend.
     * @return the HTTP body of the response when HTTP code is OK (200).
     * @throws URISyntaxException if the input address is not a valid URI.
     * @throws RuntimeException if an error occurred when connecting or processing the HTTP
     *                               request.
     */
    public String sendHttpsGetRequest(String address, QueryInput queryInput)
            throws URISyntaxException, RuntimeException {
        return sendHttpGetRequest(address, queryInput);

    }

    /**
     * Sends a GET request with basic authentication to an external HTTPS endpoint.
     *
     * @param address the URL.
     * @param username The username.
     * @param password The password.
     * @param queryInput Header and params for data request from backend.
     * @return The HTTP response when HTTP code is OK (200).
     * @throws URISyntaxException if the input address is not a valid URI.
     * @throws RuntimeException if an error occurred when connecting or processing the HTTP
     *                               request.
     */
    public String sendHttpsGetRequestWithBasicAuth(String address, String username,
        String password, QueryInput queryInput) throws URISyntaxException, RuntimeException {
        final var auth = username + ":" + password;
        final var encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        final var authHeader = "Basic " + new String(encodedAuth);

        if (queryInput != null) {
            address = replacePathVariablesInUrl(address, queryInput.getPathVariables());
            address = addQueryParamsToURL(address, queryInput.getParams());
        } else {
            if (address.contains("{")) {
                throw new IllegalArgumentException("Missing path variables.");
            }
        }

        try {
            final var uri = new URI(address);
            Response response;
            if (queryInput != null && queryInput.getHeaders() != null) {
                queryInput.getHeaders().put(HttpHeaders.AUTHORIZATION, authHeader);
                response = httpService.getWithHeaders(uri,queryInput.getHeaders());
            } else {
                QueryInput queryInput_temp = new QueryInput();
                queryInput_temp.getHeaders().put(HttpHeaders.AUTHORIZATION, authHeader);
                response = httpService.getWithHeaders(uri,queryInput_temp.getHeaders());
            }

            if (response.code() < 200 || response.code() >= 300) {
                response.close();
                // Not the expected response code.
                LOGGER.debug("Could not retrieve data. Expectation failed. [url=({})]", address);
                throw new HttpClientErrorException(HttpStatus.EXPECTATION_FAILED);
            } else {
                return Objects.requireNonNull(response.body()).string();
            }
        } catch (IOException exception) {
            // Catch all the HTTP, IOExceptions.
            LOGGER.warn("Failed to send the http get request. [url=({})]", address);
            throw new RuntimeException("Failed to send the http get request.", exception);
        }
    }

    /**
     * Replaces all parts of a given URL that are marked as path variables, if any, using the values
     * supplied in the path variables map.
     *
     * @param address the URL possibly containing path variables
     * @param pathVariables map containing the values for the path variables by name
     * @return the URL with path variables substituted
     */
    private String replacePathVariablesInUrl(String address, Map<String, String> pathVariables)
            throws IllegalArgumentException {
        if (pathVariables != null) {
            long pathVariableCount = address.chars().filter(ch -> ch == '{').count();
            if (pathVariableCount != pathVariables.size()) {
                throw new IllegalArgumentException("The number of supplied path variables does not " +
                        "match the number of path variables in the URL.");
            }

            // http://localhost:8080/{path}/{id}
            for (int i = 1; i <= pathVariableCount; i++) {
                String pathVariableName = address.substring(address.indexOf("{") + 1,
                        address.indexOf("}"));

                String pathVariableValue = pathVariables.get(pathVariableName); // resource
                if (pathVariableValue == null) {
                    throw new IllegalArgumentException("No value found for path variable with" +
                            " name '" + pathVariableName + "'.");
                }

                // Should always be first index of braces because all prior should have been replaced.
                address = address.substring(0, address.indexOf("{")) // http://localhost:8080/
                        + pathVariableValue // resource
                        + address.substring(address.indexOf("}") + 1); // /{id}
            }
        }
        return address;
    }

    /**
     * Enrich the URL address with given query parameters. If the query parameters are empty, the
     * address remains unchanged.
     *
     * @param address URL address to be enriched.
     * @param queryParams Query parameters that have to be added on the address.
     * @return Address string.
     */
    private String addQueryParamsToURL(String address, Map<String, String> queryParams) {
        if(queryParams != null) {
            if(!queryParams.isEmpty()) {
                address = address.concat("?");
                for (Map.Entry<String, String> param : queryParams.entrySet()) {
                    address = address.concat(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8)
                                    + "=" + URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8) + "&");
                }
                return StringUtils.removeEnd(address,"&");
            }
        }
        return address;
    }
}
