package io.dataspaceconnector.extension.monitoring;

import org.apache.camel.CamelContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = { CamelInfoContributorExt.class })
class CamelInfoContributorExtTest {

    @Autowired
    private CamelInfoContributorExt camelInfoContributor;

    @MockBean
    private CamelContext camelContext;

    @Test
    @WithMockUser("ADMIN")
    public void contribute_validContextInformation_equals() {
        /* ARRANGE */
        var builder = new Info.Builder();
        camelInfoContributor.contribute(builder);

        Map<String, Object> map = Map.of(
                "name", "camel-1",
                "version", "3.11.2",
                "startDate", "2021-09-23T12:04:42.682+00:00",
                "uptime", "6s783ms",
                "status", "status"
        );

        Mockito.doReturn("camel-1").when(camelContext).getName();
        Mockito.doReturn("3.11.2").when(camelContext).getVersion();
        Mockito.doReturn("2021-09-23T12:04:42.682+00:00").when(camelContext).getStartDate();
        Mockito.doReturn("6s783ms").when(camelContext).getUptime();
        Mockito.doReturn("status").when(camelContext).getStatus();

        /* ACT */
        camelInfoContributor.contribute(builder);
        var info = builder.build();

        /* ASSERT */
        assertEquals(map, info.get("camel"));
    }
}
