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

import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.Objects;

/**
 * Used for inner places of unfolded transitions (has a originalTrans
 * field to access the original transition which was unfolded).
 */
@Getter
@Setter
public class InnerPlace extends PlaceImpl {

    /**
     * Original Transition, which was unfolded to create the InnerPlace.
     */
    private Transition originalTrans;

    /**
     * Inner places of unfolded transition.
     * @param id Id of the inner place.
     * @param pOriginalTrans Original Transition, which was unfolded to create the InnerPlace.
     */
    public InnerPlace(final URI id, final Transition pOriginalTrans) {
        super(id);
        this.originalTrans = pOriginalTrans;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node deepCopy() {
        final var copy = new InnerPlace(this.getID(), this.originalTrans);
        copy.setMarkers(this.getMarkers());
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

        final var place = (InnerPlace) o;

        return originalTrans.equals(place.originalTrans)
                && getMarkers() == place.getMarkers()
                && Objects.equals(getID(), place.getID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return originalTrans.hashCode() + super.getID().hashCode();
    }
}
