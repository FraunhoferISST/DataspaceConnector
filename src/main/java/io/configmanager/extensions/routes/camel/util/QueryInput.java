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
package io.configmanager.extensions.routes.camel.util;

import lombok.Data;

import java.util.HashMap;

/**
 * Camel Query-Input data.
 */
@Data
public class QueryInput {
    /**
     * The Header of the Query-Input.
     */
    private HashMap<String, String> headers = new HashMap<>();

    /**
     * The Params of the Query-Input.
     */
    private HashMap<String, String> params = new HashMap<>();

    /**
     * The Path-Variables of the Query-Input.
     */
    private HashMap<String, String> pathVariables = new HashMap<>();
}
