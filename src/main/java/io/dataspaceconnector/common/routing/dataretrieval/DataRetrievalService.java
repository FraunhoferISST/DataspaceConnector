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

import io.dataspaceconnector.common.exception.DataRetrievalException;
import io.dataspaceconnector.common.net.HttpAuthentication;
import io.dataspaceconnector.common.net.QueryInput;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Interface for services that retrieve data.
 */
public interface DataRetrievalService {

    /**
     * Retrieves the data.
     *
     * @param target The target URL.
     * @param input The query input.
     * @return The data as a response.
     * @throws IOException if an IO error occurs.
     * @throws DataRetrievalException if any other error occurs.
     */
    Response get(URL target, QueryInput input) throws IOException, DataRetrievalException;

    /**
     * Retrieves the data using authentication.
     *
     * @param target The target URL.
     * @param input The query input.
     * @param auth The list of authentications.
     * @return The data as a response.
     * @throws IOException if an IO error occurs.
     * @throws DataRetrievalException if any other error occurs.
     */
    Response get(URL target, QueryInput input, List<? extends HttpAuthentication> auth)
            throws IOException, DataRetrievalException;

}
