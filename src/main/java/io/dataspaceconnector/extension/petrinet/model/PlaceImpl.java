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
 * Implementation class of the {@link Place} interface.
 */
public class PlaceImpl implements Place {
    /**
     * The id of the Place.
     */
    private URI id;

    /**
     * The num of markers at the place.
     */
    private int markers;

    /**
     * The outgoing arcs of the place.
     */
    @JsonIgnore
    private Set<Arc> sourceArcs;

    /**
     * The incoming arcs of the place.
     */
    @JsonIgnore
    private Set<Arc> targetArcs;

    /**
     * Creates a new PetiNets place.
     * @param pId The id for the place.
     */
    public PlaceImpl(final URI pId) {
        this.id = pId;
        this.sourceArcs = new HashSet<>();
        this.targetArcs = new HashSet<>();
        this.markers = 0;
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
        return Transition.class.isAssignableFrom(other.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node deepCopy() {
        final var copy = new PlaceImpl(this.getID());
        copy.setMarkers(this.getMarkers());
        return copy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMarkers() {
        return markers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMarkers(final int pMarkers) {
        this.markers = pMarkers;
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

        final var place = (PlaceImpl) o;

        return markers == place.markers && Objects.equals(id, place.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
