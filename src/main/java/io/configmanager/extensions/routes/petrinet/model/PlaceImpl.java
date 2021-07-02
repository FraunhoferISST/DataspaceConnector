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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation class of the {@link Place} interface.
 */
public class PlaceImpl implements Place {
    private URI id;
    private int markers;

    @JsonIgnore
    private Set<Arc> sourceArcs;

    @JsonIgnore
    private Set<Arc> targetArcs;

    public PlaceImpl(final URI id) {
        this.id = id;
        this.sourceArcs = new HashSet<>();
        this.targetArcs = new HashSet<>();
        this.markers = 0;
    }

    @Override
    public URI getID() {
        return id;
    }

    @Override
    public Set<Arc> getSourceArcs() {
        return sourceArcs;
    }

    @Override
    public Set<Arc> getTargetArcs() {
        return targetArcs;
    }

    @Override
    public boolean isComplementOf(final Node other) {
        return Transition.class.isAssignableFrom(other.getClass());
    }

    @Override
    public Node deepCopy() {
        final var copy = new PlaceImpl(this.getID());
        copy.setMarkers(this.getMarkers());
        return copy;
    }

    @Override
    public int getMarkers() {
        return markers;
    }

    @Override
    public void setMarkers(final int markers) {
        this.markers = markers;
    }

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

    public boolean equalsExceptMarking(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final var place = (PlaceImpl) o;
        return Objects.equals(id, place.id);
    }

}
