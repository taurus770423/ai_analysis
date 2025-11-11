package co.winmon.groupbuy.service;

import co.winmon.groupbuy.client.LineAuthClient;
import co.winmon.groupbuy.client.dto.LineVerifyResponse;
import co.winmon.groupbuy.config.AppProperties;
import co.winmon.groupbuy.dto.auth.LineLoginRequest;
import co.winmon.groupbuy.dto.auth.LoginResponse;
import co.winmon.groupbuy.entity.CustomerDetailsEntity;
import co.winmon.groupbuy.repository.CustomerDetailsRepository;
import co.winmon.groupbuy.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LineAuthClient lineAuthClient;
    private final CustomerDetailsRepository customerDetailsRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppProperties appProperties;

    @Transactional
    public LoginResponse lineLogin(LineLoginRequest loginRequest) {

        // 1. 驗證 Line Access Token 並獲取 Profile
        // 注意: WebClient (WebFlux) 預設是非阻塞的。
        // 為了在 Spring MVC (阻塞) 中使用，我們調用 .block()
        // 在大型專案中，最好保持整個鏈的非阻塞性 (Controller 回傳 Mono<LoginResponse>)
        // 但為了簡化，我們先使用 .block()
        var lineProfile = lineAuthClient.getProfile(loginRequest.lineAccessToken())
                .doOnError(e -> { throw new BadCredentialsException("Invalid Line Access Token"); })
                .block();

        if (lineProfile == null || lineProfile.getUserId() == null) {
            throw new BadCredentialsException("Failed to get Line profile.");
        }

        // 2. 查找或創建用戶
        CustomerDetailsEntity customerDetailsEntity = customerDetailsRepository.findByLineUserId(lineProfile.getUserId())
                .orElseGet(() -> createNewUser(lineProfile));

        // 3. 生成我們系統的 JWT
        String jwt = jwtTokenProvider.generateToken(customerDetailsEntity.getId()); // 使用我們系統的 User ID

        return new LoginResponse(jwt);
    }

    private CustomerDetailsEntity createNewUser(LineVerifyResponse lineProfile) {
        CustomerDetailsEntity newCustomerDetailsEntity = new CustomerDetailsEntity();
        newCustomerDetailsEntity.setLineUserId(lineProfile.getUserId());
        newCustomerDetailsEntity.setDisplayName(lineProfile.getDisplayName());
        newCustomerDetailsEntity.setPictureUrl(lineProfile.getPictureUrl());
        // Email 需要另外的流程 (e.g., LIFF) 來獲取

        return customerDetailsRepository.save(newCustomerDetailsEntity);
    }
}