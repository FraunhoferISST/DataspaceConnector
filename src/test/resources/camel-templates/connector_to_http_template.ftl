<routes xmlns="http://camel.apache.org/schema/spring">

    <route id="${routeId}" errorHandlerRef="${errorHandlerRef}">

        <from uri="timer://foo?fixedRate=true&amp;period=60000"/>

        <setHeader name="CamelHttpMethod"><constant>GET</constant></setHeader>
        <setHeader name="Authorization"><constant>${connectorAuthHeader}</constant></setHeader>
        <to uri="${startUrl}"/>

        <convertBodyTo type="java.lang.String"/>
        <log message="Fetched data: ${r"${body}"}"/>

        <#list routeStepEndpoints as endpoint>
            <setHeader name="CamelHttpMethod"><constant>${endpoint.getHttpMethod().toString()}</constant></setHeader>
            <to uri="${endpoint.getEndpointUrl().toString()}"/>
        </#list>

        <setHeader name="CamelHttpMethod"><constant>POST</constant></setHeader>
        <#if genericEndpointAuthHeader??>
            <setHeader name="Authorization"><constant>${genericEndpointAuthHeader}</constant></setHeader>
        </#if>
        <to uri="${endUrl}"/>

    </route>

</routes>
