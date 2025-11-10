package co.winmon.groupbuy.controller;

import co.winmon.groupbuy.component.JwtTokenProvider;
import co.winmon.groupbuy.dto.AuthResponse;
import co.winmon.groupbuy.dto.LiffLoginRequest;
import co.winmon.groupbuy.model.User;
import co.winmon.groupbuy.repository.UserRepository;
import co.winmon.groupbuy.service.LineApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth") // 監聽 /auth 路徑
public class AuthController {

    private final LineApiService lineApiService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository; // 注入 UserRepository

    public AuthController(LineApiService lineApiService,
                          JwtTokenProvider jwtTokenProvider,
                          UserRepository userRepository) { // 添加 UserRepository
        this.lineApiService = lineApiService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    /**
     * 專門處理 LIFF (Line Front-end Framework) 登入
     * @param request 包含從 LIFF 取得的 Line Access Token
     * @return 包含我們系統 JWT 的 AuthResponse
     */
    @PostMapping("/line/liff-login")
    public ResponseEntity<?> authenticateLiffUser(@RequestBody LiffLoginRequest request) {
        try {
            // 1. 使用 Line Access Token 獲取 Line 用戶資料
            // 這裡我們假設 LineApiService 提供了 findOrCreateUser 方法
            // (這個方法在 Service 層結合了 API 呼叫和資料庫儲存邏輯)

            // 為了確保，我們在這裡手動實現 CustomOAuth2UserService 中的邏輯
            LineApiService.LineProfile profile = lineApiService.getUserProfile(request.getLineAccessToken())
                    .block(); // 在 Web (Servlet) 環境中，我們 'block' 來等待 WebFlux 的回應

            if (profile == null) {
                throw new RuntimeException("無法從 Line 獲取用戶資料");
            }

            // 2. 查找或建立用戶 (與 CustomOAuth2UserService 相同的邏輯)
            User user = userRepository.findByLineUserId(profile.getUserId())
                    .map(existingUser -> {
                        // 更新現有用戶
                        existingUser.setDisplayName(profile.getDisplayName());
                        existingUser.setPictureUrl(profile.getPictureUrl());
                        existingUser.setEmail(profile.getEmail()); // Line Profile 預設可能為 null
                        return userRepository.save(existingUser);
                    })
                    .orElseGet(() -> {
                        // 建立新用戶
                        User newUser = new User();
                        newUser.setLineUserId(profile.getUserId());
                        newUser.setDisplayName(profile.getDisplayName());
                        newUser.setPictureUrl(profile.getPictureUrl());
                        newUser.setEmail(profile.getEmail());
                        newUser.setRole("ROLE_USER");
                        return userRepository.save(newUser);
                    });


            // 3. 驗證成功，產生我們 "自己" 的 JWT
            // 注意：我們是基於 user 物件中的 Line ID 來產生 token
            String jwt = jwtTokenProvider.generateToken(user.getLineUserId());

            // 4. 回傳 JWT 給 LIFF 前端
            return ResponseEntity.ok(new AuthResponse(jwt));

        } catch (Exception e) {
            // 處理驗證失敗 (例如 Line Token 無效)
            System.err.println("!!! LIFF 登入失敗 !!!");
            System.err.println("錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).body("LIFF 登入失敗: " + e.getMessage());
        }
    }
}