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

/**
 * Interface for messages exchanged between the processors in Camel routes.
 *
 * @param <H> the header type.
 * @param <B> the body/payload type.
 */
public interface RouteMsg<H, B> {
    /**
     * Returns the header of this RouteMsg.
     *
     * @return the header.
     */
    H getHeader();

    /**
     * Returns the body/payload of this RouteMsg.
     *
     * @return the body/payload.
     */
    B getBody();
}
