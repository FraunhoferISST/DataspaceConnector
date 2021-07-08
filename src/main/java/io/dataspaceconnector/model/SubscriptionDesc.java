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
package io.dataspaceconnector.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;

/**
 * Describes a subscriber. Use this for creating or updating a subscriber.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SubscriptionDesc extends AbstractDescription<Subscription> {

    /**
     * The id of the resource or artifact that the subscriber subscribed to.
     */
    private URI target;

    /**
     * The URL to use when notifying the subscriber about updates to a resource.
     */
    private URI url;

    /**
     * A connector or backend system identifier.
     */
    private URI subscriber;

    /**
     * Indicates whether the subscriber wants the data to be pushed.
     */
    private boolean pushData;

}
