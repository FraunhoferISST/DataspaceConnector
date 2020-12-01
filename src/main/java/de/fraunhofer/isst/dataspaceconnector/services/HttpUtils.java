package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.util.ClientProvider;
import io.jsonwebtoken.lang.Assert;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.http.HttpHeaders;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * This class builds up http or https endpoint connections.
 *
 * @version $Id: $Id
 */
@Service
public class HttpUtils {
    private final ClientProvider clientProvider;

    /**
     * <p>Constructor for HttpUtils.</p>
     *
     * @param configurationContainer a {@link ConfigurationContainer} object.
     */
    @Autowired
    public HttpUtils(@NotNull ConfigurationContainer configurationContainer) throws IllegalArgumentException, GeneralSecurityException {
        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null");

        this.clientProvider = new ClientProvider(configurationContainer);

        Assert.notNull(clientProvider, "The clientProvider cannot be null.");
    }

    /**
     * Sends a get request to an external http endpoint.
     *
     * @param address The url.
     * @return The http response when http code is ok (200).
     * @throws MalformedURLException - if the input address is not a valid url.
     * @throws RuntimeException - if an error occurred when connecting or processing the http
     * request.
     */
    public String sendHttpGetRequest(String address) throws MalformedURLException,
            RuntimeException {
        try {
            final var url = new URL(address);

            var con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            final var responseCodeOk = 200;
            final var responseCodeUnauthorized = 401;
            final var responseMalformed = -1;

            final var responseCode = con.getResponseCode();

            if (responseCode == responseCodeOk) {
                // Request was ok, read the response
                try (var in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    var content = new StringBuilder();
                    var inputLine = "";
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }

                    return content.toString();
                }
            } else if (responseCode == responseCodeUnauthorized) {
                // The request is not authorized
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
            } else if (responseCode == responseMalformed) {
                // The response code could not be read
                throw new HttpClientErrorException(HttpStatus.EXPECTATION_FAILED);
            } else {
                // This function should never be thrown
                throw new NotImplementedException("Unsupported return value " +
                        "from getResponseCode.");
            }

        } catch (MalformedURLException exception) {
            // The parameter address is not an url.
            throw exception;
        } catch (Exception exception) {
            // Catch all the HTTP, IOExceptions
            throw new RuntimeException("Failed to send the http get request.", exception);
        }
    }

    /**
     * Sends a post request to an external http endpoint.
     *
     * @param endpointAddress The requested url.
     * @param input           an array of {@link byte} objects.
     * @return The http response code.
     * @throws MalformedURLException - if the input address is not a valid url.
     * @throws RuntimeException - if an error occurred when connecting or processing the http
     * request.
     */
    public int sendHttpPostRequest(String endpointAddress, byte[] input) throws MalformedURLException, RuntimeException {
        try {
            final var endpoint = new URL(endpointAddress);

            var con = (HttpURLConnection) endpoint.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setDoOutput(true);

            try (var os = con.getOutputStream()) {
                os.write(input, 0, input.length);
            }

            final var responseCode = con.getResponseCode();
            con.disconnect();

            return responseCode;

        } catch (MalformedURLException exception) {
            // The parameter address is not an url.
            throw exception;
        } catch (Exception exception) {
            // Catch all the HTTP, IOExceptions
            throw new RuntimeException("Failed to send the http get request.", exception);
        }
    }

    /**
     * <p>sendHttpsGetRequest.</p>
     *
     * @param address a {@link java.lang.String} object.
     * @return The http body of the response when http code is ok (200).
     * @throws MalformedURLException - if the input address is not a valid url.
     * @throws RuntimeException - if an error occurred when connecting or processing the http
     * request.
     */
    public String sendHttpsGetRequest(String address) throws MalformedURLException, RuntimeException {
        try {
            final var request = new Request.Builder().url(address).get().build();

            var client = clientProvider.getClient();
            Response response = client.newCall(request).execute();

            if (response.code() < 200 || response.code() >= 300) {
                response.close();
                // Not the expected response code
                throw new HttpClientErrorException(HttpStatus.EXPECTATION_FAILED);
            } else {
                // Read the response
                final var rawResponseString =
                        new String(response.body().byteStream().readAllBytes());
                response.close();

                return rawResponseString;
            }
        } catch (MalformedURLException exception) {
            // The parameter address is not an url.
            throw exception;
        } catch (Exception exception) {
            // Catch all the HTTP, IOExceptions
            throw new RuntimeException("Failed to send the http get request.", exception);
        }
    }

    /**
     * Sends a get request with basic authentication to an external https endpoint.
     *
     * @param address  The url.
     * @param username The username.
     * @param password The password.
     * @return The http response when http code is ok (200).
     * @throws MalformedURLException - if the input address is not a valid url.
     * @throws RuntimeException - if an error occurred when connecting or processing the http
     * request.
     */
    public String sendHttpsGetRequestWithBasicAuth(String address, String username,
                                                   String password) throws MalformedURLException, RuntimeException {
        final var auth = username + ":" + password;
        final var encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        final var authHeader = "Basic " + new String(encodedAuth);

        try {
            final var request = new Request.Builder().url(address)
                    .header(HttpHeaders.AUTHORIZATION, authHeader).get().build();

            final var client = clientProvider.getClient();
            final var response = client.newCall(request).execute();

            if (response.code() < 200 || response.code() >= 300) {
                response.close();
                // Not the expected response code
                throw new HttpClientErrorException(HttpStatus.EXPECTATION_FAILED);
            } else {
                String rawResponseString = new String(response.body().byteStream().readAllBytes());
                response.close();

                return rawResponseString;
            }
        } catch (MalformedURLException exception) {
            // The parameter address is not an url.
            throw exception;
        } catch (Exception exception) {
            // Catch all the HTTP, IOExceptions
            throw new RuntimeException("Failed to send the http get request.", exception);
        }
    }
}
