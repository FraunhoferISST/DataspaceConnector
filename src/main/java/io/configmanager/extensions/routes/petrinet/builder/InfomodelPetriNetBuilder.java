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
package io.configmanager.extensions.routes.petrinet.builder;

import de.fraunhofer.iais.eis.AppRoute;
import de.fraunhofer.iais.eis.Endpoint;
import de.fraunhofer.iais.eis.RouteStep;
import io.configmanager.extensions.routes.petrinet.model.Arc;
import io.configmanager.extensions.routes.petrinet.model.ArcImpl;
import io.configmanager.extensions.routes.petrinet.model.Node;
import io.configmanager.extensions.routes.petrinet.model.PetriNet;
import io.configmanager.extensions.routes.petrinet.model.PetriNetImpl;
import io.configmanager.extensions.routes.petrinet.model.Place;
import io.configmanager.extensions.routes.petrinet.model.PlaceImpl;
import io.configmanager.extensions.routes.petrinet.model.Transition;
import io.configmanager.extensions.routes.petrinet.model.TransitionImpl;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provide static methods, to generate a Petri Net (https://en.wikipedia.org/wiki/Petri_net) from an Infomodel AppRoute.
 */
@UtilityClass
public class InfomodelPetriNetBuilder {

    /**
     * Generate a Petri Net from a given infomodel {@link AppRoute}.
     * RouteSteps will be represented as Places, Endpoints as Transitions.
     *
     * @param appRoute an Infomodel {@link AppRoute}
     * @return a Petri Net created from the AppRoute
     */
    public static PetriNet petriNetFromAppRoute(final AppRoute appRoute,
                                                final boolean includeAppRoute) {

        //create sets for places, transitions and arcs
        final var places = new HashMap<URI, Place>();
        final var transitions = new HashMap<URI, Transition>();
        final var arcs = new HashSet<Arc>();

        if (includeAppRoute) {
            //create initial place from AppRoute
            final var place = new PlaceImpl(appRoute.getId());
            places.put(place.getID(), place);

            //for every AppRouteStart create a Transition and add AppRouteStart -> AppRoute
            for (final var endpoint : appRoute.getAppRouteStart()) {
                final var trans = getTransition(transitions, endpoint);
                final var arc = new ArcImpl(trans, place);

                arcs.add(arc);
            }

            //for every AppRouteEnd create a Transition and add AppRoute -> AppRouteEnd
            for (final var endpoint : appRoute.getAppRouteEnd()) {
                final var trans = getTransition(transitions, endpoint);
                final var arc = new ArcImpl(place, trans);

                arcs.add(arc);
            }
        }

        //add every SubRoute of the AppRoute to the PetriNet
        for (final var subroute : appRoute.getHasSubRoute()) {
            addSubRouteToPetriNet(subroute, arcs, places, transitions);
        }

        //create a PetriNet with all Arcs, Transitions and Places from the AppRoute
        final var nodes = new HashSet<Node>();
        nodes.addAll(places.values());
        nodes.addAll(transitions.values());

        final var petriNet = new PetriNetImpl(appRoute.getId(), nodes, arcs);
        addFirstAndLastNode(petriNet);

        return petriNet;
    }

    /**
     * Add a {@link RouteStep} to the Petri Net as a new Subroute.
     *
     * @param subRoute the subRoute that will be added to the current Petri Net
     * @param arcs list of arcs of the current Petri Net
     * @param places list of places of the current Petri Net
     * @param transitions list of transitions of the current Petri Net
     */
    private static void addSubRouteToPetriNet(final RouteStep subRoute,
                                              final Set<Arc> arcs,
                                              final Map<URI, Place> places,
                                              final Map<URI, Transition> transitions) {

        //if a place with subroutes ID already exists in the map, the SubRoute was already added to the Petri Net
        if (places.containsKey(subRoute.getId())) {
            return;
        }

        //create a new place from the subRoute
        final var place = new PlaceImpl(subRoute.getId());
        places.put(place.getID(), place);

        //for every AppRouteStart create a transition and add AppRouteStart -> SubRoute
        for (final var endpoint : subRoute.getAppRouteStart()) {
            final var trans = getTransition(transitions, endpoint);
            final var arc = new ArcImpl(trans, place);
            arcs.add(arc);
        }

        //for every AppRouteEnd create a transition and add SubRoute -> AppRouteEnd
        for (final var endpoint : subRoute.getAppRouteEnd()) {
            final var trans = getTransition(transitions, endpoint);
            final var arc = new ArcImpl(place, trans);
            arcs.add(arc);
        }
    }

    /**
     * Get the transition for the given {@link Endpoint} by ID, or generate a new one if no transition for that endpoint exists.
     *
     * @param transitions the transition that will be created or found in the map
     * @param endpoint the endpoint for which the transition should be found
     * @return the existing transition with id from the map, or a new transition
     */
    private static Transition getTransition(final Map<URI, Transition> transitions,
                                            final Endpoint endpoint) {
        if (transitions.containsKey(endpoint.getId())) {
            return transitions.get(endpoint.getId());
        } else {
            final var trans = new TransitionImpl(endpoint.getId());
            transitions.put(trans.getID(), trans);
            return trans;
        }
    }

    /**
     * Add a source node to every transition without input and a sink node to every transition without output.
     *
     * @param petriNet
     */
    private static void addFirstAndLastNode(final PetriNet petriNet) {
        final var first = new PlaceImpl(URI.create("place://source"));
        final var last = new PlaceImpl(URI.create("place://sink"));

        first.setMarkers(1);

        for (final var node : petriNet.getNodes()) {
            if (node instanceof TransitionImpl) {
                //if node has no arc with itself as target, add arc: first->node
                if (node.getTargetArcs().isEmpty()) {
                    final var arc = new ArcImpl(first, node);
                    petriNet.getArcs().add(arc);
                }
                //if node has no arc with itself as source, add arc: node->last
                if (node.getSourceArcs().isEmpty()) {
                    final var arc = new ArcImpl(node, last);
                    petriNet.getArcs().add(arc);
                }
            }
        }
        petriNet.getNodes().add(first);
        petriNet.getNodes().add(last);
    }

}
