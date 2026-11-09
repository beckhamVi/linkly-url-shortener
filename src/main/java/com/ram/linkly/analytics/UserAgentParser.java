package com.ram.linkly.analytics;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Component;

@Component
public class UserAgentParser {

    private final UserAgentAnalyzer analyzer = UserAgentAnalyzer.newBuilder()
            .hideMatcherLoadStats()
            .withCache(10_000)
            .build();

    public ParsedAgent parse(String userAgentString) {
        if (userAgentString == null || userAgentString.isBlank()) {
            return new ParsedAgent("Unknown", "Unknown", "Unknown");
        }

        UserAgent agent = analyzer.parse(userAgentString);
        return new ParsedAgent(
                agent.getValue("AgentName"),
                agent.getValue("OperatingSystemNameVersion"),
                agent.getValue("DeviceClass"));
    }

    public record ParsedAgent(String browser, String operatingSystem, String deviceType) {
    }
}