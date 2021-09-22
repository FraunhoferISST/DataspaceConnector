package io.dataspaceconnector.common.util;

import java.net.URL;

import io.dataspaceconnector.config.BasePath;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Helper class for managing route references.
 */
@Component
@RequiredArgsConstructor
public final class RouteReferenceHelper {

    /**
     * The default HTTP base URL of the application.
     */
    @Value("${application.http.base-url}")
    private String defaultBaseUrl;

    /**
     * Checks whether a URL is a route reference by checking whether the URL starts with the path
     * to this connector's routes API. The full path to the routes API consists of the application's
     * base URL and the path segment for the routes API. When communicating over IDSCP2, no request
     * context is available to get the base URL from, so then a default value is used.
     *
     * @param url The URL to check.
     * @return True, if the URL is a route reference; false otherwise.
     */
    public boolean isRouteReference(final URL url) {
        String applicationBaseUrl;
        try {
            applicationBaseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        } catch (IllegalStateException e) {
            //communicating via IDSCPv2, no current request context available
            applicationBaseUrl = defaultBaseUrl;
        }

        final var routesApiUrl = applicationBaseUrl + BasePath.ROUTES;
        return url.toString().startsWith(routesApiUrl);
    }

}
