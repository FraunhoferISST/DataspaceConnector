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
package io.dataspaceconnector.common.net;

import java.io.InputStream;

import io.dataspaceconnector.common.routing.dataretrieval.Response;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Represents data retrieved via HTTP.
 */
@Getter
@RequiredArgsConstructor
public class HttpResponse implements Response {

    /**
     * The response code.
     */
    private final int code;

    /**
     * The data.
     */
    private final @NonNull InputStream data;

}
