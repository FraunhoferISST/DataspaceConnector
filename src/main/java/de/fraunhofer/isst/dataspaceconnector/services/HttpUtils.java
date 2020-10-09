package de.fraunhofer.isst.dataspaceconnector.services;

import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.util.ClientProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * This class builds up http or https endpoint connections.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class HttpUtils {

    private ClientProvider clientProvider;

    @Autowired
    /**
     * <p>Constructor for HttpUtils.</p>
     *
     * @param configurationModel a {@link de.fraunhofer.iais.eis.ConfigurationModel} object.
     * @param keyStoreManager a {@link de.fraunhofer.isst.ids.framework.util.KeyStoreManager} object.
     */
    public HttpUtils(ConfigurationContainer configurationContainer) throws NoSuchAlgorithmException, KeyManagementException {
        this.clientProvider = new ClientProvider(configurationContainer);
    }

    /**
     * Sends a get request to an external http endpoint.
     *
     * @param address The url.
     * @return The http response.
     * @throws java.io.IOException if any.
     */
    public String sendHttpGetRequest(String address) throws IOException {
        URL url = new URL(address);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int status = con.getResponseCode();
        if (status == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            return content.toString();
        } else {
            return null;
        }
    }

    /**
     * Sends a post request to an external http endpoint.
     *
     * @param endpoint The requested url.
     * @return Response as string.
     * @param input an array of {@link byte} objects.
     * @throws java.io.IOException if any.
     */
    public int sendHttpPostRequest(String endpoint, byte[] input) throws IOException {
        URL url = new URL(endpoint);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();
        con.disconnect();

        return responseCode;
    }

    /**
     * <p>sendHttpGetRequestWithBasicAuth.</p>
     *
     * @param address a {@link java.lang.String} object.
     * @param username a {@link java.lang.String} object.
     * @param password a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String sendHttpGetRequestWithBasicAuth(String address, String username, String password) {
        return null;
    }

    /**
     * <p>sendHttpsGetRequest.</p>
     *
     * @param address a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     * @throws java.security.KeyManagementException if any.
     * @throws java.security.NoSuchAlgorithmException if any.
     */
    public String sendHttpsGetRequest(String address) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        Request request = new Request.Builder()
                .url(address)
                .get()
                .build();

        OkHttpClient client = clientProvider.getClient();
        Response response = client.newCall(request).execute();

        if (response.code() < 200 || response.code() >= 300) {
            response.close();
            throw new IOException("Not OK");
        } else {
            String rawResponseString = new String(response.body().byteStream().readAllBytes());
            response.close();

            return rawResponseString;
        }
    }

    /**
     * Sends a get request with basic authentication to an external https endpoint.
     *
     * @param address The url.
     * @param username The username.
     * @param password The password.
     * @return The http response.
     * @throws java.io.IOException if any.
     * @throws java.security.KeyManagementException if any.
     * @throws java.security.NoSuchAlgorithmException if any.
     */
    public String sendHttpsGetRequestWithBasicAuth(String address, String username, String password) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(StandardCharsets.ISO_8859_1));
        String authHeader = "Basic " + new String(encodedAuth);

        Request request = new Request.Builder()
                .url(address)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .get()
                .build();

        OkHttpClient client = clientProvider.getClient();
        Response response = client.newCall(request).execute();

        if (response.code() < 200 || response.code() >= 300) {
            response.close();
            throw new IOException("Not OK");
        } else {
            String rawResponseString = new String(response.body().byteStream().readAllBytes());
            response.close();

            return rawResponseString;
        }
    }
}
