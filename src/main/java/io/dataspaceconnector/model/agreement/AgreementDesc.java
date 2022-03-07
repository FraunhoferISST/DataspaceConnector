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
package io.dataspaceconnector.model.agreement;

import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.base.Description;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.List;

/**
 * Describes a contract agreement's properties.
 */
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AgreementDesc extends Description {

    /**
     * The agreement id on provider side.
     */
    private URI remoteId;

    /**
     * Indicates whether both parties have agreed.
     */
    private boolean confirmed;

    /**
     * The definition of the contract.
     **/
    private String value;

    /**
     * The artifacts this agreement refers to.
     */
    private List<Artifact> artifacts;
}
