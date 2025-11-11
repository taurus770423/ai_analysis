package co.winmon.groupbuy.client;

import co.winmon.groupbuy.client.dto.LineVerifyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LineAuthClient {

    private final WebClient webClient = WebClient.create("https://api.line.me");

    /**
     * 驗證 Line Access Token
     * https://developers.line.biz/en/docs/line-login/verify-access-token/
     */
    public Mono<LineVerifyResponse> verifyToken(String accessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth2/v2.1/verify")
                        .queryParam("access_token", accessToken)
                        .build())
                .retrieve()
                .bodyToMono(LineVerifyResponse.class);
    }

    /**
     * 獲取用戶 Profile (如果需要更多資訊)
     * https://developers.line.biz/en/docs/line-login/getting-user-profiles/
     */
    public Mono<LineVerifyResponse> getProfile(String accessToken) {
        return webClient.get()
                .uri("/v2/profile")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(LineVerifyResponse.class);
    }
}