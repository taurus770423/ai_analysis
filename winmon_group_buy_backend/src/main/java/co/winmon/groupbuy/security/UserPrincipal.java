package co.winmon.groupbuy.security;

import co.winmon.groupbuy.entity.CustomerDetailsEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final String id;
    private final String username; // 在我們這裡，這可能是 email 或 lineUserId
    private final String password; // 設為 null，因為是 Line 登入
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(String id, String username, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = null;
        this.authorities = authorities;
    }

    public static UserPrincipal create(CustomerDetailsEntity customerDetailsEntity) {
//        List<GrantedAuthority> authorities = customerDetailsEntity.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
//                .collect(Collectors.toList());
        List<GrantedAuthority> authorities = Collections.emptyList();

        // 假設 User 的 ID 是 String (UUID)
        // 如果是 Long, 需轉換 String.valueOf(user.getId())
        return new UserPrincipal(
                customerDetailsEntity.getId(),
                customerDetailsEntity.getLineUserId(), // 使用 lineUserId 作為 Spring Security 的 'username'
                authorities
        );
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    // 省略 equals 和 hashCode
}