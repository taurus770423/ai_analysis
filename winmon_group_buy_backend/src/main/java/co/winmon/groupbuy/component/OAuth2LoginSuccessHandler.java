package co.winmon.groupbuy.component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${frontend.web-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. 產生我們自己的 JWT
        String token = jwtTokenProvider.generateToken(authentication);

        // 2. 準備重導向回 React 前端的 URL，並附上 token
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                .path("/auth/callback") // React 接收 token 的路徑
                .queryParam("token", token)
                .build().toUriString();

        // 3. 清除 Spring Security 建立的 session (因為我們要 stateless)
        clearAuthenticationAttributes(request);

        // 4. 重導向
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}