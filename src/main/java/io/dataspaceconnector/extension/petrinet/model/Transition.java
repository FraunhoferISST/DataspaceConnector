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
 * Transitions are Nodes, which decide if a step can be taken in the petri net.
 *
 * If all previous nodes of a transistion have a marker, those markers will be taken away
 * and every following node of a transition will get a marker:
 *
 * X -> T -> O  ==> O -> T -> X
 */
@JsonSubTypes({@JsonSubTypes.Type(TransitionImpl.class)})
@JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = "@type")
public interface Transition extends Node {
    /**
     * Transitions are Nodes, which decide if a step can be taken in the petri net.
     * @return The context of the transition.
     */
    ContextObject getContext();
}
