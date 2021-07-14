package io.dataspaceconnector.service;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.PermissionBuilder;
import de.fraunhofer.iais.eis.Rule;
import io.dataspaceconnector.service.message.type.ContractAgreementService;
import io.dataspaceconnector.service.message.type.ContractRequestService;
import io.dataspaceconnector.service.message.type.exceptions.InvalidResponse;
import io.dataspaceconnector.service.usagecontrol.ContractManager;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = { ContractNegotiator.class })
class ContractNegotiatorTest {

    @MockBean
    private ContractRequestService contractReqSvc;

    @MockBean
    private ContractManager contractManager;

    @MockBean
    private ContractAgreementService agreementSvc;

    @MockBean
    private EntityPersistenceService persistenceSvc;

    @Autowired
    private ContractNegotiator negotiator;

    @Test
    @SuppressWarnings("unchecked")
    public void negotiate_validInput_producesAgreement()
            throws InvalidResponse, DatatypeConfigurationException {
        /* ARRANGE */
        final var ruleList = (List<Rule>) (List<?>) Arrays.asList(new PermissionBuilder()
                                                                          ._action_(List.of(Action.NOTIFY))
                                                                          .build());
        final var request = new ContractRequestBuilder()
                ._contractStart_(DatatypeFactory.newInstance()
                                                .newXMLGregorianCalendar("2009-05-07T17:05:45.678Z"))
                .build();
        final var recipient = URI.create("https://recipient");
        Mockito.when(contractManager.buildContractRequest(eq(ruleList))).thenReturn(request);

        final var response = new HashMap<String, String>();
        response.put("payload", "Bye");
        Mockito.when(contractReqSvc.sendMessageAndValidate(eq(recipient), eq(request))).thenReturn(response);

        final var agreement = new ContractAgreementBuilder()
                ._contractStart_(DatatypeFactory.newInstance()
                                                .newXMLGregorianCalendar("2009-05-07T17:05:45.678Z"))
                .build();
        Mockito.when(contractManager.validateContractAgreement(eq(response.get("payload")), eq(request))).thenReturn(agreement);

        final var agreementId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
        Mockito.when(persistenceSvc.saveContractAgreement(eq(agreement))).thenReturn(agreementId);

        /* ACT */
        final var result = negotiator.negotiate(recipient, ruleList);

        /* ASSERT */
        Mockito.verify(agreementSvc, Mockito.atLeastOnce()).sendMessageAndValidate(eq(recipient), eq(agreement));
        assertEquals(agreementId, result);
    }
}
