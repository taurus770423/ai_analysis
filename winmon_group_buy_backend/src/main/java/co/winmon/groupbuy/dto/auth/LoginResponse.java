package co.winmon.groupbuy.dto.auth;

public record LoginResponse(
        String accessToken
) {
    public String getTokenType() {
        return "Bearer";
    }
}