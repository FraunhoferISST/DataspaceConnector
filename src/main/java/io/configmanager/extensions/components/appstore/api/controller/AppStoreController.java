package io.configmanager.extensions.components.appstore.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.configmanager.extensions.components.appstore.api.AppStoreApi;
import io.configmanager.extensions.components.appstore.api.service.AppStoreService;
import io.configmanager.util.json.JsonUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;

/**
 * The api class implements the AppStoreApi and offers the possibilities to manage
 * the app stores in the configuration manager.
 */
@Slf4j
@RestController
@RequestMapping("/api/ui")
@Tag(name = "Extension: Component App Store")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AppStoreController implements AppStoreApi {

    transient AppStoreService appStoreService;
    transient ObjectMapper objectMapper;

    /**
     * This method creates an app store with the given parameters.
     *
     * @param accessUrl uri of the app store
     * @param title     title of the app store
     * @return a suitable http response
     */
    @Override
    public ResponseEntity<String> createAppStore(URI accessUrl, String title) {
        if (log.isInfoEnabled()) {
            log.info(">> POST /appstore accessUrl: " + accessUrl + " title: " + title);
        }

        appStoreService.createAppStore(accessUrl, title);
        return ResponseEntity.ok(JsonUtils.jsonMessage("message", "Created a new app store with accessUrl: " + accessUrl));
    }

    /**
     * This method updates the app store with the given parameters.
     *
     * @param accessUrl of the app store
     * @param title     title of the app store
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> updateAppStore(URI accessUrl, String title) {
        if (log.isInfoEnabled()) {
            log.info(">> PUT /appstore accessUrl: " + accessUrl + " title: " + title);
        }
        ResponseEntity<String> response;

        if (appStoreService.updateAppStore(accessUrl, title)) {
            final var jsonObject = new JSONObject();
            jsonObject.put("message", "Updated the app store ");
            jsonObject.put("accessUrl", accessUrl.toString());
            response = ResponseEntity.ok(jsonObject.toJSONString());
        } else {
            response = ResponseEntity.badRequest().body("Could not update the given app store");
        }

        return response;
    }


    /**
     * This method deletes the app store with the given accessUrl.
     *
     * @param accessUrl of the app store
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> deleteAppStore(URI accessUrl) {
        if (log.isInfoEnabled()) {
            log.info(">> DELETE /appstore accessUrl " + accessUrl);
        }
        ResponseEntity<String> response;

        if (appStoreService.deleteAppStore(accessUrl)) {
            response = ResponseEntity.ok(JsonUtils.jsonMessage("message", "App Store with accessUrl: " + accessUrl + " is deleted"));
        } else {
            response = ResponseEntity.badRequest().body("Could not delete the app store with the accessUrl:" + accessUrl);
        }

        return response;
    }

    /**
     * This method returns a list of all app stores as string.
     *
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> getAllAppStores() {
        if (log.isInfoEnabled()) {
            log.info(">> GET /appstores");
        }
        ResponseEntity<String> response;

        final var appStores = appStoreService.getAppStores();

        try {
            response = new ResponseEntity<>(objectMapper.writeValueAsString(appStores), HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}
