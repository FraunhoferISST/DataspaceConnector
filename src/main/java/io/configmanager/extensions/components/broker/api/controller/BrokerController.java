/*
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
package io.configmanager.extensions.components.broker.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.configmanager.extensions.components.broker.api.BrokerApi;
import io.configmanager.extensions.components.broker.api.service.BrokerService;
import io.configmanager.util.json.JsonUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
 * The api class implements the BrokerAPI and offers the possibilities to manage
 * the brokers in the configuration manager.
 */
@Slf4j
@RestController
@RequestMapping("/api/ui")
@Tag(name = "Extension: Component Metadata Broker")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BrokerController implements BrokerApi {
    /**
     * The BrokerService.
     */
    private final BrokerService brokerService;

    /**
     * Objectmapper used to write values as strings.
     */
    private final ObjectMapper objectMapper;

    /**
     * This method creates a broker with the given parameters.
     *
     * @param brokerUri uri of the broker
     * @param title     title of the broker
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> createBroker(final URI brokerUri, final String title) {
        brokerService.createBroker(brokerUri, title);
        return ResponseEntity.ok(
                JsonUtils.jsonMessage("message",
                        "Created a new broker with id: " + brokerUri));
    }

    /**
     * This method updates the broker with the given parameters.
     *
     * @param brokerId id of the broker
     * @param title    title of the broker
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> updateBroker(final URI brokerId, final String title) {
        ResponseEntity<String> response;

        if (brokerService.updateBroker(brokerId, title)) {
            final var jsonObject = new JSONObject();
            jsonObject.put("message", "Updated the broker");
            jsonObject.put("brokerId", brokerId.toString());
            response = ResponseEntity.ok(jsonObject.toJSONString());
        } else {
            response = ResponseEntity.badRequest().body("Could not update the broker");
        }

        return response;
    }

    /**
     * This method deletes the broker with the given id.
     *
     * @param brokerUri uri of the broker
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> deleteBroker(final URI brokerUri) {
        ResponseEntity<String> response;

        if (brokerService.deleteBroker(brokerUri)) {
            response = ResponseEntity.ok(
                    JsonUtils.jsonMessage("message",
                            "Broker with ID: " + brokerUri + " is deleted"));
        } else {
            response = ResponseEntity
                    .badRequest()
                    .body("Could not delete the broker with the id:" + brokerUri);
        }

        return response;
    }

    /**
     * This method returns a list of all brokers as string.
     *
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> getAllBrokers() {
        ResponseEntity<String> response;

        final var brokers = brokerService.getBrokers();

        try {
            response = new ResponseEntity<>(
                    objectMapper.writeValueAsString(brokers),
                    HttpStatus.OK);

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return response;
    }
}
