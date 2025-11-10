package co.winmon.groupbuy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    // 我們系統自己簽發的 JWT
    private String jwt;
}