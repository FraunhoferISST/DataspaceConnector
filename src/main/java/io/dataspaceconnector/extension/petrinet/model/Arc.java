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

/**
 * Interface for Arcs. Arcs are the edges in petri nets and can only connect two Nodes of
 * different type.
 */
@JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = "@type")
@JsonSubTypes({@JsonSubTypes.Type(ArcImpl.class)})
public interface Arc {
    /**
     * Getter for the source node of this arc: X -> ...
     * @return the source node of this arc
     */
    Node getSource();

    /**
     * Getter for the target node of this arc: ... -> X
     * @return the target node of this arc
     */
    Node getTarget();

    /**
     * Setter for the source node of this arc: X -> ...
     * @throws IllegalArgumentException if node has same type as the current target.
     * @param source node that will be set as source
     */
    void setSource(Node source);

    /**
     * Setter for the target node of this arc: ... -> X
     * @throws  IllegalArgumentException if node has same type as the current source.
     * @param target node that will be set as target
     */
    void setTarget(Node target);

}
