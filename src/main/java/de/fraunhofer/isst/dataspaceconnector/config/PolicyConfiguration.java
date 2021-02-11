package de.fraunhofer.isst.dataspaceconnector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PolicyConfiguration {
    @Value("${policy.negotiation}")
    private boolean policyNegotiation;

    @Value("${policy.allow-unsupported-patterns}")
    private boolean unsupportedPatterns;

    @Value("${policy.framework}")
    private PolicyFramework policyFramework;

    public boolean isPolicyNegotiation() {
        return policyNegotiation;
    }

    public void setPolicyNegotiation(boolean policyNegotiation) {
        this.policyNegotiation = policyNegotiation;
    }

    public boolean isUnsupportedPatterns() {
        return unsupportedPatterns;
    }

    public void setUnsupportedPatterns(boolean unsupportedPatterns) {
        this.unsupportedPatterns = unsupportedPatterns;
    }

    public PolicyFramework getPolicyFramework() {
        return policyFramework;
    }

    public void setPolicyFramework(PolicyFramework policyFramework) {
        this.policyFramework = policyFramework;
    }

    private enum PolicyFramework {
        INTERNAL("internal"),
        MY_DATA("mydata");

        private final String pattern;

        PolicyFramework(String string) {
            pattern = string;
        }

        @Override
        public String toString() {
            return pattern;
        }
    }
}
