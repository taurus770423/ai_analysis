package co.winmon.groupbuy.service;

import co.winmon.groupbuy.service.impl.LineApiServiceImpl;
import lombok.Data;
import reactor.core.publisher.Mono;

public interface LineApiService {
    Mono<LineApiServiceImpl.LineProfile> getUserProfile(String lineAccessToken);

    @Data
    public static class LineProfile {
        private String userId;
        private String displayName;
        private String pictureUrl;
        private String email;
    }
}
