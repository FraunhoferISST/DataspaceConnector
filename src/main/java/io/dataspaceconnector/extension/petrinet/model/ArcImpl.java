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
package io.dataspaceconnector.extension.petrinet.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Implementation class of the {@link Arc} interface.
 */
public class ArcImpl implements Arc {
    /**
     * The source node of the arc.
     */
    private Node source;

    /**
     * The target node of the arc.
     */
    private Node target;

    /**
     * The implementation of an arc with source and target node.
     * @param pSource The arc source node.
     * @param pTarget The arc target node.
     */
    @SuppressFBWarnings("MC_OVERRIDABLE_METHOD_CALL_IN_CONSTRUCTOR")
    public ArcImpl(final Node pSource, final Node pTarget) {
        if (pSource.isComplementOf(pTarget)) {
            this.source = pSource;
            pSource.getSourceArcs().add(this);
            this.target = pTarget;
            pTarget.getTargetArcs().add(this);
        } else {
            throw new IllegalArgumentException(
                    String.format(
                            "Node source is of type %s, target should be another Type!",
                            pSource.getClass().getSimpleName()
                    )
            );
        }
    }

    /**
     * Returns the source node of the arc.
     * @return The arc source node.
     */
    @Override
    public Node getSource() {
        return source;
    }

    /**
     * Returns the target node of the arc.
     * @return The arc target node.
     */
    @Override
    public Node getTarget() {
        return target;
    }

    /**
     * Sets the source node of the arc.
     * @param pSource Node that will be set as source.
     */
    @Override
    public void setSource(final Node pSource) {
        if (target.isComplementOf(pSource)) {
            //if given node is a different type as current target: set as source
            this.source.getSourceArcs().remove(this);
            this.source = pSource;
            pSource.getSourceArcs().add(this);
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

    /**
     * Sets the targe node of the arc.
     * @param pTarget Node that will be set as target.
     */
    @Override
    public void setTarget(final Node pTarget) {
        if (source.isComplementOf(pTarget)) {
            //if given node is a different type as current source: set as target
            this.target.getTargetArcs().remove(this);
            this.target = pTarget;
            pTarget.getTargetArcs().add(this);
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

    /**
     * Checks if the arc is equal to the passed object.
     * @param o The object to check against the arc.
     * @return True if the arc equals the passed object.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (source == null || target == null) {
            return false;
        }

        final var arc = (ArcImpl) o;

        return source.equals(arc.source) && target.equals(arc.target);
    }

    /**
     * Return the hashcode for the arc.
     * @return The arc hashcode.
     */
    @Override
    public int hashCode() {
        var hashcode = 0;

        if (source != null) {
            hashcode += source.hashCode();
        }

        if (target != null) {
            hashcode += target.hashCode();
        }
        return hashcode;
    }
}
