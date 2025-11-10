package co.winmon.groupbuy.dto;

import co.winmon.groupbuy.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    /**
     * 取得當前登入者 (已通過 JWT 驗證) 的資料
     * React 前端在登入成功後應呼叫此 API
     * @param user Spring Security 會自動從 JWT 解析並注入當前用戶
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal User user) {
        // user 物件是從 JwtAuthenticationFilter -> SecurityContextHolder 來的
        return ResponseEntity.ok(user);
    }
}
