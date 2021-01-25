package de.fraunhofer.isst.dataspaceconnector.services.utils;

import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.util.ClientProvider;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.http.HttpHeaders;
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
 * This class builds up HTTP or HTTPS endpoint connections and sends GET requests.
 */
@Service
public class HttpUtils {

    private final ClientProvider clientProvider;

    /**
     * Constructor for HttpUtils.
     *
     * @throws IllegalArgumentException if any of the parameters is null.
     * @throws GeneralSecurityException if the framework has an error.
     */
    @Autowired
    public HttpUtils(ConfigurationContainer configurationContainer)
        throws IllegalArgumentException, GeneralSecurityException {
        if (configurationContainer == null) {
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null");
        }

        this.clientProvider = new ClientProvider(configurationContainer);
    }

    /**
     * Sends a GET request to an external HTTP endpoint
     *
     * @param address the URL.
     * @return the HTTP response if HTTP code is OK (200).
     * @throws MalformedURLException if the input address is not a valid URL.
     * @throws RuntimeException if an error occurred when connecting or processing the HTTP
     *                               request.
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
     * Sends a GET request to an external HTTPS endpoint
     *
     * @param address the URL.
     * @return the HTTP body of the response when HTTP code is OK (200).
     * @throws MalformedURLException if the input address is not a valid URL.
     * @throws RuntimeException if an error occurred when connecting or processing the HTTP
     *                               request.
     */
    public String sendHttpsGetRequest(String address)
        throws MalformedURLException, RuntimeException {
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
     * Sends a GET request with basic authentication to an external HTTPS endpoint.
     *
     * @param address the URL.
     * @param username The username.
     * @param password The password.
     * @return The HTTP response when HTTP code is OK (200).
     * @throws MalformedURLException if the input address is not a valid URL.
     * @throws RuntimeException if an error occurred when connecting or processing the HTTP
     *                               request.
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
