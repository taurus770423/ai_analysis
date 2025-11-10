package co.winmon.groupbuy.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Builder
@Entity
@Table(name = "customer_details")
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails, OAuth2User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "line_user_id", unique = true, nullable = false, updatable = false)
    private String lineUserId;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Column(name = "email")
    private String email;

    @Builder.Default
    @Column(name = "create_time", nullable = false, updatable = false)
    private Instant createTime = Instant.now();

    @Column(name = "last_login_time")
    private Instant lastLoginTime;

    @Column(nullable = false)
    private String role; // 例如 "ROLE_USER", "ROLE_ADMIN"

    @Transient // 不存入資料庫
    private Map<String, Object> attributes;

    // --- UserDetails 介面實作 ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return null; // 我們不用密碼登入
    }

    @Override
    public String getUsername() {
        return lineUserId; // 我們使用 lineUserId 作為 Spring Security 的 "username"
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // --- OAuth2User 介面實作 ---

    @Override
    public Map<String, Object> getAttributes() {
        // 主要用於網頁版登入流程中傳遞資料
        if (attributes == null) {
            this.attributes = new HashMap<>();
            this.attributes.put("userId", this.lineUserId);
            this.attributes.put("displayName", this.displayName);
            this.attributes.put("pictureUrl", this.pictureUrl);
            this.attributes.put("email", this.email);
        }
        return attributes;
    }

    @Override
    public String getName() {
        // 必須回傳我們在 application.properties 中設定的 user-name-attribute (userId)
        return this.lineUserId;
    }
}