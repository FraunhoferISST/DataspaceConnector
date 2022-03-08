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
package io.dataspaceconnector.model.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.URI;

/**
 * Class for all artifact request message parameters.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public final class ArtifactRequestMessageDesc extends MessageDesc {

    /**
     * The requested artifact of the message.
     */
    private URI requestedArtifact;

    /**
     * The transfer contract of the message.
     */
    private URI transferContract;

    /**
     * All args constructor.
     *
     * @param recipient The message's recipient.
     * @param artifact  The requested artifact.
     * @param contract  The transfer contract.
     */
    public ArtifactRequestMessageDesc(final URI recipient, final URI artifact, final URI contract) {
        super(recipient);

        this.requestedArtifact = artifact;
        this.transferContract = contract;
    }
}
