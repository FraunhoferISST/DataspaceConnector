package io.dataspaceconnector.services.messages.handler;

import java.util.List;

import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.utils.ContractUtils;

public class RuleExtractor extends IdsTransformer<RouteMsg<ContractRequestMessageImpl, ContractRequest>,
                                                   RouteMsg<ContractRequestMessageImpl, List<Rule>> >{

    @Override
    protected RouteMsg<ContractRequestMessageImpl, List<Rule>> processInternal(
            RouteMsg<ContractRequestMessageImpl, ContractRequest> msg) throws Exception {
            return new Request<>(msg.getHeader(), ContractUtils.extractRulesFromContract(msg.getBody()));
    }

}
