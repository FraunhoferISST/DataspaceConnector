/*
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
package io.configmanager.extensions.routes.petrinet.evaluation.formula.transition;

import io.configmanager.extensions.routes.petrinet.model.Node;
import lombok.AllArgsConstructor;

import java.util.List;

import static io.configmanager.extensions.routes.petrinet.evaluation.formula.transition.TransitionEV.transitionEV;
import static io.configmanager.extensions.routes.petrinet.evaluation.formula.transition.TransitionNOT.transitionNOT;

/**
 * Evaluates to true, if there exists a path, where parameter holds for every transition.
 */
@AllArgsConstructor
public class TransitionALONG implements TransitionFormula {
    private TransitionFormula parameter;

    public static TransitionALONG transitionALONG(final TransitionFormula parameter) {
        return new TransitionALONG(parameter);
    }
    
    @Override
    public boolean evaluate(final Node node, final List<List<Node>> paths) {
        return transitionNOT(transitionEV(transitionNOT(parameter))).evaluate(node, paths);
    }

    @Override
    public String symbol() {
        return "ALONG";
    }

    @Override
    public String writeFormula() {
        return String.format("%s(%s)", symbol(), parameter.writeFormula());
    }
}
