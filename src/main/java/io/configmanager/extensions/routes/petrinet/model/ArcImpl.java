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
package io.configmanager.extensions.routes.petrinet.model;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

/**
 * Implementation class of the {@link Arc} interface.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ArcImpl implements Arc {
    Node source;
    Node target;

    public ArcImpl(final Node source, final Node target) {
        if (source.isComplementOf(target)) {
            this.source = source;
            source.getSourceArcs().add(this);
            this.target = target;
            target.getTargetArcs().add(this);
        } else {
            throw new IllegalArgumentException(
                    String.format(
                            "Node source is of type %s, target should be another Type!",
                            source.getClass().getSimpleName()
                    )
            );
        }
    }

    @Override
    public Node getSource() {
        return source;
    }

    @Override
    public Node getTarget() {
        return target;
    }

    @Override
    public void setSource(final Node source) {
        if (target.isComplementOf(source)) {
            //if given node is a different type as current target: set as source
            this.source.getSourceArcs().remove(this);
            this.source = source;
            source.getSourceArcs().add(this);
        } else {
            //if given node is same type as current target: throw an Exception
            throw new IllegalArgumentException(
                    String.format(
                            "Node target is of type %s, source should be another Type!",
                            target.getClass().getSimpleName()
                    )
            );
        }
    }

    @Override
    public void setTarget(final Node target) {
        if (source.isComplementOf(target)) {
            //if given node is a different type as current source: set as target
            this.target.getTargetArcs().remove(this);
            this.target = target;
            target.getTargetArcs().add(this);
        } else {
            //if given node is same type as current source: throw an Exception
            throw new IllegalArgumentException(
                    String.format(
                            "Node source is of type %s, target should be another Type!",
                            source.getClass().getSimpleName()
                    )
            );
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var arc = (ArcImpl) o;

        return source.equals(arc.source) && target.equals(arc.target);
    }

}
