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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation class of the {@link Transition} interface.
 */
public class TransitionImpl implements Transition {
    /**
     * ID of the transition.
     */
    private URI id;

    /**
     * The context of the transition.
     */
    private ContextObject contextObject;

    /**
     * All source arcs of the transition.
     */
    @JsonIgnore
    private Set<Arc> sourceArcs;

    /**
     * All target arcs of the transition.
     */
    @JsonIgnore
    private Set<Arc> targetArcs;

    /**
     * Creates a new transition object.
     * @param pId The id of the transition.
     */
    public TransitionImpl(final URI pId) {
        this.id = pId;
        this.sourceArcs = new HashSet<>();
        this.targetArcs = new HashSet<>();
    }

    /**
     * Sets the context of the transition.
     * @param pContextObject The transition context.
     */
    public void setContextObject(final ContextObject pContextObject) {
        this.contextObject = pContextObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextObject getContext() {
        return contextObject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getID() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Arc> getSourceArcs() {
        return sourceArcs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Arc> getTargetArcs() {
        return targetArcs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isComplementOf(final Node other) {
        return Place.class.isAssignableFrom(other.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node deepCopy() {
        final var copy = new TransitionImpl(this.getID());
        if (this.contextObject != null) {
            copy.setContextObject(this.contextObject.deepCopy());
        }

        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var trans = (TransitionImpl) o;

        return Objects.equals(id, trans.id) && Objects.equals(contextObject, trans.contextObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
