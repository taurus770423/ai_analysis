package co.winmon.groupbuy.service.impl;

import co.winmon.groupbuy.model.User;
import co.winmon.groupbuy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 呼叫 Line API 取得用戶資料
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. 處理用戶資料並存入資料庫
        return processOAuth2User(oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String lineUserId = (String) attributes.get("userId"); // Line API 回傳的是 userId

        Optional<User> userOptional = userRepository.findByLineUserId(lineUserId);
        User user;

        if (userOptional.isPresent()) {
            // 用戶已存在，更新資料
            user = userOptional.get();
            user.setDisplayName((String) attributes.get("displayName"));
            user.setPictureUrl((String) attributes.get("pictureUrl"));
            // 'email' 範圍需要額外申請，這裡假設有拿到
            user.setEmail((String) attributes.get("email"));
        } else {
            // 新用戶，建立資料
            user = new User();
            user.setLineUserId(lineUserId);
            user.setDisplayName((String) attributes.get("displayName"));
            user.setPictureUrl((String) attributes.get("pictureUrl"));
            user.setEmail((String) attributes.get("email"));
            user.setRole("ROLE_USER");
        }

        userRepository.save(user);

        // 返回我們自訂的 User 物件 (它也實作了 OAuth2User)
        return user;
    }
}