package co.winmon.groupbuy.service;

import co.winmon.groupbuy.dto.line.*;
import co.winmon.groupbuy.exception.LoginFailedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys; // *** 需要 Import ***
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey; // *** 需要 Import ***
import java.nio.charset.StandardCharsets; // *** 需要 Import ***

@Service
public class LineApiService {

    private final RestTemplate restTemplate;
    private final String channelId;
    private final String channelSecret;
    private final String redirectUri;
    private final JwtParser jwtParser; // (這個不變)

    private static final String LINE_TOKEN_URL = "https://api.line.me/oauth2/v2.1/token";
    private static final String LINE_PROFILE_URL = "https://api.line.me/v2/profile";
    private static final String LINE_VERIFY_URL = "https://api.line.me/oauth2/v2.1/verify";
    private static final String LINE_ISSUER = "https://access.line.me";


    // *** 修正 #1: 更新建構子 ***
    // 我們必須在這裡就設定好 Parser，讓它知道如何驗證簽名
    public LineApiService(RestTemplate restTemplate,
                          @Value("${line.login.channel-id}") String channelId,
                          @Value("${line.login.channel-secret}") String channelSecret,
                          @Value("${line.login.redirect-uri}") String redirectUri) {
        this.restTemplate = restTemplate;
        this.channelId = channelId;
        this.channelSecret = channelSecret;
        this.redirectUri = redirectUri;

        // 1. 從 channelSecret 建立一個 HS256 的金鑰
        SecretKey key = Keys.hmacShaKeyFor(channelSecret.getBytes(StandardCharsets.UTF_8));

        // 2. 建立一個 "會驗證" 的 Parser
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)         // <-- 關鍵：設定簽名金鑰 (HS256)
                .requireIssuer(LINE_ISSUER) // <-- 關鍵：要求 'iss' 必須是 Line
                .requireAudience(channelId) // <-- 關鍵：要求 'aud' 必須是我們的 Channel ID
                .build();
    }

    /**
     * Web Login Flow: 用 code 交換 access_token 和 id_token
     */
    public LineTokenResponse exchangeCodeForToken(String code) {
        // (這個方法保持不變)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", code);
        map.add("redirect_uri", redirectUri);
        map.add("client_id", channelId);
        map.add("client_secret", channelSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<LineTokenResponse> response = restTemplate.postForEntity(
                    LINE_TOKEN_URL, request, LineTokenResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new LoginFailedException("Line Token API 請求失敗: " + e.getResponseBodyAsString(), e);
        }
    }


    // *** 修正 #2: 更新 parseAndVerifyIdToken 方法 ***
    /**
     * Web Login Flow: 解析並驗證 ID Token
     */
    public LineIdTokenPayload parseAndVerifyIdToken(String idToken) {
        try {
            // *** 核心修正 ***

            // 1. 使用 .parseClaimsJws() (JWS = JSON Web Signature)
            // 2. 傳入 "完整" 的 idToken (不再需要 substring 移除簽名)
            // 3. jwtParser 會自動驗證 簽名, iss, aud
            //    (如果任何一項失敗，都會拋出 Exception)
            Claims claims = jwtParser.parseClaimsJws(idToken)
                    .getBody();

            // (舊的 substring 和手動驗證 iss/aud 程式碼都可以刪除了)

            // 手動映射到 DTO
            LineIdTokenPayload payload = new LineIdTokenPayload();
            payload.setIss(claims.getIssuer());
            payload.setSub(claims.getSubject());
            payload.setAud(claims.getAudience()); // .getAudience() 回傳 String
            payload.setExp(claims.getExpiration().getTime() / 1000);
            payload.setIat(claims.getIssuedAt().getTime() / 1000);

            // 使用強型別的 getter
            payload.setName(claims.get("name", String.class));
            payload.setPicture(claims.get("picture", String.class));
            payload.setEmail(claims.get("email", String.class));

            return payload;

        } catch (Exception e) {
            e.printStackTrace();

            throw new LoginFailedException("ID Token 解析或驗證失敗: " + e.getMessage(), e);
        }
    }


    /**
     * LIFF Flow: 驗證 LIFF 的 Access Token
     * (這個方法保持不變)
     */
    public void verifyLiffToken(String liffAccessToken) {
        String url = LINE_VERIFY_URL + "?access_token=" + liffAccessToken;
        try {
            LineVerifyResponse response = restTemplate.getForObject(url, LineVerifyResponse.class);
            if (response == null || !response.getClientId().equals(channelId)) {
                throw new LoginFailedException("LIFF Token 驗證失敗: Client ID 不匹配");
            }
        } catch (HttpClientErrorException e) {
            throw new LoginFailedException("Line Verify API 請求失敗: " + e.getResponseBodyAsString(), e);
        }
    }

    /**
     * LIFF Flow: 用 LIFF Access Token 獲取使用者資料
     * (這個方法保持不變)
     */
    public LineProfile getProfileFromLiffToken(String liffAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(liffAccessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<LineProfile> response = restTemplate.exchange(
                    LINE_PROFILE_URL, HttpMethod.GET, entity, LineProfile.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new LoginFailedException("Line Profile API 請求失敗: " + e.getResponseBodyAsString(), e);
        }
    }
}