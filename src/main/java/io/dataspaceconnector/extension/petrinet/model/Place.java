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
 * Places are one type of node of a petri net. They can contain markers, which decide,
 * which transitions can be used to take a step forward in the PetriNet.
 */
@JsonSubTypes({@JsonSubTypes.Type(PlaceImpl.class)})
@JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = "@type")
public interface Place extends Node {

    /**
     * Getter for the number of markers on this place node.
     * @return number of markers
     */
    int getMarkers();

    /**
     * Setter for markers on this place node.
     * @param markers the number of markers this place should have
     */
    void setMarkers(int markers);

}
