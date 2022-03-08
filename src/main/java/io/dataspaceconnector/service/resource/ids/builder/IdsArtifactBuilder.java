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
package io.dataspaceconnector.service.resource.ids.builder;

import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.common.ids.mapping.ToIdsObjectMapper;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.service.resource.ids.builder.base.AbstractIdsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * Converts dsc artifacts to ids artifacts.
 */
@Component
public final class IdsArtifactBuilder extends AbstractIdsBuilder<Artifact,
        de.fraunhofer.iais.eis.Artifact> {

    /**
     * Constructs an IdsArtifactBuilder.
     *
     * @param selfLinkHelper the self link helper.
     */
    @Autowired
    public IdsArtifactBuilder(final SelfLinkHelper selfLinkHelper) {
        super(selfLinkHelper);
    }

    @Override
    protected de.fraunhofer.iais.eis.Artifact createInternal(final Artifact artifact,
                                                             final int currentDepth,
                                                             final int maxDepth)
            throws ConstraintViolationException {
        // Prepare artifact attributes.
        final var created = ToIdsObjectMapper.getGregorianOf(artifact
                                                            .getCreationDate());

        return new ArtifactBuilder(getAbsoluteSelfLink(artifact))
                ._byteSize_(BigInteger.valueOf(artifact.getByteSize()))
                ._checkSum_(BigInteger.valueOf(artifact.getCheckSum()).toString())
                ._creationDate_(created)
                ._fileName_(artifact.getTitle())
                .build();
    }
}
