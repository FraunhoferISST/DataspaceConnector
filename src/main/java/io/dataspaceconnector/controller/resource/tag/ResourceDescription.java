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
package io.dataspaceconnector.controller.resource.tag;

/**
 * The descriptions of tags for resources.
 */
public final class ResourceDescription {

    /**
     * Tag description for catalogs.
     */
    public static final String CATALOGS = "Endpoints for operations on catalogs";

    /**
     * Tag description for rules.
     */
    public static final String RULES = "Endpoints for operations on rules";

    /**
     * Tag description for representations.
     */
    public static final String REPRESENTATIONS = "Endpoints for operations on representations";

    /**
     * Tag description for contracts.
     */
    public static final String CONTRACTS = "Endpoints for operations on contracts";

    /**
     * Tag description for offered resources.
     */
    public static final String OFFERS = "Endpoints for operations on offered resources";

    /**
     * Tag description for requested resources.
     */
    public static final String REQUESTS = "Endpoints for operations on requested resources";

    /**
     * Tag description for contract/policy handling.
     */
    public static final String AGREEMENTS = "Endpoints for contract/policy handling";

    /**
     * Tag description for artifacts.
     */
    public static final String ARTIFACTS = "Endpoints for operations on artifacts";

    private ResourceDescription() {
        // Nothing to do here.
    }
}
