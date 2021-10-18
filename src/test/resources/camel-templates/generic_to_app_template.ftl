<routes xmlns="http://camel.apache.org/schema/spring">

    <route id="${routeId}" errorHandlerRef="${errorHandlerRef}">

        <from uri="timer://generic-to-app-route?fixedRate=true&amp;period=60000"/>

        <setHeader name="CamelHttpMethod"><constant>GET</constant></setHeader>
        <#if genericEndpointAuthHeaderKey?? && genericEndpointAuthHeaderValue??>
            <setHeader name="${genericEndpointAuthHeaderKey}"><constant>${genericEndpointAuthHeaderValue}</constant></setHeader>
        </#if>
        <to uri="$startUrl"/>

        <convertBodyTo type="java.lang.String"/>
        <log message="Sending data: ${r"${body}"}"/>

        <#list routeStepEndpoints as endpoint>
            <setHeader name="CamelHttpMethod"><constant>${endpoint.getHttpMethod().toString()}</constant></setHeader>
            <to uri="${endpoint.getEndpointUrl()}"/>
        </#list>

        <setHeader name="CamelHttpMethod"><constant>POST</constant></setHeader>
        <to uri="$endUrl"/>

    </route>

</routes>
