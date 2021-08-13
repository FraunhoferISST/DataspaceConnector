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
package io.dataspaceconnector.extension.petrinet;

import de.fraunhofer.iais.eis.AppRouteBuilder;
import de.fraunhofer.iais.eis.Endpoint;
import de.fraunhofer.iais.eis.EndpointBuilder;
import de.fraunhofer.iais.eis.GenericEndpointBuilder;
import de.fraunhofer.iais.eis.RouteStep;
import de.fraunhofer.iais.eis.RouteStepBuilder;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.extension.petrinet.builder.GraphVizGenerator;
import io.dataspaceconnector.extension.petrinet.builder.InfomodelPetriNetBuilder;
import io.dataspaceconnector.extension.petrinet.builder.RuleFormulaBuilder;
import io.dataspaceconnector.extension.petrinet.evaluation.formula.CTLEvaluator;
import io.dataspaceconnector.extension.petrinet.evaluation.formula.TrueOperator;
import io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeNOT;
import io.dataspaceconnector.extension.petrinet.model.Arc;
import io.dataspaceconnector.extension.petrinet.model.ArcImpl;
import io.dataspaceconnector.extension.petrinet.model.ContextObject;
import io.dataspaceconnector.extension.petrinet.model.Node;
import io.dataspaceconnector.extension.petrinet.model.PetriNet;
import io.dataspaceconnector.extension.petrinet.model.PetriNetImpl;
import io.dataspaceconnector.extension.petrinet.model.Place;
import io.dataspaceconnector.extension.petrinet.model.PlaceImpl;
import io.dataspaceconnector.extension.petrinet.model.TransitionImpl;
import io.dataspaceconnector.extension.petrinet.simulator.ParallelEvaluator;
import io.dataspaceconnector.extension.petrinet.simulator.PetriNetSimulator;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.dataspaceconnector.extension.petrinet.evaluation.formula.FalseOperator.falseOperator;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.TrueOperator.trueOperator;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeALONG.nodeALONG;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeAND.nodeAND;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeEV.nodeEV;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeEXISTMODAL.nodeEXISTMODAL;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeEXISTNEXT.nodeEXISTNEXT;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeEXISTUNTIL.nodeEXISTUNTIL;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeExpression.nodeExpression;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeFORALLMODAL.nodeFORALLMODAL;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeFORALLNEXT.nodeFORALLNEXT;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeFORALLUNTIL.nodeFORALLUNTIL;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeINV.nodeINV;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeMODAL.nodeMODAL;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeNF.nodeNF;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeNOT.nodeNOT;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodeOR.nodeOR;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.state.NodePOS.nodePOS;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.ArcExpression.arcExpression;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionAF.transitionAF;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionALONG.transitionALONG;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionAND.transitionAND;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionEV.transitionEV;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionEXISTMODAL.transitionEXISTMODAL;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionEXISTNEXT.transitionEXISTNEXT;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionFORALLMODAL.transitionFORALLMODAL;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionFORALLNEXT.transitionFORALLNEXT;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionFORALLUNTIL.transitionFORALLUNTIL;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionINV.transitionINV;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionMODAL.transitionMODAL;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionNOT.transitionNOT;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionOR.transitionOR;
import static io.dataspaceconnector.extension.petrinet.evaluation.formula.transition.TransitionPOS.transitionPOS;
import static io.dataspaceconnector.common.ids.policy.PolicyPattern.PROHIBIT_ACCESS;
import static io.dataspaceconnector.common.ids.policy.PolicyPattern.PROVIDE_ACCESS;
import static io.dataspaceconnector.common.ids.policy.PolicyPattern.USAGE_LOGGING;
import static io.dataspaceconnector.common.ids.policy.PolicyPattern.USAGE_NOTIFICATION;
import static io.dataspaceconnector.common.ids.policy.PolicyPattern.USAGE_UNTIL_DELETION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test building a PetriNet from a randomly generated AppRoute
 */
@Log4j2
@NoArgsConstructor
class InfomodelPetriNetBuilderTest {
    private final static int MINIMUM_ENDPOINT = 2;
    private final static int MAXIMUM_ENDPOINT = 3;

    private final static int MINIMUM_SUBROUTE = 2;
    private final static int MAXIMUM_SUBROUTE = 3;

    private final static int MINIMUM_STARTEND = 1;
    private final static int MAXIMUM_STARTEND = 3;

    @Test
    void buildPetrinetFromAppRoute() {
        final var appRoute = new AppRouteBuilder(URI.create("http://approute"))
                ._routeDeployMethod_("CAMEL")
                ._appRouteStart_(Util.asList(new GenericEndpointBuilder()._accessURL_(URI.create("http://test")).build()))
                ._appRouteOutput_(Util.asList())
                .build();

        assertTrue(InfomodelPetriNetBuilder.buildAndCheck(appRoute));
    }


    @Test
    void testBuildFormula_PROVIDE_ACCESS() {
        var formula = RuleFormulaBuilder.buildFormula(PROVIDE_ACCESS, null, null);
        assertEquals(TrueOperator.class, formula.getClass());
    }

    @Test
    void testBuildFormula_USAGE_UNTIL_DELETION() {
        var formula = RuleFormulaBuilder.buildFormula(USAGE_UNTIL_DELETION, null,
                URI.create("6ba7b810-9ffff-11d1-80b4-00c04fd430c8"));
        assertEquals(NodeNOT.class, formula.getClass());
    }

    @Test
    void testBuildFormula_USAGE_LOGGING() {
        var formula = RuleFormulaBuilder.buildFormula(USAGE_LOGGING, null,
                URI.create("6ba7b810-9ffff-11d1-80b4-00c04fd430c8"));
        assertEquals(NodeNOT.class, formula.getClass());
    }

    @Test
    void testBuildFormula_USAGE_NOTIFICATION() {
        var formula = RuleFormulaBuilder.buildFormula(USAGE_NOTIFICATION, null,
                URI.create("6ba7b810-9ffff-11d1-80b4-00c04fd430c8"));
        assertEquals(NodeNOT.class, formula.getClass());
    }

    @Test
    void testBuildFormula_PROHIBIT_ACCESS() {
        var formula = RuleFormulaBuilder.buildFormula(PROHIBIT_ACCESS, null,
                URI.create("6ba7b810-9ffff-11d1-80b4-00c04fd430c8"));
        assertEquals(NodeNOT.class, formula.getClass());
    }

    /**
     * Example: Generate a random PetriNet, try to simulate it and print out the GraphViz representation
     * Generated PetriNet can have an infinite amount of possible configurations, if this happens the
     * example will run indefinitely.
     */
    @Test
    @SuppressWarnings("unchecked")
    void testBuildPetriNet() throws IOException {
        //Randomly generate an AppRoute
        final var endpointlist = new ArrayList<Endpoint>();
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(MINIMUM_ENDPOINT, MAXIMUM_ENDPOINT); i++){
            endpointlist.add(new EndpointBuilder(URI.create("http://endpoint" + i)).build());
        }
        final var subroutes = new ArrayList<RouteStep>();
        for (int i = 0; i < ThreadLocalRandom.current().nextInt(MINIMUM_SUBROUTE,MAXIMUM_SUBROUTE); i++){
            subroutes.add(new RouteStepBuilder(URI.create("http://subroute" + i))
                    ._routeDeployMethod_("CAMEL")
                    ._appRouteStart_((ArrayList<Endpoint>) randomSubList(endpointlist))
                    ._appRouteEnd_((ArrayList<Endpoint>) randomSubList(endpointlist)).build());
        }
        final var appRoute = new AppRouteBuilder(URI.create("http://approute"))
                ._routeDeployMethod_("CAMEL")
                ._appRouteStart_((ArrayList<Endpoint>) randomSubList(endpointlist))
                ._appRouteEnd_((ArrayList<Endpoint>) randomSubList(endpointlist))
                ._hasSubRoute_(subroutes)
                .build();

        //build a petriNet from the generated AppRoute and log generated GraphViz representation
        final var petriNet = InfomodelPetriNetBuilder.petriNetFromAppRoute(appRoute, false);
        final var ser = new Serializer();
        if (log.isInfoEnabled()) {
            log.info(ser.serialize(appRoute));
            log.info(GraphVizGenerator.generateGraphViz(petriNet));
        }

        //build a full Graph of all possible steps in the PetriNet and log generated GraphViz representation
        final var graph = PetriNetSimulator.buildStepGraph(petriNet);

        if (log.isInfoEnabled()) {
            log.info(String.valueOf(graph.getArcs().size()));
            log.info(GraphVizGenerator.generateGraphViz(graph));
        }

        final var allPaths = PetriNetSimulator.getAllPaths(graph);

        if (log.isInfoEnabled()) {
            log.info(allPaths.toString());
        }

        final var formula = nodeAND(nodeMODAL(transitionNOT(falseOperator())), nodeOR(nodeNF(nodeExpression(x -> true, "testMsg")), trueOperator()));
        final var formula2 = nodeAND(nodeFORALLNEXT(nodeMODAL(transitionAF(arcExpression(x -> true,"")))), trueOperator());
        final var formula3 = nodeEXISTUNTIL(nodeMODAL(trueOperator()), nodeNF(nodeExpression(x -> x.getSourceArcs().isEmpty(), "")));

        if (log.isInfoEnabled()) {
            log.info("Formula 1: " + formula.writeFormula());
            log.info("Result: " + CTLEvaluator.evaluate(formula, graph.getInitial().getNodes().stream().filter(node -> node instanceof Place).findAny().get(), allPaths));
            log.info("Formula 2: " + formula2.writeFormula());
            log.info("Result: " + CTLEvaluator.evaluate(formula2, graph.getInitial().getNodes().stream().filter(node -> node instanceof Place).findAny().get(), allPaths));
            log.info("Formula 3: " + formula3.writeFormula());
            log.info("Result: " + CTLEvaluator.evaluate(formula3, graph.getInitial().getNodes().stream().filter(node -> node.getID().equals(URI.create("place://source"))).findAny().get(), allPaths));
        }

        //Testing building further formulas
        final var formula4 = nodeALONG(formula3);
        assertEquals("ALONG", formula4.symbol());
        assertEquals("ALONG(EXIST_UNTIL(MODAL(TrueOperator), NF()))", formula4.writeFormula());

        final var formula5 = nodeEV(formula3);
        assertEquals("EV", formula5.symbol());
        assertEquals("EV(EXIST_UNTIL(MODAL(TrueOperator), NF()))", formula5.writeFormula());

        final var formula6 = nodePOS(formula3);
        assertEquals("POS", formula6.symbol());
        assertEquals("POS(EXIST_UNTIL(MODAL(TrueOperator), NF()))", formula6.writeFormula());

        final var formula7 = nodeINV(formula3);
        assertEquals("INV", formula7.symbol());
        assertEquals("INV(EXIST_UNTIL(MODAL(TrueOperator), NF()))", formula7.writeFormula());

        final var formula8 = nodeFORALLUNTIL(formula3, formula3);
        assertEquals("FORALL_UNTIL", formula8.symbol());
        assertEquals("FORALL_UNTIL(EXIST_UNTIL(MODAL(TrueOperator), NF()), EXIST_UNTIL(MODAL(TrueOperator), NF()))", formula8.writeFormula());

        final var formula9 = nodeFORALLMODAL(
                formula3,
                transitionNOT(falseOperator()));
        assertEquals("FORALL_MODAL",formula9.symbol());
        assertEquals("FORALL_MODAL(EXIST_UNTIL(MODAL(TrueOperator), NF()), NOT(falseOperator))",formula9.writeFormula());

        final var formula10 = nodeEXISTMODAL(
                formula3,
                transitionNOT(falseOperator()));
        assertEquals("EXIST_MODAL",formula10.symbol());
        assertEquals("EXIST_MODAL(EXIST_UNTIL(MODAL(TrueOperator), NF()), NOT(falseOperator))",formula10.writeFormula());

        final var formula11 = nodeNOT(formula3);
        assertEquals("NOT", formula11.symbol());
        assertEquals("NOT(EXIST_UNTIL(MODAL(TrueOperator), NF()))", formula11.writeFormula());

        final var formula12 = nodeEXISTNEXT(formula3);
        assertEquals("EXIST_NEXT", formula12.symbol());
        assertEquals("EXIST_NEXT(EXIST_UNTIL(MODAL(TrueOperator), NF()))", formula12.writeFormula());

        final var formula13 = nodeAND(nodeMODAL(transitionEXISTNEXT(falseOperator())), nodeOR(nodeNF(nodeExpression(x -> true, "testMsg")), trueOperator()));
        assertEquals("AND", formula13.symbol());
        assertEquals("AND(MODAL(EXIST_NEXT(falseOperator)), OR(NF(testMsg), TrueOperator))", formula13.writeFormula());

        final var formula14 = nodeAND(nodeMODAL(transitionALONG(falseOperator())), nodeOR(nodeNF(nodeExpression(x -> true, "testMsg")), trueOperator()));
        assertEquals("AND", formula14.symbol());
        assertEquals("AND(MODAL(ALONG(falseOperator)), OR(NF(testMsg), TrueOperator))", formula14.writeFormula());

        final var formula15 = nodeAND(nodeMODAL(transitionEXISTMODAL(falseOperator(), formula8)), nodeOR(nodeNF(nodeExpression(x -> true, "testMsg")), trueOperator()));
        assertEquals("AND", formula15.symbol());
        assertEquals("AND(MODAL(EXIST_MODAL(falseOperator, FORALL_UNTIL(EXIST_UNTIL(MODAL(TrueOperator), NF()), EXIST_UNTIL(MODAL(TrueOperator), NF())))), OR(NF(testMsg), TrueOperator))", formula15.writeFormula());

        final var formula16 = nodeAND(nodeMODAL(transitionFORALLUNTIL(falseOperator(), falseOperator())), nodeOR(nodeNF(nodeExpression(x -> true, "testMsg")), trueOperator()));
        assertEquals("AND", formula16.symbol());
        assertEquals("AND(MODAL(FORALL_UNTIL(falseOperator, falseOperator)), OR(NF(testMsg), TrueOperator))", formula16.writeFormula());

        final var formula17 = nodeAND(nodeMODAL(transitionFORALLMODAL(falseOperator(), falseOperator())), nodeOR(nodeNF(nodeExpression(x -> true, "testMsg")), trueOperator()));
        assertEquals("AND", formula17.symbol());
        assertEquals("AND(MODAL(FORALL_MODAL(falseOperator, falseOperator)), OR(NF(testMsg), TrueOperator))", formula17.writeFormula());

        final var formula18 = nodeAND(nodeMODAL(transitionFORALLNEXT(falseOperator())), nodeOR(nodeNF(nodeExpression(x -> true, "testMsg")), trueOperator()));
        assertEquals("AND", formula18.symbol());
        assertEquals("AND(MODAL(FORALL_NEXT(falseOperator)), OR(NF(testMsg), TrueOperator))", formula18.writeFormula());

        final var formula19 = nodeAND(nodeMODAL(transitionINV(falseOperator())), nodeOR(nodeNF(nodeExpression(x -> true, "testMsg")), trueOperator()));
        assertEquals("AND", formula19.symbol());
        assertEquals("AND(MODAL(INV(falseOperator)), OR(NF(testMsg), TrueOperator))", formula19.writeFormula());
    }

    /**
     * Example: Create a set of Formulas and evaluate them on the example PetriNet
     */
    @Test
    void testExamplePetriNet(){
        //build the example net and log DOT visualization
        final var petriNet = buildPaperNet();
        if (log.isInfoEnabled()) {
            log.info(GraphVizGenerator.generateGraphViz(petriNet));
        }

        //build stepGraph
        final var graph = PetriNetSimulator.buildStepGraph(petriNet);
        if (log.isInfoEnabled()) {
            log.info(String.format("%d possible states!", graph.getSteps().size()));
        }

        //get set of paths f7rom calculated stepgraph
        final var allPaths = PetriNetSimulator.getAllPaths(graph);
        if (log.isInfoEnabled()) {
            log.info(String.format("Found %d valid Paths!", allPaths.size()));
        }

        //Evaluate Formula 1: a transition is reachable, which reads data without 'france' in context, after that transition data is overwritten or erased (or an end is reached)
        final var formulaFrance = transitionPOS(
                transitionAND(
                        transitionAF(arcExpression(x -> x.getContext().getRead() != null && x.getContext().getRead().equals("data") && !x.getContext().getContext().contains("france"), "")),
                        transitionEV(
                                transitionOR(
                                        transitionAF(arcExpression(x -> x.getContext().getWrite() != null && "data".equals(x.getContext().getWrite()) || x.getContext().getErase() != null && "data".equals(x.getContext().getErase()), "")),
                                        transitionMODAL(nodeNF(nodeExpression(x -> x.getSourceArcs().isEmpty(), " ")))
                                )
                        )
                )
        );
        if (log.isInfoEnabled()) {
            log.info("Formula France: " + formulaFrance.writeFormula());
            log.info("Result: " + CTLEvaluator.evaluate(formulaFrance, graph.getInitial().getNodes().stream().filter(node -> node.getID().equals(URI.create("trans://getData"))).findAny().get(), allPaths));
        }

        //Evaluate Formula 2: a transition is reachable, which reads data
        final var formulaDataUsage = nodeMODAL(transitionPOS(transitionAF(arcExpression(x -> x.getContext().getRead() != null && x.getContext().getRead().equals("data"), ""))));
        if (log.isInfoEnabled()) {
            log.info("Formula Data: " + formulaDataUsage.writeFormula());
            log.info("Result: " + CTLEvaluator.evaluate(formulaDataUsage, graph.getInitial().getNodes().stream().filter(node -> node.getID().equals(URI.create("place://start"))).findAny().get(), allPaths));
        }

        //Evaluate Formula 3: a transition is reachable, which is reading data. From there another transition is reachable, which also reads data, from this the end or a transition which overwrites or erases data is reachable.
        final var formulaUseAndDelete = transitionPOS(
                transitionAND(
                        transitionAF(arcExpression(x -> x.getContext().getRead() != null && "data".equals(x.getContext().getRead()), "")),
                        transitionPOS(
                                transitionAND(
                                        transitionAF(arcExpression(x -> x.getContext().getRead() != null || "data".equals(x.getContext().getRead()), "")),
                                        transitionEV(
                                                transitionOR(
                                                        transitionAF(arcExpression(x -> x.getContext().getWrite() != null && "data".equals(x.getContext().getWrite()) || x.getContext().getErase() != null && "data".equals(x.getContext().getErase()), "")),
                                                        transitionMODAL(nodeNF(nodeExpression(x -> x.getSourceArcs().isEmpty(), " ")))
                                                )
                                        )
                                )

                        )
                )
        );
        if (log.isInfoEnabled()) {
            log.info("Formula Use And Delete: " + formulaUseAndDelete.writeFormula());
            log.info("Result: " + CTLEvaluator.evaluate(formulaUseAndDelete, graph.getInitial().getNodes().stream().filter(node -> node.getID().equals(URI.create("trans://getData"))).findAny().get(), allPaths));
        }
    }

    /**
     * Example: Unfold the example PetriNet and check for parallel evaluations
     */
    @Test
    @Disabled
    void testUnfoldNet(){
        //build example petrinet
        final var petriNet = buildPaperNet();

        //unfold and visualize example petrinet
        final var unfolded = PetriNetSimulator.getUnfoldedPetriNet(petriNet);
        if (log.isInfoEnabled()) {
            log.info(GraphVizGenerator.generateGraphViz(unfolded));
        }

        //build step graph of unfolded net
        final var unfoldedGraph = PetriNetSimulator.buildStepGraph(unfolded);
        if (log.isInfoEnabled()) {
            log.info(String.format("Step Graph has %d possible combinations!", unfoldedGraph.getSteps().size()));
        }

        //get possible parallel executions of transitions from the calculated stepgraph
        final var parallelSets = PetriNetSimulator.getParallelSets(unfoldedGraph);
        if (log.isInfoEnabled()) {
            log.info(String.format("Found %d possible parallel executions!", parallelSets.size()));
        }

        //evaluate: 3 transitions are reading data in parallel
        final var result = ParallelEvaluator.nParallelTransitionsWithCondition(x -> x.getContext().getRead() != null && x.getContext().getRead().equals("data"), 3, parallelSets);
        if (log.isInfoEnabled()) {
            log.info(String.format("3 parallel reading Transitions: %s", result));
        }
    }

    /**
     * @param input A List
     * @param <T> Generic Type for given list
     * @return a random sublist with a size between MINIMUM_STARTEND and MAXIMUM_STARTEND
     */
    public static <T> ArrayList<? extends T> randomSubList(final List<T> input) {
        final var newSize = ThreadLocalRandom.current().nextInt(MINIMUM_STARTEND,MAXIMUM_STARTEND);
        final var list = new ArrayList<>(input);
        Collections.shuffle(list);
        final ArrayList<T> newList = new ArrayList<>();
        for(int i = 0; i< newSize; i++){
            newList.add(list.get(i));
        }
        return newList;
    }

    /**
     * Build the example PetriNet from the paper, to evaluate formulas on
     * @return Example PetriNet described in the WFDU Paper
     */
    private PetriNet buildPaperNet(){
        //create nodes
        final var start = new PlaceImpl(URI.create("place://start"));
        start.setMarkers(1);
        final var copy = new PlaceImpl(URI.create("place://copy"));
        final var init = new PlaceImpl(URI.create("place://init"));
        final var dat1 = new PlaceImpl(URI.create("place://data1"));
        final var dat2 = new PlaceImpl(URI.create("place://data2"));
        final var con1 = new PlaceImpl(URI.create("place://control1"));
        final var con2 = new PlaceImpl(URI.create("place://control2"));
        final var con3 = new PlaceImpl(URI.create("place://control3"));
        final var con4 = new PlaceImpl(URI.create("place://control4"));
        final var sample = new PlaceImpl(URI.create("place://sample"));
        final var mean = new PlaceImpl(URI.create("place://mean"));
        final var med = new PlaceImpl(URI.create("place://median"));
        final var rules = new PlaceImpl(URI.create("place://rules"));
        final var stor1 = new PlaceImpl(URI.create("place://stored1"));
        final var stor2 = new PlaceImpl(URI.create("place://stored2"));
        final var stor3 = new PlaceImpl(URI.create("place://stored3"));
        final var stor4 = new PlaceImpl(URI.create("place://stored4"));
        final var end = new PlaceImpl(URI.create("place://end"));
        final var nodes = new HashSet<Node>(List.of(start, copy, init, dat1, dat2, con1, con2, con3, con4, sample, mean, med, rules, stor1, stor2, stor3, stor4, end));
        //create transitions with context
        final var initTrans = new TransitionImpl(URI.create("trans://init"));
        initTrans.setContextObject(new ContextObject(Collections.emptySet(), null, null, null, ContextObject.TransType.CONTROL));

        final var getData = new TransitionImpl(URI.create("trans://getData"));
        getData.setContextObject(new ContextObject(new HashSet<>(Arrays.asList("")), null, new HashSet<>(Arrays.asList("data")), null, ContextObject.TransType.APP));

        final var copyData = new TransitionImpl(URI.create("trans://copyData"));
        copyData.setContextObject(new ContextObject(new HashSet<>(Arrays.asList("a")), new HashSet<>(Arrays.asList("data")), new HashSet<>(Arrays.asList("data")), null, ContextObject.TransType.APP));

        final var extract = new TransitionImpl(URI.create("trans://extractSample"));
        extract.setContextObject(new ContextObject(new HashSet<>(Arrays.asList("france")), new HashSet<>(Arrays.asList("data")), new HashSet<>(Arrays.asList("sample")), new HashSet<>(Arrays.asList("data")), ContextObject.TransType.APP));

        final var calcMean = new TransitionImpl(URI.create("trans://calcMean"));
        calcMean.setContextObject(new ContextObject(new HashSet<>(Arrays.asList("france")), new HashSet<>(Arrays.asList("data")), new HashSet<>(Arrays.asList("mean")), new HashSet<>(Arrays.asList("data")), ContextObject.TransType.APP));

        final var calcMed = new TransitionImpl(URI.create("trans://calcMedian"));
        calcMed.setContextObject(new ContextObject(new HashSet<>(Arrays.asList("france")), new HashSet<>(Arrays.asList("data")), new HashSet<>(Arrays.asList("median")), new HashSet<>(Arrays.asList("data")), ContextObject.TransType.APP));

        final var calcRules = new TransitionImpl(URI.create("trans://calcAPrioriRules"));
        calcRules.setContextObject(new ContextObject(new HashSet<>(Arrays.asList("france", "high_performance")), new HashSet<>(Arrays.asList("data")), new HashSet<>(Arrays.asList("rules")), new HashSet<>(Arrays.asList("data")), ContextObject.TransType.APP));

        final var store1 = new TransitionImpl(URI.create("trans://storeData1"));
        store1.setContextObject(new ContextObject(Collections.emptySet(), new HashSet<>(Arrays.asList("sample")), null, new HashSet<>(Arrays.asList("sample")), ContextObject.TransType.APP));

        final var store2 = new TransitionImpl(URI.create("trans://storeData2"));
        store2.setContextObject(new ContextObject(Collections.emptySet(), new HashSet<>(Arrays.asList("mean")), null, new HashSet<>(Arrays.asList("mean")), ContextObject.TransType.APP));

        final var store3 = new TransitionImpl(URI.create("trans://storeData3"));
        store3.setContextObject(new ContextObject(Collections.emptySet(), new HashSet<>(Arrays.asList("median")), null, new HashSet<>(Arrays.asList("median")), ContextObject.TransType.APP));

        final var store4 = new TransitionImpl(URI.create("trans://storeData4"));
        store4.setContextObject(new ContextObject(Collections.emptySet(), new HashSet<>(Arrays.asList("rules")), null, new HashSet<>(Arrays.asList("rules")), ContextObject.TransType.APP));

        final var endTrans = new TransitionImpl(URI.create("trans://end"));
        endTrans.setContextObject(new ContextObject(Collections.emptySet(), null, null, null, ContextObject.TransType.CONTROL));
        nodes.addAll(List.of(initTrans, getData, copyData, extract, calcMean, calcMed, calcRules, store1, store2, store3, store4, endTrans));

        //create arcs
        final var arcs = new HashSet<Arc>();
        arcs.add(new ArcImpl(start, initTrans));
        arcs.add(new ArcImpl(initTrans, copy));
        arcs.add(new ArcImpl(initTrans, copy));
        arcs.add(new ArcImpl(initTrans, init));
        arcs.add(new ArcImpl(init, getData));
        arcs.add(new ArcImpl(getData, dat1));
        arcs.add(new ArcImpl(copy, copyData));
        arcs.add(new ArcImpl(dat1, copyData));
        arcs.add(new ArcImpl(copyData, dat1));
        arcs.add(new ArcImpl(copyData, dat2));
        arcs.add(new ArcImpl(getData, con1));
        arcs.add(new ArcImpl(getData, con2));
        arcs.add(new ArcImpl(getData, con3));
        arcs.add(new ArcImpl(getData, con4));
        arcs.add(new ArcImpl(dat2, extract));
        arcs.add(new ArcImpl(dat2, calcMean));
        arcs.add(new ArcImpl(dat2, calcMed));
        arcs.add(new ArcImpl(dat2, calcRules));
        arcs.add(new ArcImpl(con1, extract));
        arcs.add(new ArcImpl(con2, calcMean));
        arcs.add(new ArcImpl(con3, calcMed));
        arcs.add(new ArcImpl(con4, calcRules));
        arcs.add(new ArcImpl(extract, sample));
        arcs.add(new ArcImpl(calcMean, mean));
        arcs.add(new ArcImpl(calcMed, med));
        arcs.add(new ArcImpl(calcRules, rules));
        arcs.add(new ArcImpl(extract, copy));
        arcs.add(new ArcImpl(calcMean, copy));
        arcs.add(new ArcImpl(calcMed, copy));
        arcs.add(new ArcImpl(calcRules, copy));
        arcs.add(new ArcImpl(sample, store1));
        arcs.add(new ArcImpl(mean, store2));
        arcs.add(new ArcImpl(med, store3));
        arcs.add(new ArcImpl(rules, store4));
        arcs.add(new ArcImpl(store1, stor1));
        arcs.add(new ArcImpl(store2, stor2));
        arcs.add(new ArcImpl(store3, stor3));
        arcs.add(new ArcImpl(store4, stor4));
        arcs.add(new ArcImpl(stor1, endTrans));
        arcs.add(new ArcImpl(stor2, endTrans));
        arcs.add(new ArcImpl(stor3, endTrans));
        arcs.add(new ArcImpl(stor4, endTrans));
        arcs.add(new ArcImpl(endTrans, end));
        //create petriNet and visualize
        return new PetriNetImpl(URI.create("https://petrinet"), nodes, arcs);
    }
}
