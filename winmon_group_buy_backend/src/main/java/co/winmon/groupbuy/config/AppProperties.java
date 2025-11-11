package co.winmon.groupbuy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Line line, Cors cors) {
    public record Jwt(String secretKey, long expirationMs) {}
    public record Line(String channelId) {}
    public record Cors(String allowedOrigins) {}
}