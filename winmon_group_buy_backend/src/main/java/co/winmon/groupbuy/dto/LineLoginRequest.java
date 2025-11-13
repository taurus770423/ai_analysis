package co.winmon.groupbuy.dto;

import lombok.Data;

@Data
public class LineLoginRequest {
    // 來自 Web Login
    private String code;
    // 來自 LIFF
    private String accessToken;
}