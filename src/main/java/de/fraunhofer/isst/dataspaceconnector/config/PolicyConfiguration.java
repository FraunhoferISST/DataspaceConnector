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
    private UsageControlFramework usageControlFramework;

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

    public UsageControlFramework getUsageControlFramework() {
        return usageControlFramework;
    }

    public void setUsageControlFramework(UsageControlFramework usageControlFramework) {
        this.usageControlFramework = usageControlFramework;
    }

    public enum UsageControlFramework {
        INTERNAL("INTERNAL"),
        MYDATA("MYDATA"),
        MYDATA_INTERCEPTOR("MYDATA_INTERCEPTOR");

        private final String pattern;

        UsageControlFramework(String string) {
            pattern = string;
        }

        @Override
        public String toString() {
            return pattern;
        }
    }
}
