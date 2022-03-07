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
package io.dataspaceconnector.model.artifact;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArtifactTests {

    @Test
    public void incrementAccessCounter_willAddOnlyOne() {
        ArtifactDesc desc = new ArtifactDesc();
        ArtifactFactory factory = new ArtifactFactory();
        var artifact = factory.create(desc);

        final var before = artifact.getNumAccessed();

        artifact.incrementAccessCounter();

        assertEquals(1, artifact.getNumAccessed());
        assertEquals(0, before);
    }
}
