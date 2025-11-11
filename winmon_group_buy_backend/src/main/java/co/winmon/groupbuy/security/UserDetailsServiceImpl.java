package co.winmon.groupbuy.security;

import co.winmon.groupbuy.entity.CustomerDetailsEntity;
import co.winmon.groupbuy.repository.CustomerDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CustomerDetailsRepository customerDetailsRepository;

    // Spring Security 默認使用 'username'
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 在我們的系統中，username 可能是 email 或 Line User ID，這裡我們假設是 email (如果有的話)
        // 但由於我們是 Line 登入，我們需要一個方法來通過 ID 加載
        // 這裡我們先用 email (或唯一 ID) 來滿足 UserDetailsService 介面
        CustomerDetailsEntity customerDetailsEntity = customerDetailsRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        return UserPrincipal.create(customerDetailsEntity);
    }

    // 這是我們在 JwtAuthFilter 中真正會用到的
    @Transactional
    public UserDetails loadUserById(String id) {
        CustomerDetailsEntity customerDetailsEntity = customerDetailsRepository.findById(id) // 假設 User ID 是 String (e.g. UUID)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return UserPrincipal.create(customerDetailsEntity);
    }
}