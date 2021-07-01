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
package io.configmanager.extensions.routes.petrinet.evaluation.formula.state;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Custom Expression to be evaluated on a {@link de.fraunhofer.isst.configmanager.extensions.routes.petrinet.model.Place}.
 */
@Getter
@AllArgsConstructor
public class NodeExpression {

    /**
     * Subexpression (function from {@link de.fraunhofer.isst.configmanager.extensions.routes.petrinet.model.Place} to boolean.
     */
    private NodeSubExpression subExpression;

    /**
     * Information message to return when subExpression is not fulfilled by a transition.
     */
    private String message;

    public static NodeExpression nodeExpression(final NodeSubExpression nodeSubExpression,
                                                final String message) {
        return new NodeExpression(nodeSubExpression, message);
    }
}
