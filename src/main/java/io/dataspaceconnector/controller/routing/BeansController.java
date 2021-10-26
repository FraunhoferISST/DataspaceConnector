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
package io.dataspaceconnector.controller.routing;

import io.dataspaceconnector.controller.routing.tag.CamelDescription;
import io.dataspaceconnector.controller.routing.tag.CamelName;
import io.dataspaceconnector.controller.util.ResponseCode;
import io.dataspaceconnector.controller.util.ResponseDescription;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

/**
 * Controller for adding and removing beans at runtime.
 */
@Log4j2
@RequiredArgsConstructor
@RestController
@ApiResponses(value = {
        @ApiResponse(responseCode = ResponseCode.OK, description = ResponseDescription.OK),
        @ApiResponse(responseCode = ResponseCode.UNAUTHORIZED,
                description = ResponseDescription.UNAUTHORIZED),
        @ApiResponse(responseCode = ResponseCode.BAD_REQUEST,
                description = ResponseDescription.BAD_REQUEST)})
@RequestMapping("/api/beans")
@Tag(name = CamelName.CAMEL, description = CamelDescription.CAMEL)
public class BeansController {

    /**
     * Reader for parsing beans from XML and automatically adding them to application context.
     */
    private final @NonNull XmlBeanDefinitionReader beanReader;

    /**
     * Bean registry of the application context.
     */
    private final @NonNull BeanDefinitionRegistry beanRegistry;

    /**
     * Adds one or more beans from an XML file to the application context.
     *
     * @param file the XML file.
     * @return a response entity with code 200 or 500, if an error occurs.
     */
    @Hidden
    @PostMapping
    @Operation(summary = "Add a bean to the application context.")
    public ResponseEntity<String> addBeans(@RequestParam("file") final MultipartFile file) {
        try {
            if (file == null) {
                throw new IllegalArgumentException("File must not be null.");
            }

            final var xml = new String(file.getBytes(), StandardCharsets.UTF_8);
            final var numberOfBeans =
                    beanReader.loadBeanDefinitions(new InputSource(new StringReader(xml)));

            if (log.isInfoEnabled()) {
                log.info("Added {} beans to the application context.", numberOfBeans);
            }

            return new ResponseEntity<>("Successfully added " + numberOfBeans
                    + " beans to application context.", HttpStatus.OK);
        } catch (IllegalArgumentException | IOException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not read XML file because file was null.");
            }
            return new ResponseEntity<>("File must not be null.", HttpStatus.BAD_REQUEST);
        } catch (BeanDefinitionStoreException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not read bean(s) from XML file. [exception=({})]",
                        e.getMessage(), e);
            }
            return new ResponseEntity<>("Could not add beans to application context.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes a bean from the application context by its ID.
     *
     * @param beanId the bean ID.
     * @return a response entity with code 200 or 500, if an error occurs.
     */
    @Hidden
    @DeleteMapping("/{beanId}")
    @Operation(summary = "Remove a bean from the application context.")
    public ResponseEntity<String> removeBean(@PathVariable("beanId") final String beanId) {
        try {
            beanRegistry.removeBeanDefinition(beanId);

            if (log.isInfoEnabled()) {
                log.info("Removed bean from the application context. [id=({})]", beanId);
            }

            return new ResponseEntity<>("Successfully removed bean with ID " + beanId + " .",
                    HttpStatus.OK);
        } catch (NoSuchBeanDefinitionException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not remove bean from application context. "
                        + "[id=({}), exception=({})]", beanId, e.getMessage(), e);
            }
            return new ResponseEntity<>("No bean found with ID " + beanId + " .",
                    HttpStatus.BAD_REQUEST);
        }
    }

}
