package co.winmon.groupbuy.config;

import co.winmon.groupbuy.component.OAuth2LoginSuccessHandler;
import co.winmon.groupbuy.filter.JwtAuthenticationFilter;
import co.winmon.groupbuy.service.impl.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 啟用 @PreAuthorize 等方法級別的安全性
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${frontend.web-url}")
    private String frontendWebUrl;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 關閉 CSRF
                .cors(withDefaults()) // 啟用 CORS (會使用 WebConfig 中的設定)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 設為 STATELESS
                .authorizeHttpRequests(authorize -> authorize
                        // 1. 公開端點 (不需要驗證)
                        // 這些路徑是 Spring Boot 看到的 "應用程式內部路徑" (Nginx 轉發後, 且扣除 context-path)
                        .requestMatchers(
                                "/auth/**",               // 我們的自訂登入 (LIFF)
                                "/oauth2/**",               // Spring Security OAuth2 登入流程
                                "/login/oauth2/code/**",    // OAuth2 回呼
                                "/api/products/public",     // 公開的商品 API (範例)
                                "/h2-console/**",           // H2 資料庫 (開發用)
                                "/error"                    // Spring Boot 預設錯誤頁面
                        ).permitAll()
                        // 2. 其他所有端點都需要驗證
                        .anyRequest().authenticated()
                )
                // OAUTH2 登入設定
                .oauth2Login(oauth2 -> oauth2
                        // Spring Security "監聽" 的登入端點
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/oauth2/authorization")
                        )
                        // Spring Security "監聽" 的回呼端點
                        .redirectionEndpoint(endpoint -> endpoint
                                .baseUri("/login/oauth2/code/*")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // 處理用戶資料的服務
                        )
                        .successHandler(oAuth2LoginSuccessHandler) // 登入成功處理器
                        .failureHandler((request, response, exception) -> { // 登入失敗處理器

                            // *** 關鍵修正點 ***
                            // 將錯誤訊息印到後端 Console
                            System.err.println("!!! OAuth2 登入失敗 !!!");
                            System.err.println("錯誤類型: " + exception.getClass().getName());
                            System.err.println("錯誤訊息: " + exception.getMessage());
                            exception.printStackTrace(); // <--- 這会印出完整的錯誤堆疊

                            // 導向回前端的登入頁
                            response.sendRedirect(frontendWebUrl);
                        })
                );

        // 將 JWT 過濾器加在 Spring Security 預設的過濾器之前
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 允許 H2 Console (如果使用 H2) 顯示 frame
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}