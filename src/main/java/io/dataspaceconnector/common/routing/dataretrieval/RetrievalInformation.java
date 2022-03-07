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
package io.dataspaceconnector.common.routing.dataretrieval;

import io.dataspaceconnector.common.net.QueryInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.net.URI;


/**
 * Bundles information for retrieving data from another connector.
 */
@AllArgsConstructor
@Data
@RequiredArgsConstructor
public class RetrievalInformation {

    /**
     * The transferContract with which the data transfer is authorized.
     */
    private @NonNull URI transferContract;

    /**
     * If the data should be downloaded.
     * null  - Let the connector decide.
     * true  - Always download.
     * false - Do not download the data under any condition.
     */
    private Boolean forceDownload;

    /**
     * Query option for limiting the scope of the data pulled.
     * The query input may be ignored by some connectors.
     */
    private QueryInput queryInput;
}
