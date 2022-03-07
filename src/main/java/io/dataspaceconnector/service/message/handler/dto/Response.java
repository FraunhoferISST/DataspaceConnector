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
package io.dataspaceconnector.service.message.handler.dto;

import de.fraunhofer.iais.eis.Message;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Implementation of the {@link RouteMsg} interface for responses. Should contain a subclass of
 * either {@link de.fraunhofer.iais.eis.ResponseMessage} or
 * {@link de.fraunhofer.iais.eis.NotificationMessage} as header.
 */
@Data
@RequiredArgsConstructor
public class Response implements RouteMsg<Message, String> {
    /**
     * The header.
     */
    private final @NonNull Message header;

    /**
     * The body/payload.
     */
    private final @NonNull String body;
}
