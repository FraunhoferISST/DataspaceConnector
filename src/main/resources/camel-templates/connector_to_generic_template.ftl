<routes xmlns="http://camel.apache.org/schema/spring">

    <route id="${routeId}" errorHandlerRef="${errorHandlerRef}">

        <from uri="direct:${routeId}"/>

        <#if startUrl??>
            <setHeader name="CamelHttpMethod"><constant>GET</constant></setHeader>
            <setHeader name="Authorization"><constant>${connectorAuthHeader}</constant></setHeader>
            <to uri="${startUrl}"/>
        </#if>

        <convertBodyTo type="java.lang.String"/>
        <log message="Sending data: ${r"${body}"}"/>

        <#list routeStepEndpoints as endpoint>
            <setHeader name="CamelHttpMethod"><constant>${endpoint.getHttpMethod().toString()}</constant></setHeader>
            <to uri="${endpoint.getEndpointUrl()}"/>
        </#list>

        <setHeader name="CamelHttpMethod"><constant>POST</constant></setHeader>
        <#if genericEndpointAuthHeaderKey?? && genericEndpointAuthHeaderValue??>
            <setHeader name="${genericEndpointAuthHeaderKey}"><constant>${genericEndpointAuthHeaderValue}</constant></setHeader>
        </#if>
        <process ref="headerProcessor"/>
        <to uri="${endUrl}"/>

    </route>

</routes>
