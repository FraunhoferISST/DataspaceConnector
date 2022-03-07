/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.controller.resource.view.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.dataspaceconnector.controller.resource.view.util.ViewConstants;
import io.dataspaceconnector.model.resource.PaymentMethod;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * A DTO for controlled exposing of offered resource information in API responses.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "resources", itemRelation = "resource")
public class OfferedResourceView extends RepresentationModel<OfferedResourceView> {

    /**
     * The creation date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ViewConstants.DATE_TIME_FORMAT)
    private ZonedDateTime creationDate;

    /**
     * The last modification date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ViewConstants.DATE_TIME_FORMAT)
    private ZonedDateTime modificationDate;

    /**
     * The title of the resource.
     */
    private String title;

    /**
     * The description of the resource.
     */
    private String description;

    /**
     * The keywords of the resource.
     */
    private List<String> keywords;

    /**
     * The publisher of the resource.
     */
    private URI publisher;

    /**
     * The language of the resource.
     */
    private String language;

    /**
     * The license of the resource.
     */
    private URI license;

    /**
     * The version of the resource.
     */
    private long version;

    /**
     * The owner of the resource.
     */
    private URI sovereign;

    /**
     * The endpoint of the resource.
     */
    private URI endpointDocumentation;

    /**
     * The payment modality.
     */
    private PaymentMethod paymentModality;

    /**
     * A list of resource IDs pointing at sample resources.
     */
    private List<URI> samples;

    /**
     * Additional properties.
     */
    private Map<String, String> additional;
}
