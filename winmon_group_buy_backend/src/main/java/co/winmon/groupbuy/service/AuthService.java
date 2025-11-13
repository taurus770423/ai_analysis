// src/main/java/co/winmon/groupbuy/service/AuthService.java
package co.winmon.groupbuy.service;

import co.winmon.groupbuy.dto.JwtResponse;
import co.winmon.groupbuy.dto.LineLoginRequest;
import co.winmon.groupbuy.dto.line.LineIdTokenPayload;
import co.winmon.groupbuy.dto.line.LineProfile;
import co.winmon.groupbuy.dto.line.LineTokenResponse;
import co.winmon.groupbuy.entity.UserDetailsEntity;
import co.winmon.groupbuy.entity.UserLineDetailsEntity;
import co.winmon.groupbuy.entity.key.UserLineDetailsKey;
import co.winmon.groupbuy.exception.LoginFailedException;
import co.winmon.groupbuy.repository.UserDetailsRepository;
import co.winmon.groupbuy.repository.UserLineDetailsRepository;
import co.winmon.groupbuy.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final LineApiService lineApiService;
    private final UserService userService;
    private final UserDetailsRepository userDetailsRepository;
    private final UserLineDetailsRepository userLineDetailsRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public JwtResponse lineLogin(LineLoginRequest request) {
        if (request.getAccessToken() != null) {
            // 流程 1: LIFF 登入 (使用 accessToken)
            log.info("User login by access token: {}", request.getAccessToken());
            return lineLiffLogin(request.getAccessToken());
        } else if (request.getCode() != null) {
            // 流程 2: Web 登入 (使用 code)
            log.info("User login by code {}", request.getCode());
            return lineWebLogin(request.getCode());
        } else {
            throw new LoginFailedException("請求無效，缺少 code 或 accessToken");
        }
    }

    private JwtResponse lineWebLogin(String code) {
        // 1. 用 Code 換 Token
        LineTokenResponse tokenResponse = lineApiService.exchangeCodeForToken(code);

        // 2. 解析並驗證 ID Token
        LineIdTokenPayload payload = lineApiService.parseAndVerifyIdToken(tokenResponse.getIdToken());

        // 3. 處理使用者資料並簽發 JWT
        UserDetailsEntity customerDetails = processLineLogin(payload.getSub(), payload.getName(), payload.getPicture(), payload.getEmail());
        String jwt = jwtTokenProvider.generateToken(customerDetails.getId());
        return new JwtResponse(jwt);
    }

    private JwtResponse lineLiffLogin(String liffAccessToken) {
        // 1. (安全性) 驗證此 Token 是否是發給我們的 Channel
        lineApiService.verifyLiffToken(liffAccessToken);

        // 2. 獲取使用者 Profile
        LineProfile profile = lineApiService.getProfileFromLiffToken(liffAccessToken);

        // 3. 處理使用者資料並簽發 JWT
        // (LIFF 預設拿不到 Email，除非您有申請並在 scope 中加入)
        UserDetailsEntity customerDetails = this.processLineLogin(profile.getUserId(), profile.getDisplayName(), profile.getPictureUrl(), null);
        String jwt = jwtTokenProvider.generateToken(customerDetails.getId());
        return new JwtResponse(jwt);
    }

    private UserDetailsEntity processLineLogin(String lineUserId, String displayName, String pictureUrl, String email) {
        Optional<UserDetailsEntity> customerOpt = this.userDetailsRepository.findByLineUserId(lineUserId);
        UserDetailsEntity customer;
        if(customerOpt.isPresent()) {
            customer = customerOpt.get();
            customer.setLastLoginTime(Instant.now());
            customer = this.userDetailsRepository.save(customer);
        } else {
            customer = this.userDetailsRepository.save(UserDetailsEntity.builder().build());
        }

        Optional<UserLineDetailsEntity> customerLineOpt = this.userLineDetailsRepository.findBylineUserId(lineUserId);
        if(customerLineOpt.isPresent()) {
            customerLineOpt.get().setDisplayName(displayName);
            customerLineOpt.get().setPictureUrl(pictureUrl);
            customerLineOpt.get().setEmail(email);
            this.userLineDetailsRepository.save(customerLineOpt.get());
        } else {
            UserLineDetailsEntity customerLineDetails = UserLineDetailsEntity.builder()
                    .id(
                        UserLineDetailsKey.builder()
                            .userId(customer.getId())
                            .lineUserId(lineUserId)
                            .build()
                    ).displayName(displayName)
                    .pictureUrl(pictureUrl)
                    .email(email)
                    .build();
            this.userLineDetailsRepository.save(customerLineDetails);
        }

        return customer;
    }
}