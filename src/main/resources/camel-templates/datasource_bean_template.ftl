<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
       http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

    <bean id="${dataSourceId}" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="url" value="${url}"/>
        <property name="driverClassName" value="${driver}" />
        <property name="username" value="${username}" />
        <property name="password" value="${password}"/>
    </bean>

</beans>
