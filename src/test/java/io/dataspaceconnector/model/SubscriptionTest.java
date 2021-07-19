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

import java.util.UUID;

public class SubscriptionTest {

//    @Test
//    public void equals_verify() {
//        EqualsVerifier.simple()
//                .forClass(Subscription.class)
//                .withPrefabValues(RequestedResource.class, getRequestedResource1(), getRequestedResource2())
//                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
//                .verify();
//    }

    private RequestedResource getRequestedResource1() {
        final var resource = new RequestedResource();
        resource.setId(UUID.randomUUID());
        resource.setTitle("resource 1");
        return resource;
    }

    private RequestedResource getRequestedResource2() {
        final var resource = new RequestedResource();
        resource.setId(UUID.randomUUID());
        resource.setTitle("resource 2");
        return resource;
    }

}
