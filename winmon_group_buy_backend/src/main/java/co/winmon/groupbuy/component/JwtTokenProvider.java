package co.winmon.groupbuy.component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secret,
                            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    // 根據 Authentication (網頁版登入成功後) 產生 JWT
    public String generateToken(Authentication authentication) {
        // 在 OAuth2 流程中，principal 的 name 就是我們的 lineUserId
        String lineUserId = authentication.getName();
        return createToken(lineUserId);
    }

    // 根據 Line User ID (LIFF 登入) 產生 JWT
    public String generateToken(String lineUserId) {
        return createToken(lineUserId);
    }

    private String createToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // 從 JWT 中取得 Line User ID
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        return claims.getSubject();
    }

    // 驗證 JWT
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            // Token 驗證失敗 (過期、格式錯誤等)
            return false;
        }
    }
}