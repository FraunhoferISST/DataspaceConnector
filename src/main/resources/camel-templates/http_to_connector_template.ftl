<routes xmlns="http://camel.apache.org/schema/spring">

    <route id="${routeId}" errorHandlerRef="${errorHandlerRef}">

        <from uri="direct:${routeId}"/>

        <setHeader name="CamelHttpMethod"><constant>GET</constant></setHeader>
        <#if genericEndpointAuthHeader??>
            <setHeader name="Authorization"><constant>${genericEndpointAuthHeader}</constant></setHeader>
        </#if>
        <to uri="${startUrl}"/>

        <convertBodyTo type="java.lang.String"/>
        <log message="Fetched data: ${r"${body}"}"/>

        <#list routeStepEndpoints as endpoint>
            <setHeader name="CamelHttpMethod"><constant>$endpoint.getHttpMethod().toString()</constant></setHeader>
            <to uri="${endpoint.getEndpointUrl().toString()}"/>
        </#list>

        <#if endUrl??>
            <setHeader name="CamelHttpMethod"><constant>PUT</constant></setHeader>
            <setHeader name="Authorization"><constant>${connectorAuthHeader}</constant></setHeader>
            <to uri="${endUrl}"/>
        </#if>

    </route>

</routes>
