package co.winmon.groupbuy.service.impl;

import co.winmon.groupbuy.service.LineApiService;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LineApiServiceImpl implements LineApiService {
    private final WebClient webClient;

    public LineApiServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.line.me/v2").build();
    }

    /**
     * 使用 LIFF 提供的 Access Token 獲取 Line 用戶資料
     */
    @Override
    public Mono<LineProfile> getUserProfile(String lineAccessToken) {
        return this.webClient.get()
            .uri("/profile")
            .header("Authorization", "Bearer " + lineAccessToken)
            .retrieve()
            .bodyToMono(LineProfile.class);
    }
}
