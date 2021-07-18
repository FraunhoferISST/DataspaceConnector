package io.dataspaceconnector.camel.processor;

import java.net.URI;
import java.util.Optional;

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.service.message.processing.ClearingHouseService;
import lombok.SneakyThrows;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ClearingHouseLoggingProcessor.class})
public class ClearingHouseLoggingProcessorTest {

    @Mock
    private Exchange exchange;

    @Mock
    private Message in;

    @MockBean
    private ClearingHouseService clearingHouseService;

    @Autowired
    private ClearingHouseLoggingProcessor processor;

    @BeforeEach
    public void init() {
        when(exchange.getIn()).thenReturn(in);
        doNothing().when(clearingHouseService).logIdsMessage(any());
    }

    @Test
    @SneakyThrows
    public void process_requestInBody_logMessageHeader() {
        /* ARRANGE */
        final var message = getMessage();
        final var request = new Request<>(message, null, Optional.empty());

        when(in.getBody(Request.class)).thenReturn(request);

        /* ACT */
        processor.process(exchange);

        /* ASSERT */
        verify(clearingHouseService, times(1)).logIdsMessage(message);
    }

    @Test
    @SneakyThrows
    public void process_responseInBody_logMessageHeader() {
        /* ARRANGE */
        final var message = getMessage();
        final var response = new Response(message, "body");

        when(in.getBody(Response.class)).thenReturn(response);

        /* ACT */
        processor.process(exchange);

        /* ASSERT */
        verify(clearingHouseService, times(1)).logIdsMessage(message);
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private DescriptionRequestMessage getMessage() {
        return new DescriptionRequestMessageBuilder()
                ._issuerConnector_(URI.create("https://connector.com"))
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._securityToken_(new DynamicAttributeTokenBuilder()
                        ._tokenValue_("value")
                        ._tokenFormat_(TokenFormat.JWT)
                        .build())
                ._modelVersion_("version")
                ._senderAgent_(URI.create("https://connector.com"))
                .build();
    }

}
