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
package io.dataspaceconnector.controller.resource.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * A DTO for controlled exposing of subscription information in API responses.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "subscriptions", itemRelation = "subscription")
public class SubscriptionView extends RepresentationModel<SubscriptionView> {

    /**
     * The creation date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime creationDate;

    /**
     * The last modification date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime modificationDate;

    /**
     * The id of the resource or artifact that the subscriber subscribed to.
     */
    private URI target;

    /**
     * The URL to use when notifying the subscriber about updates to a resource.
     */
    private URI location;

    /**
     * A connector or backend system identifier.
     */
    private URI subscriber;

    /**
     * Indicates whether the subscriber wants the data to be pushed.
     */
    private boolean pushData;

    /**
     * Indicates whether the subscriber is an ids participant or not.
     */
    private boolean idsProtocol;

    /**
     * Additional properties.
     */
    private Map<String, String> additional;

}
