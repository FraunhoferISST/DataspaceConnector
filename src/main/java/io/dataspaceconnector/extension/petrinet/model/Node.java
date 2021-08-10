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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;

/**
 * Interface for Nodes. Nodes can be either {@link Transition} or {@link Place}.
 *
 * Places are regular Nodes, while Transitions decide,
 * which Steps can be made in the PetriNet.
 */
@JsonSubTypes({@JsonSubTypes.Type(Transition.class), @JsonSubTypes.Type(Place.class)})
@JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = "@type")
public interface Node extends HasId {

    /**
     * @return get all {@link Arc}, where this node is the source (this -> other)
     */
    Set<Arc> getSourceArcs();

    /**
     * @return get all {@link Arc}, where this node is the target (other -> this)
     */
    Set<Arc> getTargetArcs();

    /**
     * @param other another node
     * @return true if this node has a different type (eg. other=place, this=transition)
     */
    boolean isComplementOf(Node other);

    /**
     * Create a deep copy of the Node.
     * @return A deep copy of the Node.
     */
    Node deepCopy();
}
